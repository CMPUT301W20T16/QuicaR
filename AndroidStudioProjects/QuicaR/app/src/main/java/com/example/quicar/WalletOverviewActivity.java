package com.example.quicar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.example.util.MyUtil;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WalletOverviewActivity extends AppCompatActivity implements OnGetUserDataListener{

    Button change_pay;
    Button card_pay;
    Button camera_scan;
    Button card_info;
    Button recharge;
    ImageView qr_code;
    TextView balance;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    String currentBalance;
    User user;
    String time;
    private OnGetUserDataListener listener = this;
    DecimalFormat decimalFormat = new DecimalFormat(".00");

    // every 30 seconds refresh the qr code 1 time
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(runnable, 1000 * 30);
        }
        void update() {
            generate_qr(qr_code);
        }
    };

    // every 30 seconds refresh the qr code 1 time
//    private Runnable runnable2 = new Runnable() {
//        public void run() {
//            this.update();
//            handler2.postDelayed(runnable2, 1000 * 2);
//        }
//        void update() {
//            if (user != null) {
//                UserDataHelper.getInstance().updateUserProfile(user, listener);
//                user = DatabaseHelper.getInstance().getCurrentUser();
//                currentBalance = "( $ " + user.getAccountInfo().getWallet().getBalance().toString() + " )";
//                balance.setText(currentBalance);
//                balance.bringToFront();
//            }
//        }
//    };

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        qr_code = (ImageView) findViewById(R.id.qr_code);
        change_pay = (Button)findViewById(R.id.change_pay);
        card_pay = (Button)findViewById(R.id.card_pay);
        camera_scan = (Button)findViewById(R.id.camera_scan);
        card_info = (Button)findViewById(R.id.card_information);
        recharge = (Button)findViewById(R.id.recharge);
        balance = (TextView)findViewById(R.id.balance);
        UserDataHelper.getInstance().setOnNotifyListener(this);

        handler.postDelayed(runnable, 1000 * 30);
        generate_qr(qr_code);

        //handler2.postDelayed(runnable2, 1000 * 2);
        user = DatabaseHelper.getInstance().getCurrentUser();
        if(user.getAccountInfo().getWallet() == null) {
            System.out.println(user.getAccountInfo().getUserName());
        }
        String currentBalance = "( $ " + decimalFormat.format(user.getAccountInfo().getWallet().getBalance()) + " )";
        balance.setText(currentBalance);
        balance.bringToFront();

        camera_scan.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ScanTransferActivity.class)));

        card_info.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ManageCardActivity.class)));

        recharge.setOnClickListener(v -> {
            if (DatabaseHelper.getInstance().getCurrentUser().getAccountInfo().getWallet().getBankAccountArrayList().size() != 0) {
                startActivity(new Intent(getApplicationContext(), RechargeActivity.class));
            }else{
                Toast.makeText(getApplicationContext(), "You haven't bind a card, please bind a card first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ManageCardActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    protected void generate_qr(ImageView qr_code) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        time = df.format(LocalDateTime.now());
        Gson gson = new Gson();
        String json = gson.toJson(DatabaseHelper.getInstance().getCurrentUser());
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        int y = point.y;
        int icon = x < y ? x : y;
        icon = icon * 3 / 4;
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(time + "\n" + json, BarcodeFormat.QR_CODE.toString(), icon);
        try {
            Bitmap bitmap = qrCodeGenerator.encodeAsBitmap();
            qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu wallet_menu)
    {
        getMenuInflater().inflate(R.menu.wallet_menu, wallet_menu);
        return super.onCreateOptionsMenu(wallet_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())//得到被点击的item的itemId
        {
            case android.R.id.home:
//                startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
                MyUtil.goToIntent(WalletOverviewActivity.this);
                break;
            case R.id.help: //对应的ID就是在add方法中所设定的Id
                startActivity(new Intent(getApplicationContext(), WalletIntro.class));
                break;
            case R.id.pay_record:
                startActivity(new Intent(getApplicationContext(), PayHistoryActivity.class));
                break;
            case R.id.change_password:
                startActivity(new Intent(getApplicationContext(), PayPasswordChangeEnterActivity.class));
                break;
            case R.id.reset:
                startActivity(new Intent(getApplicationContext(), ResetWalletActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onSuccess(User user, String tag) {

    }

    @Override
    public void onUpdateNotification(User user) {
        if (user != null) {
            currentBalance = "( $ " + decimalFormat.format(user.getAccountInfo().getWallet().getBalance()) + " )";
            balance.setText(currentBalance);
            balance.bringToFront();
        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onBackPressed(){
//        startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
        MyUtil.goToIntent(WalletOverviewActivity.this);
    }
//        //according to different click control different action
//        if (item.getItemId() == R.id.help){
//            finish();
//        }
//        if (item.getItemId() == R.id.pay_record){
//            finish();
//        }
//        return true;
//    }
}
