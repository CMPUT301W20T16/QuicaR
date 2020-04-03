package com.example.quicar;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


/**
 * This is a UI test for driver activity
 * from browsing open requests to receiving a payment from rider
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class DriverActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.CAMERA");

    @Test
    public void driverActivityTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.sign_in_email),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("dandan2"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.sign_in_password),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.rememberCheckbox), withText("Remember me"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear),
                                        2),
                                0),
                        isDisplayed()));
        appCompatCheckBox.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sign_in_button), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear),
                                        2),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction cardView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.open_requests_list),
                                childAtPosition(
                                        withId(R.id.bottom_sheet_open_requests),
                                        1)),
                        0),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Accept"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction button_SF_Pro_Display_Medium = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm Pick Up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear),
                                        0),
                                6),
                        isDisplayed()));
        button_SF_Pro_Display_Medium.perform(click());

        ViewInteraction button_SF_Pro_Display_Medium2 = onView(
                allOf(withId(R.id.confirm_button), withText("Ride Complete"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                2),
                        isDisplayed()));
        button_SF_Pro_Display_Medium2.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.scan),
                        childAtPosition(
                                allOf(withId(R.id.qr_linear),
                                        childAtPosition(
                                                withId(R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.confirm), withText("comfirm"),
                        childAtPosition(
                                allOf(withId(R.id.rider_linear),
                                        childAtPosition(
                                                withId(R.id.design_bottom_sheet),
                                                0)),
                                1),
                        isDisplayed()));
        button2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
