package com.example.studentmanagementsystem.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class EmailVerifyActivity extends AppCompatActivity {

    MaterialButton verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser.isEmailVerified()) {
            startActivity(new Intent(EmailVerifyActivity.this, DashboardActivity.class));
        }
        verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(getApplicationContext(), "Email verification sent to " + mUser.getEmail(), Toasty.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(EmailVerifyActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toasty.error(getApplicationContext(), "Error occurred ( " + task.getException() + " )", Toasty.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}