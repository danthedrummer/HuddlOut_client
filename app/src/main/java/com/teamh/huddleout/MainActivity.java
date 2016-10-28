package com.teamh.huddleout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://huddlout-server-reccy.c9users.io:8081/getTime";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        resultView.setText("Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Changes the theme (Prototype - Needs actual StyleManager.java class!)
    private void changeTheme(Theme theme) {
        currentTheme = theme;
        System.out.println("updating theme..." + theme.name());
        recreate();
    }
}
