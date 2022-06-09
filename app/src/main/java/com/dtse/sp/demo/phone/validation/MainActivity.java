package com.dtse.sp.demo.phone.validation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String countryCodeStr;
    String phoneNumberStr;
    EditText num1;
    EditText num2;
    EditText num3;
    EditText num4;
    EditText num5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        findViewById(R.id.id_button_sms).setOnClickListener(view -> authService());
        checkForPermission();
    }

    private void init() {
        num1 = findViewById(R.id.id_num1);
        num2 = findViewById(R.id.id_num2);
        num3 = findViewById(R.id.id_num3);
        num4 = findViewById(R.id.id_num4);
        num5 = findViewById(R.id.id_num5);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new MySMSBroadcastReceiver(), filter);
    }


    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.READ_SMS", "android.permission.RECEIVE_SMS", "android.permission.BROADCAST_SMS"
            }, 1000);
        }
    }

    private void authService() {
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(15)
                .locale(Locale.ENGLISH)
                .build();

        try {
            EditText editText = findViewById(R.id.id_phone_number);
            countryCodeStr = editText.getText().toString().substring(0, 2);
            phoneNumberStr = editText.getText().toString().substring(2);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Input a valid phone number", Toast.LENGTH_LONG).show();
            return;
        }

        Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(countryCodeStr, phoneNumberStr, settings);
        task.addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
            // The verification code request is successful.

        }).addOnFailureListener(TaskExecutors.uiThread(), e -> {
            e.printStackTrace();
        });
        task.addOnCompleteListener(task1 -> System.out.println("---onComplete: " + task1));
    }

    class MySMSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage msg = msgs[0];

            String phone = msg.getDisplayMessageBody().split(":")[1];
            phone = phone.replaceAll(" ", "");
            phone = phone.replace(".", "");
            char[] chars = phone.toCharArray();
            num1.setText(String.valueOf(chars[0]), TextView.BufferType.NORMAL);
            num2.setText(String.valueOf(chars[1]), TextView.BufferType.NORMAL);
            num3.setText(String.valueOf(chars[2]), TextView.BufferType.NORMAL);
            num4.setText(String.valueOf(chars[3]), TextView.BufferType.NORMAL);
            num5.setText(String.valueOf(chars[4]), TextView.BufferType.NORMAL);

        }
    }
}