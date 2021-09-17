package com.raiyanshahid.letstalk.groups.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.GroupChats
import com.raiyanshahid.letstalk.groups.Model.AllUsersModel
import com.raiyanshahid.letstalk.groups.Model.GroupList_Model
import com.raiyanshahid.letstalk.groups.Model.ParticipantsUser_Model
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class GroupInfo_Adapter (arrayList: ArrayList<ParticipantsUser_Model>, mContext: Context) : RecyclerView.Adapter<GroupInfo_Adapter.MyViewHolder>() {
    var newUser: ArrayList<ParticipantsUser_Model> = ArrayList<ParticipantsUser_Model>()
    var arrayList: ArrayList<ParticipantsUser_Model>
    var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_group_participants, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ParticipantsUser_Model = arrayList[position]
        holder.username.setText(arrayList[position].name)
        var userUid : String ?= null
        userUid = arrayList[position].uid

        Glide.with(mContext)
            .load(arrayList[position].userImage)
            .placeholder(R.drawable.avatar)
            .into(holder.usersImage)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView
        var usersImage: CircleImageView

        init {
            username = itemView.findViewById(R.id.suugestion_profile)
            usersImage = itemView.findViewById(R.id.suggestion_image)
        }
    }

    init {
        this.arrayList = arrayList
        this.mContext = mContext
    }


}