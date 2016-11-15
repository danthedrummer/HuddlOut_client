package com.teamh.huddleout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String TAG = "DevMsg";

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = (Button)findViewById(R.id.registerButton);
        final EditText username = (EditText)findViewById(R.id.usernameField);
        final EditText password = (EditText)findViewById(R.id.passwordField);
        final TextView messageTxt = (TextView)findViewById(R.id.messageTxt);

        final HuddlOutAPI hAPI = new HuddlOutAPI(this.getApplicationContext());

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        final RequestQueue reQueue = hAPI.login(username.getText().toString(), password.getText().toString());
                        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
                            @Override
                            public void onRequestFinished(Request request) {
                            if(hAPI.getAuth()){
                                ActivitySwap.swapToNextActivity(LoginActivity.this, MainMenuActivity.class);
                            }else{
                                messageTxt.setText("INVALID DETAILS");
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
                        ActivitySwap.swapToNextActivity(LoginActivity.this, RegisterActivity.class);
                    }
                }
        );
    }
}
