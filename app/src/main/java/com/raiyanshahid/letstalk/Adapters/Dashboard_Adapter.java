package com.raiyanshahid.letstalk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raiyanshahid.letstalk.Activities.CommentsActivity;
import com.raiyanshahid.letstalk.Activities.ProfileActivity;
import com.raiyanshahid.letstalk.Fragments.UserFragments;
import com.raiyanshahid.letstalk.Models.DashboardModel;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.TimeAgo;
import com.raiyanshahid.letstalk.databinding.LayoutDashboardBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class Dashboard_Adapter extends RecyclerView.Adapter<Dashboard_Adapter.UsersViewHolder> {

    Context context;
    ArrayList<DashboardModel> users;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public Dashboard_Adapter(Context context, ArrayList<DashboardModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dashboard, parent, false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        DashboardModel user = users.get(position);
        holder.binding.name.setText(user.getName());
//        holder.binding.bio.setText(user.getBio());
        holder.binding.time.setText(user.getTime());
        String uid = user.getUid();

        byte[] var18 = Base64.decode(user.getImage(), 0);
        Intrinsics.checkNotNullExpressionValue(var18, "Base64.decode(message, Base64.DEFAULT)");
        byte[] bytes = var18;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.binding.dashboardImage.setImageBitmap(bitmap);
        String timeAgo = TimeAgo.getTimeAgo(Long.parseLong(user.getTime()));
        holder.binding.time.setText(timeAgo);


         auth = FirebaseAuth.getInstance();
         database = FirebaseDatabase.getInstance();

         database.getReference().child("Likes").child(user.getTime()).child(auth.getUid())
                 .addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists())
                 {
                     holder.binding.likeImg.setVisibility(View.VISIBLE);
                     holder.binding.unlikeImg.setVisibility(View.GONE);
                 }
                 else
                 {
                     holder.binding.likeImg.setVisibility(View.GONE);
                     holder.binding.unlikeImg.setVisibility(View.VISIBLE);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

         database.getReference("Likes").child(user.getTime())
                 .addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String totalLikeString = String.valueOf(snapshot.getChildrenCount());
                         holder.binding.totalLike.setText(totalLikeString);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });

        database.getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userImage = snapshot.child("profileImage").getValue(String.class);
                if (userImage != null) {
                    Glide.with(context)
                            .load(userImage)
                            .placeholder(R.drawable.avatar)
                            .into(holder.binding.imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getUid() != uid)
                {
                    Fragment fragment = new UserFragments();
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                {

                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            }
        });

        holder.binding.unlikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.likeImg.setVisibility(View.VISIBLE);
                holder.binding.unlikeImg.setVisibility(View.GONE);
                saveLike(user.getTime());
            }
        });

        holder.binding.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.likeImg.setVisibility(View.GONE);
                holder.binding.unlikeImg.setVisibility(View.VISIBLE);
                unLike(user.getTime());
            }
        });


//        holder.binding.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                long time = new Date().getTime();
//                HashMap<String,String> hashMap = new HashMap<>();
//                hashMap.put("image",user.getImage());
//                hashMap.put("uid", auth.getUid());
//                hashMap.put("time", String.valueOf(time));
//
//                database.getReference().child("SavedPhotos").child(auth.getUid()).child(String.valueOf(time)).setValue(hashMap)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });


        holder.binding.top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) holder.binding.dashboardImage.getDrawable();
                Bitmap bitmap1 =drawable.getBitmap();
                FileOutputStream fileOutputStream = null;
                File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File dir = new File(sdcard.getAbsolutePath() +"/LetsTalk");
                dir.mkdirs();
                String fileName =String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir,fileName);
                Toast.makeText(context, "File Saved", Toast.LENGTH_SHORT).show();
                try
                {
                    fileOutputStream = new FileOutputStream(outFile);
                    bitmap1.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outFile));
                    context.sendBroadcast(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (outFile != null)
                {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra("comments", fileName);
                    intent.putExtra("id", user.getTime());
                    context.startActivity(intent);
                }
            }
        });

    }

    private void saveLike(String  message)
    {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("like","1");
        database.getReference().child("Likes").child(message).child(auth.getUid())
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    private void unLike(String  message)
    {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("like","1");
        database.getReference().child("Likes").child(message).child(auth.getUid())
                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        LayoutDashboardBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutDashboardBinding.bind(itemView);
        }
    }
}
