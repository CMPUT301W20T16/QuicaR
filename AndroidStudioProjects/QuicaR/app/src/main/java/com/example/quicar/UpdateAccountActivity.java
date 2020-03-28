package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class UpdateAccountActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        ListView list = (ListView) findViewById(R.id.update_username);
        ArrayList<String> optionList = new ArrayList<>();
        optionList.add("Change Username");
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
                    EditText username_change = viewDialog.findViewById(R.id.username_change);
                    EditText pwd_confirm = viewDialog.findViewById(R.id.password_check);
                    builder.setView(viewDialog);
                    builder.setTitle("change username");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newUserName = username_change.getText().toString();
                            String checkPwd = pwd_confirm.getText().toString();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // Get auth credentials from the user for re-authentication. The example below shows
                            // email and password credentials but there are multiple possible providers,
                            // such as GoogleAuthProvider or FacebookAuthProvider.
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), checkPwd);

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Log.d(TAG, "User re-authenticated.");
                                            FirebaseAuth.getInstance().getCurrentUser().updateEmail(newUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                        ref.child(uid).child("accountInfo").child("email").setValue(newUserName);
                                                        Query query = ref.orderByChild("AccountInfo/email").equalTo(newUserName);
                                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                                                                    User myUser = dataSnapshot1.getValue(User.class);
                                                                    myUser.getAccountInfo().setEmail(newUserName);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                        Toast.makeText(getApplicationContext(), "email updated successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                            // String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            // DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                            // Query query = ref.orderByChild("accountInfo/userName").equalTo(newUserName);
/*
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(UpdateAccountActivity.this, "username exist", Toast.LENGTH_SHORT).show();
                                        username_change.setError("duplicate username");
                                    } else {
                                        ref.child(uid).child("accountInfo").child("userName").setValue(newUserName);

                                        Toast.makeText(UpdateAccountActivity.this, "username updated", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
*/
                        }
                    });
                    builder.create().show();
                }

            }
        });
    }


}
