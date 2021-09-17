package com.raiyanshahid.letstalk.groups

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.raiyanshahid.letstalk.groups.Model.AllUsersModel
import com.raiyanshahid.letstalk.Models.User
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.Adapter.AllUsersAdapter
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class CreatingGroup : AppCompatActivity(), SetGroupValue {

    private val actionBar: ActionBar? = null
    private var firebaseAuth: FirebaseAuth? = null
    var groupTitle: EditText? = null
    var groupDesc: EditText? = null
    var back: ImageView? = null
    var selectImage: LinearLayout? = null
    var groupImage: CircleImageView? = null
    private var createGroup: Button? = null
    private var progressDialog: ProgressDialog? = null
    var database: FirebaseDatabase? = null
    var firstName: String? = null
    var userImage: String? = null
    var image: Uri? = null
    var imagePath: String? = null
    var bitmap: Bitmap? = null
    var encodeBitmapImage: String? = null
    var spinner: Spinner? = null
    var recyclerView: RecyclerView? = null
    var usersArrayList: ArrayList<User>? = null
    var newUsersList: ArrayList<AllUsersModel>? = null
    var adapter: AllUsersAdapter? = null
    private val SHARED_PREFERENCES_NAME = "MY_SP"
    var sharedPreferences: SharedPreferences? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_group)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        groupTitle = findViewById(R.id.groupname)
        groupDesc = findViewById(R.id.description)
        createGroup = findViewById(R.id.group_btn)
        back = findViewById(R.id.back)
        selectImage = findViewById(R.id.selectImage)
        groupImage = findViewById(R.id.groupImag)
        spinner = findViewById(R.id.spinner)
        recyclerView = findViewById(R.id.recyclerView)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        checkUser()
        usersArrayList = ArrayList()
        newUsersList = ArrayList()

        database!!.reference.child("users").child(firebaseAuth!!.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        firstName = snapshot.child("name").getValue(String::class.java)
                        userImage = snapshot.child("profileImage").getValue(String::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

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

        back?.setOnClickListener { finish() }
        createGroup?.setOnClickListener(View.OnClickListener {
            val GroupName: String = groupTitle?.text.toString()
            val GroupDescription: String = groupDesc?.text.toString()

            if (TextUtils.isEmpty(GroupName)) {
                Toast.makeText(this, "Add Group Title", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(GroupDescription)) {
                Toast.makeText(this, "Add Group Description", Toast.LENGTH_SHORT).show()
            } else {
                startCreatingGroup()
            }
        })

        selectImage?.setOnClickListener {
            Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        ImagePicker.with(this@CreatingGroup)
                            .crop() //Crop image(Optional), Check Customization for more option
                            .compress(1024) //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(
                                1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                            .start()
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                        p0: com.karumi.dexter.listener.PermissionRequest?,
                        permissionToken: PermissionToken?
                    ) {
                        permissionToken!!.continuePermissionRequest()
                    }
                }).check()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val filepath = data!!.data
            image = data.data
            val file: File = File(image.toString())
            imagePath = file.absolutePath

            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
            try {
                val inputStream = contentResolver.openInputStream(filepath!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                groupImage?.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap!!)
            } catch (e: Exception) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteofimage = byteArrayOutputStream.toByteArray()
        encodeBitmapImage = Base64.encodeToString(byteofimage, Base64.DEFAULT)
    }

    private fun startCreatingGroup() {
        val timestamp = "" + System.currentTimeMillis()
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("creating group")
        val title = groupTitle!!.text.toString().trim { it <= ' ' }
        val desc = groupDesc!!.text.toString().trim { it <= ' ' }
        if ((newUsersList!!.size) == 0) {
            Toast.makeText(this, "Atleast follow one user", Toast.LENGTH_SHORT).show()
        }

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Please Enter group title", Toast.LENGTH_SHORT).show()
            progressDialog?.dismiss()
        } else {
            val hashMap = HashMap<String, String>()
            hashMap["groupImage"]="" + encodeBitmapImage
            hashMap["groupId"] = "" + timestamp
            hashMap["groupTitle"] = "" + title
            hashMap["admin"] = "" + firstName
            hashMap["groupDescription"] = "" + desc
            hashMap["timestamp"] = "" + timestamp
            hashMap["createdBy"] = "" + firebaseAuth!!.uid
            val reference = FirebaseDatabase.getInstance().getReference("Groups_Chat")
            reference.child(timestamp).setValue(hashMap).addOnSuccessListener {
                val hashMap1 = HashMap<String, String?>()
                hashMap1["uid"] = firebaseAuth!!.uid
                hashMap1["role"] = "creator"
                hashMap1["name"] = firstName
                hashMap1["userImage"] = userImage
                hashMap1["timestamp"] = timestamp
                val reference1 = FirebaseDatabase.getInstance().getReference("Groups_Chat")
                reference1.child(timestamp).child("Participants").child(firebaseAuth!!.uid!!)
                    .setValue(hashMap1).addOnSuccessListener {
                        addParticipant(timestamp, newUsersList!!)
                        sharedPreferences = this.getSharedPreferences(
                            SHARED_PREFERENCES_NAME,
                            Context.MODE_PRIVATE
                        )
                        finish()
                        sharedPreferences!!.edit().putString("groupId", timestamp).apply()
                    }

            }
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

