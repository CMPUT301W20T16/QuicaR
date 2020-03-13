package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;


/**
 * This class extend DatabaseHelper and mainly handle user data
 */
public class UserDataHelper {
    final String TAG = "quicarDB-user";
    public static String ADD_USER_TAG = "add user";
    public static String UPDATE_USER_TAG = "update user";
    public static String GET_USER_TAG = "get user";

    private CollectionReference collectionReferenceUser;
    private FirebaseFirestore db;

    private static UserDataHelper userDataHelper;


    /**
     * This is the constructor of UserDataHelper
     */
    private UserDataHelper() {
        collectionReferenceUser = DatabaseHelper.getInstance().getCollectionReferenceUser();
        db = DatabaseHelper.getInstance().getDb();
    }

    /**
     * This method is the only static method that create a singleton for UserDataHelper
     * @return
     *  return the instance of UserDataHelper singleton
     */
    public static UserDataHelper getInstance() {
        if (userDataHelper == null)
            userDataHelper = new UserDataHelper();
        return userDataHelper;
    }

    /**
     * This is the method that add a user to firebase
     * @param newUser
     *  new user to be added
     * @param listener
     *  listener for notification
     */
    private void addUser(final User newUser, final OnGetUserDataListener listener) {
        collectionReferenceUser
                .document()
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  "User addition successful");
                        listener.onSuccess(null, ADD_USER_TAG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "User addition failed" + e.toString());
                    }
                });
    }

    /**
     * This is the method that delete a user from firebase
     * @param userID
     *  id of user to be deleted
     */
    private void delUser(final String userID) {
        collectionReferenceUser
                .document(userID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, userID + " deletion successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, userID + " deletion failed" + e.toString());
                    }
                });
    }

    /**
     * This id the method that update a user in firebase
     * @param user
     *  user ot be updated
     * @param userID
     *  id of the user
     * @param listener
     *  listener for notification
     */
    private void updateUser(final User user, final String userID, final OnGetUserDataListener listener) {

        final DocumentReference userDocRef = collectionReferenceUser.document(userID);

        db.runTransaction(new Transaction.Function<String>() {
            @Override
            public String apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocRef);
                System.out.println("docSnapshot---------" + snapshot);
                User userTmp = snapshot.toObject(User.class);
                System.out.println( "request temp---------" + userTmp);
                if (true) {
                    //  might need to check for any validation (eg. username is unique)
                    transaction.set(userDocRef, user);
                    return userID;
                } else {
                    throw new FirebaseFirestoreException("User update error",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String userID) {
                Log.d(TAG, "Transaction success: " + userID);
                listener.onSuccess(user, UserDataHelper.UPDATE_USER_TAG);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                        listener.onFailure("Transaction failure. " + e);
                    }
                });
    }

    /**
     * This method return the user object that match user name
     * @param userName
     *  candidate user name
     * @param listener
     *  listener for notification (onSuccess, onFailure)
     */
    void getUser(final String userName, final OnGetUserDataListener listener) {
        if (userName == null || userName.length() == 0) {
            listener.onFailure("user provided is a null object");
        }
        collectionReferenceUser
                .whereEqualTo("account.userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            User user = null;
                            String userID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                user = document.toObject(User.class);
                                userID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + userName + " \" has more than one account");
                                listener.onFailure(userName + " has more than one account");
                            } else if (user == null) {
                                listener.onFailure(userName + " has no account");
                            } else {
                                listener.onSuccess(user, GET_USER_TAG);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    /**
     * This method will check if the user already exists and call addUser method
     * @param newUser
     *  new user to be added
     * @param listener
     *  listener for notification
     */
    void addNewUser(final User newUser, final OnGetUserDataListener listener) {
        if (newUser == null) {
            listener.onFailure("user provided is a null object");
            return;
        }
        final String userName = newUser.getName();
        collectionReferenceUser
                .whereEqualTo("account.userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
//                            User user = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                user = document.toObject(User.class);
                                count++;
                            }
                            if (count > 0) {
                                System.out.println("*****  user \" " + userName + " \" has an existing account");
                                listener.onFailure(userName + " has anexisting account");
                            } else {
                                addUser(newUser, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    /**
     * This method will check if the user exists and call updateUser method
     * @param user
     *  user object to be updated
     * @param listener
     *  listener for notification
     */
    void updateUserProfile(final User user, final OnGetUserDataListener listener) {
        if (user == null || user.getName() == null || user.getName().length() == 0) {
            listener.onFailure("user name provided is a null or empty");
            return;
        }
        final String userName = user.getName();
        collectionReferenceUser
                .whereEqualTo("account.userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
//                            User user = null;
                            String userID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                user = document.toObject(User.class);
                                userID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + userName + " \" has more than one account");
                                listener.onFailure(userName + " has more than one request");
                            } else if (count == 0) {
                                System.out.println("*****  user \" " + userName + " \" has no existing account");
                                listener.onFailure(userName + " has no existing account");
                            } else {
                                updateUser(user, userID, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

}
