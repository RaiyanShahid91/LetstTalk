package com.raiyanshahid.letstalk.groups.Adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.TimeAgo
import com.raiyanshahid.letstalk.groups.GroupChats
import com.raiyanshahid.letstalk.groups.Model.GroupMessage_Model
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.util.*

class GroupMessage_Adapter(private val mcontext: GroupChats, private val chatList: ArrayList<GroupMessage_Model>) : RecyclerView.Adapter<GroupMessage_Adapter.HolderGroupChat>() {

    var ITEM_SEND = 1
    var ITEM_RECEIVE = 2
    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1
    private var firebaseAuth: FirebaseAuth? = null
    var drawable: BitmapDrawable? = null
    var bitmap: Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderGroupChat {

        Log.i("LoadMessage", "Adapter" + chatList.size)
        return if (viewType == MSG_TYPE_RIGHT) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_groupchat_right, parent, false)
            HolderGroupChat(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_groupchat_left, parent, false)
            HolderGroupChat(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HolderGroupChat, position: Int) {
        val model: GroupMessage_Model = chatList[position]
        val message: String = model.getMessage()!!
        val senderUid: String = model.getSender()!!
        val timestamp: String = model.getTimestamp()!!
        val sendername: String = model.getUsername()!!

        if (message.length>200)
        {
            val bytes: ByteArray = Base64.decode(message, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            holder.message.setVisibility(View.GONE);
            holder.chatTime.setVisibility(View.VISIBLE);
            setUserName(model, holder, position)
            val timeAgo = TimeAgo.getTimeAgo(timestamp.toLong())
            holder.chatTime.setText(timeAgo)
            holder.profileImage.setVisibility(View.VISIBLE);
            holder.imageView.visibility = View.VISIBLE
            holder.linearlayout.setVisibility(View.GONE);
            holder.imageView.setImageBitmap(bitmap)
        }
        else {
            holder.imageView.visibility = View.GONE


            setUserName(model, holder, position)
            holder.profileImage.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.VISIBLE);
            holder.chatTime.setVisibility(View.VISIBLE);
            holder.senderName.setVisibility(View.VISIBLE);
            holder.message.setText(message);
            holder.linearlayout.setVisibility(View.VISIBLE);
            val timeAgo = TimeAgo.getTimeAgo(timestamp.toLong())
            holder.chatTime.setText(timeAgo)        }

        val timeAgo: String = TimeAgo.getTimeAgo(timestamp.toLong())


    }

    private fun saveImage(model: GroupMessage_Model, holder: GroupMessage_Adapter.HolderGroupChat, position: Int) {

        drawable = holder.imageView.getDrawable() as BitmapDrawable?
        bitmap = drawable!!.bitmap
        Log.i("File", "value"+bitmap)
        var fileOutputStream: FileOutputStream? = null
        val sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        val diretory = File(sdcard.absolutePath + "/Dasboard_image")
        diretory.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outfile = File(diretory, fileName)
        Toast.makeText(mcontext,"File save Succesfully", Toast.LENGTH_SHORT).show()

        try {
            fileOutputStream = FileOutputStream(outfile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(outfile)
            mcontext.sendBroadcast(intent)
        } catch (e: FileNotFoundException) {
            Log.i("File", "exception"+e.printStackTrace())
            e.printStackTrace()
        } catch (e: IOException) {
            Log.i("File", "exception"+e.printStackTrace())
            e.printStackTrace()
        }
    }

    private fun setUserName(model: GroupMessage_Model, holder: HolderGroupChat, position: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.orderByChild("uid").equalTo(model.getSender())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val profile = "" + ds.child("profileImage").value
                        val username = "" + ds.child("name").value
                        Glide.with(mcontext)
                            .load(profile)
                            .placeholder(R.drawable.avatar)
                            .into(holder.profileImage)
                        holder.senderName.setText(username);
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        Log.i("LoadMessage", "Adapter" + chatList.size)
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseAuth = FirebaseAuth.getInstance()
        Log.i("LoadMessage", "firebase" + firebaseAuth!!.uid)
        return if (chatList[position].getSender().equals(firebaseAuth!!.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    class HolderGroupChat(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView
        var senderName: TextView
        var chatTime: TextView
        var imageView: ImageView
        var profileImage:CircleImageView
        var linearlayout: LinearLayout

        init {
            imageView = itemView.findViewById(R.id.sender_image_chat)
            message = itemView.findViewById(R.id.msg_sender)
            senderName = itemView.findViewById(R.id.msg_senderName)
            profileImage =itemView.findViewById(R.id.chat_user_profile)
            chatTime =itemView.findViewById(R.id.chat_time)
            linearlayout =itemView.findViewById(R.id.msg_sender_linear)

        }
    }
}