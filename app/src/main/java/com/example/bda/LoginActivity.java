package com.example.bda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView backButton;
    private TextInputEditText loginEmail,loginPassword;
    private TextView forgotPassword;
    private Button  loginButton;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    // using Firebase listener to check Whether user is Alreday Login Or Not;
    // State Of User In Application;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Blood Donation App");
        backButton = findViewById(R.id.backButton);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Checking State OF User In Application If user Is Already LogIn Sending User To New Activity;
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        // Else User Has To Log In For Performing Any Action;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SelectRegistrationActivity.class));
            }
        });
        // Checks To check When user Clicks on LogIn Button That Fields Are filled Or Empty;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = loginEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Email is Required");
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is Required");
                }
                else{
                    loader.setMessage("Login in Process");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    // Signing In user Using Fire Base Auth with email And Password.
                    // If Task Is Completed using Add on Complete Listener Sending User To Main Activity;
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "LogIn Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}