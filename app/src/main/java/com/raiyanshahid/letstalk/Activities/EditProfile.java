package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityEditProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding  binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String name;
    String bio;
    String profileImageBitmap;
    String dateOfBirth;
    String email;
    String password;
    String phone;
    String time;
    String uid;
    String token;
    FirebaseStorage storage;
    String profileImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        name = getIntent().getStringExtra("name");
        bio = getIntent().getStringExtra("bio");
        profileImageBitmap = getIntent().getStringExtra("userImage");
        dateOfBirth = getIntent().getStringExtra("dob");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        phone = getIntent().getStringExtra("phone");
        time = getIntent().getStringExtra("time");
        uid = getIntent().getStringExtra("uid");
        token = getIntent().getStringExtra("token");

        if (profileImageBitmap != null) {
            Glide.with(this)
                    .load(profileImageBitmap)
                    .placeholder(R.drawable.avatar)
                    .into(binding.imageView);
        }
        else
        {
            binding.imageView.setImageResource(R.drawable.avatar);
        }

        binding.fullname.setText(name);
        binding.bio.setText(bio);



        binding.attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(EditProfile.this,R.style.BottomSheetDialogTheme);
                View bottomsheetView= LayoutInflater.from(EditProfile.this).inflate(R.layout.layout_choosecamera_galley,
                        (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                bottomsheetView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.with(EditProfile.this)
                                .cameraOnly()
                                .cropSquare()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomsheetView.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.with(EditProfile.this)
                                .galleryOnly()
                                .cropSquare()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomsheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomsheetView);
                bottomSheetDialog.show();
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = binding.fullname.getText().toString();
                String Bio = binding.bio.getText().toString();

                if (profileImage != null) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", Name);
                    hashMap.put("email", email);
                    hashMap.put("profileImage", profileImage);
                    hashMap.put("phoneNumber", phone);
                    hashMap.put("password", password);
                    hashMap.put("dateofBirth", dateOfBirth);
                    hashMap.put("uid", uid);
                    hashMap.put("time", time);
                    hashMap.put("bio", Bio);
                    hashMap.put("token", token);
                    database.getReference().child("users").child(String.valueOf(auth.getUid())).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(EditProfile.this,R.style.BottomSheetDialogTheme);
                                    View bottomsheetView= LayoutInflater.from(EditProfile.this).inflate(R.layout.layout_bottom_register,
                                            (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                                    bottomSheetDialog.setContentView(bottomsheetView);
                                    bottomSheetDialog.show();
                                    Toast.makeText(EditProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditProfile.this, DashboardActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
                else
                {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", Name);
                    hashMap.put("email", email);
                    hashMap.put("profileImage", profileImageBitmap);
                    hashMap.put("phoneNumber", phone);
                    hashMap.put("password", password);
                    hashMap.put("dateofBirth", dateOfBirth);
                    hashMap.put("uid", uid);
                    hashMap.put("time", time);
                    hashMap.put("bio", Bio);
                    hashMap.put("token", token);

                    database.getReference().child("users").child(String.valueOf(auth.getUid())).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditProfile.this, DashboardActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                binding.imageView.setImageURI(uri);
                Calendar calendar = Calendar.getInstance();
                StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if (task.isSuccessful())
                       {
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   profileImage = String.valueOf(uri);
                               }
                           });
                       }
                    }
                });
            }
        }
    }
}