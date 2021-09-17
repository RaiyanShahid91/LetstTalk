package com.raiyanshahid.letstalk.groups

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.Models.User
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.Adapter.AllUsersAdapter
import com.raiyanshahid.letstalk.groups.Model.AllUsersModel
import java.util.ArrayList
import java.util.HashMap

class AddParticipants : AppCompatActivity() , SetGroupValue
{
    var adapter: AllUsersAdapter? = null
    var newUsersList: ArrayList<AllUsersModel>? = null
    var usersArrayList: ArrayList<User>? = null
    var recyclerView: RecyclerView? = null
    var database: FirebaseDatabase? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var AddParticipants: Button? = null
    var back: ImageView? = null
    var groupId : String ?= null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_participants)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        back = findViewById(R.id.back)
        recyclerView = findViewById(R.id.recyclerViewAddParticipants)
        AddParticipants = findViewById(R.id.group_btn)
        groupId = intent.getStringExtra("groupId")
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersArrayList = ArrayList()
        newUsersList = ArrayList()
        checkUser()

        back?.setOnClickListener { finish() }

        val reference = database!!.reference.child("users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val registrationModel: User? = dataSnapshot.getValue(User::class.java)
                    if (firebaseAuth!!.uid != registrationModel!!.uid)
                        usersArrayList!!.add(registrationModel)
                }
                adapter!!.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        adapter = AllUsersAdapter(usersArrayList!!, this,this )
        recyclerView?.setAdapter(adapter)

        AddParticipants?.setOnClickListener {
            addParticipant(groupId!!,newUsersList!!)
        }
    }

    private fun checkUser() {
        val user = firebaseAuth!!.currentUser
        if (user != null) {

        }
    }

    fun addParticipant(groupId: String, user: ArrayList<AllUsersModel>) {
        for (i in user.indices) {
            val model: AllUsersModel = user[i]
            val timestamp = "" + System.currentTimeMillis()
            val hashMap = HashMap<String, String>()
            hashMap["uid"] = model.getUid()!!
            hashMap["name"] = model.getName()!!
            hashMap["userImage"] = model.getUserImage()!!
            hashMap["role"] = "participant"
            hashMap["timestamp"] = "" + timestamp
            val reference = FirebaseDatabase.getInstance().getReference("Groups_Chat")
            reference.child(groupId).child("Participants").child(model.getUid()!!)
                .setValue(hashMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Added fail", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun setData(user: ArrayList<AllUsersModel>?) {
        newUsersList = user as ArrayList<AllUsersModel>
    }
}