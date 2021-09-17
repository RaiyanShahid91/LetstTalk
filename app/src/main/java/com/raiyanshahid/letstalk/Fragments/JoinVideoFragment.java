package com.raiyanshahid.letstalk.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.FragmentJoinVideoBinding;
import com.raiyanshahid.letstalk.databinding.FragmentVideoBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class JoinVideoFragment extends Fragment {

    FragmentJoinVideoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentJoinVideoBinding.inflate(getLayoutInflater(), container, false);

        URL serverUrl;
        try {
            serverUrl=new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOption= new JitsiMeetConferenceOptions.Builder().setServerURL(serverUrl)
                    .setWelcomePageEnabled(true).build();
            JitsiMeet.setDefaultConferenceOptions(defaultOption);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secretCode = binding.code.getText().toString();
                if(secretCode.isEmpty()) {
                    binding.code.setError("Please Enter meeting Code to Join");
                    return;
                }
                else
                {
                    Toast.makeText(getContext(), "Connecting Please Wait"+"\n"+"Do not Exit the Application", Toast.LENGTH_SHORT).show();
                    JitsiMeetConferenceOptions options=new JitsiMeetConferenceOptions.Builder().setRoom(secretCode).setWelcomePageEnabled(true).build();
                    JitsiMeetActivity.launch(getContext(),options);
                    binding.code.setText("");
                }
            }
        });

        return  binding.getRoot();
    }
}