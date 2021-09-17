package com.raiyanshahid.letstalk.publicGroup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.databinding.ActivityCreatePublicGroupBinding
import com.raiyanshahid.letstalk.publicGroup.Adapter.TopicSelected_Adapter
import com.raiyanshahid.letstalk.publicGroup.Model.Topic_selected
import com.raiyanshahid.letstalk.publicGroup.TopicInterface.SelectedTopicModel
import com.raiyanshahid.letstalk.publicGroup.TopicInterface.SetTopic
import java.util.HashMap

class CreatePublicGroup : AppCompatActivity(),SetTopic {

    var sdgAdapter: TopicSelected_Adapter? = null

    var sdgModel : ArrayList<Topic_selected>? = null
    var selectSdgModel : ArrayList<SelectedTopicModel>? = null
    var binding : ActivityCreatePublicGroupBinding ?= null
    var image : Uri?= null
    var storage: FirebaseStorage? = null
    var database : FirebaseDatabase ?= null
    var auth : FirebaseAuth ?= null
    var userImage : String ?= null
    var userName : String ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePublicGroupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        sdgModel = ArrayList()
        selectSdgModel = ArrayList()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        sdgModel!!.add(Topic_selected(R.drawable.education, "F","1"))
        sdgModel!!.add(Topic_selected(R.drawable.sports, "F","2"))
        sdgModel!!.add(Topic_selected(R.drawable.entertainment, "F","3"))
        sdgModel!!.add(Topic_selected(R.drawable.history, "F","4"))
        sdgModel!!.add(Topic_selected(R.drawable.sports, "F","5"))
        sdgModel!!.add(Topic_selected(R.drawable.travel, "F","6"))

        sdgAdapter = TopicSelected_Adapter(sdgModel!!, this@CreatePublicGroup, this)
        binding!!.gridview?.setAdapter(sdgAdapter)

        binding!!.back.setOnClickListener {
            finish()
        }

        binding!!.selectImage.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this@CreatePublicGroup, R.style.BottomSheetDialogTheme)
            val bottomsheetView = LayoutInflater.from(this).inflate(
                R.layout.layout_choosecamera_galley,
                bottomSheetDialog.findViewById<View>(R.id.bottomsheetdialog) as CardView?
            )
            bottomsheetView.findViewById<View>(R.id.camera).setOnClickListener {
                ImagePicker.with(this)
                    .cameraOnly()
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
                bottomSheetDialog.dismiss()
            }
            bottomsheetView.findViewById<View>(R.id.gallery).setOnClickListener {
                ImagePicker.with(this)
                    .galleryOnly()
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
                bottomSheetDialog.dismiss()
            }
            bottomsheetView.findViewById<View>(R.id.close)
                .setOnClickListener { bottomSheetDialog.dismiss() }

            bottomSheetDialog.setContentView(bottomsheetView)
            bottomSheetDialog.show()
        }

        binding!!.groupBtn.setOnClickListener {
            val groupName: String = binding!!.edtGroupTitle.getText().toString().trim()
            val description: String = binding!!.edtGroupDescription.getText().toString().trim()
            val timestamp = "" + System.currentTimeMillis()
        if (TextUtils.isEmpty(groupName) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter valid data ", Toast.LENGTH_SHORT).show()
        }
        if ((sdgModel!!.size) == 0 || sdgModel!!.size <= 2) {
            Toast.makeText(this@CreatePublicGroup, "Select any one ", Toast.LENGTH_SHORT).show() }

            if (image != null)
            {
                selectSdg(selectSdgModel!!)
            }
        }

        database!!.reference.child("users").child(auth!!.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userImage = snapshot.child("profileImage").getValue(String::class.java)
                        userName = snapshot.child("name").getValue(String::class.java)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            image = data!!.data
            binding?.groupImag?.setImageURI(image)
            if (image != null) {
                val timestamp = "" + System.currentTimeMillis()
                val storageReference = storage!!.reference.child("PublicGroup").child(timestamp)
                storageReference.putFile(image!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            image = uri
                        }
                    }
                }
            }

        }
    }

    private fun selectSdg(user: ArrayList<SelectedTopicModel>) {
        val groupName: String = binding!!.edtGroupTitle.getText().toString().trim()
        val description: String = binding!!.edtGroupDescription.getText().toString().trim()
        val url: String = binding!!.url.getText().toString().trim()

        val timestamp = "" + System.currentTimeMillis()

        if (TextUtils.isEmpty(groupName) || TextUtils.isEmpty(description) || TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Please enter valid data ", Toast.LENGTH_SHORT).show()
        }
        if ((sdgModel!!.size) == 0) {
            Toast.makeText(this@CreatePublicGroup, "Select three or more Sdg", Toast.LENGTH_SHORT).show()
        }
        if (image != null)
        {
            for (i in user.indices) {
                val model: SelectedTopicModel = user[i]
                val hashMap = HashMap<String, String>()
                hashMap["groupid"] = "" + timestamp
                hashMap["title"] = "" + groupName
                hashMap["description"] = "" + description
                hashMap["url"] = "" + url
                hashMap["topic"] = "" + model.getPosition()!!
                hashMap["groupImage"] = "" + image
                hashMap["profileImage"] = "" + userImage
                hashMap["createdBy"] = "" + userName
                val reference = FirebaseDatabase.getInstance().getReference("PublicGroup")
                reference.child(timestamp).setValue(hashMap).addOnSuccessListener {
                    val hashMap1 = HashMap<String, String?>()
                    hashMap1["uid"] = auth!!.uid
                    hashMap1["role"] = "creator"
                    hashMap1["timestamp"] = timestamp
                    val reference1 = FirebaseDatabase.getInstance().getReference("PublicGroupParticipants")
                    reference1.child(timestamp).child("Participants").child(auth!!.uid!!)
                        .setValue(hashMap1).addOnSuccessListener {
                            Toast.makeText(
                                this@CreatePublicGroup,
                                "Tcm Group created",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                }
            }
        }
        else {
            for (i in user.indices) {
                val model: SelectedTopicModel = user[i]
                val hashMap = HashMap<String, String>()
                hashMap["groupid"] = "" + timestamp
                hashMap["title"] = "" + groupName
                hashMap["description"] = "" + description
                hashMap["url"] = "" + url
                hashMap["topic"] = "" + model.getPosition()!!
                hashMap["groupImage"] = "https://firebasestorage.googleapis.com/v0/b/let-stalk-30ef0.appspot.com/o/Logo%2Fletstalk_logo.png?alt=media&token=d08d5d76-5358-416b-99bc-038978de5d0f"
                hashMap["profileImage"] = "" + userImage
                hashMap["createdBy"] = "" + userName
                val reference = FirebaseDatabase.getInstance().getReference("PublicGroup")
                reference.child(timestamp).setValue(hashMap).addOnSuccessListener {
                    val hashMap1 = HashMap<String, String?>()
                    hashMap1["uid"] = auth!!.uid
                    hashMap1["role"] = "creator"
                    hashMap1["timestamp"] = timestamp
                    val reference1 = FirebaseDatabase.getInstance().getReference("PublicGroupParticipants")
                    reference1.child(timestamp).child("Participants").child(auth!!.uid!!)
                        .setValue(hashMap1).addOnSuccessListener {
                            Toast.makeText(
                                this@CreatePublicGroup,
                                "Tcm Group created",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                }
            }
        }
    }


    override fun setdata(user: ArrayList<SelectedTopicModel>) {
        selectSdgModel = user as ArrayList<SelectedTopicModel>
    }

}