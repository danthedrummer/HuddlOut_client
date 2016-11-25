package com.teamh.huddleout;

import android.app.Activity;
import android.os.Bundle;

import android.os.Handler;

public class LaunchActivity extends Activity {

    //Timer to tell the app when to swap from the launch screen to the login screen
    final Handler HANDLER = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        HANDLER.postDelayed(new Runnable(){
            @Override
            public void run(){
                ActivitySwap.swapToNextActivity(LaunchActivity.this, LoginActivity.class);
                finish();
            }
        }, 4000);
    }


}
