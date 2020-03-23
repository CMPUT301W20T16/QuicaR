package com.example.quicar;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UserProfileActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<UserProfileActivity> rule = new ActivityTestRule<>(UserProfileActivity.class, true, true);

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
        solo.assertCurrentActivity("Not in UserProfileActivity", UserProfileActivity.class);
    }


    /**
     * Check whether the user is update correct or not
     */

    @Test
    public void checkUpdateUser() {
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);

        // cannot edit email and username
        final TextInputLayout emailLayout = solo.getCurrentActivity().findViewById(R.id.profile_email);
        final TextInputLayout usernameLayout = solo.getCurrentActivity().findViewById(R.id.profile_username);

        // changed
        final TextInputLayout phoneLayout = solo.getCurrentActivity().findViewById(R.id.profile_phone);
        final TextInputLayout firstNameLayout = solo.getCurrentActivity().findViewById(R.id.profile_firstName);
        final TextInputLayout lastNameLayout = solo.getCurrentActivity().findViewById(R.id.profile_lastName);
        //gender
        final  TextInputLayout birthDateLayout = solo.getCurrentActivity().findViewById(R.id.profile_birthDate);
        final TextInputLayout passwordLayout = solo.getCurrentActivity().findViewById(R.id.profile_password);

        solo.enterText(phoneLayout.getEditText(), "12345678");
        solo.enterText(firstNameLayout.getEditText(), "Mushroom");
        solo.enterText(lastNameLayout.getEditText(), "Teemo");
        solo.enterText(passwordLayout.getEditText(),"Mushroom");


//        solo.clickOnText("Preferences");
//        solo.clickOnText("User");


//        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.spinner_gender));
//        solo.waitForText("male");
//        solo.clickOnText("male");
        // 0 is the first spinner in the layout
//        View view1 = solo.getView(Spinner.class, 0);
//        solo.clickOnView(view1);
//        solo.scrollToTop(); // I put this in here so that it always keeps the list at start
//// select the 10th item in the spinner
//        solo.clickOnView(solo.getView(TextView.class, 2));
//
//
//        solo.clickOnView(solo.getView(R.id.save_button));

        Assert.assertTrue(solo.waitForText("Mushroom"));
        Assert.assertTrue(solo.waitForText("12345678"));
        Assert.assertTrue(solo.waitForText("Teemo"));


//        Assert.assertTrue(solo.waitForText("male"));
        solo.goBack();

    }
    /**
     * Check Spinner works or not
     */
    @Test
    public void checkSpinner() {
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
        solo.pressSpinnerItem(0,0);
        System.out.println("here");
        Assert.assertTrue(solo.isSpinnerTextSelected(0,"no selection") );
        solo.goBack();
    }


}
