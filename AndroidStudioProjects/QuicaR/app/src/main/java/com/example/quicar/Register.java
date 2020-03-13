package com.example.quicar;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import com.google.protobuf.DescriptorProtos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Register extends AppCompatActivity implements OnGetUserDataListener {
    private TextInputLayout userName, email, pwd, confirm_pwd;
    private Button signUpButton;
    // private String mEmail, mpwd, mConfirm_pwd;
    private TextView signInText;
    FirebaseAuth auth;
    //DatabaseReference databaseReference;
    FirebaseDatabase database;
    private String fetchUserName;
    boolean validateName = true;
    OnGetUserDataListener listener = this;

    public interface SimpleCallback {
        void callback(boolean data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = findViewById(R.id.username);
        email = findViewById(R.id.sign_email);
        pwd = findViewById(R.id.password);
        confirm_pwd = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);
        signInText = findViewById(R.id.signInButtonText);
        auth = FirebaseAuth.getInstance();

        // databaseReference = FirebaseDatabase.getInstance().getReference("User");


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mUserName = userName.getEditText().getText().toString().trim();
                final String mEmail = email.getEditText().getText().toString().trim();
                final String mPwd = pwd.getEditText().getText().toString();
                String mConfirm_pwd = confirm_pwd.getEditText().getText().toString();


                if (!validateEmail(mEmail) | !validatePassword(mPwd) | !validateConfirmPassword(mConfirm_pwd, mPwd) | !validateName |!validateUserName(mUserName)) {
                    return;
                }

                DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference().child("User");
                Query query = rootRef.orderByChild("accountInfo/userName").equalTo(mUserName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(Register.this, "Username exist", Toast.LENGTH_SHORT).show();
                            userName.setError("Duplicate username");
                            // finishedCallBack.callback(false);
                        }  else {
                            // Toast.makeText(Register.this, "User not found", Toast.LENGTH_SHORT).show();
                            // finishedCallBack.callback(true);
                            auth.createUserWithEmailAndPassword(mEmail, mPwd).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User user = new User();
                                        user.setBasic(mUserName, mEmail, mPwd);
                                        UserDataHelper.getInstance().addNewUser(user, listener);
                                        DatabaseHelper.getInstance().setCurrentUser(user);
                                        database.getInstance().getReference("User").child(auth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Register.this, "sign up success", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(Register.this, "sign up failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    /**
     * To validate user inputs username
     * @param username
     *      user inputs username
     * @return
     *      return if the user correctly inputs username
     * */
    public boolean validateUserName(String username) {

        if (TextUtils.isEmpty(username)) {
            this.userName.setError("Field can't be empty");
            return false;
        }  else {
            this.userName.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs email
     * @param email
     *      user inputs email
     * @return
     *      return if the user correctly inputs email
     * */
    public boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Field can't be empty");
            return false ;
        } else {
            this.email.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs password
     * @param password
     *      user inputs password
     * @return
     *      return if the user correctly inputs password
     * */
    public boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            this.pwd.setError("Field can't be empty");
            return false;
        } else {
            this.pwd.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs password and re-entered password matches
     * @param confirmPassword
     *      user inputs password
     * @param signPassword
     *      user re-enters password
     * @return
     *      return if the user correctly inputs confirmed password
     * */
    public boolean validateConfirmPassword(String confirmPassword, String signPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            this.confirm_pwd.setError("Field can't be empty");
            return false ;
        } else if (!confirmPassword.equals(signPassword)) {
            this.pwd.setError("Those passwords didn't match");
            this.confirm_pwd.setError("Those passwords didn't match");
            return false;
        }else {
            this.confirm_pwd.setError(null);
            return true;
        }
    }

    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.ADD_USER_TAG) {
            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Intent homeIntent = new Intent(Register.this, RiderRequestActivity.class);
            startActivity(homeIntent);
        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
