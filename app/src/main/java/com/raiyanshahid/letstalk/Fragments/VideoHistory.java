package com.raiyanshahid.letstalk.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Adapters.Dashboard_Adapter;
import com.raiyanshahid.letstalk.Adapters.Meeting_Adapter;
import com.raiyanshahid.letstalk.Adapters.Profile_Adapter;
import com.raiyanshahid.letstalk.Models.DashboardModel;
import com.raiyanshahid.letstalk.Models.Meeting_Model;
import com.raiyanshahid.letstalk.Models.Profile_Model;
import com.raiyanshahid.letstalk.OverlapDecoration;
import com.raiyanshahid.letstalk.R;

import java.util.ArrayList;


public class VideoHistory extends Fragment {

    private ArrayList<Meeting_Model> modelArrayList;
    private Meeting_Adapter adapter;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_history, container, false);
        modelArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        database.getReference().child("Video").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Meeting_Model user = snapshot1.getValue(Meeting_Model.class);
                    modelArrayList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new Meeting_Adapter(getContext(),modelArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return  view;
    }
}