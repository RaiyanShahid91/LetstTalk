package com.raiyanshahid.letstalk.Fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.FragmentUserFragmentsBinding;
import com.raiyanshahid.letstalk.databinding.FragmentVideoBinding;

import java.util.Date;
import java.util.HashMap;

public class VideoFragments extends Fragment {

    FragmentVideoBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(getLayoutInflater(), container, false);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.videoFramLayout,new JoinVideoFragment()).commit();

        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        database.getReference().child("users").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.child("name").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.joinmeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.joinmeet.setTextColor(Color.BLACK);
                binding.history.setTextColor(Color.GRAY);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.videoFramLayout,new JoinVideoFragment()).commit();
            }
        });

        binding.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.history.setTextColor(Color.BLACK);
                binding.joinmeet.setTextColor(Color.GRAY);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.videoFramLayout,new VideoHistory()).commit();
            }
        });

        binding.createVideoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomsheetView= LayoutInflater.from(getActivity()).inflate(R.layout.layout_createvideoconference,
                        (CardView)bottomSheetDialog.findViewById(R.id.bottomsheetdialogVideo));
                TextView secretCodeText = bottomsheetView.findViewById(R.id.secretcode);
                String val = ""+((int)(Math.random()*9000)+1000);
                long time = new Date().getTime();
                String secretCode = String.valueOf(time)+ "LetsTalk"+val;
                bottomsheetView.findViewById(R.id.createRoom).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("name", userName);
                        hashMap.put("time", String.valueOf(time));
                        hashMap.put("uid", auth.getUid());
                        hashMap.put("secretCode", secretCode);
                        database.getReference().child("Video").child(auth.getUid()).child(String.valueOf(time)).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        bottomsheetView.findViewById(R.id.secreatcodeLayout).setVisibility(View.VISIBLE);
                                        bottomsheetView.findViewById(R.id.share).setVisibility(View.VISIBLE);
                                        bottomsheetView.findViewById(R.id.createRoom).setVisibility(View.GONE);
                                        secretCodeText.setText(secretCode);
                                    }
                                });
                    }
                });
                bottomsheetView.findViewById(R.id.copycode).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager myClipboard =  (ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData myClip = ClipData.newPlainText("text", secretCode);
                        myClipboard.setPrimaryClip(myClip);
                        Toast.makeText(getContext(), "Code Copied", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomsheetView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String textshare = userName+" has invited you to join a video meeting on Let'sTalk.\n " +
                                "\n"+
                                "Join the meeting with code : "+secretCode+
                                " \n\n"+
                                "Enjoy your meet";
                        Intent intentt = new Intent(Intent.ACTION_SEND);
                        intentt.setType("text/plain");
                        intentt.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intentt.putExtra(Intent.EXTRA_TEXT, textshare);
                        startActivity(Intent.createChooser(intentt, "Share Via"));
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


        return binding.getRoot();
    }
}