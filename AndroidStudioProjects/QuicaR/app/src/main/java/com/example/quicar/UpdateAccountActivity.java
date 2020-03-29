package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateAccountActivity extends AppCompatActivity implements OnGetUserDataListener{
    private static final String TAG = "UpdateAccountActivity";
    private OnGetUserDataListener listener = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        ListView list = (ListView) findViewById(R.id.update_username);
        ArrayList<String> optionList = new ArrayList<>();
        optionList.add("Change Email");
        optionList.add("Change Password");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, optionList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAccountActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(UpdateAccountActivity.this);
                    View viewDialog = inflater.inflate(R.layout.username_update_dialog, null);
                    EditText email_change = viewDialog.findViewById(R.id.email_update);
                    EditText pwd_confirm = viewDialog.findViewById(R.id.password_check);
                    builder.setView(viewDialog);
                    builder.setTitle("change email");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newEmail = email_change.getText().toString();
                            String checkPwd = pwd_confirm.getText().toString();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // Get auth credentials from the user for re-authentication. The example below shows
                            // email and password credentials but there are multiple possible providers,
                            // such as GoogleAuthProvider or FacebookAuthProvider.
                            String oldEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                            if (TextUtils.isEmpty(newEmail)) {
                                Toast.makeText(getApplicationContext(), "Email field can not be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(checkPwd)) {
                                // pwd_confirm.setError("Field can not be empty");
                                Toast.makeText(getApplicationContext(), "password field can not be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!isValidEmail(newEmail)) {
                                Toast.makeText(getApplicationContext(), "wrong email format", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(oldEmail, checkPwd);

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Re-auth success");
                                                // check if email is unique
                                                FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                                Log.d(TAG, "this email is already in use");
                                                                // Toast.makeText(getApplicationContext(), "this email is already in use", Toast.LENGTH_SHORT).show();
                                                            } else if (task.getResult().getSignInMethods().size() == 0) {
                                                                // update the email
                                                                Log.d(TAG, "valid new email");
                                                                FirebaseAuth.getInstance().getCurrentUser().updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                                                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                                            ref.child(uid).child("accountInfo").child("email").setValue(newEmail);
                                                                            User currentUser = DatabaseHelper.getInstance().getCurrentUser();
                                                                            currentUser.getAccountInfo().setEmail(newEmail);
                                                                            UserDataHelper.getInstance().updateUserProfile(currentUser, listener);
                                                                            Toast.makeText(getApplicationContext(), "email updated successful", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "re-auth failed");
                                                Toast.makeText(getApplicationContext(), "wrong password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("CANCEL", null);
                    builder.create().show();
                }

                if (position == 1) {
                    AlertDialog.Builder builderPwd = new AlertDialog.Builder(UpdateAccountActivity.this);
                    LayoutInflater inflaterPwd = LayoutInflater.from(UpdateAccountActivity.this);
                    View viewDialogPwd = inflaterPwd.inflate(R.layout.password_update_dialog, null);
                    EditText mOriginEmail = viewDialogPwd.findViewById(R.id.origin_pwd);
                    EditText mpwdUpdate = viewDialogPwd.findViewById(R.id.pwd_dialog_change);
                    EditText mpwdConfirm = viewDialogPwd.findViewById(R.id.pwd_confirm_dialog);
                    builderPwd.setView(viewDialogPwd);
                    builderPwd.setTitle("Change Password");
                    builderPwd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String originEmail = mOriginEmail.getText().toString();
                            String pwdReset = mpwdUpdate.getText().toString();
                            String pwdResetConfirm = mpwdConfirm.getText().toString();
                            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (TextUtils.isEmpty(originEmail)) {
                                Toast.makeText(getApplicationContext(), "Email field can not be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(pwdReset)) {
                                Toast.makeText(getApplicationContext(), "password field can not be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(pwdResetConfirm)) {
                                Toast.makeText(getApplicationContext(), "confirm password field can not be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            if (!isValidEmail(originEmail)) {
                                Toast.makeText(getApplicationContext(), "wrong email format", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            // Get auth credentials from the user for re-authentication. The example below shows
                            // email and password credentials but there are multiple possible providers,
                            // such as GoogleAuthProvider or FacebookAuthProvider.
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(mUser.getEmail(), originEmail);

                            // Prompt the user to re-provide their sign-in credentials
                            mUser.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User re-authenticated.");
                                                Toast.makeText(getApplicationContext(), "re-authenticated", Toast.LENGTH_SHORT).show();
                                                if (!pwdReset.equals(pwdResetConfirm)) {
                                                    Toast.makeText(getApplicationContext(), "new passwords didn't match", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    mUser.updatePassword(pwdReset)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "User password updated.");
                                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                                                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                                        ref.child(uid).child("accountInfo").child("password").setValue(pwdReset);
                                                                        User currentUser1 = DatabaseHelper.getInstance().getCurrentUser();
                                                                        currentUser1.getAccountInfo().setPassword(pwdReset);
                                                                        UserDataHelper.getInstance().updateUserProfile(currentUser1, listener);
                                                                        Toast.makeText(getApplicationContext(), "password updated", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }

                                            } else {
                                                Log.d(TAG, "User re-authenticated failed");
                                                Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    builderPwd.create().show();
                }

            }
        });
    }

    public static boolean isValidEmail(String email)
    {
        if (email != null)
        {
            Pattern p = Pattern.compile("^[A-Za-z].*?@gmail\\.com$");
            Matcher m = p.matcher(email);
            return m.find();
        }
        return false;
    }

    @Override
    public void onSuccess(User user, String tag) {
        if (tag.equals(UserDataHelper.UPDATE_USER_TAG)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Intent homeIntent = new Intent(UpdateAccountActivity.this, Login.class);
            startActivity(homeIntent);
            // System.out.println("updated email sucessfully");
        }
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }


}
