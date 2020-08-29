package com.example.studentmanagementsystem.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.studentmanagementsystem.Activities.AllStudentsActivity;
import com.example.studentmanagementsystem.Activities.EditStudentActivity;
import com.example.studentmanagementsystem.Activities.LoginActivity;
import com.example.studentmanagementsystem.Activities.RemoveStudentActivity;
import com.example.studentmanagementsystem.Activities.SearchStudentActivity;
import com.example.studentmanagementsystem.Activities.StudentAddActivity;
import com.example.studentmanagementsystem.Model.Users;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class DashboardFragment extends Fragment implements View.OnClickListener {

    private TextView nameTV, greetTV;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);

        LinearLayout myLayout = view.findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float height = displayMetrics.heightPixels;

        ImageView imageViewAnim = view.findViewById(R.id.imageViewAnim);
        imageViewAnim.animate().translationY((height / 3) - height).setDuration(3000);

        nameTV = view.findViewById(R.id.adminDashboardNameTV);
        greetTV = view.findViewById(R.id.greetingMessage);
        LinearLayout greetingLayout = view.findViewById(R.id.greetingLayout);
        greetingLayout.animate().translationY((height - imageViewAnim.getY()) / 20).alpha(100.0f).setDuration(3000).setStartDelay(500);

        CardView addStudentCardView = view.findViewById(R.id.DashboardAddStudentCardView);
        CardView editStudentCardView = view.findViewById(R.id.DashboardEditStudentCardView);
        CardView removeStudentCardView = view.findViewById(R.id.DashboardRemoveStudentCardView);
        CardView searchStudentCardView = view.findViewById(R.id.DashboardSearchStudentCardView);
        CardView allStudentCardView = view.findViewById(R.id.DashboardAllStudentCardView);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        String id = mUser.getUid();
        db = FirebaseFirestore.getInstance();
        searchAddedAdmin(id);

        addStudentCardView.setOnClickListener(this);
        removeStudentCardView.setOnClickListener(this);
        editStudentCardView.setOnClickListener(this);
        searchStudentCardView.setOnClickListener(this);
        allStudentCardView.setOnClickListener(this);

        return view;
    }

    private void searchAddedAdmin(String userID) {
        DocumentReference docRef = db.collection("Users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users users = documentSnapshot.toObject(Users.class);
                String name = users.getName();
                if (name.equals("default")) {
                    nameTV.setText("");
                } else {
                    DateFormat df = new SimpleDateFormat("HH");
                    int time = Integer.parseInt(df.format(Calendar.getInstance().getTime()));
                    String welcomeNote = "";//\U+1F31E
                    if (time > 3 && time < 12) {
                        welcomeNote = "Good Morning ";
                    } else if (time == 12) {
                        welcomeNote = "Good Noon ";
                    } else if (time >= 13 && time < 17) {
                        welcomeNote = "Good Afternoon ";
                    } else if (time >= 17 && time < 22) {
                        welcomeNote = "Good Evening ";
                    } else if (time >= 22 && time <= 24) {
                        welcomeNote = "Good Night ";
                    } else if (time >= 0 && time <= 3) {
                        welcomeNote = "Good Night ";
                    }
                    greetTV.setText(welcomeNote + "!!");
                    nameTV.setText(users.getName());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.DashboardAddStudentCardView:
                startActivity(new Intent(getContext(), StudentAddActivity.class));
                break;
            case R.id.DashboardRemoveStudentCardView:
                startActivity(new Intent(getContext(), RemoveStudentActivity.class));
                break;
            case R.id.DashboardEditStudentCardView:
                startActivity(new Intent(getContext(), EditStudentActivity.class));
                break;
            case R.id.DashboardSearchStudentCardView:
                startActivity(new Intent(getContext(), SearchStudentActivity.class));
                break;
            case R.id.DashboardAllStudentCardView:
                startActivity(new Intent(getContext(), AllStudentsActivity.class));
                break;
        }

    }
}
