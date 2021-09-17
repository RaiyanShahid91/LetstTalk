package com.raiyanshahid.letstalk.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Adapters.Dashboard_Adapter;
import com.raiyanshahid.letstalk.Models.DashboardModel;
import com.raiyanshahid.letstalk.OverlapDecoration;
import com.raiyanshahid.letstalk.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class HomeFragments extends Fragment {

    private RecyclerView cardStack;
    private ArrayList<DashboardModel> modelArrayList;
    private LinearLayout postImage;

    FirebaseDatabase database;
    FirebaseAuth auth;
    String userName;
    String bio;
    String profileImage;
    Dashboard_Adapter adapter;
    String base64;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_fragments, container, false);
        modelArrayList = new ArrayList<>();
        cardStack = v.findViewById(R.id.recyclerView);
        postImage = v.findViewById(R.id.postImage);
        progressBar = v.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomsheetView= LayoutInflater.from(getActivity()).inflate(R.layout.layout_choosecamera_galley,
                        (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                bottomsheetView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.with(getActivity())
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
                        ImagePicker.with(getActivity())
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

        database.getReference().child("users").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.child("name").getValue(String.class);
                        bio = snapshot.child("bio").getValue(String.class);
                        profileImage = snapshot.child("profileImage").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Dashboard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    DashboardModel user = snapshot1.getValue(DashboardModel.class);
                    modelArrayList.add(user);
                    progressBar.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });


        adapter = new Dashboard_Adapter(getContext(),modelArrayList);
        cardStack.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        cardStack.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return  v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
            if (uri != null) {
                base64 = this.convertBase64(Uri.parse(String.valueOf(uri)));
                long time = new Date().getTime();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("name", userName);
                hashMap.put("bio", bio);
                hashMap.put("time", String.valueOf(time));
                hashMap.put("image", base64);
                hashMap.put("profileImage", profileImage);
                hashMap.put("uid", auth.getUid());
                database.getReference().child("Dashboard").child(String.valueOf(time)).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                database.getReference().child("ProfileDashboard").child(auth.getUid()).child(String.valueOf(time)).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                            }
                        });
        }}
    }

    private final String convertBase64(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream)stream);
        byte[] bytes = stream.toByteArray();
        String var10000 = Base64.encodeToString(bytes, 0);
        Intrinsics.checkNotNullExpressionValue(var10000, "encodeToString(bytes, Base64.DEFAULT)");
        String base64 = var10000;
        return base64;
    }




}