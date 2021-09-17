package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Adapters.Profile_Adapter;
import com.raiyanshahid.letstalk.Models.Profile_Model;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityProfileBinding;

import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;

public class ProfileActivity extends AppCompatActivity {


    ActivityProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    User user;
    String userName;
    String userImage ;

    private ArrayList<Profile_Model> model;
    private Profile_Adapter adapter;
    String uid;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.darkblue));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = getIntent().getStringExtra("uid");

        model = new ArrayList<>();

        database.getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                 userName = snapshot.child("name").getValue(String.class);
                String bio = snapshot.child("bio").getValue(String.class);
                 userImage = snapshot.child("profileImage").getValue(String.class);
                if (userImage != null) {

                    Glide.with(ProfileActivity.this)
                            .load(userImage)
                            .placeholder(R.drawable.avatar)
                            .into(binding.imageView);
                }
                binding.name.setText(userName);
                binding.bio.setText(bio);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        post();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileImage.class);
                intent.putExtra("image",userImage);
                intent.putExtra("name", userName);
                startActivity(intent);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.postView.setBackgroundResource(R.color.black);
                binding.postText.setTextColor(Color.BLACK);
                binding.downloadText.setTextColor(Color.GRAY);
                binding.downloadView.setBackgroundResource(R.color.darkgrey);
                post();
            }
        });

        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.postView.setBackgroundResource(R.color.darkgrey);
                binding.postText.setTextColor(Color.GRAY);
                binding.downloadText.setTextColor(Color.BLACK);
                binding.downloadView.setBackgroundResource(R.color.black);
                download();
            }
        });

        adapter = new Profile_Adapter(model, this);
        binding.gridViewUploadedPhotos.setAdapter(adapter);
        binding.gridViewSavedPhotos.setAdapter(adapter);



    }

    private void post()
    {
        database.getReference().child("ProfileDashboard").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Profile_Model user = snapshot1.getValue(Profile_Model.class);
                    model.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void download()
    {
        database.getReference().child("SavedPhotos").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Profile_Model user = snapshot1.getValue(Profile_Model.class);
                    model.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}