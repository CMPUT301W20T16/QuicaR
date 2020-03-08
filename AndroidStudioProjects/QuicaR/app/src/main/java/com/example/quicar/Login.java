package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private TextInputLayout userID, pwd;
    private Button loginButton;
    private TextView signUpButton;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.userID = findViewById(R.id.sign_in_email);
        this.pwd = findViewById(R.id.sign_in_password);
        loginButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.signUpText);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myID = userID.getEditText().getText().toString().trim();
                String mypwd = pwd.getEditText().getText().toString();
                if (!validateEmail(myID) | !validatePassword(mypwd)) {
                    return;
                }
                if (!checkUserNameOrEmail(myID)){
                    String getEmail = retrieveEmail();
                    mAuth.signInWithEmailAndPassword(getEmail, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                DatabaseHelper.setCurrentUserName(myID);
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(Login.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    mAuth.signInWithEmailAndPassword(myID, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                DatabaseHelper.setCurrentUserName("new user");
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(Login.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

    }

    public boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            this.userID.setError("Field can't be empty");
            return false ;
        } else {
            this.userID.setError(null);
            return true;
        }
    }

    public boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            this.pwd.setError("Field can't be empty");
            return false ;
        } else {
            this.pwd.setError(null);
            return true;
        }
    }

    public boolean checkUserNameOrEmail(String id) {
        if (id.contains("@")) {
            return true;
        }
        return false;
    }

    public String retrieveEmail() {
        String email = mAuth.getInstance().getCurrentUser().getEmail().toString();
        return email;
    }

}