package com.example.bda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SelectRegistrationActivity extends AppCompatActivity {
    private Button donorButton, recipientButton;
    private TextView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);
        getSupportActionBar().setTitle("Blood Donation App");
        donorButton=findViewById(R.id.donorButton);
        recipientButton=findViewById(R.id.recipientButton);
        donorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectRegistrationActivity.this,DonorRegistrationActivity.class));
            }
        });
        recipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectRegistrationActivity.this,RecipientRegistrationActivity.class));
            }
        });
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectRegistrationActivity.this,LoginActivity.class));
            }
        });
    }
}