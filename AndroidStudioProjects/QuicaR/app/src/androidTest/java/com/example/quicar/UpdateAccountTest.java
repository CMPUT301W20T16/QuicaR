package com.example.quicar;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpdateAccountTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<UpdateAccountActivity> rule = new ActivityTestRule<>(UpdateAccountActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void updateEmail() {
        solo.clickInList(1);
        solo.assertCurrentActivity("wrong activity", UpdateAccountActivity.class);
        final EditText emailInput = (EditText) solo.getView(R.id.email_update);
        final EditText passwordCheck = (EditText) solo.getView(R.id.password_check);
        solo.enterText(emailInput, "newUser1@gmail.com");
        solo.enterText(passwordCheck, "123123");
        solo.clickOnText("OK");
        assertTrue(solo.waitForText("email updated successful"));
    }

    @Test
    public void checkDuplicateUpdateEmail() {
        solo.clickInList(1);
        solo.assertCurrentActivity("wrong activity", UpdateAccountActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_update), "newUser1@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password_check), "123123");
        solo.clickOnText("OK");
        assertTrue(solo.waitForText("This email is already in use"));
    }

    @Test
    public void updatePwd() {
        solo.clickInList(2);
        solo.assertCurrentActivity("wrong activity", UpdateAccountActivity.class);
        solo.enterText((EditText) solo.getView(R.id.origin_pwd), "123123");
        solo.enterText((EditText) solo.getView(R.id.pwd_dialog_change), "123456");
        solo.enterText((EditText) solo.getView(R.id.pwd_confirm_dialog), "123456");
        solo.clickOnText("OK");
        assertTrue(solo.waitForText("password updated"));
    }
}
