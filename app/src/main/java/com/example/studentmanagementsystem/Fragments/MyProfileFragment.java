package com.example.studentmanagementsystem.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Model.Users;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private TextInputEditText adminProfileNameTIET, adminProfileGenderTIET, adminProfileAddressTIET;
    private CircleImageView admin_picImageView;
    private Uri resultUri;
    private String name, gender, address;

    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String downloadURL;

    private boolean isPicUpload = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        RelativeLayout myLayout = view.findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        admin_picImageView = view.findViewById(R.id.admin_picImageView);

        adminProfileNameTIET = view.findViewById(R.id.adminProfileNameTIET);
        adminProfileGenderTIET = view.findViewById(R.id.adminProfileGenderTIET);
        adminProfileAddressTIET = view.findViewById(R.id.adminProfileAddressTIET);

        Button updateButton = view.findViewById(R.id.adminProfileUpdateButton);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String id = mUser.getUid();
        LoadUserData(id);

        admin_picImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = adminProfileNameTIET.getText().toString().trim();
                gender = adminProfileGenderTIET.getText().toString().trim();
                address = adminProfileAddressTIET.getText().toString().trim();
                if (!validateGender(gender) | !validateName(name) | !validateAddress(address)) {

                } else {
                    saveData();
                }
            }
        });
        return view;
    }


    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            Toasty.error(getActivity(), "Address Field is Empty").show();
            adminProfileAddressTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            Toasty.error(getActivity(), "Name Field is Empty").show();
            adminProfileNameTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateGender(String gender) {

        if (gender.isEmpty()) {
            Toasty.error(getActivity(), "Gender Field is Empty").show();
            adminProfileGenderTIET.requestFocus();
            return false;
        } else {
            if (gender.equalsIgnoreCase("Male") | gender.equalsIgnoreCase("Female")) {
                return true;
            } else {
                Toasty.error(getActivity(), "Only Male or Female is allowed").show();
                adminProfileGenderTIET.requestFocus();
                return false;

            }
        }
    }

    private void LoadUserData(String userID) {
        DocumentReference docRef = db.collection("Users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users users = documentSnapshot.toObject(Users.class);
                String loadName = users.getName();
                String loadGender = users.getGender();
                String loadAddress = users.getAddress();
                String imageURL = users.getImageURL();


                if (!loadName.equals("default")) {
                    adminProfileNameTIET.setText(loadName);
                } else {
                    adminProfileNameTIET.setText("");
                }
                if (!loadGender.equals("default")) {
                    adminProfileGenderTIET.setText(loadGender);
                } else {
                    adminProfileGenderTIET.setText("");
                }
                if (!loadAddress.equals("default")) {
                    adminProfileAddressTIET.setText(loadAddress);
                } else {
                    adminProfileAddressTIET.setText("");
                }
                if (!imageURL.equals("default")) {
                    Glide.with(getContext()).load(imageURL).into(admin_picImageView);
                } else {
                    admin_picImageView.setImageResource(R.drawable.admin_pic);
                }

            }
        });
    }


    private void cropImage() {
        isPicUpload = false;
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(false)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                uploadImage();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                isPicUpload = false;
                Toasty.error(getContext(), "Error is: " + error, Toasty.LENGTH_SHORT, true).show();
            }
        }

    }

    public void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Update Image...");
        if (resultUri != null) {
            pd.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + ".jpg");

            StorageTask uploadTask = fileReference.putFile(resultUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        downloadURL = downloadUri.toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> map = new HashMap<>();
                        map.put("imageURL", downloadURL);
                        db.collection("Users").document(mUser.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.hide();
                                LoadUserData(mUser.getUid());
                                Toasty.success(getContext(), "Image Upload Successfully...", Toasty.LENGTH_SHORT, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.hide();
                                Toasty.error(getContext(), "Failed : " + e, Toasty.LENGTH_SHORT, true).show();
                            }
                        });
                    } else {
                        pd.hide();
                        Toasty.error(getContext(), "Failed", Toasty.LENGTH_SHORT, true).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.hide();
                    Toasty.error(getContext(), "Failed : " + e, Toasty.LENGTH_SHORT, true).show();
                }
            });
        } else {
            pd.hide();
            Toasty.warning(getContext(), "No image Selected", Toasty.LENGTH_SHORT, true).show();
        }
    }

    public void saveData() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Saving Data...");
        pd.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("gender", gender);
        map.put("address", address);
        db.collection("Users").document(mUser.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.hide();
                LoadUserData(mUser.getUid());
                Toasty.success(getContext(), "Success", Toasty.LENGTH_SHORT, true).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.hide();
                Toasty.error(getContext(), "Failed : " + e, Toasty.LENGTH_SHORT, true).show();
            }
        });

    }
}
