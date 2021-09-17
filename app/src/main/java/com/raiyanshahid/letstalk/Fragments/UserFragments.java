package com.raiyanshahid.letstalk.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.raiyanshahid.letstalk.Activities.DashboardActivity;
import com.raiyanshahid.letstalk.Activities.EditProfile;
import com.raiyanshahid.letstalk.Activities.ProfileActivity;
import com.raiyanshahid.letstalk.Activities.ProfileImage;
import com.raiyanshahid.letstalk.Adapters.Profile_Adapter;
import com.raiyanshahid.letstalk.Adapters.TopStatusAdapter;
import com.raiyanshahid.letstalk.Adapters.UsersAdapter;
import com.raiyanshahid.letstalk.Models.Profile_Model;
import com.raiyanshahid.letstalk.Models.Status;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.Models.UserStatus;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.FragmentUserFragmentsBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class UserFragments extends Fragment {


    FragmentUserFragmentsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    User user;
    private ArrayList<Profile_Model> model;
    private Profile_Adapter adapter;

    String userName;
    String bio;
    String userImage;
    String dateOfBirth;
    String email;
    String password;
    String phone;
    String time;
    String uid;
    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserFragmentsBinding.inflate(getLayoutInflater(), container, false);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayoutprofile,new Post_Fragment()).commit();

        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        model = new ArrayList<>();




        database.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                userName = snapshot.child("name").getValue(String.class);
                bio = snapshot.child("bio").getValue(String.class);
                userImage = snapshot.child("profileImage").getValue(String.class);
                dateOfBirth = snapshot.child("dateofBirth").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);
                phone = snapshot.child("phoneNumber").getValue(String.class);
                time = snapshot.child("time").getValue(String.class);
                uid = snapshot.child("uid").getValue(String.class);
                token = snapshot.child("token").getValue(String.class);

                if (userImage != null) {
                    Glide.with(getContext())
                            .load(userImage)
                            .placeholder(R.drawable.avatar)
                            .into(binding.imageView);
                }
                binding.email.setText(email);
                binding.dob.setText(dateOfBirth);
                binding.name.setText(userName);
                binding.bio.setText(bio);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.postView.setBackgroundResource(R.color.black);
                binding.postText.setTextColor(Color.BLACK);
                binding.downloadText.setTextColor(Color.GRAY);
                binding.downloadView.setBackgroundResource(R.color.darkgrey);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayoutprofile,new Post_Fragment()).commit();
            }
        });

        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.postView.setBackgroundResource(R.color.darkgrey);
                binding.postText.setTextColor(Color.GRAY);
                binding.downloadText.setTextColor(Color.BLACK);
                binding.downloadView.setBackgroundResource(R.color.black);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayoutprofile,new Download_Fragment()).commit();
            }
        });

        binding.editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtra("name",userName);
                intent.putExtra("bio",bio);
                intent.putExtra("userImage",userImage);
                intent.putExtra("dob",dateOfBirth);
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                intent.putExtra("phone",phone);
                intent.putExtra("time",time);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileImage.class);
                intent.putExtra("image",userImage);
                intent.putExtra("name", userName);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    public void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }


}