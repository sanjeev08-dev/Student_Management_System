package com.example.studentmanagementsystem.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Fragments.DashboardFragment;
import com.example.studentmanagementsystem.Fragments.MyProfileFragment;
import com.example.studentmanagementsystem.Model.Users;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    FirebaseUser mUser;
    FirebaseFirestore db;
    TextView nameTV, emailTV;
    StorageReference storageReference;
    CircleImageView adminpic;
    private GoogleSignInClient mGoogleSignInClient;
    boolean isAtteched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String userID = mUser.getUid();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        db = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        searchAddedAdmin(userID);

        View headerView = navigationView.getHeaderView(0);
        nameTV = (TextView) headerView.findViewById(R.id.AdminNameTV);
        emailTV = (TextView) headerView.findViewById(R.id.AdminEmailIdTV);

        adminpic = (CircleImageView) headerView.findViewById(R.id.admin_picIV);


        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_student_dashboard);
        }
    }

    public void searchAddedAdmin(String userID) {
        DocumentReference docRef = db.collection("Users").document(userID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Users users = documentSnapshot.toObject(Users.class);
                String name = users.getName();
                if (name.equals("default")) {
                    nameTV.setText("");
                } else {
                    nameTV.setText(users.getName());
                }
                emailTV.setText(users.getEmail());
                String imageURL = users.getImageURL();
                if (!imageURL.equals("default")) {
                    if (isAtteched) {
                        Glide.with(DashboardActivity.this).load(users.getImageURL()).into(adminpic);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_student_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_my_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAtteched = true;

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAtteched = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchAddedAdmin(mUser.getUid());

    }

    @Override
    protected void onResume() {
        super.onResume();
        searchAddedAdmin(mUser.getUid());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                break;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Your app link will be added here");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Student Management System");
                startActivity(Intent.createChooser(shareIntent, "Share"));
                break;
        }
        return false;
    }
}
