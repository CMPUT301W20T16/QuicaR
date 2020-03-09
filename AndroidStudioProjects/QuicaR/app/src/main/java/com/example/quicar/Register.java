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

public class Register extends AppCompatActivity implements OnGetUserDataListener {
    private TextInputLayout email, pwd, confirm_pwd;
    private Button signUpButton;
    // private String mEmail, mpwd, mConfirm_pwd;
    private TextView signInText;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.sign_email);
        pwd = findViewById(R.id.password);
        confirm_pwd = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);
        signInText = findViewById(R.id.signInButtonText);
        auth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mEmail = email.getEditText().getText().toString().trim();
                final String mPwd = pwd.getEditText().getText().toString();
                String mConfirm_pwd = confirm_pwd.getEditText().getText().toString();
                if (!validateEmail(mEmail) | !validatePassword(mPwd) | !validateConfirmPassword(mConfirm_pwd, mPwd)) {
                    return;
                }

                auth.createUserWithEmailAndPassword(mEmail, mPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "sign up successful", Toast.LENGTH_SHORT).show();
//                            AccountInfo accountInfo = new AccountInfo();
//                            accountInfo.setAccNo(mEmail);
//                            accountInfo.setPassword(mPwd);
                            User newUser = new User();
                            newUser.setBasic(mEmail, mPwd);
                            DatabaseHelper.setCurrentUserName(newUser.getName());
                            addUser(newUser);
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
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

    public boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            this.pwd.setError("Field can't be empty");
            return false ;
        } else {
            this.pwd.setError(null);
            return true;
        }
    }

    public boolean validateConfirmPassword(String confirmPassword, String signPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            this.confirm_pwd.setError("Field can't be empty");
            return false ;
        } else if (!confirmPassword.equals(signPassword)) {
            this.confirm_pwd.setError("Those passwords didn't match");
            return false;
        }else {
            this.confirm_pwd.setError(null);
            return true;
        }
    }

    private void addUser(User newUser) {
        UserDataHelper.addNewUser(newUser, this);
    }

    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.ADD_USER_TAG)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onUserExists(Boolean exists, String tag) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}