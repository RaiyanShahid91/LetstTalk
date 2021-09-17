package com.raiyanshahid.letstalk.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.groups.Adapter.GroupList_Adapter
import com.raiyanshahid.letstalk.groups.CreatingGroup
import com.raiyanshahid.letstalk.groups.Model.GroupList_Model
import java.util.ArrayList

class GroupFragments : Fragment() {

    var recyclerView: RecyclerView? = null
    var adapter: GroupList_Adapter? = null
    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var usersArrayList: ArrayList<GroupList_Model>? = null
    var createGroup : LinearLayout?= null
    var back : ImageView?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view : View = inflater.inflate(R.layout.fragment_group_fragments, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        createGroup = view.findViewById(R.id.Group)

        usersArrayList = ArrayList()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val reference = database!!.reference.child("Groups_Chat")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList?.clear()
                for (dataSnapshot in snapshot.children) {
                    if (dataSnapshot.child("Participants").child(auth!!.uid!!).exists())
                    {
                        val registrationModel: GroupList_Model? = dataSnapshot.getValue(GroupList_Model::class.java)
                        val newsModel = GroupList_Model()
                        newsModel.groupTitle = registrationModel?.groupTitle
                        newsModel.groupId = registrationModel?.groupId
                        newsModel.timestamp = registrationModel?.timestamp
                        newsModel.groupDescription = registrationModel?.groupDescription
                        newsModel.groupImage = registrationModel?.groupImage
                        newsModel.createdBy = registrationModel?.createdBy
                        newsModel.admin = registrationModel?.admin
                        usersArrayList!!.add(newsModel)
                    }
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        recyclerView?.setLayoutManager(LinearLayoutManager(context))
        adapter = GroupList_Adapter(usersArrayList!!, requireContext())
        recyclerView?.setAdapter(adapter)

        createGroup?.setOnClickListener {
            val intent = Intent(context, CreatingGroup::class.java);
            startActivity(intent);
        }


        return  view
    }
}