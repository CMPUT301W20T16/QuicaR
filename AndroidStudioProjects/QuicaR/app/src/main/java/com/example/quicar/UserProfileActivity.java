package com.example.quicar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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


    private TextInputLayout emailLayout, phoneLayout, usernameLayout, firstNameLayout, lastNameLayout, birthDateLayout, genderLayout,passwordLayout;

    private boolean issuccess = false;
    private  boolean isfalse = false;
    FirebaseAuth mAuth;


    private  User user ;
    private OnGetUserDataListener listener = this;
    final Calendar myCalendar = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button saveButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_profile);
        this.emailLayout = findViewById(R.id.profile_email);
        this.phoneLayout = findViewById(R.id.profile_phone);
        this.usernameLayout = findViewById(R.id.profile_username);
        this.firstNameLayout = findViewById(R.id.profile_firstName);
        this.lastNameLayout = findViewById(R.id.profile_lastName);
        this.genderLayout = findViewById(R.id.profile_gender);
        this.birthDateLayout = findViewById(R.id.profile_birthDate);
        this.passwordLayout = findViewById(R.id.profile_password);
        saveButton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();


        // set date picker
        // Reference: Alireza Noorali, Android_coder
        // URL:https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        final DatePickerDialog.OnDateSetListener Dates = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        birthDateLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UserProfileActivity.this, Dates, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });





//        String userName = DatabaseHelper.getCurrentUserName();
        //Test
        String userName = "Hello";
        // get current user
        UserDataHelper.getUser(userName,this);

//        long startTime = System.currentTimeMillis();
//        long endTime;
//        while (true){
//            System.out.println("i am  waiting");
//
//            endTime = System.currentTimeMillis();
//            if ((endTime - startTime) > 5000 ){
//                break;
//            }
//            if (user == null) {
//                System.out.println("no user result");
//            }
//
//        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validateFlag = checkValidate();

                if(!validateFlag) {
                    ;
                    System.out.println("not validate");
                    Toast.makeText(UserProfileActivity.this,
                            "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    ;
                    setDefault();
//                    System.out.println("Good");
                    updateUser();

//                    System.out.println(user.getAccountInfo().getPhone());
                    UserDataHelper.updateUserProfile(user,listener);

                }



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


    /**
     *Run when pass the validate, to update user information
     */
    private void updateUser() {
        String email,username,phone,firstName,lastName,gender,password,accNo;
        Wallet wallet = null;
        accNo = email = username = phone = firstName = lastName = gender = password = null;
        Date birthDate = null;

        if(this.user != null){
            System.out.println("gu");
            if (this.birthDateLayout.getEditText().getText() != null) {
                String sDate1 = this.birthDateLayout.getEditText().getText().toString();
//                birthDate =new SimpleDateFormat("MMM dd,yyyy").parse(sDate1);
            }

            if (this.emailLayout.getEditText().getText() != null) {
                email = this.emailLayout.getEditText().getText().toString();
            }

            if (this.usernameLayout.getEditText().getText() != null) {
                username = this.usernameLayout.getEditText().getText().toString();
            }

            if(this.phoneLayout.getEditText().getText()!= null){
                phone = this.phoneLayout.getEditText().getText().toString();
//                System.out.println("here");
                System.out.println(phone);

            }
            if(this.firstNameLayout.getEditText().getText() != null){
                firstName = this.firstNameLayout.getEditText().getText().toString();
            }
            if(this.lastNameLayout.getEditText().getText()!= null){
                lastName = this.lastNameLayout.getEditText().getText().toString();
            }
            if (this.genderLayout.getEditText().getText()!= null){
                gender = this.genderLayout.getEditText().getText().toString();
            }
            if (this.passwordLayout.getEditText() != null){
                password = this.passwordLayout.getEditText().getText().toString();
            }
        }


        user.setAccountInfo(accNo, firstName,lastName, birthDate, gender,  phone,  email,  username, password, wallet);
        ;

    }

    /**
     * check Validate
     * @nretur
     */

    private boolean checkValidate() {
        boolean flag = true;

        if (!validateEmail()) {
            flag = false;
        }
        if(!validatePassWord()) {
            flag = false;
        }
        if(!validateFirstName()){
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
    public void setDefault() {
        if(this.user != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //set default user info
            if (user.getAccountInfo().getBirthDate() != null) {
                this.birthDateLayout.getEditText().setText(sdf.format(user.getAccountInfo().getBirthDate()));
            } else {
                ;
            }
            if (user.getAccountInfo().getEmail() != null) {
                this.emailLayout.getEditText().setText(user.getAccountInfo().getEmail());
            }

            if (user.getAccountInfo().getUserName() != null) {
                this.usernameLayout.getEditText().setText(user.getAccountInfo().getUserName());
            }
            if(user.getAccountInfo().getPhone() != null){
                this.phoneLayout.getEditText().setText(user.getAccountInfo().getPhone());
            }
            if(user.getAccountInfo().getFirstName() != null){
                this.firstNameLayout.getEditText().setText(user.getAccountInfo().getFirstName());
            }
            if(user.getAccountInfo().getLastName() != null){
                this.lastNameLayout.getEditText().setText(user.getAccountInfo().getLastName());
            }
            if (user.getAccountInfo().getGender() != null){
                this.genderLayout.getEditText().setText(user.getAccountInfo().getGender());
            }
            if (user.getAccountInfo().getPassword() != null){
                this.passwordLayout.getEditText().setText(user.getAccountInfo().getPassword());
            }

            System.out.println("setting default");
        }


    }

    /**
     * for check valid user input email
     */
    public boolean validateEmail() {
        String email = emailLayout.getEditText().getText().toString().trim();
        if(!email.contains("@")){
            return false;
        }
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
        String firstName = firstNameLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            this.firstNameLayout.setError("Field can't be empty");
            return false ;
        } else {
            this.firstNameLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid user input LastName
     */
    private boolean validateLastName() {
        String lastTime = lastNameLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(lastTime)) {
            this.lastNameLayout.setError("Field can't be empty");
            return false ;
        } else {
            this.lastNameLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input gender, Check later
     */
    private boolean validateGender() {
        return true;
    }

    /**
     * for check valid user input birthDate, Check later
     */
    private boolean validateBirthDate() {
        return true;
    }


    /**
     * This is an extra method that return date and time as a string in standard format
     * @return
     *  date and time as a string in standard format
     */
    public String getDateTimeString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy", Locale.CANADA);
        if (date == null)
            throw new IllegalArgumentException();
        return sdf.format(date);
    }


    /**
     *
     * for implement to get databasework
     */
    @Override
    public void onSuccess(User user, String tag) {

        if (tag == UserDataHelper.GET_USER_TAG){
            this.user = user;
            this.issuccess = true;
            System.out.println("isSuccess");
        }
        System.out.println(user);
        this.setDefault();

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isSuccess");
        this.isfalse = true;

    }

    @Override
    public  void  onUserExists(Boolean exists, String tag){

    }

    // design to show update format
    private void updateLabel() {
        String myFormat = "MMM dd,yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthDateLayout.getEditText().setText(sdf.format(myCalendar.getTime()));
    };
}
