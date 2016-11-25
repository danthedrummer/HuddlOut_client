package com.teamh.huddleout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText username = (EditText)findViewById(R.id.usernameField);
        final EditText password = (EditText)findViewById(R.id.passwordField);
        final TextView messageTxt = (TextView)findViewById(R.id.messageTxt);

        Button registerButton = (Button)findViewById(R.id.registrationSubmitButton);

        final HuddlOutAPI hAPI = new HuddlOutAPI(this.getApplicationContext());

        registerButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        final RequestQueue reQueue = hAPI.register(username.getText().toString(), password.getText().toString());
                        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
                            @Override
                            public void onRequestFinished(Request request) {
                                if(hAPI.getAuth()){
                                    ActivitySwap.swapToNextActivity(RegisterActivity.this, MainMenuActivity.class);
                                    finish();
                                }else{
                                    messageTxt.setText(hAPI.getMessage());
                                }
                            }
                        };
                        reQueue.addRequestFinishedListener(finishedListener);
                    }
                }
        );
    }
}
