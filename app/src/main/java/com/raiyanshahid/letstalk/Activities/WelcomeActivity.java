package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    ActivityWelcomeBinding binding;
    FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString("email", "");
        if (auth.getUid() != null && s1 != null)
        {
            startActivity(new Intent(this, DashboardActivity.class));
        }
        else {
            binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

            binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
            });


            binding.register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                }
            });
        }
    }
}