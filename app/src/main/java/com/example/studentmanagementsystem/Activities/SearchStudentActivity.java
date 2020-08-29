package com.example.studentmanagementsystem.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SearchStudentActivity extends AppCompatActivity {

    TextInputEditText searchStudentRollNoTIET, searchStudentNameTIET, searchStudentFatherNameTIET, searchStudentMotherNameTIET, searchStudentGenderTIET, searchStudentPhoneNumberTIET, searchStudentEmailTIET, searchStudentAddressTIET;
    CircleImageView searchStudentPic;
    ScrollView searchStudentScrollView;
    private static final int REQUEST_CALL = 101;
    ProgressDialog pd;
    FirebaseUser mUser;
    FirebaseFirestore firebaseFirestore;

    String rollNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_student);

        RelativeLayout myLayout = findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        searchStudentRollNoTIET = findViewById(R.id.searchStudentRollNoTIET);
        searchStudentNameTIET = findViewById(R.id.searchStudentNameTIET);
        searchStudentFatherNameTIET = findViewById(R.id.searchStudentFatherNameTIET);
        searchStudentMotherNameTIET = findViewById(R.id.searchStudentMotherNameTIET);
        searchStudentGenderTIET = findViewById(R.id.searchStudentGenderTIET);
        searchStudentPhoneNumberTIET = findViewById(R.id.searchStudentPhoneNumberTIET);
        searchStudentEmailTIET = findViewById(R.id.searchStudentEmailTIET);
        searchStudentAddressTIET = findViewById(R.id.searchStudentAddressTIET);

        searchStudentPic = findViewById(R.id.searchStudentPic);

        searchStudentScrollView = findViewById(R.id.searchStudentScrollView);
        pd = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    public void searchStudentData(View view) {
        rollNo = searchStudentRollNoTIET.getText().toString().trim();

        if (rollNo.isEmpty()) {
            Toasty.error(SearchStudentActivity.this, "Enter roll no.").show();

        } else {

            //search student data
            loadData(rollNo);
            hideKeyboard((Button) view);
        }
    }

    private void loadData(final String rollNo) {
        pd.setMessage("Finding Data...");
        pd.show();

        DocumentReference reference = firebaseFirestore.collection("Users").document(mUser.getUid()).collection("Students").document(rollNo);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (!documentSnapshot.exists()) {
                        pd.hide();
                        Toasty.error(SearchStudentActivity.this, "Roll no. not found").show();
                        searchStudentScrollView.setVisibility(View.INVISIBLE);
                    } else {
                        Students students = documentSnapshot.toObject(Students.class);

                        searchStudentNameTIET.setText(students.getName());
                        searchStudentFatherNameTIET.setText(students.getFathername());
                        searchStudentMotherNameTIET.setText(students.getMothername());
                        searchStudentGenderTIET.setText(students.getGender());
                        searchStudentPhoneNumberTIET.setText(students.getPhone());
                        searchStudentEmailTIET.setText(students.getEmail());
                        searchStudentAddressTIET.setText(students.getAddress());
                        Glide.with(SearchStudentActivity.this).load(students.getImageURL()).into(searchStudentPic);


                        pd.hide();
                        searchStudentScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void callStudent(View view) {
        callStudentNumber();
    }

    private void callStudentNumber() {
        String number = searchStudentPhoneNumberTIET.getText().toString().trim();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callStudentNumber();
            } else {
                Toasty.warning(this, "Permission Denied", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    public void SMSStudent(View view) {
        String smsNumber = String.format("smsto: %s", searchStudentPhoneNumberTIET.getText().toString().trim());
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse(smsNumber));
        startActivity(smsIntent);
    }

    public void MailStudent(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", searchStudentEmailTIET.getText().toString().trim(), null));
        startActivity(emailIntent);
    }

    public void WhatsAppStudent(View view) {
        String phoneNumber = searchStudentPhoneNumberTIET.getText().toString().trim();
        String msg = "Hello!! " + searchStudentNameTIET.getText().toString().trim();

        String toNumber = phoneNumber;
        toNumber = toNumber.replace("+", "").replace(" ", "").replace("-", "");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        String url = "https://api.whatsapp.com/send?phone=" + toNumber + "&text=" + msg;
        sendIntent.setData(Uri.parse(url));
        startActivity(sendIntent);
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

}
