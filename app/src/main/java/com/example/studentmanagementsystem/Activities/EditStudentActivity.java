package com.example.studentmanagementsystem.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class EditStudentActivity extends AppCompatActivity {


    TextInputEditText editStudentRollNoTIET, editStudentNameTIET, editStudentFatherNameTIET, editStudentMotherNameTIET, editStudentEmailTIET, editStudentAddressTIET;
    CircleImageView editStudentPic;
    ScrollView editStudentScrollView;
    boolean isNewPicLoad = false;
    String rollNo;
    Uri imageUri;
    String studentName, studentFatherName, studentMotherName, studentPhoneNo, studentEmail, studentAddress;
    private static final Pattern PATTERN_EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    FirebaseUser mUser;
    ProgressDialog pd;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    String downloadURL;
    TextInputEditText editStudentPhoneNumberTIET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        RelativeLayout myLayout = findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        editStudentRollNoTIET = findViewById(R.id.editStudentRollNoTIET);

        editStudentNameTIET = findViewById(R.id.editStudentNameTIET);
        editStudentFatherNameTIET = findViewById(R.id.editStudentFatherNameTIET);
        editStudentMotherNameTIET = findViewById(R.id.editStudentMotherNameTIET);
        editStudentPhoneNumberTIET = findViewById(R.id.editStudentPhoneNumberTIET);
        editStudentEmailTIET = findViewById(R.id.editStudentEmailTIET);
        editStudentAddressTIET = findViewById(R.id.editStudentAddressTIET);

        editStudentPic = findViewById(R.id.editStudentPic);

        editStudentScrollView = findViewById(R.id.editStudentScrollView);

        storageReference = FirebaseStorage.getInstance().getReference("Student Images");
        firebaseFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(this);
    }

    public void searchStudentDetails(View view) {
        rollNo = editStudentRollNoTIET.getText().toString().trim();

        if (rollNo.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Please Enter roll number first").show();
        } else {

            //Load Student Data
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
                        Toasty.warning(EditStudentActivity.this, "Roll No. not found").show();
                        editStudentScrollView.setVisibility(View.INVISIBLE);
                    } else {
                        Students students = documentSnapshot.toObject(Students.class);

                        editStudentNameTIET.setText(students.getName());
                        editStudentFatherNameTIET.setText(students.getFathername());
                        editStudentMotherNameTIET.setText(students.getMothername());
                        editStudentPhoneNumberTIET.setText(students.getPhone());
                        editStudentEmailTIET.setText(students.getEmail());
                        editStudentAddressTIET.setText(students.getAddress());
                        Glide.with(EditStudentActivity.this).load(students.getImageURL()).into(editStudentPic);


                        pd.hide();
                        editStudentScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void ModifyData(View view) {
        studentName = editStudentNameTIET.getText().toString().trim();
        studentFatherName = editStudentFatherNameTIET.getText().toString().trim();
        studentMotherName = editStudentMotherNameTIET.getText().toString().trim();
        studentPhoneNo = editStudentPhoneNumberTIET.getText().toString().trim();
        studentEmail = editStudentEmailTIET.getText().toString().trim();
        studentAddress = editStudentAddressTIET.getText().toString().trim();

        if (!validateName(studentName) || !validateFatherName(studentFatherName) || !validateMotherName(studentMotherName) || !validatePhoneNo(studentPhoneNo) || !validateEmail(studentEmail) || !validateAddress(studentAddress)) {
        } else {
            updateStudentDetails(rollNo);
        }
    }

    private void updateStudentDetails(final String rollNo) {
        //update Student Details
        pd.setMessage("Updating.....");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(rollNo
                    + ".jpg");

            StorageTask<UploadTask.TaskSnapshot> uploadTask = fileReference.putFile(imageUri);
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
                        downloadURL = downloadUri.toString(); //IMAGE URL

                        //Update Data
                        updateData();

                    } else {
                    }
                }
            });

        } else {
            updateData();
        }
    }

    private void updateData() {
        String name = editStudentNameTIET.getText().toString().trim();
        String father_name = editStudentFatherNameTIET.getText().toString().trim();
        String mother_name = editStudentMotherNameTIET.getText().toString().trim();
        String phone = editStudentPhoneNumberTIET.getText().toString().trim();
        String email = editStudentEmailTIET.getText().toString().trim();
        String address = editStudentAddressTIET.getText().toString().trim();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("fathername", father_name);
        map.put("mothername", mother_name);
        map.put("phone", phone);
        map.put("email", email);
        map.put("address", address);
        if (isNewPicLoad) {
            map.put("imageURL", downloadURL);
        }
        firebaseFirestore.collection("Users").document(mUser.getUid())
                .collection("Students").document(rollNo).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.hide();
                            editStudentRollNoTIET.setText("");
                            editStudentScrollView.setVisibility(View.GONE);
                            Toasty.success(EditStudentActivity.this, "Details update success", Toasty.LENGTH_SHORT, true).show();
                        } else {
                            pd.hide();
                            Toasty.error(EditStudentActivity.this, "Failed : " + task.getException(), Toasty.LENGTH_SHORT, true).show();
                        }
                    }
                });

    }

    private boolean validateAddress(String studentAddress) {
        if (studentAddress.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Enter Address").show();
            editStudentAddressTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail(String studentEmail) {
        if (studentEmail.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Email can not be empty").show();
            editStudentEmailTIET.requestFocus();
            return false;
        } else if (!PATTERN_EMAIL.matcher(studentEmail).matches()) {
            Toasty.error(EditStudentActivity.this, "Email Valid Email").show();
            editStudentEmailTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhoneNo(String studentPhoneNo) {
        if (studentPhoneNo.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Enter Phone Number").show();
            editStudentPhoneNumberTIET.requestFocus();
            return false;
        }
        if (studentPhoneNo.length() <= 12) {
            Toasty.error(EditStudentActivity.this, "Enter Valid Phone Number").show();
            editStudentPhoneNumberTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMotherName(String studentMotherName) {
        if (studentMotherName.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Enter Mother Name").show();
            editStudentMotherNameTIET.requestFocus();
            return false;
        } else if (studentMotherName.matches("(?=.*[0-9]).*") || studentMotherName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(EditStudentActivity.this, "Enter Valid Mother Name").show();
            editStudentMotherNameTIET.requestFocus();
            return false;
        } else
            return true;
    }

    private boolean validateFatherName(String studentFatherName) {
        if (studentFatherName.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Enter Father Name").show();
            editStudentFatherNameTIET.requestFocus();
            return false;
        } else if (studentFatherName.matches("(?=.*[0-9]).*") || studentFatherName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(EditStudentActivity.this, "Enter Valid Father Name").show();
            editStudentFatherNameTIET.requestFocus();
            return false;
        } else
            return true;
    }

    private boolean validateName(String studentName) {
        if (studentName.isEmpty()) {
            Toasty.error(EditStudentActivity.this, "Enter Student Name").show();
            editStudentNameTIET.requestFocus();
            return false;
        } else if (studentName.matches("(?=.*[0-9]).*") || studentName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(EditStudentActivity.this, "Enter Valid Student Name").show();
            editStudentNameTIET.requestFocus();
            return false;
        } else
            return true;
    }

    public void loadNewPic(View view) {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(false)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imageUri = result.getUri();
            editStudentPic.setImageURI(imageUri);
            isNewPicLoad = true;

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toasty.error(EditStudentActivity.this, "Failed", Toasty.LENGTH_SHORT, true).show();
            isNewPicLoad = false;
        }
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }


}
