package com.teamh.huddleout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = (Button)findViewById(R.id.registerButton);

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivity(LoginActivity.this, MainMenuActivity.class);
                    }
                }
        );

        registerButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        System.out.println("bepis");
                        ActivitySwap.swapToNextActivity(LoginActivity.this, RegisterActivity.class);
                    }
                }
        );
    }
}
