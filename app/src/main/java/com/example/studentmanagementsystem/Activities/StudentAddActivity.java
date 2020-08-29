package com.example.studentmanagementsystem.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.studentmanagementsystem.Fragments.DatePickerFragment;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class StudentAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final Pattern PATTERN_EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    Boolean is_studentPicLoad = false;
    Uri imageUri;
    Float hResult, iResult;
    CircleImageView studentPic;
    TextView highSchoolResult, interResult;
    MaskedEditText studentAddPhoneNumberTIET;
    RadioGroup radioGroupGender;
    RadioButton radioMale, radioFemale;
    TextInputEditText studentAddNameTIET, studentAddFatherNameTIET, studentAddMotherNameTIET, studentAddRollNumberTIET, studentAddEmailTIET, studentAddAddressTIET, studentAddHighSchoolMOTIET, studentAddHighSchoolMMTIET, studentAddInterSchoolMOTIET, studentAddInterSchoolMMTIET, studentAddDOBTV;
    String sName, sFName, sMName, sGender, sDOB, sRollNo, sPhoneNo, sEmail, sAddress, sHighSchoolMM, sHighSchoolMO, sInterSchoolMM, sInterSchoolMO;

    FirebaseUser mUser;
    FirebaseFirestore firebaseFirestore;

    StorageReference storageReference;
    private StorageTask uploadTask;
    String downloadURL;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        RelativeLayout myLayout = findViewById(R.id.myLayout);
        AnimationDrawable drawable = (AnimationDrawable) myLayout.getBackground();
        drawable.setEnterFadeDuration(1000);
        drawable.setExitFadeDuration(2000);
        drawable.start();

        studentPic = findViewById(R.id.studentPicAdd);

        studentAddNameTIET = findViewById(R.id.studentAddNameTIET);
        studentAddFatherNameTIET = findViewById(R.id.studentAddFatherNameTIET);
        studentAddMotherNameTIET = findViewById(R.id.studentAddMotherNameTIET);
        studentAddRollNumberTIET = findViewById(R.id.studentAddRollNumberTIET);
        studentAddPhoneNumberTIET = findViewById(R.id.studentAddPhoneNumberTIET);
        studentAddEmailTIET = findViewById(R.id.studentAddEmailTIET);
        studentAddAddressTIET = findViewById(R.id.studentAddAddressTIET);
        studentAddHighSchoolMMTIET = findViewById(R.id.studentAddHighSchoolMMTIET);
        studentAddHighSchoolMOTIET = findViewById(R.id.studentAddHighSchoolMOTIET);
        studentAddInterSchoolMMTIET = findViewById(R.id.studentAddInterSchoolMMTIET);
        studentAddInterSchoolMOTIET = findViewById(R.id.studentAddInterSchoolMOTIET);

        highSchoolResult = findViewById(R.id.studentHighSchoolPercentageTV);
        interResult = findViewById(R.id.studentInterSchoolPercentageTV);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        studentAddDOBTV = findViewById(R.id.studentAddDOBTV);

        pd = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference("Student Images");

        studentAddPhoneNumberTIET.setLongClickable(false);
        studentAddHighSchoolMMTIET.setLongClickable(false);
        studentAddHighSchoolMOTIET.setLongClickable(false);
        studentAddInterSchoolMMTIET.setLongClickable(false);
        studentAddInterSchoolMOTIET.setLongClickable(false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void loadPic(View view) {
        is_studentPicLoad = false;
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
            studentPic.setImageURI(imageUri);
            is_studentPicLoad = true;

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toasty.error(this, "Failed", Toasty.LENGTH_SHORT).show();
            is_studentPicLoad = false;
        }
    }

    public void submitData(View view) {
        sName = studentAddNameTIET.getText().toString().trim();
        sFName = studentAddFatherNameTIET.getText().toString().trim();
        sMName = studentAddMotherNameTIET.getText().toString().trim();
        sRollNo = studentAddRollNumberTIET.getText().toString().trim();
        sPhoneNo = studentAddPhoneNumberTIET.getText().toString().trim();
        sEmail = studentAddEmailTIET.getText().toString().trim();
        sAddress = studentAddAddressTIET.getText().toString().trim();
        sHighSchoolMM = studentAddHighSchoolMMTIET.getText().toString().trim();
        sHighSchoolMO = studentAddHighSchoolMOTIET.getText().toString().trim();
        sInterSchoolMM = studentAddInterSchoolMMTIET.getText().toString().trim();
        sInterSchoolMO = studentAddInterSchoolMOTIET.getText().toString().trim();
        if (!validatePic(is_studentPicLoad) || !validateDOB() || !validateName(sName) || !validateFName(sFName) || !validateMName(sMName) || !validateRoll(sRollNo) || !validatePhone(sPhoneNo) || !validateEmail(sEmail) || !validateAddress(sAddress) || !validateHighMM(sHighSchoolMM) || !validateHighMO(sHighSchoolMO) || !validateInterMM(sInterSchoolMM) || !validateInterMO(sInterSchoolMO)) {

        } else if (!validateHighSchoolM(sHighSchoolMO, sHighSchoolMM) || !validateInterM(sInterSchoolMO, sInterSchoolMM)) {

        } else {
            int selectedId = radioGroupGender.getCheckedRadioButtonId();
            RadioButton radioSexButton = findViewById(selectedId);

            sDOB = studentAddDOBTV.getText().toString().trim();
            sGender = "" + radioSexButton.getText();
            sDOB = studentAddDOBTV.getText().toString().trim();

            //check student is already added or not
            firebaseFirestore.collection("Users").document(mUser.getUid()).collection("Students").document(sRollNo).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Toasty.warning(StudentAddActivity.this, "Data is already exist", Toasty.LENGTH_SHORT).show();
                            } else {
                                pd.setMessage("Saving Data Please Wait...");
                                pd.show();
                                final String iPercentage = String.valueOf(iResult);
                                final String hPercetage = String.valueOf(hResult);

                                //save image and get IMAGE URL

                                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                        + ".jpg");

                                uploadTask = fileReference.putFile(imageUri);
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

                                            //Save Data
                                            Students students = new Students(sRollNo, sName, sFName, sMName, sGender, sDOB, sPhoneNo, sEmail, sAddress, hPercetage, iPercentage, downloadURL);
                                            firebaseFirestore.collection("Users").document(mUser.getUid())
                                                    .collection("Students").document(sRollNo).set(students)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            pd.hide();
                                                            Toasty.success(StudentAddActivity.this, "Data is saved", Toasty.LENGTH_SHORT).show();
                                                            clearData();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.hide();
                                                    Toasty.error(StudentAddActivity.this, "" + e, Toasty.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.hide();
                                        Toasty.error(StudentAddActivity.this, "" + e, Toasty.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void clearData() {

    }


    private boolean validateDOB() {
        if (studentAddDOBTV.getText().toString().trim().equalsIgnoreCase("Select Date of birth")) {
            Toasty.warning(this, "Select Date of birth!!", Toasty.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateInterM(String sInterSchoolMO, String sInterSchoolMM) {
        Float iMO = Float.parseFloat(sInterSchoolMO);
        Float iMM = Float.parseFloat(sInterSchoolMM);
        if (iMO > iMM) {
            Toasty.error(StudentAddActivity.this, "Inter MO should be less than MM").show();
            return false;
        } else {
            iResult = (iMO * 100) / iMM;

            interResult.setText("Inter School Percentage: " + iResult + " %");
            interResult.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private boolean validateHighSchoolM(String sHighSchoolMO, String sHighSchoolMM) {
        Float hMO = Float.parseFloat(sHighSchoolMO);
        Float hMM = Float.parseFloat(sHighSchoolMM);

        if (hMO > hMM) {
            Toasty.error(StudentAddActivity.this, "High School MO should be less than MM").show();
            return false;
        } else {
            hResult = (hMO * 100) / hMM;
            highSchoolResult.setText("High School Percentage: " + hResult + " %");
            highSchoolResult.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private boolean validateInterMO(String sInterSchoolMO) {
        if (sInterSchoolMO.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter Inter school marks.").show();
            studentAddInterSchoolMMTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateInterMM(String sInterSchoolMM) {
        if (sInterSchoolMM.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter Inter school marks.").show();
            studentAddInterSchoolMMTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateHighMO(String sHighSchoolMO) {
        if (sHighSchoolMO.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter High school marks.").show();
            studentAddHighSchoolMOTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateHighMM(String sHighSchoolMM) {
        if (sHighSchoolMM.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter High school marks.").show();
            studentAddHighSchoolMMTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAddress(String sAddress) {
        if (sAddress.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter Student address.").show();
            studentAddAddressTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail(String sEmail) {
        if (sEmail.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter Student email.").show();
            studentAddEmailTIET.requestFocus();
            return false;
        } else if (!PATTERN_EMAIL.matcher(sEmail).matches()) {
            Toasty.error(StudentAddActivity.this, "Enter valid email.").show();
            studentAddEmailTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhone(String sPhoneNo) {
        if (sPhoneNo.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter phone number.").show();
            studentAddPhoneNumberTIET.requestFocus();
            return false;
        }
        if (sPhoneNo.length() <= 12) {
            Toasty.error(StudentAddActivity.this, "Enter valid phone.").show();
            studentAddPhoneNumberTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRoll(String sRollNo) {
        if (sRollNo.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter Student roll no.").show();
            studentAddRollNumberTIET.requestFocus();
            return false;
        } else if (!sRollNo.matches("(?=.*[0-9]).*") || !(sRollNo.length() == 10)) {
            Toasty.error(StudentAddActivity.this, "Enter valid roll no.").show();
            studentAddRollNumberTIET.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMName(String sMName) {
        if (sMName.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter mother name").show();
            studentAddMotherNameTIET.requestFocus();
            return false;
        } else if (sMName.matches("(?=.*[0-9]).*") || sMName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(StudentAddActivity.this, "Enter valid mother name").show();
            studentAddMotherNameTIET.requestFocus();
            return false;
        } else
            return true;
    }

    private boolean validateFName(String sFName) {
        if (sFName.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter father name").show();
            studentAddFatherNameTIET.requestFocus();
            return false;
        } else if (sFName.matches("(?=.*[0-9]).*") || sFName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(StudentAddActivity.this, "Enter valid father name!!").show();
            studentAddFatherNameTIET.requestFocus();
            return false;
        } else
            return true;
    }

    private boolean validateName(String sName) {
        if (sName.isEmpty()) {
            Toasty.error(StudentAddActivity.this, "Enter student name!!").show();
            studentAddNameTIET.requestFocus();
            return false;
        } else if (sName.matches("(?=.*[0-9]).*") || sName.matches("(?=.*[!@#$%^&*()_+-]).*")) {
            Toasty.error(StudentAddActivity.this, "Enter valid student name!!").show();
            studentAddNameTIET.requestFocus();
            return false;
        } else
            return true;

    }

    private boolean validatePic(Boolean is_studentPicLoad) {
        if (is_studentPicLoad == false) {
            Toasty.warning(this, "Please upload student photo!!", Toasty.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void DOB_Picker(View view) {
        DialogFragment dialogPicker = new DatePickerFragment();
        dialogPicker.show(getSupportFragmentManager(), "Select Date of Birth");
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        studentAddDOBTV.setText(new StringBuilder().append(dayOfMonth).append("/").append(month).append("/").append(year));

    }
}
