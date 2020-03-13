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
     * Check whether the user is correct or not
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
//               this.genderLayout = findViewById(R.id.profile_gender);
        final  TextInputLayout birthDateLayout = solo.getCurrentActivity().findViewById(R.id.profile_birthDate);
        final TextInputLayout passwordLayout = solo.getCurrentActivity().findViewById(R.id.profile_password);

        solo.enterText(phoneLayout.getEditText(), "12345678");
        solo.enterText(firstNameLayout.getEditText(), "Mushroom");
        solo.enterText(lastNameLayout.getEditText(), "Teemo");
        solo.enterText(passwordLayout.getEditText(),"Mushroom");
        solo.clickOnView(solo.getView(R.id.save_button));

        Assert.assertTrue(solo.waitForText("Mushroom"));
        Assert.assertTrue(solo.waitForText("12345678"));
        Assert.assertTrue(solo.waitForText("Teemo"));
        solo.goBack();

    }

}
