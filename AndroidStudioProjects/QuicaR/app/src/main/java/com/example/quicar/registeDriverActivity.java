package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class registeDriverActivity extends AppCompatActivity implements OnGetUserDataListener {
    private TextInputLayout licenseNumberLayout, birthDateLayout, plate_numberLayout;
    private Button validateButton;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        licenseNumberLayout = findViewById(R.id.profile_driver_license_number);
        birthDateLayout = findViewById(R.id.profile_birthDate);
        plate_numberLayout = findViewById(R.id.profile_validate_plate_number);
        validateButton = findViewById(R.id.validate_button);
        auth = FirebaseAuth.getInstance();

    }


    /**
     * for check valid user input LicenseNumber
     */
    private boolean validateLicenseNumberLayout() {
        String LicenseNumber = licenseNumberLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(LicenseNumber)) {
            this.licenseNumberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.licenseNumberLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input birth date
     */
    private boolean validateBirthDate() {
        String birthDate = licenseNumberLayout.getEditText().getText().toString();

        if (TextUtils.isEmpty(birthDate)) {
            this.birthDateLayout.setError("Field can't be empty");
            return false;
        } else {
            this.birthDateLayout.setError(null);
            return true;
        }
    }


    /**
     * for check valid user input
     */
    private boolean validatePlateNumber() {
        String plateNumber = plate_numberLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(plateNumber)) {
            this.plate_numberLayout.setError("Field can't be empty");
            return false;
        } else {
            this.plate_numberLayout.setError(null);
            return true;
        }
    }

    public void Validate (View v){
        if (!validateLicenseNumberLayout() || !validateBirthDate()|| !validateBirthDate() || !validatePlateNumber()) {
            return;
        }

        /**
         * delete birthday in here
         */


        User currentUser = DatabaseHelper.getInstance().getCurrentUser();
        currentUser.getAccountInfo().getDriverInfo().setLicense(licenseNumberLayout.getEditText().getText().toString());
        currentUser.getAccountInfo().getDriverInfo().setPlateNumber(plate_numberLayout.getEditText().getText().toString());
        currentUser.setDriver(true);

        DatabaseHelper.getInstance().setCurrentUser(currentUser);

        UserDataHelper.getInstance().updateUserProfile(currentUser,this);




        //String input = "Teemo" ;
        //Toast.makeText(this,input,Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.UPDATE_USER_TAG)
            startActivity(new Intent(getApplicationContext(), DriverBrowsingActivity.class));
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }

}
