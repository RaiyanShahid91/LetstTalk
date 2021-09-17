package com.raiyanshahid.letstalk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.raiyanshahid.letstalk.Activities.ProfileActivity;
import com.raiyanshahid.letstalk.Models.CommentsModel;
import com.raiyanshahid.letstalk.Models.User;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.TimeAgo;
import com.raiyanshahid.letstalk.databinding.LayoutCommentsBinding;

import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.UsersViewHolder> {

    Context context;
    ArrayList<CommentsModel> users;

    public CommentsAdapter(Context context, ArrayList<CommentsModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public CommentsAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comments, parent, false);

        return new CommentsAdapter.UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.UsersViewHolder holder, int position) {
        CommentsModel user = users.get(position);
        holder.binding.name.setText(user.getName());
        String timeAgo = TimeAgo.getTimeAgo(Long.parseLong(user.getTimestamp()));
        holder.binding.time.setText(timeAgo);

        holder.binding.message.setText(user.getComment());

        Glide.with(context)
                .load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.imageView);

        holder.binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        LayoutCommentsBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutCommentsBinding.bind(itemView);
        }
    }

}

