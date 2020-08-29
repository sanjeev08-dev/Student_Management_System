package com.example.studentmanagementsystem.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.Model.Users;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity implements TextWatcher {

    TextInputEditText adminSignupEmailTIET, adminSignupPasswordTIET, adminSignupCPasswordTIET;
    Button btn_email_signin;
    SignInButton signInButton;
    String email, pass;

    ProgressDialog pd;
    FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        adminSignupEmailTIET = findViewById(R.id.adminSignupEmailTIET);
        adminSignupPasswordTIET = findViewById(R.id.adminSignupPasswordTIET);
        adminSignupCPasswordTIET = findViewById(R.id.adminSignupCPasswordTIET);
        btn_email_signin = findViewById(R.id.btn_email_signup);
        signInButton = findViewById(R.id.sign_in_button);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");

        mAuth = FirebaseAuth.getInstance();

        adminSignupPasswordTIET.addTextChangedListener(this);
        adminSignupCPasswordTIET.addTextChangedListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btn_email_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailRegistration();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleRegistration();
            }
        });

    }


    private void emailRegistration() {
        email = adminSignupEmailTIET.getText().toString().trim();
        pass = adminSignupPasswordTIET.getText().toString().trim();
        String cpass = adminSignupCPasswordTIET.getText().toString().trim();

        if (!validateEmail(email) | !validatePassword(pass) | !validateCPassword(cpass)) {

        } else {
            pd.show();
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                String id = user.getUid();
                                String name = "default";
                                String imageURL = "default";
                                String email = user.getEmail();
                                String gender = "default";
                                String address = "default";
                                dataSaving(id, name, imageURL, email, gender, address);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toasty.error(SignUpActivity.this, "Authentication failed.",
                                        Toasty.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    private void googleRegistration() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toasty.error(this, "Canceled", Toasty.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        pd.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String id = firebaseUser.getUid();
                            String name = firebaseUser.getDisplayName();
                            String imageURL = String.valueOf(firebaseUser.getPhotoUrl());
                            String email = firebaseUser.getEmail();
                            String gender = "default";
                            String address = "default";
                            dataSaving(id, name, imageURL, email, gender, address);
                        } else {
                            Toasty.error(SignUpActivity.this, "You can't register with email id or password", Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dataSaving(String id, String name, String imageURL, String email, String gender, String address) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Users users = new Users(id, name, imageURL, email, gender, address);
        db.collection("Users").document(id).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                pd.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(SignUpActivity.this, e.getMessage(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toasty.error(SignUpActivity.this, "Email Field is empty !!").show();
            adminSignupEmailTIET.requestFocus();
            return false;
        } else {
            return true;
        }

    }


    private boolean validatePassword(String pass) {
        if (pass.isEmpty()) {
            Toasty.error(SignUpActivity.this, "Password Field is empty !!").show();
            return false;
        } else if (pass.length() < 6) {
            Toasty.error(SignUpActivity.this, "password must be strong.").show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCPassword(String cpass) {
        if (cpass.isEmpty()) {
            Toasty.error(SignUpActivity.this, "Confirm Password Field is empty !!").show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String pass = adminSignupPasswordTIET.getText().toString().trim();
        int passwordScore = 0;

        if (pass.length() >= 10)
            passwordScore += 2;
        else
            passwordScore += 1;

        if (pass.matches("(?=.*[0-9]).*"))
            passwordScore += 2;

        if (pass.matches("(?=.*[a-z]).*"))
            passwordScore += 2;

        if (pass.matches("(?=.*[A-Z]).*"))
            passwordScore += 2;

        if (pass.matches("(?=.*[~!@#$%^&*()_-]).*"))
            passwordScore += 2;

        if (passwordScore <= 2) {
            Toasty.error(SignUpActivity.this, "Password is too weak !!").show();
        } else if (passwordScore <= 4) {
            Toasty.error(SignUpActivity.this, "Password is weak !!").show();
        } else if (passwordScore <= 6) {
            Toasty.warning(SignUpActivity.this, "Password is medium !!").show();
        } else if (passwordScore <= 8) {
            Toasty.success(SignUpActivity.this, "Strong Password !!").show();
        } else {
            Toasty.success(SignUpActivity.this, "Very Strong Password !!").show();
        }
        String cPass = adminSignupCPasswordTIET.getText().toString().trim();
        if (pass.equals(cPass)) {
            Toasty.success(SignUpActivity.this, "Password is same !!").show();
        } else {
            Toasty.error(SignUpActivity.this, "Password is not same !!").show();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

