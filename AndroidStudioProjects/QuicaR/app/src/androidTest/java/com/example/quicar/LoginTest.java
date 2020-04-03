package com.example.quicar;

import android.util.Log;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule = new ActivityTestRule<>(Login.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkSignInUsername() {
        solo.assertCurrentActivity("wrong activity", Login.class);
        //solo.enterText((EditText) solo.getView(R.id.sign_in_email), "usertest");
        final TextInputLayout userNameInput = (TextInputLayout) solo.getCurrentActivity().findViewById(R.id.sign_in_email);
        final TextInputLayout passwordInput = (TextInputLayout) solo.getCurrentActivity().findViewById(R.id.sign_in_password);
        solo.enterText(userNameInput.getEditText(), "yao");
        solo.enterText(passwordInput.getEditText(), "123456");
        // solo.clickOnButton("LOGIN");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.clearEditText(userNameInput.getEditText());
        solo.clearEditText(passwordInput.getEditText());
        assertTrue(solo.waitForText("Login successful"));
    }

    @Test
    public void checkSignInEmail() {
        solo.assertCurrentActivity("wrong activity", Login.class);
        //solo.enterText((EditText) solo.getView(R.id.sign_in_email), "usertest");
        final TextInputLayout userEmailInput = (TextInputLayout) solo.getCurrentActivity().findViewById(R.id.sign_in_email);
        final TextInputLayout userPasswordInput = (TextInputLayout) solo.getCurrentActivity().findViewById(R.id.sign_in_password);
        solo.enterText(userEmailInput.getEditText(), "mushroom@gmail.com");
        solo.enterText(userPasswordInput.getEditText(), "mushroom");
        // solo.clickOnButton("LOGIN");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.clearEditText(userEmailInput.getEditText());
        solo.clearEditText(userPasswordInput.getEditText());
        assertTrue(solo.waitForText("Login successful"));
    }

    @Test
    public void checkSignUpPageSwitched() {
        solo.clickOnView(solo.getView(R.id.signUpText));
        solo.assertCurrentActivity("Not switched", Register.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
