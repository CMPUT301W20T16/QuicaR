package com.example.quicar;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RegisterDriverActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<RegisterDriverActivity> rule = new ActivityTestRule<>(RegisterDriverActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Check whether the ShowActivity Activity opens by clicking on a list view item or not.
     */

    @Test
    public void checkActivityChange() {
        solo.assertCurrentActivity("Not in RegisterDriverActivity", RegisterDriverActivity.class);
    }


    /**
     * Check whether the user is update correct or not
     */

    @Test
    public void checkUpdateUser() {
        solo.assertCurrentActivity("Wrong Activity", RegisterDriverActivity.class);
        // changed input
        TextInputLayout licenseLayout = solo.getCurrentActivity().findViewById(R.id.profile_driver_license_number);
        TextInputLayout sinNumberLayout = solo.getCurrentActivity().findViewById(R.id.profile_driver_sin);


        TextInputLayout plateNumberLayout = solo.getCurrentActivity().findViewById(R.id.profile_validate_plate_number);


        solo.enterText(licenseLayout.getEditText(), "12345678");
        solo.enterText(sinNumberLayout.getEditText(), "Mushroom");
        solo.enterText(plateNumberLayout.getEditText(), "Teemo");

        Assert.assertTrue(solo.waitForText("Mushroom"));
        Assert.assertTrue(solo.waitForText("12345678"));
        Assert.assertTrue(solo.waitForText("Teemo"));

        solo.goBack();

    }


}

