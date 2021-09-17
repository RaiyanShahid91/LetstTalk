package com.raiyanshahid.letstalk.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.publicGroup.Adapter.PublicGroup_Adapter
import com.raiyanshahid.letstalk.publicGroup.CreatePublicGroup
import com.raiyanshahid.letstalk.publicGroup.Model.PublicGroup_Model

class PublicGroupFragments : Fragment() {

    var createGroup : LinearLayout?= null
    var groupRecyclerView : RecyclerView?= null
    var groupModel : ArrayList<PublicGroup_Model>? = null
    var groupAdapter : PublicGroup_Adapter?= null
    var database : FirebaseDatabase?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View=  inflater.inflate(R.layout.fragment_public_group_fragments, container, false)

        createGroup = view.findViewById(R.id.createGroup)
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView)
        database = FirebaseDatabase.getInstance()
        groupModel = ArrayList()


        val reference = database!!.reference.child("PublicGroup")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupModel?.clear()
                for (dataSnapshot in snapshot.children) {
                    val registrationModel: PublicGroup_Model? = dataSnapshot.getValue(PublicGroup_Model::class.java)
                    val newsModel = PublicGroup_Model()
                    newsModel.title = registrationModel?.title
                    newsModel.groupImage = registrationModel?.groupImage
                    newsModel.description = registrationModel?.description
                    newsModel.url = registrationModel?.url
                    newsModel.createdBy = registrationModel?.createdBy
                    newsModel.topic = registrationModel?.topic
                    newsModel.description = registrationModel?.description
                    newsModel.groupid = registrationModel?.groupid
                    groupModel!!.add(newsModel)

                }
                groupAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        groupAdapter = PublicGroup_Adapter(groupModel!!, requireContext())
        groupRecyclerView?.setAdapter(groupAdapter)

        createGroup!!.setOnClickListener {
            startActivity(Intent(context, CreatePublicGroup::class.java))
        }

        return  view
    }
}