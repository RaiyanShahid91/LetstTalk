package com.raiyanshahid.letstalk.publicGroup

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.databinding.ActivityTcmGroupChatBinding
import com.raiyanshahid.letstalk.publicGroup.Adapter.TcmGroupAdapter
import com.raiyanshahid.letstalk.publicGroup.Model.TcmGroupModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TcmGroupChat : AppCompatActivity() {

    var sdgTitle : String ?= null
    var sdgDescription : String ?= null
    var sdgId : String ?= null
    var sdg : String ?= null
    var totalUsers : String ?= null
    var sdgGroupImage : String ?= null

    var senderName : String ?= null
    private val SHARED_PREFERENCES_NAME = "SHARE_IMAGE"
    var sharedPreferences: SharedPreferences? = null

    lateinit var binding : ActivityTcmGroupChatBinding
    var groupAdapter: TcmGroupAdapter? = null
    var messagesArrayList: ArrayList<TcmGroupModel>? = null
    var groupRole : String ?= null
    var imageUri : Uri ?= null
    var database : FirebaseDatabase ?= null
    var firebaseAuth : FirebaseAuth ?= null
    var storage : FirebaseStorage ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTcmGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        sdgId = intent.getStringExtra("sdgid")
        sdgTitle = intent.getStringExtra("sdgname")
        sdgDescription = intent.getStringExtra("sdgdescription")
        sdg = intent.getStringExtra("sdg")
        totalUsers = intent.getStringExtra("totalUsers")
        sdgGroupImage = intent.getStringExtra("sdgimage")

        Glide.with(this@TcmGroupChat)
            .load(sdgGroupImage)
            .placeholder(R.drawable.avatar)
            .into(binding.groupProfile)
        messagesArrayList = ArrayList()
        binding.groupname.setText(sdgTitle+" TCM")

        binding.totalUsers.setText("Group with "+totalUsers+" members")

        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.back.setOnClickListener {
            finish()
        }

      database!!.reference.child("user").child(firebaseAuth!!.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        senderName = snapshot.child("name").getValue(String::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        loadMessage()
        loadMyGroupRole()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.recMessage?.setLayoutManager(linearLayoutManager)
        groupAdapter = TcmGroupAdapter(this@TcmGroupChat, messagesArrayList!!)
        binding.recMessage?.setAdapter(groupAdapter)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        val imageId: String? = sharedPreferences!!.getString("imageId", null)
        Log.i("Imagevalue",""+imageId)
        if(imageId!=null)
        {
            sendImage(imageId)
            sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
            sharedPreferences!!.edit().putString("imageId", null).apply()
        }

        binding.senttextBtn?.setOnClickListener(View.OnClickListener {
            val message = binding.editMessage?.getText().toString()
            binding.editMessage?.getText()!!.clear()
            if (message.isEmpty()) {
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val timestamp = "" + System.currentTimeMillis()
            val hashMap = HashMap<String, Any>()
            hashMap["sender"] = "" + firebaseAuth!!.uid
            hashMap["message"] = "" + message
            hashMap["timestamp"] = "" + timestamp
            hashMap["username"] = "" + senderName
            val reference = FirebaseDatabase.getInstance().getReference("SdgParticipants").child("GroupMessage")//image posting
            reference.child(sdgId!!).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener {

                }.addOnFailureListener {
                    Toast.makeText(this, "Message sent fail", Toast.LENGTH_SHORT).show()
                }
        })

        binding.senderImagebtn?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        })
    }

    private fun loadMyGroupRole() {
        val ref = FirebaseDatabase.getInstance().getReference("SdgParticipants")
        ref.child(sdgId!!).child("Participants")
            .orderByChild("uid").equalTo(firebaseAuth!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        groupRole = "" + ds.child("role").value
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadMessage() {
        messagesArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("SdgParticipants").child("GroupMessage")
        ref.child(sdgId!!).child("Messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesArrayList?.clear()
                for (ds in snapshot.children) {
                    val model: TcmGroupModel? = ds.getValue(TcmGroupModel::class.java)
                    messagesArrayList?.add(model!!)
                    Log.i("LoadMessage", "checking" + messagesArrayList!!.size)
                }
                groupAdapter!!.notifyDataSetChanged()
                if (messagesArrayList!!.size > 0) {
                    binding.recMessage?.smoothScrollToPosition(messagesArrayList!!.size - 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val storageReference = storage!!.reference.child("SdgParticipants").child(firebaseAuth!!.uid!!)
        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.data
                val base64: String = convertBase64(imageUri)
                sendImage(base64)
            }
        }
    }

    private fun sendImage(base64: String) {
        val currentTime = Calendar.getInstance().time
        val timestamp = "" + System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = "" + firebaseAuth!!.uid
        hashMap["message"] = "" + base64
        hashMap["timestamp"] = "" + timestamp
        hashMap["username"] = "" + senderName
        database = FirebaseDatabase.getInstance()
        database!!.reference.child("SdgParticipants").child("GroupMessage")
            .child(sdgId!!)
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
        val base64: String = Base64.encodeToString(bytes, Base64.DEFAULT)
        return base64
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence").child(currentId!!).setValue(currentTime)
    }
}