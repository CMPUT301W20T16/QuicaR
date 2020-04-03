package com.example.quicar;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.font.Button_SF_Pro_Display_Medium;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class RiderSelectionActivityTest {
    AutocompleteSupportFragment pickup, dest;

    private Solo solo;
    @Rule
    public ActivityTestRule<RiderSelectLocationActivity> rule =
            new ActivityTestRule<>(RiderSelectLocationActivity.class, true, true);

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
        FragmentActivity fragmentActivity = (FragmentActivity) solo.getCurrentActivity();
        pickup = (AutocompleteSupportFragment) fragmentActivity.getSupportFragmentManager().findFragmentById(R.id.pick_up);
        dest = (AutocompleteSupportFragment) fragmentActivity.getSupportFragmentManager().findFragmentById(R.id.destination);



        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSignUp() {
        solo.assertCurrentActivity("should be the second Activity", RiderSelectLocationActivity.class);


    }

}
