package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {

    private TextInputLayout email, phone, username, firstname, lastname, birthdate, gender;
    private Button savebutton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_profile);
        this.email = findViewById(R.id.profile_email);
        this.phone = findViewById(R.id.profile_phone);
        this.username = findViewById(R.id.profile_username);
        this.firstname = findViewById(R.id.profile_lastname);
        this.lastname = findViewById(R.id.profile_birthdate);
        this.gender = findViewById(R.id.profile_gender);


        savebutton = findViewById(R.id.save_button);
        mAuth = FirebaseAuth.getInstance();

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myEmail = email.getEditText().getText().toString().trim();
                final String myPhone = phone.getEditText().getText().toString();
                final String myUsername = username.getEditText().getText().toString();
                final String myFirstname = firstname.getEditText().getText().toString();
                final String myLastname = lastname.getEditText().getText().toString();
                final String myGender = gender.getEditText().getText().toString();



                if (!validateEmail(myEmail) | !validatePhone(myPhone) | !validatePhone( myPhone ) | !validateUsername( myUsername )) {
                    return;
                }

                // need to be change here

//                mAuth.signInWithEmailAndPassword(myEmail, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(UserProfile.this, "Login successful", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        } else {
//                            Toast.makeText(UserProfile.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        });


    }

    public boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Field can't be empty");
            return false ;
        } else {
            this.email.setError(null);
            return true;
        }
    }

    public boolean validatePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            this.phone.setError("Field can't be empty");
            return false ;
        } else {
            this.phone.setError(null);
            return true;
        }
    }

    public boolean validateUsername(String password) {
        if (TextUtils.isEmpty(password)) {
            this.username.setError("Field can't be empty");
            return false ;
        } else {
            this.username.setError(null);
            return true;
        }

    }
}