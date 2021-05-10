package com.test.weatherapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.test.weatherapp.R;
import com.test.weatherapp.helper.PermissionHelper;

public class SplashScreen extends AppCompatActivity {
    private static long SPLASH_MILLIS = 1000;
    public static String [] RequiredPermission;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        RequiredPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumeWork();
    }

    private void mCheckPermission() {
        permissionHelper = new PermissionHelper(this, RequiredPermission, 111);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onPermissionDenied() {
            }
            @Override
            public void onPermissionDeniedBySystem() {
            }
        });
    }

    private void mResumeWork() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCheckPermission();
                } else {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_MILLIS);
    }

}