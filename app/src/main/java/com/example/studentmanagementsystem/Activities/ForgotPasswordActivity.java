package com.example.studentmanagementsystem.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText emailTIET;
    TextView note;
    Button btn_login;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailTIET = findViewById(R.id.emailTIET);
        note = findViewById(R.id.note);
        btn_login = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailTIET.getText().toString().trim().isEmpty()) {
                    Toasty.error(ForgotPasswordActivity.this, "Email is Empty").show();
                    emailTIET.requestFocus();
                } else {
                    String email = emailTIET.getText().toString().trim();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                note.setText("Check your email");
                                Toasty.info(ForgotPasswordActivity.this, "Please check email inbox", Toasty.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toasty.error(ForgotPasswordActivity.this, "Error:" + error, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}
