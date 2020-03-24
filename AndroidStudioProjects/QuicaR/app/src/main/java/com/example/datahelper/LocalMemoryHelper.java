package com.example.datahelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * This class handle storing, retrieving and removing data from sharedpreferences of android
 * as local memory
 */
public class LocalMemoryHelper {
    private Context context;
    private int length = 0;

    /**
     * This is the constructor of LocalMemoryHelper
     * @param context
     */
    public LocalMemoryHelper(Context context) {
        this.context = context;
    }

    /**
     * This method get an array list (pointer) as parameter and store data into the the list
     * so that data is retrieved from local memory to the list
     * @param stateDataList
     *  This is the list that will get data from local memory
     */
    public void retrieveItems(ArrayList<UserState> stateDataList) {
        //  retrieved value will be added into the stateDataList so that all values
        //  can be transfer back to where this function is call, without returning any value
        while(true) {
            //  never know where to stop as length is not given
            //  this loop will end when it obtain null value, maintaining the memory is important
            SharedPreferences pref = context.getSharedPreferences("MyPref", 0);

            Gson gson = new Gson();

            // key of the json string is "userState" + position in the list
            String json = pref.getString("userState" + length, null);

            if(json != null) {
                stateDataList.add(gson.fromJson(json, UserState.class));
            } else {
                break;
            }
            length++;

        }
    }

    /**
     * This method store an item into local memory
     * @param newUserState
     *  This is the candidate item to add
     */
    public void addItem(UserState newUserState) {
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        //  citation: Stackoverflow post by Muhammad Aamir Ali, edited by Ben,
        //  URL: https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object
        //  to store object into sharedpreferences
        //  new implementation is added to dependencies in the build.gradle file
        Gson gson = new Gson();
        String json = gson.toJson(newUserState);
        editor.putString("userState" + length, json);
        editor.commit();

        length++;
    }

    /**
     * This method delete an item from the local memory using it's index / position
     * @param position
     *  This is the candidate index of the item wanted to be deleted
     */
    public void deleteItem(int position) {
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove("userState" + position);

        for (int i = position ; i < length ; i++) {

            //  move all stored string that is after this position to the front by one
            int next_pos = i + 1;
            String json = pref.getString("userState" + String.valueOf(next_pos), null);
            editor.putString("userState" + i, json);

        }

        //  remove the extra last item so that there is no repetition of the last item
        int last_pos = length - 1;
        editor.remove("userState" + last_pos);
        editor.commit();

        //  reset the length of the list and position of chosen item
        length--;
    }

    /**
     * This method will update an item in the local memory
     * @param position
     *  This is the candidate position / index of the item to be updated
     * @param newUserState
     *  This is the new object of the item with new values
     */
    public void modifyItem(int position, UserState newUserState) {
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        //  update stored string in sharedpreferences
        Gson gson = new Gson();
        String json = gson.toJson(newUserState);

        //  citation: Title: JD JournalDev
        //  URL: https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
        //  initialization of sharedpreferences
        editor.putString("userState" + position, json);    // Storing string
        editor.commit();    // commit changes
    }


}
