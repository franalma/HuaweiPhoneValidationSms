package com.dtse.sp.demo.phone.validation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.id_button_sms).setOnClickListener(view -> authService());
    }

    private void authService(){
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.ENGLISH)
                .build();

        String countryCodeStr = "34";
        String phoneNumberStr = "647394970";
        Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(countryCodeStr, phoneNumberStr, settings);
        task.addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
            // The verification code request is successful.
            System.out.println("--verification code: "+ verifyCodeResult.getShortestInterval());

        }).addOnFailureListener(TaskExecutors.uiThread(), e -> {
            System.out.println("--onFailure:");
            e.printStackTrace();
        });
        task.addOnCompleteListener(task1 -> System.out.println("---onComplete: "+ task1));
    }
}