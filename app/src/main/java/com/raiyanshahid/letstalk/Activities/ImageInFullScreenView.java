package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityImageInFullScreenViewBinding;

public class ImageInFullScreenView extends AppCompatActivity {

    String imageUrl;

    ActivityImageInFullScreenViewBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityImageInFullScreenViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        imageUrl = getIntent().getStringExtra("image");


        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.imageView);

        


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}