package com.example.quicar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.DriverInfo;
import com.example.user.User;
import com.example.user.Wallet;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity implements OnGetUserDataListener {




    private TextInputLayout emailLayout, phoneLayout, usernameLayout, firstNameLayout, lastNameLayout, birthDateLayout,passwordLayout;
    private  TextInputLayout plateNumberLayout,licenseLayout,sinNumberLayout;

    private boolean issuccess = false;
    private  boolean isfalse = false;
    FirebaseAuth mAuth;

    private Spinner spinnerGender;
    private User user ;
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
//        this.genderLayout = findViewById(R.id.profile_gender);
        this.lastNameLayout = findViewById(R.id.profile_lastName);
        this.birthDateLayout = findViewById(R.id.profile_birthDate);
        this.passwordLayout = findViewById(R.id.profile_password);

        this.plateNumberLayout = findViewById(R.id.profile_driver_plateNumber);
        this.licenseLayout = findViewById(R.id.profile_driver_license_number);
        this.sinNumberLayout = findViewById(R.id.profile_driver_sin);

        saveButton = findViewById(R.id.save_button);
        //?? set cannot edit
        this.emailLayout.setEnabled(false);
        this.usernameLayout.setEnabled(false);

        // below set for driver mode invisible
//        this.plateNumberLayout.setVisibility(TextInputLayout.GONE);
////        this.licenseLayout.setVisibility(TextInputLayout.GONE);
////        this.sinNumberLayout.setVisibility(TextInputLayout.GONE);
        // user is not driver


        closeDriverInfo();

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
        String userName = DatabaseHelper.getInstance().getCurrentUserName();
        // get current user
        UserDataHelper.getInstance().getUser(userName,this);

//
//        if (user != null) {
//            System.out.println("gu");
//            if(user.isDriver()) {
//                System.out.println("i am driver");
//                openDriverInfo();
//            }
//        } else {
//            System.out.println("null user");
//        }



        spinnerGender = (Spinner) findViewById(R.id.spinner_gender);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validateFlag = checkValidate();
                System.out.println((spinnerGender.getSelectedItem()).toString() );
                if(!validateFlag) {
                    ;
                    System.out.println("not validate");
                    Toast.makeText(UserProfileActivity.this,
                            "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    ;
//                    setDefault();
//                    System.out.println("Good");
                    updateUser();
                    if(user.isDriver()) {
                        updateDriver();
                    }

//                    System.out.println(user.getAccountInfo().getPhone());
//                    System.out.println(user.getAccountInfo().getPhone());
                    UserDataHelper.getInstance().updateUserProfile(user,listener);
                    Toast.makeText(UserProfileActivity.this,
                            "Saved successfully", Toast.LENGTH_SHORT).show();

                }

            }
        });





    }


    /**
     *Run when you are not driver, do not show driver info
     */
    private  void closeDriverInfo() {
        this.plateNumberLayout.setVisibility(View.GONE);
        this.licenseLayout.setVisibility(View.GONE);
        this.sinNumberLayout.setVisibility(View.GONE);

    }
    /**
     *Run when you are driver, show driver info
     */
    private  void openDriverInfo (){
        this.plateNumberLayout.setVisibility(View.VISIBLE);
        this.licenseLayout.setVisibility(View.VISIBLE);
        this.sinNumberLayout.setVisibility(View.VISIBLE);

    }


    /**
     *Run when pass the validate, to update user information
     */
    private void updateUser() {
        String email,username,phone,firstName,lastName,gender,password,accNo;
        Wallet wallet = user.getAccountInfo().getWallet();
        accNo = email = username = phone = firstName = lastName = gender = password = null;
        String sbirthDate = null;
        Date birthDate;
        birthDate = null;
        if(this.user != null){
            if (this.birthDateLayout.getEditText().getText() != null) {
                sbirthDate  = this.birthDateLayout.getEditText().getText().toString();
                try {
                    birthDate = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(sbirthDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    birthDate = null;
//                    System.out.println("Teemo");
//                    System.out.println(birthDate);
                }
            }
            //change later
            if (this.emailLayout.getEditText().getText() != null) {
                email = this.emailLayout.getEditText().getText().toString();
                ;
            }

            if (this.usernameLayout.getEditText().getText() != null) {
                username = this.usernameLayout.getEditText().getText().toString();
                ;
            }

            if(this.phoneLayout.getEditText().getText()!= null){
                phone = this.phoneLayout.getEditText().getText().toString();
//                System.out.println("here");
//                System.out.println(phone);

            }
            if(this.firstNameLayout.getEditText().getText() != null){
                firstName = this.firstNameLayout.getEditText().getText().toString();
            }
            if(this.lastNameLayout.getEditText().getText()!= null){
                lastName = this.lastNameLayout.getEditText().getText().toString();
//                System.out.println(lastName);
            }
            if (this.spinnerGender.getSelectedItem()!= null){
//            if (this.genderLayout.getEditText().getText()!= null){
//                gender = this.genderLayout.getEditText().getText().toString();
                gender = spinnerGender.getSelectedItem().toString();
            }
            if (this.passwordLayout.getEditText() != null){
                password = this.passwordLayout.getEditText().getText().toString();
            }
        }
        // ???
//        System.out.println(birthDate);
        user.setAccountInfo(accNo, firstName,lastName, birthDate, gender,  phone,  email,  username, password, wallet);
//        System.out.println(user.getAccountInfo().getBirthDate());
        ;

    }


    /**
     *Run when pass the validate, to update driver information
     */

    private void updateDriver() {
        String license,sin,plate;
        license = sin = plate  = null;

        if(this.user != null){
            if (this.licenseLayout.getEditText().getText() != null) {
                license  = this.licenseLayout.getEditText().getText().toString();
            }
            //change later
            if (this.plateNumberLayout.getEditText().getText() != null) {
                plate = this.plateNumberLayout.getEditText().getText().toString();
                ;
            }

            if (this.sinNumberLayout.getEditText().getText() != null) {
                sin = this.sinNumberLayout.getEditText().getText().toString();
                ;
            }

            DriverInfo driverInfo = user.getAccountInfo().getDriverInfo();
            Double rating = driverInfo.getRating();
            user.setDriverInfo( rating,  plate,  license,  sin);
        }
        // ???
//        System.out.println(birthDate);

//        System.out.println(user.getAccountInfo().getBirthDate());
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

        if(user.isDriver()){
            if(!validateDriverLicense()){
                flag = false;
            }
            if(!validateDriverPlate()){
                flag = false;
            }
            if(!validateDriverSin()){
                flag = false;
            }
        }
        return  flag;
    }



    /**
     * defined for set default information to help user to update info
     */
    private void setDefault() {
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
                String gender = user.getAccountInfo().getGender();
                spinnerGender.setSelection(((ArrayAdapter<String>)spinnerGender.getAdapter()).getPosition(gender));
//                this.genderLayout.getEditText().setText(user.getAccountInfo().getGender());
            }
            if (user.getAccountInfo().getPassword() != null){
                this.passwordLayout.getEditText().setText(user.getAccountInfo().getPassword());
            }

            System.out.println("setting user default");

            if (user.isDriver()) {
                setDriverDefault();
                System.out.println("setting driver default");
            }


        }


    }

    /**
     * for setting Driver Default
     */
    private void setDriverDefault() {

        if (user != null) {
            System.out.println("gu");
            if(user.isDriver()) {
                System.out.println("i am driver");
                openDriverInfo();
            }
        } else {
            System.out.println("null user");
        }

        if(user.getAccountInfo().getDriverInfo().getLicense() != null){
            this.licenseLayout.getEditText().setText(user.getAccountInfo().getDriverInfo().getLicense());
        }
        if(user.getAccountInfo().getDriverInfo().getPlateNumber() != null){
            this.plateNumberLayout.getEditText().setText(user.getAccountInfo().getDriverInfo().getPlateNumber());
        }
        if(user.getAccountInfo().getDriverInfo().getSinNumber() != null){
            this.sinNumberLayout.getEditText().setText(user.getAccountInfo().getDriverInfo().getSinNumber());
        }

    }


    /**
     * for check valid driver plate number
     */
    private boolean validateDriverPlate() {
        String plate = plateNumberLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(plate)) {
            this.plateNumberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.plateNumberLayout.setError(null);
            return true;
        }
    }

    /**
     * for check valid driver license number
     */
    private boolean validateDriverLicense() {
        String license = licenseLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(license)) {
            this.licenseLayout.setError("Field can't be empty");
            return false;
        } else {
            this.licenseLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid driver sin number
     */
    private boolean validateDriverSin() {
        String sin = sinNumberLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(sin)) {
            this.sinNumberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.sinNumberLayout.setError(null);
            return true;
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
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isFalse");
        System.out.println(errorMessage);
        this.isfalse = true;
        Toast.makeText(UserProfileActivity.this,
                "Error loading user data, try later", Toast.LENGTH_SHORT).show();

    }

    // design to show update format
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthDateLayout.getEditText().setText(sdf.format(myCalendar.getTime()));

    };
}