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


    private TextInputLayout emailLayout, phoneLayout, usernameLayout, firstnameLayout, lastnameLayout, birthdateLayout, genderLayout,passwordLayout;
    private Button savebutton;
    private int getUser = 0;
    FirebaseAuth mAuth;


    private  User user ;
    private OnGetUserDataListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_profile);
        this.emailLayout = findViewById(R.id.profile_email);
        this.phoneLayout = findViewById(R.id.profile_phone);
        this.usernameLayout = findViewById(R.id.profile_username);
        this.firstnameLayout = findViewById(R.id.profile_firstname);
        this.lastnameLayout = findViewById(R.id.profile_lastname);
        this.genderLayout = findViewById(R.id.profile_gender);
        this.birthdateLayout = findViewById(R.id.profile_birthdate);
        this.passwordLayout = findViewById(R.id.profile_password);
        savebutton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();


        String userName = DatabaseHelper.getCurrentUserName();
        // get current user
        UserDataHelper.getUser(userName,this);

        this.setDefault();

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validateFlag = checkValidate();
//                boolean validateFlag = new UserProfileActivity().checkValidate();

//                if(validateFlag == false) {
//
//
//                }

//                //!!!!!@ update correct user here
//
//
//
////
////                UserDataHelper.updateUserProfile(user,listener);
//
//                // need to be change here
//
////                mAuth.signInWithEmailAndPassword(myEmail, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
////                    @Override
////                    public void onComplete(@NonNull Task<AuthResult> task) {
////                        if (task.isSuccessful()) {
////                            Toast.makeText(UserProfile.this, "Login successful", Toast.LENGTH_SHORT).show();
////                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
////                        } else {
////                            Toast.makeText(UserProfile.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });


            }
        });


    }


    private boolean checkValidate() {
        boolean flag = true;
        if (!validateEmail()) {
            flag = false;
        }
        if(!validatePassWord()) {
            flag = false;
        }
        if(!validateFirstName()){
            System.out.println(flag);
            flag = false;
        }
        if (!validateLastName()) {
            flag = false;
        }
        if(!validateGender()) {
            flag = false;
        }
        if(!validateBirthDate()) {
            flag = false;
        }
        if(!validatePhone()) {
            flag = false;
        }
        if(!validateUsername()){
            flag = false;
        }
        return  flag;
    }



    /**
     * defined for set default information to help user to update info
     */
    private void setDefault() {
        if(this.getUser == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //set default user info
            this.emailLayout.getEditText().setText(user.getAccountInfo().getEmail());
            this.usernameLayout.getEditText().setText(user.getAccountInfo().getUserName());
            this.phoneLayout.getEditText().setText(user.getAccountInfo().getPhone());
            this.firstnameLayout.getEditText().setText(user.getAccountInfo().getFirstName());
            this.lastnameLayout.getEditText().setText(user.getAccountInfo().getLastName());
            this.birthdateLayout.getEditText().setText(sdf.format(user.getAccountInfo().getBirthDate()));
            this.genderLayout.getEditText().setText(user.getAccountInfo().getGender());

            System.out.println("setting default");
        }


    }

    /**
     * for check valid user input email
     */
    public boolean validateEmail() {
        String email = emailLayout.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            this.emailLayout.setError("Field can't be empty");
            return false ;
        } else {
            this.emailLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input password
     */
    public boolean validatePassWord() {
        String confirmPassword = passwordLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            this.passwordLayout.setError("Field can't be empty");
            return false;

        }else {
            this.passwordLayout.setError(null);
            return true;
        }



    }

    /**
     * for check valid user input phoneNumber
     */
    private boolean validatePhone() {
        String phone = phoneLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(phone)) {
            this.phoneLayout.setError("Field can't be empty");
            return false;
        } else {
            this.phoneLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input userName
     */
    private boolean validateUsername() {
        String username = usernameLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(username)) {
            this.usernameLayout.setError("Field can't be empty");
            return false;
        } else {
            this.usernameLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input FirstName
     */
    private boolean validateFirstName() {
        String firstName = firstnameLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            this.usernameLayout.setError("Field can't be empty");
            return false ;
        } else {
            this.usernameLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input LastName
     */
    private boolean validateLastName() {
        String lastTime = lastnameLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(lastTime)) {
            this.usernameLayout.setError("Field can't be empty");
            return false ;
        } else {
            this.usernameLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input gender
     */
    private boolean validateGender() {
        return true;
    }

    /**
     * for check valid user input birthdate
     */
    private boolean validateBirthDate() {
        return true;
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
