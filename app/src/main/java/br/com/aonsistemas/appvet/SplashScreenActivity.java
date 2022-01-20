package br.com.aonsistemas.appvet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private AppVeterinaryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        dbHelper = new AppVeterinaryDbHelper(SplashScreenActivity.this);

        Handler handle = new Handler(getMainLooper());
        handle.postDelayed(this::start, 3000);

    }

    private void start() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        SplashScreenActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        try {
            dbHelper.close();
        } catch (Exception ignored) {}

        super.onDestroy();
    }
}