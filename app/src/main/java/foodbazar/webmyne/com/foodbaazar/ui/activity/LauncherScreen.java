package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import foodbazar.webmyne.com.foodbaazar.R;

public class LauncherScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_screen);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                Intent iHomeScreen = new Intent(LauncherScreen.this, HomeScreen.class);
//                 Intent iHomeScreen = new Intent(LauncherScreen.this, UploadImageActivity.class);

                startActivity(iHomeScreen);
                finish();

            }
        }.start();

    }

}
