package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityRegisterBinding;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatePickerDialog.OnDateSetListener setListener;
    Uri uri;
    FirebaseStorage storage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RegisterActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();            }
        });

        Calendar calendar=Calendar.getInstance();
        final  int year=calendar.get(Calendar.YEAR);
        final  int month=calendar.get(Calendar.MONTH);
        final  int day=calendar.get(Calendar.DAY_OF_MONTH);


        binding.dateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });



        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (uri != null) {
                binding.imageView.setImageURI(uri);
            }
        }
    }

    private void Register()
    {
        String Name = binding.fullname.getText().toString();
        String Email = binding.email.getText().toString();
        String Phone = binding.phonebox.getText().toString();
        String Password = binding.password.getText().toString();
        String Bio = binding.bio.getText().toString();
        String DateOfBirth = binding.dateofBirth.getText().toString();

        if(Name.isEmpty()) {
            binding.fullname.setError("Please type your First Name");
            return;
        }

        if(Email.isEmpty()) {
            binding.email.setError("Please type your email");
            return;
        }
        if(Phone.isEmpty()) {
            binding.phonebox.setError("Please type your mobile no.");
            return;
        }
        if(Password.isEmpty()) {
            binding.password.setError("Please type your password");
            return;
        }
        if(DateOfBirth.isEmpty()) {
            binding.dateofBirth.setError("Please type your date of birth");
            return;
        }

        if (uri != null) {
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
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
                                            String filePath = uri.toString();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    long time = new Date().getTime();
                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                    hashMap.put("name", Name);
                                                    hashMap.put("uid", mAuth.getUid());
                                                    hashMap.put("email", Email);
                                                    hashMap.put("phoneNumber", Phone);
                                                    hashMap.put("password", Password);
                                                    hashMap.put("dateofBirth", DateOfBirth);
                                                    hashMap.put("profileImage", filePath);
                                                    hashMap.put("time", String.valueOf(time));
                                                    hashMap.put("bio", Bio);
                                                    User user = new User(mAuth.getUid(), Name, Email, Phone,Password,"2020",filePath,String.valueOf(time),Bio);
                                                    database.getReference().child("users").child(String.valueOf(mAuth.getUid())).setValue(user)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    {
                                                                        Toast.makeText(RegisterActivity.this, "Account Created, Login to Continue using Let's Talk", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {

                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });

                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed "+"Please Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                long time = new Date().getTime();
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("name", Name);
                                hashMap.put("uid", mAuth.getUid());
                                hashMap.put("email", Email);
                                hashMap.put("phoneNumber", Phone);
                                hashMap.put("password", Password);
                                hashMap.put("dateofBirth", DateOfBirth);
                                hashMap.put("profileImage", "No Profile Image");
                                hashMap.put("time", String.valueOf(time));
                                hashMap.put("bio", Bio);
                                database.getReference().child("users").child(String.valueOf(mAuth.getUid())).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Account Created, Login to Continue using Let's Talk", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed "+"Please Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }


}