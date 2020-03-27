package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.listener.OnGetUserDataListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                    builder.setView(viewDialog);
                    builder.setTitle("change username");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newUserName = username_change.getText().toString();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                            Query query = ref.orderByChild("accountInfo/userName").equalTo(newUserName);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(UpdateAccountActivity.this, "username exist", Toast.LENGTH_SHORT).show();
                                        username_change.setError("duplicate username");
                                    } else {
                                        ref.child(uid).child("accountInfo").child("userName").setValue(username_change.getText().toString());
                                        Toast.makeText(UpdateAccountActivity.this, "username updated", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    builder.create().show();
                }

            }
        });
    }
}
