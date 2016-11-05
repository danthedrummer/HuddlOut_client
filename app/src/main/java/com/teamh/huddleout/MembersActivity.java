package com.teamh.huddleout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        this.setTitle("Group Members");
    }
}
