package com.example.quicar;

public interface OnGetUserDataListener {

    //  notify listener that new user account is added successfully
    void onSuccessAddUser();

    //  notify listener that user profile is updated successfully
    void onSuccessUpdateUser();

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage);
}
