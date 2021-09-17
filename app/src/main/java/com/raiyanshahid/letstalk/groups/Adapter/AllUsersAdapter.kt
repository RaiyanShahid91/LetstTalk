package com.raiyanshahid.letstalk.groups.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raiyanshahid.letstalk.groups.Model.AllUsersModel
import com.raiyanshahid.letstalk.Models.User
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.SetGroupValue
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class AllUsersAdapter(arrayList: ArrayList<User>, mContext: Context, setvalue: SetGroupValue) : RecyclerView.Adapter<AllUsersAdapter.MyViewHolder>() {
    var newUser: ArrayList<AllUsersModel> = ArrayList<AllUsersModel>()
    var c = 0
    var follow = true
    var arrayList: ArrayList<User>
    var mContext: Context
    var itemPosition :Int?=0
    var followers:String?=null
    var setvalue: SetGroupValue
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_addparticipant, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: User = arrayList[position]
        holder.username.setText(arrayList[position].name)
        Glide.with(mContext)
            .load(model.profileImage)
            .placeholder(R.drawable.avatar)
            .into(holder.userImage)
        holder.follow.setOnClickListener {
            followers =  holder.follow.getText().toString();
            if(itemPosition!=position)
            {
                follow=true
            }
            if(itemPosition!=position && followers!!.equals("Added"))
            {
                follow=false
            }
            if (follow) {
                itemPosition  =position
                Log.i("Following Member Text$followers",".....")
                holder.follow.text = "Added"
                newUser.add(AllUsersModel(model.uid, model.name, model.profileImage))
                Log.i("Follow User", "users"+newUser.size)
                Log.i("Add User ${newUser.size}","")
                follow = false
            } else {
                itemPosition  =position
                holder.follow.text = "Add"
                follow = true
                newUser.removeAt(0)
                Log.i("Follow User", "removed"+newUser.size)
                Log.i("Add User ${newUser.size}","")
            }
            setvalue.setData(newUser)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var follow: Button
        var userImage: CircleImageView
        var username: TextView
        init {
            follow = itemView.findViewById(R.id.followbtn)
            userImage = itemView.findViewById(R.id.suggestion_image)
            username = itemView.findViewById(R.id.suugestion_profile)
        }
    }

    init {
        this.arrayList = arrayList
        this.mContext = mContext
        this.setvalue = setvalue
    }
}
