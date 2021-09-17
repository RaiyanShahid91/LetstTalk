package com.raiyanshahid.letstalk.publicGroup.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.publicGroup.Model.Topic_selected
import com.raiyanshahid.letstalk.publicGroup.TopicInterface.SelectedTopicModel
import com.raiyanshahid.letstalk.publicGroup.TopicInterface.SetTopic
import java.util.ArrayList

class TopicSelected_Adapter  (
    private val sdg_model: ArrayList<Topic_selected>,
    var context: Context,
    setvalue: SetTopic
) : BaseAdapter() {
    override fun getCount(): Int {
        Log.i("ProductFragment", "size" + sdg_model.size)
        return sdg_model.size
    }
    var follow = true
    var itemPosition: Int? = 0
    var followers: String? = null
    var newUser: ArrayList<SelectedTopicModel> = ArrayList<SelectedTopicModel>()
    var setvalue: SetTopic

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        Log.i("Size", "Array" + sdg_model.size)
        Log.i("Fragment", "size" + sdg_model.size)
        return sdg_model.size.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view1: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_publicgroup_topic, parent, false)
        val model: Topic_selected = sdg_model[position]


        val imageView = view1.findViewById<ImageView>(R.id.uploadedimage)
        val selectionText = view1.findViewById<TextView>(R.id.selectedText)
        val imagePosition = view1.findViewById<TextView>(R.id.position)
        val linearLayout = view1.findViewById<LinearLayout>(R.id.linear)
        imageView.setImageResource(sdg_model.get(position)!!.getSdg()!!)
        selectionText.text = "" + sdg_model.get(position)!!.getSelect()

        imageView.setOnClickListener {
            followers = selectionText.getText().toString();
            if (itemPosition != position) {
                follow = true
            }
            if (itemPosition != position && followers!!.equals("G")) {
                linearLayout?.setVisibility(View.VISIBLE)
                follow = false
            }
            if (follow) {
                itemPosition = position
                selectionText.text = "G"
                linearLayout?.setVisibility(View.VISIBLE)
                newUser.add(SelectedTopicModel(model.getPosition()))
                follow = false
            } else {
                itemPosition = position
                selectionText.text = "F"
                newUser.removeAt(0)
                linearLayout?.setVisibility(View.INVISIBLE)
                follow = true
            }
            setvalue.setdata(newUser)
        }


        return view1
    }

    init {
        this.setvalue = setvalue
    }
}