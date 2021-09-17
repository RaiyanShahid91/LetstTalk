package com.raiyanshahid.letstalk.groups.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.Activities.ProfileImage
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.GroupChats
import com.raiyanshahid.letstalk.groups.Model.GroupList_Model
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class GroupList_Adapter (arrayList: ArrayList<GroupList_Model>, mContext: Context) : RecyclerView.Adapter<GroupList_Adapter.MyViewHolder>() {
    var newUser: ArrayList<GroupList_Model> = ArrayList<GroupList_Model>()
    var arrayList: ArrayList<GroupList_Model>
    var mContext: Context
    var followers:String?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_groups, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: GroupList_Model = arrayList[position]
        holder.groupname.setText(arrayList[position].groupTitle)
        holder.grouplastMsgTime.setText(arrayList[position].timestamp)
        holder.grouplastMsg.setText(arrayList[position].groupDescription)

        val bytes: ByteArray = Base64.decode(arrayList[position].groupImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.groupimage.setImageBitmap(bitmap)

        var totalUsers : String ?= null

        var userReference = FirebaseDatabase.getInstance().getReference("Groups_Chat")
            .child(arrayList[position].groupId!!).child("Participants")
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalUsers = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, GroupChats::class.java)
            intent.putExtra("name", arrayList.get(position).groupTitle)
            intent.putExtra("profileImage", arrayList[position].groupImage)
            intent.putExtra("groupID", arrayList.get(position).groupId)
            intent.putExtra("createdby", arrayList.get(position).createdBy)
            intent.putExtra("admin", arrayList.get(position).admin)
            intent.putExtra("groupdesc", arrayList.get(position).groupDescription)
            intent.putExtra("timestamp", arrayList.get(position).timestamp)
            intent.putExtra("groupMembers", totalUsers)
            mContext.startActivity(intent)
        }

        


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var groupimage: CircleImageView
        var groupname: TextView
        var grouplastMsg: TextView
        var grouplastMsgTime: TextView

        init {
            groupimage = itemView.findViewById(R.id.groupImage)
            groupname = itemView.findViewById(R.id.groupname)
            grouplastMsg = itemView.findViewById(R.id.lastmsg)
            grouplastMsgTime = itemView.findViewById(R.id.time)
        }
    }

    init {
        this.arrayList = arrayList
        this.mContext = mContext
    }
}
