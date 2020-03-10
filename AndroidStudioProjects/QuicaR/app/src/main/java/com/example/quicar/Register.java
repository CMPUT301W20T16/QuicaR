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

                // databaseReference = FirebaseDatabase.getInstance().getReference();
//                Query query = databaseReference.child("User");
//                //ValueEventListener eventListener = new ValueEventListener() {
//                query.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                          for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
//
//                                String uid = dataSnapshot1.getKey();
//                                DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
//                                uidRef.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
//                                            fetchUserName = dataSnapshot2.child("userName").getValue(String.class);
//                                            if (fetchUserName != null && fetchUserName.equals(mUserName)) {
//                                                validateName = false;
//                                                userName.setError("Duplicate username");
//                                                Toast.makeText(Register.this, "Duplicate username", Toast.LENGTH_SHORT).show();
////
//                                            }
//                                        }
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                          }
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                checkUniqueUserName(new SimpleCallback() {
                    @Override
                    public void callback(boolean data) {
                        if (!data) {
                            validateName = true;
                            userName.setError("Duplicate users");
                        } else if (data) {
                            validateName = false;
                        }
                    }
                },mUserName);
//

                if (!validateEmail(mEmail) | !validatePassword(mPwd) | !validateConfirmPassword(mConfirm_pwd, mPwd) | !validateName) {
                    return;
                }

                auth.createUserWithEmailAndPassword(mEmail, mPwd).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            user.setBasic(mUserName, mEmail, mPwd);
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
        });
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    public boolean validateUserName(String username) {

        if (TextUtils.isEmpty(username)) {
            this.userName.setError("Field can't be empty");
            return false;
        }  else {
            this.userName.setError(null);
            return true;
        }
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
            return false;
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

    private void checkUniqueUserName(SimpleCallback finishedCallBack, String myUserName) {

         DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference().child("User");
//          DatabaseReference userNameRef = rootRef.child("User").child("accountInfo").child(myUserName);
//         ValueEventListener eventListener = new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                 if (dataSnapshot.exists())
//                     finishedCallBack.callback(false);
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError databaseError) {
//
//             }
//         };
//        userNameRef.addListenerForSingleValueEvent(eventListener);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    String uid = dataSnapshot1.getKey();
                    Query query = rootRef.child(uid).child("accountInfo").orderByChild("userName").equalTo(myUserName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(Register.this, "query exist", Toast.LENGTH_SHORT).show();
                                finishedCallBack.callback(false);
                            }  else {
                                Toast.makeText(Register.this, "query not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.ADD_USER_TAG)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}