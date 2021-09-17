package com.raiyanshahid.letstalk.Activities

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.databinding.ActivityProfileImageBinding

class ProfileImage : AppCompatActivity()
{
    var profileImage : String ?= null
    var name : String ?= null
    lateinit var binding : ActivityProfileImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileImage = intent.getStringExtra("image");
        name = intent.getStringExtra("name")

        Glide.with(this@ProfileImage)
            .load(profileImage)
            .placeholder(R.drawable.avatar)
            .into(binding.image)

        binding.name.setText(name)

        binding.back.setOnClickListener { finish() }

    }
}