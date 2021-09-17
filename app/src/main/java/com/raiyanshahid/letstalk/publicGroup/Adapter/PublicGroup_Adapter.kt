package com.raiyanshahid.letstalk.publicGroup.Adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.Models.User
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.TimeAgo
import com.raiyanshahid.letstalk.publicGroup.Model.PublicGroup_Model
import com.raiyanshahid.letstalk.publicGroup.TcmGroupChat
import java.util.HashMap

class PublicGroup_Adapter  (arrayList: ArrayList<PublicGroup_Model>, mContext: Context) : RecyclerView.Adapter<PublicGroup_Adapter.MyViewHolder>() {

    var arrayList: ArrayList<PublicGroup_Model>
    var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_publicgroup, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model: PublicGroup_Model = arrayList[position]
        var totalUsers : String ?= null
        holder.groupname.setText(arrayList[position].title)
        val timeAgo = TimeAgo.getTimeAgo(arrayList[position].groupid!!.toLong())
        holder.createdAt.setText("Created on "+timeAgo)
        holder.createdBy.setText("Owner "+arrayList[position].createdBy)
        Glide.with(mContext)
            .load(arrayList[position].groupImage)
            .placeholder(R.drawable.letstalk_logo)
            .into(holder.groupimage)

        var userImage : String ?= null
        var userName : String ?= null

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userName = snapshot.child("name").getValue(String::class.java)
                    userImage = snapshot.child("profileImage").getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        holder.itemView.setOnClickListener {
            val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
            val rootRef = FirebaseDatabase.getInstance().reference
            val userNameRef = rootRef.child("PublicGroupParticipants").child(arrayList[position].groupid!!)
                .child("Participants").child(firebaseAuth!!.uid!!)
            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        val bottomSheetDialog = BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme)
                        val bottomsheetView = LayoutInflater.from(mContext).inflate(R.layout.layout_joingroup,
                            bottomSheetDialog.findViewById<CardView>(R.id.bottomJoinGroup) as CardView?)

                        bottomsheetView.findViewById<TextView>(R.id.groupTitle).setText(arrayList[position].title)
                        bottomsheetView.findViewById<TextView>(R.id.member).setText(totalUsers+" members")
                        bottomsheetView.findViewById<TextView>(R.id.description).setText(arrayList[position].description)
                        bottomsheetView.findViewById<TextView>(R.id.url).setText(arrayList[position].url)
                        bottomsheetView.findViewById<TextView>(R.id.createdby).setText("Created by "+arrayList[position].createdBy)
                        val timeAgo = TimeAgo.getTimeAgo(arrayList[position].groupid!!.toLong())
                        bottomsheetView.findViewById<TextView>(R.id.createdOn).setText("Created on "+timeAgo)
                        bottomsheetView.findViewById<ImageView>(R.id.close)
                            .setOnClickListener { bottomSheetDialog.dismiss() }

                        bottomsheetView.findViewById<Button>(R.id.joingroup).setOnClickListener {
                            val timestamp = "" + System.currentTimeMillis()
                            val hashMap = HashMap<String, String>()
                            hashMap["uid"] = firebaseAuth!!.uid!!
                            hashMap["role"] = "participant"
                            hashMap["userImage"] = userImage!!
                            hashMap["user"] = userName!!
                            hashMap["timestamp"] = "" + timestamp
                            val reference = FirebaseDatabase.getInstance().getReference("PublicGroupParticipants")
                            reference.child(arrayList[position].groupid!!).child("Participants").child(firebaseAuth!!.uid!!).setValue(hashMap)
                                .addOnSuccessListener {
                                    Toast.makeText(mContext, "You Added successfully in "+ arrayList[position].title, Toast.LENGTH_SHORT).show()
                                    val intent = Intent(mContext, TcmGroupChat::class.java)
                                    intent.putExtra("sdgid",arrayList[position].groupid)
                                    intent.putExtra("totalUsers",totalUsers)
                                    intent.putExtra("sdgimage",arrayList[position].groupImage)
                                    intent.putExtra("sdgname",arrayList[position].title)
                                    intent.putExtra("sdgdescription",arrayList[position].description)
                                    intent.putExtra("sdg",arrayList[position].topic)
                                    mContext.startActivity(intent)
                                }.addOnFailureListener {
                                    Toast.makeText(mContext, " Failed!! Try Again Later", Toast.LENGTH_SHORT).show()
                                }
                        }
                        bottomSheetDialog.setContentView(bottomsheetView)
                        bottomSheetDialog.show()
                    }
                    else
                    {
                        val intent = Intent(mContext, TcmGroupChat::class.java)
                        intent.putExtra("sdgid",arrayList[position].groupid)
                        intent.putExtra("totalUsers",totalUsers)
                        intent.putExtra("sdgimage",arrayList[position].groupImage)
                        intent.putExtra("sdgname",arrayList[position].title)
                        intent.putExtra("sdgdescription",arrayList[position].description)
                        intent.putExtra("sdg",arrayList[position].topic)
                        mContext.startActivity(intent)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(ContentValues.TAG, databaseError.message) //Don't ignore errors!
                }
            }
            userNameRef.addListenerForSingleValueEvent(eventListener)
        }


        //This is used fo taking the total numbers of Members ina Tcm Group
        var userReference = FirebaseDatabase.getInstance().getReference("PublicGroupParticipants")
            .child(arrayList[position].groupid!!).child("Participants")
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalUsers = dataSnapshot.childrenCount.toString()
                holder.groupMember.setText(totalUsers)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var groupimage: ImageView
        var groupname: TextView
        var groupMember: TextView
        var groupVisibility : TextView
        var createdAt : TextView
        var createdBy : TextView


        init {
            groupimage = itemView.findViewById(R.id.groupImage)
            groupname = itemView.findViewById(R.id.groupName)
            groupMember = itemView.findViewById(R.id.totalmember)
            groupVisibility = itemView.findViewById(R.id.groupVisisbility)
            createdAt = itemView.findViewById(R.id.createdDate)
            createdBy = itemView.findViewById(R.id.createdBy)
        }
    }

    init {
        this.arrayList = arrayList
        this.mContext = mContext
    }
}