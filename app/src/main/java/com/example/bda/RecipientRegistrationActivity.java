package com.example.bda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipientRegistrationActivity extends AppCompatActivity {
    private TextView backButton;
    private CircleImageView profile_image;

    private TextInputEditText registerFullName,registerIdNumber,registerPhoneNumber,registerEmail,registerPassword;

    private Spinner bloodGroupsSpinner;

    private Button registerButton;

    private Uri resultUri;
    // Auth is Used For Fire Bse Authentication;
    private FirebaseAuth mAuth;
    // Save Data To Fire Base;
    private DatabaseReference userDatabaseRef;

    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_registration);
        getSupportActionBar().setTitle("Blood Donation App");
        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecipientRegistrationActivity.this,LoginActivity.class));
            }
        });
        profile_image = findViewById(R.id.profile_image);
        registerFullName = findViewById(R.id.registerFullName);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerPassword = findViewById(R.id.registerPassword);
        bloodGroupsSpinner = findViewById(R.id.bloodGroupSpinner);
        registerButton = findViewById(R.id.registerButton);
        loader = new ProgressDialog(this);

        // initializing Variables here For Fire Base And Data Base;
        mAuth = FirebaseAuth.getInstance();

        // directing user to open Gallery on clicking image view;

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // picking image from Gallery with Action_Pick Method;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Storing user Data For Verification;
                final String email = registerEmail.getText().toString().trim();
                final String password = registerPassword.getText().toString().trim();
                final String fullName = registerFullName.getText().toString().trim();
                final  String idNumber = registerIdNumber.getText().toString().trim();
                final String phoneNumber = registerPhoneNumber.getText().toString().trim();
                final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();

                // checks to check these Values are Empty or Not;

                if(TextUtils.isEmpty(email)){
                    // setting up Error if  field is empty;
                    registerEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    // setting up Error if  field is empty;
                    registerPassword.setError("Password is Required");
                    return;
                }

                if(TextUtils.isEmpty(fullName)){
                    // setting up Error if  field is empty;
                    registerFullName.setError("Full Name is Required");
                    return;
                }

                if(TextUtils.isEmpty(idNumber)){
                    // setting up Error if  field is empty;
                    registerIdNumber.setError("Id Number is Required");
                    return;
                }


                if(TextUtils.isEmpty(phoneNumber)){
                    // setting up Error if  field is empty;
                    registerPhoneNumber.setError("Phone Number is Required");
                    return;
                }

                if(bloodGroup.equals("Select Your Blood Group")){
                    // setting up Error if  field is empty;
                    Toast.makeText(RecipientRegistrationActivity.this, "Select Blood Group", Toast.LENGTH_SHORT).show();
                    return;
                }

                else{
                    // progress Dialog will Show Progress Over Any Activity;
                    loader.setMessage("Registering you ...");
                    // we do not Want User To Cancel it during Registration Process;
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // this Means That User Has Not Registered Successfully
                            if(!task.isSuccessful()){
                                // Show up Toast Error Message;
                                String error = task.getException().toString();
                                Toast.makeText(RecipientRegistrationActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                // Users Is node in Fire Base That Is Created;
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                // using HasMap To insert Data in Specific Node Created;
                                // its is A data Structure That Allow Us to Add data to Specific Node in Fire Base Data Base;
                                // HaspMap Uses key And Then Values;
                                // for Every Value Inserting in Data Base Key Should Be Unique value May or May not Be same;
                                HashMap userInfo = new HashMap();
                                // put Method To insert Data
                                userInfo.put("id ", currentUserId);
                                userInfo.put("name ", fullName);
                                userInfo.put("email ", email);
                                userInfo.put("idnumber ", idNumber);
                                userInfo.put("phonenumber ", phoneNumber);
                                userInfo.put("bloodgroup ", bloodGroup);
                                userInfo.put("type ", "recipient");
                                userInfo.put("search ", "recipient"+bloodGroup);

                                // Adding All The Information Of user To Data Base
                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RecipientRegistrationActivity.this, "Data Set Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        finish();

                                        // loader.dismiss();
                                    }
                                });
                                if(resultUri != null){
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                            .child("profile Image").child(currentUserId);
                                    Bitmap bitmap = null;

                                    try{
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);

                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte [] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);
                                    // if Image upload Fail Toast Will Appear on Screen;

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RecipientRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    // Image upload Is Successful Then Store That Path And Download Image;
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilepictureurl", imageUrl);

                                                        // saving this image url to DataBase;
                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(RecipientRegistrationActivity.this, "Image Url Added To Data Base", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(RecipientRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }

                        }
                    });

                }

            }
        });
    }
    // Display Image Inside CircularImage View;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}