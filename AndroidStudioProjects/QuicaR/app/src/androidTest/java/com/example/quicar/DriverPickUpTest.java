package com.example.quicar;

import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

public class DriverPickUpTest implements TaskLoadedCallback, OnMapReadyCallback {
    private Solo solo;

    @Rule
    public ActivityTestRule<DriverPickUpActivity> rule = new ActivityTestRule<>(DriverPickUpActivity.class, true, true);




    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        


    }

    @Test
    public void checkSignInUsername() {

        solo.assertCurrentActivity("wrong activity", DriverPickUpActivity.class);

        Button confirmButton =  solo.getCurrentActivity().findViewById(R.id.confirm_button);
        solo.clickOnView(confirmButton);


    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onTaskDone(Object... values) {

    }
}


