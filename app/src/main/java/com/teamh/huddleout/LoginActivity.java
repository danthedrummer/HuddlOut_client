package com.teamh.huddleout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String TAG = "DevMsg";

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = (Button)findViewById(R.id.registerButton);
        EditText username = (EditText)findViewById(R.id.usernameField);
        EditText password = (EditText)findViewById(R.id.passwordField);

        final HuddlOutAPI hAPI = new HuddlOutAPI(this.getApplicationContext());

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        if(!hAPI.getAuth()){
                            hAPI.login(((EditText) findViewById(R.id.usernameField)).getText().toString(), ((EditText) findViewById(R.id.passwordField)).getText().toString());
                        }

                        while(hAPI.getAuthInProgress()){
                            if(hAPI.getAuth()){
                                ActivitySwap.swapToNextActivity(LoginActivity.this, MainMenuActivity.class);
                            }
                        }


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
