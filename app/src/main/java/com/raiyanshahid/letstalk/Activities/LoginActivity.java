package com.raiyanshahid.letstalk.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.raiyanshahid.letstalk.R;
import com.raiyanshahid.letstalk.databinding.ActivityLoginBinding;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email != null)
        {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }
        else {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

            mAuth = FirebaseAuth.getInstance();

            binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUserAccount();
                }
            });
            binding.register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            binding.forgetpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this, R.style.BottomSheetDialogTheme);
                    View bottomsheetView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_bottom_phone, (CardView) bottomSheetDialog.findViewById(R.id.bottomsheetdialog));
                    TextView Email = bottomsheetView.findViewById(R.id.email);

                    bottomsheetView.findViewById(R.id.continueBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String email = Email.getText().toString();
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
                            } else {
                                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Password reset link has been send to Email", Toast.LENGTH_SHORT).show();
                                            Email.setText("");
                                            bottomSheetDialog.dismiss();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
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

        }
    }

    private void loginUserAccount()
    {
        String email, password;
        email = binding.email.getText().toString();
        password = binding.password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();
                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("email", binding.email.getText().toString());
                                    myEdit.commit();
                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finishAffinity();

                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
    }
}