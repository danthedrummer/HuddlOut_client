package com.teamh.huddleout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.net.URLEncoder;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        final String TAG = "DevMsg";

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = (Button)findViewById(R.id.registerButton);
        final EditText username = (EditText)findViewById(R.id.usernameField);
        final EditText password = (EditText)findViewById(R.id.passwordField);

        final Context context = this.getApplicationContext();
        final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(context);

        final TextView signInTitle = (TextView)findViewById(R.id.signInTitle);

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        final ProgressDialog loginProgress = new ProgressDialog(LoginActivity.this);
                        loginProgress.setTitle("Logging In");
                        loginProgress.setMessage("Please wait...");
                        loginProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        loginProgress.show();

                        final RequestQueue reQueue = hAPI.login(username.getText().toString(), password.getText().toString());
                        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
                            @Override
                            public void onRequestFinished(Request request) {
                                loginProgress.dismiss();
                                if(hAPI.getAuth()){
                                    ActivitySwap.swapToNextActivityNoHistory(LoginActivity.this, MainMenuActivity.class);
                                    finish();
                                }
                            }
                        };
                        reQueue.addRequestFinishedListener(finishedListener);
                    }
                }
        );


        registerButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivityNoHistory(LoginActivity.this, RegisterActivity.class);
                    }
                }
        );

        signInTitle.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v) {

                        Log.i(TAG, "DEV Login");

                        final RequestQueue reQueue = hAPI.login("glennncullen", "1234567");
                        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
                            @Override
                            public void onRequestFinished(Request request) {
                                if(hAPI.getAuth()){
                                    ActivitySwap.swapToNextActivityNoHistory(LoginActivity.this, MainMenuActivity.class);
                                    finish();
                                }
                            }
                        };
                        reQueue.addRequestFinishedListener(finishedListener);

                    }

                });

    }

    //Prevent back nav
    @Override
    public void onBackPressed() {
    }
}
