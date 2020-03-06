package com.example.quicar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;

public class UserProfileActivity extends AppCompatActivity implements OnGetUserDataListener {

//???
// .............................................................???
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_profile);
//        setTitle("User Profile");
//
//        // add back button on action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//    }


    private TextInputLayout email, phone, username, firstname, lastname, birthdate, gender,password;
    private Button savebutton;
    private int getUser = 0;
    FirebaseAuth mAuth;


    private  User user ;
    private OnGetUserDataListener listener;


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
        this.birthdate=findViewById(R.id.profile_birthdate);
        this.password = findViewById(R.id.profile_password);
        savebutton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();


        String userName = DatabaseHelper.getCurrentUserName();
        // get current user
        UserDataHelper.getUser(userName,this);

        this.setDefault();

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myEmail = email.getEditText().getText().toString().trim();
                final String myPhone = phone.getEditText().getText().toString();
                final String myUsername = username.getEditText().getText().toString();
                final String myFirstname = firstname.getEditText().getText().toString();
                final String myLastname = lastname.getEditText().getText().toString();
                final String myGender = gender.getEditText().getText().toString();
                final String myPassword = password.getEditText().getText().toString();



                if (!validateEmail(myEmail) | !validatePhone(myPhone) | !validatePhone( myPhone ) | !validateUsername( myUsername )|!validatePassWord(myPassword)) {
                    return;
                }

                //!!!!!@ update correct user here



//
//                UserDataHelper.updateUserProfile(user,listener);

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

    /**
     * defined for set default information to help user to update info
     */
    private void setDefault() {
        if(this.getUser == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //set default user info
            this.email.getEditText().setText(user.getAccountInfo().getEmail());
            this.username.getEditText().setText(user.getAccountInfo().getUserName());
            this.phone.getEditText().setText(user.getAccountInfo().getPhone());
            this.firstname.getEditText().setText(user.getAccountInfo().getFirstName());
            this.lastname.getEditText().setText(user.getAccountInfo().getLastName());
            this.birthdate.getEditText().setText(sdf.format(user.getAccountInfo().getBirthDate()));
            this.gender.getEditText().setText(user.getAccountInfo().getGender());

            System.out.println("setting default");
        }


    }

    /**
     * for check valid user input email
     */
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
     * for check valid user input password
     */
    public boolean validatePassWord(String confirmPassword) {

        if (TextUtils.isEmpty(confirmPassword)) {
            this.password.setError("Field can't be empty");
            return false ;

        }else {
            this.password.setError(null);
            return true;
        }



    }

    /**
     * for check valid user input phoneNumber
     */
    private boolean validatePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            this.phone.setError("Field can't be empty");
            return false ;
        } else {
            this.phone.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input userName
     */
    private boolean validateUsername(String password) {
        if (TextUtils.isEmpty(password)) {
            this.username.setError("Field can't be empty");
            return false ;
        } else {
            this.username.setError(null);
            return true;
        }
    }

    /**
     *
     * for implement to get databasework
     */
    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.GET_USER_TAG)
            this.user = user;
            this.getUser = 1;
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("your implementation worong");

    }
}
