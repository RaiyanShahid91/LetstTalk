package com.raiyanshahid.letstalk.groups

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Base64
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.Models.User
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.databinding.ActivityGroupInfoBinding
import com.raiyanshahid.letstalk.groups.Adapter.GroupInfo_Adapter
import com.raiyanshahid.letstalk.groups.Model.ParticipantsUser_Model
import java.util.*
import kotlin.jvm.internal.Intrinsics

class GroupInfo : AppCompatActivity() {

    lateinit var binding : ActivityGroupInfoBinding

    var groupTitle : String ?= null
    var groupImage : String ?= null
    var groupCreatedby : String ?= null
    var groupAdmin : String ?= null
    var groupDescription : String ?= null
    var groupTimestamp : String ?= null
    var groupId: String? = null
    var usersArrayList: ArrayList<ParticipantsUser_Model>? = null
    var adapter: GroupInfo_Adapter? = null
    var database: FirebaseDatabase? = null
    var auth : FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        usersArrayList = ArrayList()

        groupTitle = intent.getStringExtra("name")
        groupImage = intent.getStringExtra("profileImage")
        groupCreatedby = intent.getStringExtra("createdby")
        groupAdmin = intent.getStringExtra("admin")
        groupDescription = intent.getStringExtra("groupdesc")
        groupTimestamp = intent.getStringExtra("timestamp")
        groupId = intent.getStringExtra("groupID")

        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(groupTimestamp!!.toLong())
        val date: String = DateFormat.format("dd-MM-yyyy", calendar).toString()
        val bytes: ByteArray = Base64.decode(groupImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.groupImage!!.setImageBitmap(bitmap)

        binding.groupName!!.setText(groupTitle)
        binding.groupAdmin!!.setText(groupAdmin)
        binding.groupCreateddate!!.setText(date)
        binding.groupDescription!!.setText(groupDescription)

        val reference = database!!.reference.child("Groups_Chat")
        reference.child(groupId!!).child("Participants").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList?.clear()
                for (dataSnapshot in snapshot.children) {
                        val registrationModel: ParticipantsUser_Model? = dataSnapshot.getValue(ParticipantsUser_Model::class.java)
                        val newsModel = ParticipantsUser_Model()
                        newsModel.name = registrationModel?.name
                        newsModel.uid = registrationModel?.uid
                        newsModel.userImage = registrationModel?.userImage
                        usersArrayList!!.add(newsModel)
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.recyclerView?.setLayoutManager(LinearLayoutManager(this@GroupInfo, LinearLayoutManager.VERTICAL, false))
        adapter = GroupInfo_Adapter(usersArrayList!!, this)
        binding.recyclerView?.setAdapter(adapter)

        database!!.reference.child("Groups_Chat").child(groupId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUserUid = snapshot.child("createdBy").getValue(String::class.java)

                    if (currentUserUid != auth!!.uid)
                    {
                        binding.groupName.setFocusable(false);
                        binding.groupDescription.setFocusable(false);
                        binding.groupBtn.setVisibility(View.GONE)
                        binding.selectImage.setVisibility(View.GONE)
                    }
                    else
                    {
                        binding.groupName.setFocusable(true);
                        binding.groupDescription.setFocusable(true);
                        binding.groupBtn.setVisibility(View.VISIBLE)
                        binding.selectImage.setVisibility(View.VISIBLE)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        binding.back.setOnClickListener { finish() }

        binding.addParticipants.setOnClickListener {
            val intent = Intent(this, AddParticipants::class.java);
            intent.putExtra("groupId", groupId)
            startActivity(intent);
        }
    }


}