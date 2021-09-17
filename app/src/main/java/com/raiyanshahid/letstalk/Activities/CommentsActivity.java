package com.raiyanshahid.letstalk.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Adapters.CommentsAdapter;
import com.raiyanshahid.letstalk.Adapters.Dashboard_Adapter;
import com.raiyanshahid.letstalk.Models.CommentsModel;
import com.raiyanshahid.letstalk.Models.DashboardModel;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.TimeAgo;
import com.raiyanshahid.letstalk.databinding.ActivityCommentsBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class CommentsActivity extends AppCompatActivity {

    ActivityCommentsBinding binding;
    String imageUrl;
    File file;
    File directory;
    String id;
    FirebaseDatabase database;
    FirebaseAuth auth;
    User user;
    String userName;
    String userImage;
    ArrayList<CommentsModel> modelArrayList;
    CommentsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.lightpurple));

        modelArrayList = new ArrayList<>();

        imageUrl = getIntent().getStringExtra("comments");
        id = getIntent().getStringExtra("id");

        String timeAgo = TimeAgo.getTimeAgo(Long.parseLong(id));
        binding.time.setText(timeAgo);

        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();

        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        directory = new File(sdCard.getAbsolutePath() +"/LetsTalk");
        file =new File(directory,imageUrl);
        binding.dashboardImage.setImageDrawable(Drawable.createFromPath(file.toString()));


        database.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                userName = snapshot.child("name").getValue(String.class);
                userImage = snapshot.child("profileImage").getValue(String.class);
                binding.name.setText(userName);
                Glide.with(CommentsActivity.this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.avatar)
                        .into(binding.imageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("Comments").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String totalLikeString = String.valueOf(snapshot.getChildrenCount());
                        binding.totalComment.setText(totalLikeString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference("Likes").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String totalLikeString = String.valueOf(snapshot.getChildrenCount());
                        binding.totalLike.setText(totalLikeString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Likes").child(id).child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            binding.likeImg.setVisibility(View.VISIBLE);
                            binding.unlikeImg.setVisibility(View.GONE);
                        }
                        else
                        {
                            binding.likeImg.setVisibility(View.GONE);
                            binding.unlikeImg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Comments").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommentsModel user = snapshot1.getValue(CommentsModel.class);
                    modelArrayList.add(user);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter = new CommentsAdapter(CommentsActivity.this,modelArrayList);
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.commentRecyclerView.setAdapter(adapter);

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageText.getText().toString();
                if(message.isEmpty()) {
                    Toast.makeText(CommentsActivity.this, "Enter a comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    long time = new Date().getTime();
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("comments",message);
                    hashMap.put("timestamp",String.valueOf(time));
                    hashMap.put("name",userName);
                    hashMap.put("profileImage",userImage);
                    database.getReference().child("Comments").child(id).child(auth.getUid())
                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
            }
        });

        binding.unlikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.likeImg.setVisibility(View.VISIBLE);
                binding.unlikeImg.setVisibility(View.GONE);
                saveLike(id);
            }
        });

        binding.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.likeImg.setVisibility(View.GONE);
                binding.unlikeImg.setVisibility(View.VISIBLE);
                unLike(id);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void saveLike(String  message)
    {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("like","1");
        database.getReference().child("Likes").child(message).child(auth.getUid())
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    private void unLike(String  message)
    {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("like","1");
        database.getReference().child("Likes").child(message).child(auth.getUid())
                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }
}
