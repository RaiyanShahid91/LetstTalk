package com.raiyanshahid.letstalk.Adapters;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Activities.DashboardFullScreenImage;
import com.raiyanshahid.letstalk.Activities.ProfileActivity;
import com.raiyanshahid.letstalk.Fragments.UserFragments;
import com.raiyanshahid.letstalk.Models.DashboardModel;
import com.raiyanshahid.letstalk.Models.Meeting_Model;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.LayoutVideocallHistoryBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import kotlin.jvm.internal.Intrinsics;

public class Meeting_Adapter  extends RecyclerView.Adapter<Meeting_Adapter.UsersViewHolder> {

    Context context;
    ArrayList<Meeting_Model> users;

    public Meeting_Adapter(Context context, ArrayList<Meeting_Model> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Meeting_Adapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_videocall_history, parent, false);

        return new Meeting_Adapter.UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Meeting_Adapter.UsersViewHolder holder, int position) {
        Meeting_Model user = users.get(position);
        holder.binding.meetingcode.setText(user.getSecretCode());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(user.getTime()));
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        holder.binding.meetingcreateddate.setText(date);


        URL serverUrl;
        try {
            serverUrl=new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOption= new JitsiMeetConferenceOptions.Builder().setServerURL(serverUrl)
                    .setWelcomePageEnabled(true).build();
            JitsiMeet.setDefaultConferenceOptions(defaultOption);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textshare = user.getName()+" has invited you to join a video meeting on Let'sTalk.\n " +
                        "\n"+
                        "Join the meeting with code : "+user.getSecretCode()+
                        " \n\n"+
                        "Enjoy your meet";
                Intent intentt = new Intent(Intent.ACTION_SEND);
                intentt.setType("text/plain");
                intentt.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                intentt.putExtra(Intent.EXTRA_TEXT, textshare);
                context.startActivity(Intent.createChooser(intentt, "Share Via"));
            }
        });

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Video")
                        .child(user.getUid())
                        .child(user.getTime()).setValue(null);
                Toast.makeText(context, "Meeting Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Connecting Please Wait"+"\n"+"Do not Exit the Application", Toast.LENGTH_SHORT).show();
                JitsiMeetConferenceOptions options=new JitsiMeetConferenceOptions.Builder().setRoom(user.getSecretCode()).setWelcomePageEnabled(true).build();
                JitsiMeetActivity.launch(context,options);
            }
        });

        holder.binding.copied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager myClipboard =  (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", user.getSecretCode());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(context, "Code Copied", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        LayoutVideocallHistoryBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutVideocallHistoryBinding.bind(itemView);
        }
    }
}
