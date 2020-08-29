package com.example.studentmanagementsystem.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.Adopter.StudentsAdaptor;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AllStudentsActivity extends AppCompatActivity {

    private StudentsAdaptor adaptor;
    private static final int REQUEST_CALL = 102;
    RecyclerView.ViewHolder viewHolderPub;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

        RelativeLayout myLayout = findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("isFirst", true);

        if (isFirst) {
            showFirstDialog();
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentsRef = db.collection("Users").document(mUser.getUid()).collection("Students");
        Query query = studentsRef.orderBy("rollno", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Students> options = new FirestoreRecyclerOptions.Builder<Students>()
                .setQuery(query, Students.class)
                .build();

        adaptor = new StudentsAdaptor(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerviewStudents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewHolderPub = viewHolder;
                if (direction == ItemTouchHelper.RIGHT) {
                    number = (String) viewHolder.itemView.getTag(R.string.call);
                    call(number);

                } else {
                    email((String) viewHolder.itemView.getTag(R.string.email));
                }
                adaptor.notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(AllStudentsActivity.this,R.color.yellow))
                        .addSwipeLeftActionIcon(R.drawable.ic_email)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(AllStudentsActivity.this,R.color.green))
                        .addSwipeRightActionIcon(R.drawable.icon_phone)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adaptor.notifyDataSetChanged();
    }

    private void showFirstDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Guide")
                .setMessage("* Swipe Left to Right (-->) for Calling\n" +
                        "* Swipe Right to Left (<--) for Email")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirst", false);
        editor.apply();
    }

    private void email(String tag) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", tag, null));
        startActivity(emailIntent);
    }

    private void call(String tag) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + tag;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                call(number);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptor.startListening();
    }

}
