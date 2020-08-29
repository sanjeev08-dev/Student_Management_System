package com.example.studentmanagementsystem.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RemoveStudentActivity extends AppCompatActivity {
    TextInputEditText removeStudentRollNoTIET, removeStudentNameTIET, removeStudentFatherNameTIET, removeStudentMotherNameTIET, removeStudentPhoneNumberTIET, removeStudentEmailTIET, removeStudentAddressTIET;
    CircleImageView removeStudentPic;
    ScrollView removeStudentScrollView;

    String rollNo;
    ProgressDialog pd;
    FirebaseUser mUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_student);

        RelativeLayout myLayout = findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        removeStudentRollNoTIET = findViewById(R.id.removeStudentRollNoTIET);
        removeStudentNameTIET = findViewById(R.id.removeStudentNameTIET);
        removeStudentFatherNameTIET = findViewById(R.id.removeStudentFatherNameTIET);
        removeStudentMotherNameTIET = findViewById(R.id.removeStudentMotherNameTIET);
        removeStudentPhoneNumberTIET = findViewById(R.id.removeStudentPhoneNumberTIET);
        removeStudentEmailTIET = findViewById(R.id.removeStudentEmailTIET);
        removeStudentAddressTIET = findViewById(R.id.removeStudentAddressTIET);
        removeStudentPic = findViewById(R.id.removeStudentPic);
        removeStudentScrollView = findViewById(R.id.removeStudentScrollView);

        pd = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void searchStudent(View view) {
        rollNo = removeStudentRollNoTIET.getText().toString().trim();

        if (rollNo.isEmpty()) {
            Toasty.error(RemoveStudentActivity.this, "Roll No. is Empty").show();
        } else {

            //load student data
            loadStudentData(rollNo);
            hideKeyboard((Button) view);
        }
    }

    private void loadStudentData(String rollNo) {
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
                        Toasty.warning(RemoveStudentActivity.this, "Roll No. is not exist").show();
                        removeStudentScrollView.setVisibility(View.INVISIBLE);
                    } else {
                        Students students = documentSnapshot.toObject(Students.class);

                        removeStudentNameTIET.setText(students.getName());
                        removeStudentFatherNameTIET.setText(students.getFathername());
                        removeStudentMotherNameTIET.setText(students.getMothername());
                        removeStudentPhoneNumberTIET.setText(students.getPhone());
                        removeStudentEmailTIET.setText(students.getEmail());
                        removeStudentAddressTIET.setText(students.getAddress());
                        Glide.with(RemoveStudentActivity.this).load(students.getImageURL()).into(removeStudentPic);


                        pd.hide();
                        removeStudentScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }


    public void deleteData(View view) {
        pd.setMessage("Deleting Details...");
        pd.show();
        //delete student
        DocumentReference reference = firebaseFirestore.collection("Users").document(mUser.getUid()).collection("Students").document(rollNo);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.hide();
                removeStudentScrollView.setVisibility(View.GONE);
                removeStudentRollNoTIET.setText("");
                Toasty.success(RemoveStudentActivity.this, "Successfully Delete", Toasty.LENGTH_SHORT, true).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.hide();
                Toasty.error(RemoveStudentActivity.this, "Failed with : " + e, Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}
