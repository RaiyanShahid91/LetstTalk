package com.raiyanshahid.letstalk.groups

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.Adapter.GroupMessage_Adapter
import com.raiyanshahid.letstalk.groups.Model.GroupMessage_Model
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class GroupChats : AppCompatActivity()
{
    var back: ImageView?= null
    var groupName : TextView?= null
    var recyclerView : RecyclerView?= null
    var groupTitle : String ?= null
    var database: FirebaseDatabase? = null
    var auth: FirebaseAuth? = null
    var editMessage: EditText? = null
    var sendMessage: ImageButton? = null
    var gallery_image: ImageView? = null
    var groupAdapter: GroupMessage_Adapter? = null
    var storage: FirebaseStorage? = null
    var messagesArrayList: ArrayList<GroupMessage_Model>? = null
    private val SHARED_PREFERENCES_NAME = "SHARE_IMAGE"
    var sharedPreferences: SharedPreferences? = null
    var groupId: String? = null
    var senderName: String? = null
    var myGroupRole: kotlin.String? = null
    var imageUri: Uri? = null
    var groupImage : String ?= null
    var groupMembers : String ?= null
    var groupCreatedby : String ?= null
    var groupAdmin : String ?= null
    var groupDescription : String ?= null
    var groupTimestamp : String ?= null
    var groupProfileImage : ImageView ?= null
    var groupmemberText : TextView ?= null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chats)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        groupTitle = intent.getStringExtra("name")
        groupImage = intent.getStringExtra("profileImage")
        groupMembers = intent.getStringExtra("groupMembers")
        groupCreatedby = intent.getStringExtra("createdby")
        groupAdmin = intent.getStringExtra("admin")
        groupDescription = intent.getStringExtra("groupdesc")
        groupTimestamp = intent.getStringExtra("timestamp")
        groupName = findViewById(R.id.groupName)
        back = findViewById(R.id.back)
        groupProfileImage = findViewById(R.id.groupProfile)
        recyclerView = findViewById(R.id.rec_message)
        gallery_image = findViewById(R.id.sender_imagebtn)
        editMessage = findViewById(R.id.edit_message)
        sendMessage = findViewById(R.id.senttext_btn)
        groupmemberText = findViewById(R.id.groupmember)
        groupId = intent.getStringExtra("groupID")
        groupName?.setText(groupTitle)
        groupmemberText?.setText("Group with "+groupMembers+" Members")
        val bytes: ByteArray = Base64.decode(groupImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        groupProfileImage!!.setImageBitmap(bitmap)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        messagesArrayList = ArrayList()

        back?.setOnClickListener {
            finish()
        }

        database!!.reference.child("users").child(auth!!.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        senderName = snapshot.child("firstName").getValue(String::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        loadMessage()
        loadMyGroupRole()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView?.setLayoutManager(linearLayoutManager)
        groupAdapter = GroupMessage_Adapter(this@GroupChats, messagesArrayList!!)
        recyclerView?.setAdapter(groupAdapter)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)


        sendMessage?.setOnClickListener(View.OnClickListener {
            val message = editMessage?.getText().toString()
            editMessage?.getText()!!.clear()
            if (message.isEmpty()) {
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val timestamp = "" + System.currentTimeMillis()
            val hashMap = HashMap<String, Any>()
            hashMap["sender"] = "" + auth!!.uid
            hashMap["message"] = "" + message
            hashMap["timestamp"] = "" + timestamp
            hashMap["username"] = "" + senderName
            val reference = FirebaseDatabase.getInstance().getReference("Groups_Chat")//image posting
            reference.child(groupId!!).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener {

                }.addOnFailureListener {
                    Toast.makeText(this, "Message sent fail", Toast.LENGTH_SHORT).show()
                }
        })

        gallery_image?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        })

        groupProfileImage?.setOnClickListener {
            val intent = Intent(this@GroupChats, GroupInfo::class.java)
            intent.putExtra("name", groupTitle)
            intent.putExtra("profileImage", groupImage)
            intent.putExtra("groupID", groupId)
            intent.putExtra("createdby", groupCreatedby)
            intent.putExtra("admin", groupAdmin)
            intent.putExtra("groupdesc", groupDescription)
            intent.putExtra("timestamp", groupTimestamp)
            startActivity(intent)
        }
    }

    private fun loadMyGroupRole() {
        val ref = FirebaseDatabase.getInstance().getReference("Groups_Chat")
        ref.child(groupId!!).child("Participants")
            .orderByChild("uid").equalTo(auth!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        myGroupRole = "" + ds.child("role").value
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadMessage() {
        messagesArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Groups_Chat")
        ref.child(groupId!!).child("Messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesArrayList?.clear()
                for (ds in snapshot.children) {
                    val model: GroupMessage_Model? = ds.getValue(GroupMessage_Model::class.java)
                    messagesArrayList?.add(model!!)
                    Log.i("LoadMessage", "checking" + messagesArrayList!!.size)
                }
                groupAdapter!!.notifyDataSetChanged()
                if (messagesArrayList!!.size > 0) {
                    recyclerView?.smoothScrollToPosition(messagesArrayList!!.size - 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.data
                var base64: String = convertBase64(imageUri)
                sendImage(base64)
            }
        }
    }

    private fun sendImage(base64: String) {
        val timestamp = "" + System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = "" + auth!!.uid
        hashMap["message"] = "" + base64
        hashMap["timestamp"] = "" + timestamp
        hashMap["username"] = "" + senderName
        database = FirebaseDatabase.getInstance()
        database!!.reference.child("Groups_Chat")
            .child(groupId!!)
            .child("Messages").child(timestamp)
            .setValue(hashMap).addOnSuccessListener {

            }.addOnFailureListener {
                Toast.makeText(this, "Message sent fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun convertBase64(imageUri: Uri?): String {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bytes = stream.toByteArray()
        var base64: String = Base64.encodeToString(bytes, Base64.DEFAULT)
        return base64
    }
}
