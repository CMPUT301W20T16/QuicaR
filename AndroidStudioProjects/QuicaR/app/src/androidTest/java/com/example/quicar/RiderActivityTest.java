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
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * This is a UI test for rider activity
 * from sending new request to making a payment
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RiderActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void riderActivityTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.sign_in_email),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("pangkaikai1"), closeSoftKeyboard());

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

        ViewInteraction textInputEditText3 = onView(
                allOf(withText("pangkaikai1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.sign_in_email),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("pangkaikai"));

        ViewInteraction textInputEditText4 = onView(
                allOf(withText("pangkaikai"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.sign_in_email),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sign_in_button), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear),
                                        2),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction button_SF_Pro_Display_Medium = onView(
                allOf(withId(R.id.start_location), withText("Select Location"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_rec),
                                        0),
                                1),
                        isDisplayed()));
        button_SF_Pro_Display_Medium.perform(click());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.location_card_view),
                        childAtPosition(
                                allOf(withId(R.id.history_loc_list),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                3)),
                                1),
                        isDisplayed()));
        cardView.perform(longClick());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText("Start"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.places_autocomplete_search_button), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withId(R.id.destination),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("southga"), closeSoftKeyboard());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.places_autocomplete_list),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2)),
                        0),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.confirm_button), withContentDescription("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction button_SF_Pro_Display_Medium2 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm Ride"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        button_SF_Pro_Display_Medium2.perform(click());

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.ten_percent), withText("10%"),
                        childAtPosition(
                                allOf(withId(R.id.tip_rate),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                3)),
                                1),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction button_SF_Pro_Display_Medium3 = onView(
                allOf(withId(R.id.start_location), withText("Select Location"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_rec),
                                        0),
                                1),
                        isDisplayed()));
        button_SF_Pro_Display_Medium3.perform(click());

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.location_card_view),
                        childAtPosition(
                                allOf(withId(R.id.history_loc_list),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                3)),
                                0),
                        isDisplayed()));
        cardView2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.title), withText("Start"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView2.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.places_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.destination),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("hub m"), closeSoftKeyboard());

        ViewInteraction linearLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.places_autocomplete_list),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2)),
                        0),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.confirm_button), withContentDescription("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction button_SF_Pro_Display_Medium4 = onView(
                allOf(withId(R.id.cancel_button), withText("Cancel Ride"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                2),
                        isDisplayed()));
        button_SF_Pro_Display_Medium4.perform(click());
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
