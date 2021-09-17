package com.raiyanshahid.letstalk.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Adapters.Profile_Adapter;
import com.raiyanshahid.letstalk.Models.Profile_Model;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.FragmentPostBinding;
import com.raiyanshahid.letstalk.databinding.FragmentUserFragmentsBinding;

import java.util.ArrayList;

public class Post_Fragment extends Fragment {


    private Profile_Adapter adapter;
    private ArrayList<Profile_Model> model;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FragmentPostBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(getLayoutInflater(), container, false);
        model = new ArrayList<>();
        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        post();

        adapter = new Profile_Adapter(model, getContext());
        binding.gridViewSavedPhotos.setAdapter(adapter);

        return  binding.getRoot();
    }

    private void post()
    {
        database.getReference().child("ProfileDashboard").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
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