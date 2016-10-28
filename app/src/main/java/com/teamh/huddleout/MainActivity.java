package com.teamh.huddleout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {

    //Hello World activity - testing styles

    private enum Theme {DeepPurple, Purple, Lime, LightGreen}

    static private Theme currentTheme = Theme.DeepPurple;

    //Add buttons to activity
    Button buttonDeepPurple;
    Button buttonPurple;
    Button buttonLime;
    Button buttonLightGreen;

    //Add result text view
    TextView resultView;
    TextView authView;
    TextView authSuccessView;

    //DEBUG TOKEN VARIABLE FOR NETWORK TESTING
    String authToken = "test123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Apply current theme
        switch (currentTheme) {
            case DeepPurple:
                setTheme(R.style.DeepPurpleTheme);
                break;
            case Purple:
                setTheme(R.style.PurpleTheme);
                break;
            case LightGreen:
                setTheme(R.style.LightGreenTheme);
                break;
            case Lime:
                setTheme(R.style.LimeTheme);
                break;
        }

        setContentView(R.layout.activity_main);

        //Add buttons to activity
        buttonDeepPurple = (Button)findViewById(R.id.buttonDeepPurple);
        buttonPurple = (Button)findViewById(R.id.buttonPurple);
        buttonLime = (Button)findViewById(R.id.buttonLime);
        buttonLightGreen = (Button)findViewById(R.id.buttonLightGreen);

        //Add result text view
        resultView = (TextView)findViewById(R.id.resultView);
        authView = (TextView)findViewById(R.id.authView);
        authSuccessView = (TextView)findViewById(R.id.authSuccessView);

        //Add action listeners for buttons
        buttonDeepPurple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTheme(Theme.DeepPurple);
            }
        });

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTheme(Theme.Purple);
            }
        });

        buttonLime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTheme(Theme.Lime);
            }
        });

        buttonLightGreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTheme(Theme.LightGreen);
            }
        });

        /*
         *  Debug Code to test Networking
         */

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://huddlout-server-reccy.c9users.io:8081/api/test/getTime";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Time is: " + response);
                        resultView.setText("Time is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText("That didn't work!");
            }
        });

        queue.add(stringRequest);

        String url2 = "https://huddlout-server-reccy.c9users.io:8081/api/test/getAuthKey";

        // Request a string response from the provided URL.
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Auth key is: " + response.substring(1, response.length() - 1));
                        authView.setText("Auth key is: " + response.substring(1, response.length() - 1));
                        authToken = response.substring(1, response.length() - 1);
                        checkAuth();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest2);

        /*
         * End of Networking Test Code
         */

        //Proof of concept for traversing back tot he login screen
        Button loginButton = (Button)findViewById(R.id.backToLogin);

        loginButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivity(MainActivity.this, LoginActivity.class);
                    }
                }
        );
    }

    //Changes the theme (Prototype - Needs actual StyleManager.java class!)
    private void changeTheme(Theme theme) {
        currentTheme = theme;
        System.out.println("updating theme..." + theme.name());
        recreate();
    }

    //Networking debug code
    private void checkAuth() {
        System.out.println("Checking Auth...");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url3 = "https://huddlout-server-reccy.c9users.io:8081/api/test/checkAuthKey?token=" + authToken;

        // Request a string response from the provided URL.
        StringRequest r = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        authSuccessView.setText("POST response: " + response);
                        System.out.println("POST response: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                authSuccessView.setText("That didn't work!");
                System.out.println("That didn't work!");
            }
        });

        queue.add(r);
    }
}
