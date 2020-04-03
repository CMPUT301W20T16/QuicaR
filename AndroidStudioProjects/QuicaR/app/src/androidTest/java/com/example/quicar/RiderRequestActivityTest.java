package com.example.quicar;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.font.Button_SF_Pro_Display_Medium;
import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RiderRequestActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<RiderRequestActivity> rule =
            new ActivityTestRule<>(RiderRequestActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSignUp() {
        solo.assertCurrentActivity("should be the first Activity", RiderRequestActivity.class);

        final Button_SF_Pro_Display_Medium addNewRequests = solo.getCurrentActivity().findViewById(R.id.start_location);
        solo.clickOnView(addNewRequests);
    }



//    @Test
//    public void checkDuplicateUserName() {
//        solo.assertCurrentActivity("Wrong Activity", Register.class);
//        final TextInputLayout username1 = solo.getCurrentActivity().findViewById(R.id.username);
//        final TextInputLayout email1 = solo.getCurrentActivity().findViewById(R.id.sign_email);
//        final TextInputLayout password1 = solo.getCurrentActivity().findViewById(R.id.password);
//        final TextInputLayout cfmPassword1 = solo.getCurrentActivity().findViewById(R.id.confirm_password);
//        solo.enterText(username1.getEditText(), "helloworld");
//        solo.enterText(email1.getEditText(), "helloworld1@gmail.com");
//        solo.enterText(password1.getEditText(), "123456");
//        solo.enterText(cfmPassword1.getEditText(), "123456");
//        solo.clickOnView(solo.getView(R.id.sign_up_button));
//        assertTrue(solo.waitForText("Username exist", 1, 3000));
//    }
//
//    @Test
//    public void checkLoginPageSwitch() {
//        solo.clickOnView(solo.getView(R.id.signInButtonText));
//        solo.assertCurrentActivity("Not switched", Login.class);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        solo.finishOpenedActivities();
//    }
}
