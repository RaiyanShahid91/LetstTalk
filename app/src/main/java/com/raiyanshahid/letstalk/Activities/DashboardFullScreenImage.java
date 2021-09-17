package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityDashboardFullScreenImageBinding;

import kotlin.jvm.internal.Intrinsics;

public class DashboardFullScreenImage extends AppCompatActivity {

    String image ;

    ActivityDashboardFullScreenImageBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardFullScreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        image = getIntent().getStringExtra("image");

        byte[] var18 = Base64.decode(image, 0);
        Intrinsics.checkNotNullExpressionValue(var18, "Base64.decode(message, Base64.DEFAULT)");
        byte[] bytes = var18;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.image.setImageBitmap(bitmap);

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}