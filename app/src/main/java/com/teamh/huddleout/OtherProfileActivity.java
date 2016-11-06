package com.teamh.huddleout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OtherProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        this.setTitle("John Doe's Profile");
    }
}
