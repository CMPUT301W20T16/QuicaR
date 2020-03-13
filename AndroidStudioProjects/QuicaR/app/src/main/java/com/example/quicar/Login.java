package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.regex.Pattern;


public class Login extends AppCompatActivity implements OnGetUserDataListener {
    private TextInputLayout userID, pwd;
    private Button loginButton;
    private TextView signUpButton;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser currentUser;

    /* added by Jeremy */
    OnGetUserDataListener listener = this;


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
                    mAuth.signInWithEmailAndPassword(getEmail, mypwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        /* added by Jeremy */
                                        UserDataHelper.getInstance().getUser(myID, listener);
                                        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
                                        pgsBar.setVisibility(v.VISIBLE);
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    } else {
                                        Toast.makeText(Login.this,
                                                "Login failed" + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {

                    mAuth.signInWithEmailAndPassword(myID, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                currentUser = mAuth.getInstance().getCurrentUser();
                                retrieveUsername();
                                if (currentUser != null) {
                                    for (UserInfo profile : currentUser.getProviderData()) {
                                        String name = profile.getDisplayName();
                                        /* added by Jeremy */
                                        UserDataHelper.getInstance().getUser(name, listener);
                                        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
                                        pgsBar.setVisibility(v.VISIBLE);
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }
                                }
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

    /**
     * To validate user inputs email/username
     * @param email
     *      user inputs email/username
     * @return
     *      return if the user correctly inputs email/username
     * */
    public boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            this.userID.setError("Field can't be empty");
            return false ;
        } else {
            this.userID.setError(null);
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
            return false ;
        } else {
            this.pwd.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs email or username
     * @param id
     *      user inputs email/username
     * @return
     *      return whether user inputs email or username
     * */
    public boolean checkUserNameOrEmail(String id) {

//        if (id.contains("@")) {
//            return true;
//        }
//        return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (id == null)
            return false;
        return pat.matcher(id).matches();

    }

    public String retrieveEmail() {
        String email = mAuth.getInstance().getCurrentUser().getEmail().toString();
        return email;
    }

    /**
     * retrieve username according to
     * the current user's uid
     *
     * */
    public void retrieveUsername() {
        currentUser = mAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getInstance().getReference("User");

        myRef.child(currentUser.getUid()).
                child("accountInfo").child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username).build();
                currentUser.updateProfile(profileUpdates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.GET_USER_TAG) {
            DatabaseHelper.getInstance().setCurrentUser(user);
            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Intent homeIntent = new Intent(Login.this, RiderRequestActivity.class);
            startActivity(homeIntent);
        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
