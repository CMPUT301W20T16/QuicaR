package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.DriverInfo;
import com.example.user.User;
import com.example.util.MyUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterDriverActivity extends AppCompatActivity implements OnGetUserDataListener {
    private TextInputLayout licenseLayout, sinNumberLayout, plateNumberLayout;
    private Button validateButton;
    FirebaseAuth auth;

    private User user ;
    private OnGetUserDataListener listener = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        licenseLayout = findViewById(R.id.profile_driver_license_number);
        sinNumberLayout = findViewById(R.id.profile_driver_sin);
        plateNumberLayout = findViewById(R.id.profile_validate_plate_number);
        validateButton = findViewById(R.id.validate_button);
        auth = FirebaseAuth.getInstance();


        //Test
        String userName = DatabaseHelper.getInstance().getCurrentUserName();
        // get current user
        UserDataHelper.getInstance().getUser(userName,this);




        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validateFlag = checkValidate();
                if (!validateFlag) {
                    ;
                    System.out.println("not validate");
                    Toast.makeText(RegisterDriverActivity.this,
                            "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    updateDriver();

                    UserDataHelper.getInstance().updateUserProfile(user, listener);
                    Toast.makeText(RegisterDriverActivity.this,
                            "register successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //when user click
            View v = getCurrentFocus();
            if (MyUtil.isShouldHideKeyboard(v, me)) { //if user click other area
                MyUtil.disableSoftInputFromAppearing(this);  //get keyboard back
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     *Run when pass the validate, to update driver information
     */

    private void updateDriver() {
        String license,sin,plate;
        license = sin = plate  = null;

        if(this.user != null){
            System.out.println("teemo");
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
            user.setDriver(true);
            user.setDriverInfo( rating,  plate,  license,  sin);
        }

    }


    /**
     * for check valid user input LicenseNumber
     */
    private boolean validateLicenseNumberLayout(){
        String LicenseNumber = licenseLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(LicenseNumber)) {
            this.licenseLayout.setError("Field can't be empty");
            return false;
        } else {
            this.licenseLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input sinNumberLayout
     */
    private boolean validatesinNumber() {
        String birthDate = sinNumberLayout.getEditText().getText().toString();

        if (TextUtils.isEmpty(birthDate)) {
            this.sinNumberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.sinNumberLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input
     */
    private boolean validatePlateNumber() {
        String plateNumber = plateNumberLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(plateNumber)) {
            this.plateNumberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.plateNumberLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input
     */
    public boolean checkValidate (){
        if (!validateLicenseNumberLayout() || !validatesinNumber() || !validatePlateNumber()) {
            return false;
        }
        return true;

    }


    /**
     *
     * for implement to get databasework
     */
    @Override
    public void onSuccess(User user, String tag) {

        if (tag == UserDataHelper.GET_USER_TAG){
            this.user = user;
            System.out.println("isSuccess");
        } else if (tag == UserDataHelper.UPDATE_USER_TAG) {
            startActivity(new Intent(getApplicationContext(), DriverBrowsingActivity.class));
        }
        System.out.println(user);

    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isFalse");
        System.out.println(errorMessage);

        Toast.makeText(RegisterDriverActivity.this,
                "Error loading user data, try later", Toast.LENGTH_SHORT).show();

    }

}
