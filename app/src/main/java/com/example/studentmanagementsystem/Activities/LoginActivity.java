package com.example.studentmanagementsystem.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText adminLoginEmailTIET, adminLoginPasswordTIET;
    Button btn_login;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminLoginEmailTIET = findViewById(R.id.adminLoginEmailTIET);
        adminLoginPasswordTIET = findViewById(R.id.adminLoginPasswordTIET);

        btn_login = findViewById(R.id.btn_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Login Please Wait...");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        String email = adminLoginEmailTIET.getText().toString().trim();
        String pass = adminLoginPasswordTIET.getText().toString().trim();

        if (!validateEmail(email) | !validatePassword(pass)) {

        } else {
            pd.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this,EmailVerifyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        pd.hide();
                    } else {
                        pd.hide();
                        Toasty.error(LoginActivity.this, "Authentication Failed !!", Toasty.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    private boolean validatePassword(String pass) {
        if (pass.isEmpty()) {
            Toasty.error(LoginActivity.this, "Password Field is empty !!").show();
            adminLoginPasswordTIET.requestFocus();
            return false;
        } else if (pass.length() < 6) {
            Toasty.error(LoginActivity.this, "Password must be strong").show();
            adminLoginPasswordTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toasty.error(LoginActivity.this, "Email Field is empty !!").show();
            adminLoginEmailTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public void resetPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    public void signupAdmin(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (mUser != null) {
            Intent intent = new Intent(LoginActivity.this,EmailVerifyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}