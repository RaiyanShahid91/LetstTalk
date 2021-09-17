package com.raiyanshahid.letstalk.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.raiyanshahid.letstalk.Adapters.TopStatusAdapter;
import com.raiyanshahid.letstalk.Fragments.ChatFragments;
import com.raiyanshahid.letstalk.Fragments.HomeFragments;
import com.raiyanshahid.letstalk.Fragments.PublicGroup;
import com.raiyanshahid.letstalk.Fragments.UserFragments;
import com.raiyanshahid.letstalk.Fragments.VideoFragments;
import com.raiyanshahid.letstalk.Models.Status;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.Models.UserStatus;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityDashboardBinding;

import java.io.ByteArrayOutputStream;

import kotlin.jvm.internal.Intrinsics;


public class DashboardActivity extends AppCompatActivity{

    ActivityDashboardBinding binding;
    User user;
    FirebaseDatabase database;
    FirebaseAuth auth;
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
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new HomeFragments()).commit();

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

                Glide.with(DashboardActivity.this)
                        .load(userImage)
                        .placeholder(R.drawable.avatar)
                        .into(binding.profileImage);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ProfileImage.class);
                intent.putExtra("image",userImage);
                intent.putExtra("name", userName);
                startActivity(intent);
            }
        });

        binding.bottomnavigation2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;
                switch (item.getItemId())
                {
                    case R.id.home :
                        temp = new HomeFragments();
                        break;
                    case R.id.chat:
                        temp = new ChatFragments();
                        break;
                    case R.id.group:
                        temp = new PublicGroup();
                        break;
                    case R.id.video:
                        temp = new VideoFragments();
                        break;
                    case R.id.users:
                        temp = new UserFragments();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,temp).commit();

                return true;
            }
        });



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

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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

        binding.moreoprion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(DashboardActivity.this,R.style.BottomSheetDialogTheme);
                View bottomsheetView= LayoutInflater.from(DashboardActivity.this).inflate(R.layout.layout_option_bottom,
                        (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                bottomsheetView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(DashboardActivity.this,R.style.BottomSheetDialogTheme);
                        View bottomsheetView= LayoutInflater.from(DashboardActivity.this).inflate(R.layout.layout_bottom_logout,
                                (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                        bottomsheetView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                String s1 = sh.getString("email", null);
                                sh.edit().remove("email").commit();
                                auth.signOut();
                                Intent intent = new Intent(DashboardActivity.this,LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(DashboardActivity.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            }
                        });

                        bottomsheetView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetDialog.dismiss();
                            }
                        });
                        bottomSheetDialog.setContentView(bottomsheetView);
                        bottomSheetDialog.show();
                    }
                });
                bottomsheetView.findViewById(R.id.editprofile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DashboardActivity.this,EditProfile.class);
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

                bottomsheetView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomsheetView);
                bottomSheetDialog.show();
            }
        });


    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }



    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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
