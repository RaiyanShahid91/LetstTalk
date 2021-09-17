package com.raiyanshahid.letstalk.Adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.raiyanshahid.letstalk.Models.Profile_Model
import com.raiyanshahid.letstalk.R

class Profile_Adapter (private val productModel: ArrayList<Profile_Model?>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return productModel.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {

        return productModel.size.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view1: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_profile, parent, false)

        val courseModel: Profile_Model? = getItem(position) as Profile_Model?
        val courseIV: ImageView = view1.findViewById(R.id.uploadedimage)
        val message: String = productModel.get(position)!!.getImage()!!

        val bytes: ByteArray = Base64.decode(message, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        courseIV.setImageBitmap(bitmap)


        return view1
    }
}

