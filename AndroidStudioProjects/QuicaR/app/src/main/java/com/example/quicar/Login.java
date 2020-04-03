package com.example.quicar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.datahelper.UserState;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.listener.OnGetRequestDataListener;
import com.example.listener.OnGetUserDataListener;
import com.example.listener.OnGetUserStateListener;
import com.example.user.User;
import com.example.util.ConnectivityReceiver;
import com.example.util.MyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class Login extends AppCompatActivity implements OnGetUserDataListener, OnGetUserStateListener, OnGetRequestDataListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private TextInputLayout userID, pwd;
    private Button loginButton;
    private TextView signUpButton;
    private CheckBox mCheckBox;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser currentUser;
    SharedPreferences mpref;
    private static final String PREF = "mpref";


    /* added by Jeremy */
    OnGetUserDataListener listener = this;
    AppCompatActivity appCompatActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.userID = findViewById(R.id.sign_in_email);
        this.pwd = findViewById(R.id.sign_in_password);
        this.mCheckBox = findViewById(R.id.rememberCheckbox);
        loginButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.signUpText);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        MyApplication.getInstance().setConnectivityListener(this);
        checkConnection();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myID = userID.getEditText().getText().toString().trim();
                String mypwd = pwd.getEditText().getText().toString();
                mpref = getSharedPreferences(PREF, MODE_PRIVATE);
                if (mCheckBox.isChecked()) {
                    Boolean isCheck = mCheckBox.isChecked();
                    SharedPreferences.Editor editor = mpref.edit();
                    editor.putString("mpref_name", myID);
                    editor.putString("mpref_pwd", mypwd);
                    editor.putBoolean("mpref_check", isCheck);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "user saved", Toast.LENGTH_SHORT).show();
                } else {
                    mpref.edit().clear().apply();
                }

                /* Added by Jeremy */
                //MyUtil.disableSoftInputFromAppearing(appCompatActivity);
                /* end here */
                if (!validateEmail(myID) | !validatePassword(mypwd)) {
                    return;
                }
                if (!checkUserNameOrEmail(myID)) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    Query query = databaseReference.child("User").orderByChild("accountInfo/userName").equalTo(myID);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                userID.setError("username not found");
                                return;
                            }
                            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {

                                User myUser = dataSnapshot1.getValue(User.class);
                                String getEmail = myUser.getAccountInfo().getEmail();
                                mAuth.signInWithEmailAndPassword(getEmail, mypwd)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                                    /* added by Jeremy */
                                                    UserDataHelper.getInstance().getUser(myID, listener);
                                                    ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
                                                    pgsBar.setVisibility(v.VISIBLE);

                                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                } else {
                                                    Toast.makeText(Login.this,
                                                            "Login failed" + task.getException(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    mAuth.signInWithEmailAndPassword(myID, mypwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = mAuth.getInstance().getCurrentUser();
                                retrieveUsername();
                                if (currentUser != null) {
                                    for (UserInfo profile : currentUser.getProviderData()) {
                                        String name = profile.getDisplayName();
                                        /* added by Jeremy */
                                        UserDataHelper.getInstance().getUser(name, listener);
                                        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
                                        pgsBar.setVisibility(v.VISIBLE);
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }
                                }
                            } else {
                                Toast.makeText(Login.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        getPrefData();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (MyUtil.isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                MyUtil.disableSoftInputFromAppearing(appCompatActivity);  //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * To validate user inputs email/username
     * @param email
     *      user inputs email/username
     * @return
     *      return if the user correctly inputs email/username
     * */
    public boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            this.userID.setError("Field can't be empty");
            return false ;
        } else {
            this.userID.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs password
     * @param password
     *      user inputs password
     * @return
     *      return if the user correctly inputs password
     * */
    public boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            this.pwd.setError("Field can't be empty");
            return false ;
        } else {
            this.pwd.setError(null);
            return true;
        }
    }

    /**
     * To validate user inputs email or username
     * @param id
     *      user inputs email/username
     * @return
     *      return whether user inputs email or username
     * */
    public boolean checkUserNameOrEmail(String id) {

//        if (id.contains("@")) {
//            return true;
//        }
//        return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (id == null)
            return false;
        return pat.matcher(id).matches();

    }


    /**
     * retrieve username according to
     * the current user's uid
     *
     * */
    public void retrieveUsername() {
        currentUser = mAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getInstance().getReference("User");

        myRef.child(currentUser.getUid()).
                child("accountInfo").child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username).build();
                currentUser.updateProfile(profileUpdates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onSuccess(User user, String tag) {
        if (tag == UserDataHelper.GET_USER_TAG) {
            DatabaseHelper.getInstance().setCurrentUser(user);
            UserStateDataHelper.getInstance().getUserState(this);
        }
    }

    public void getPrefData() {
        SharedPreferences sp = getSharedPreferences(PREF, MODE_PRIVATE);
        if (sp.contains("mpref_name")) {
            String prefName = sp.getString("mpref_name", "not found");
            userID.getEditText().setText(prefName);
        }
        if (sp.contains("mpref_pwd")) {
            String prefPassword = sp.getString("mpref_pwd", "not found");
            pwd.getEditText().setText(prefPassword);
        }
        if (sp.contains("mpref_check")) {
            Boolean prefCheck = sp.getBoolean("mpref_check", false);
            mCheckBox.setChecked(prefCheck);
        }
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStateUpdated() {
        Request currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();
        /* add currentRequest check for null by Yuxin */
        if (currentRequest != null ) {
            if (currentRequest.getRid() != null) {
                RequestDataHelper
                        .getInstance()
                        .queryUserRequest(DatabaseHelper
                                        .getInstance()
                                        .getUserState()
                                        .getCurrentRequest()
                                        .getRid()
                                , this);
            } else {
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                ProgressBar pgsBar = (ProgressBar) findViewById(R.id.pBar);
                pgsBar.setVisibility(View.INVISIBLE);
                MyUtil.goToIntent(Login.this);
            }
        }
    }
//      Network connection part
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        System.out.println("snack bar is showing here... ... ...");
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.login), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
//        // calculate actionbar height
//        TypedValue tv = new TypedValue();
//        int actionBarHeight=0;
//        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
//        {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//        }
//
//        //  set margin
//        params.setMargins(0, actionBarHeight, 0, 0);
        sbView.setLayoutParams(params);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        System.out.println("connection changed here... ... ...");
        showSnack(isConnected);
    }

//    request listener part
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.USER_REQ_TAG)) {
            Request request = requests.get(0);
            UserState userState = DatabaseHelper.getInstance().getUserState();
            userState.setCurrentRequest(request);
            userState.setActive(request.getAccepted());
            userState.setOnGoing(request.getPickedUp());
            userState.setOnArrived(request.getHasArrived());
            DatabaseHelper.getInstance().setUserState(userState);
            UserStateDataHelper.getInstance().recordState();
            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
            pgsBar.setVisibility(View.INVISIBLE);
            MyUtil.goToIntent(this);
        }
    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onArrivedNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
    public void onFailure(String errorMessage, String tag) {
        if (tag.equals(RequestDataHelper.USER_REQ_TAG)) {

            UserState userState = DatabaseHelper.getInstance().getUserState();
            userState.setCurrentRequest(new Request());
            userState.setOnConfirm(Boolean.FALSE);
            userState.setOnMatching(Boolean.FALSE);
            userState.setActive(Boolean.FALSE);
            userState.setOnArrived(Boolean.FALSE);
            DatabaseHelper.getInstance().setUserState(userState);
            UserStateDataHelper.getInstance().recordState();
            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);
            pgsBar.setVisibility(View.INVISIBLE);
            MyUtil.goToIntent(this);
        }
    }
}