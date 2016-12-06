package com.teamh.huddleout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        final EditText username = (EditText)findViewById(R.id.usernameField);
        final EditText password = (EditText)findViewById(R.id.passwordField);
        final EditText firstName = (EditText)findViewById(R.id.firstNameField);
        final EditText lastName = (EditText)findViewById(R.id.lastNameField);

        final Context context = this.getApplicationContext();

        Button registerButton = (Button)findViewById(R.id.registrationSubmitButton);
        Button loginButton = (Button)findViewById(R.id.loginButton);

        final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(this.getApplicationContext());

        registerButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        final RequestQueue reQueue = hAPI.register(username.getText().toString(), password.getText().toString(), firstName.getText().toString(), lastName.getText().toString());
                        final ProgressDialog registerProgress = new ProgressDialog(RegisterActivity.this);
                        registerProgress.setTitle("Registering");
                        registerProgress.setMessage("Please wait...");
                        registerProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        registerProgress.show();

                        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
                            @Override
                            public void onRequestFinished(Request request) {
                                if(hAPI.getAuth()){
                                    registerProgress.dismiss();
                                    ActivitySwap.swapToNextActivityNoHistory(RegisterActivity.this, MainMenuActivity.class);
                                    finish();
                                }else{
                                    registerProgress.dismiss();
                                    String errorMsg = hAPI.getMessage();
                                    if(errorMsg != null) {
                                        errorMsg = errorMsg.toUpperCase();
                                        Popup.show(errorMsg, context);
                                    } else {
                                        Popup.show("INTERNAL ERROR", context);
                                    }
                                }
                            }
                        };
                        reQueue.addRequestFinishedListener(finishedListener);
                    }
                }
        );

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivityNoHistory(RegisterActivity.this, LoginActivity.class);
                    }
                }
        );
    }

    //Prevent back nav
    @Override
    public void onBackPressed() {
    }
}
