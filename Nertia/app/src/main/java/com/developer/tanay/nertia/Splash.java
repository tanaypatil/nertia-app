package com.developer.tanay.nertia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import static android.view.Window.FEATURE_NO_TITLE;

public class Splash extends AppCompatActivity {

    private static final String spUsername = "logged_in_username";
    private static final String spLoggedIn = "log_in_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    boolean fwd = checkPrefs();
                    String s;
                    if (fwd){
                        s = "Home";
                    }else {
                        s = "Login";
                    }
                    Intent i = new Intent("android.intent.action."+s);
                    startActivity(i);
                }
            }
        };
        t.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean checkPrefs(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.contains(spLoggedIn)) {
            if (sharedPreferences.contains(spUsername)) {
                return true;
            }
        }
        return false;
    }

}
