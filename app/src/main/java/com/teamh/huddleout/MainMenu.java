package com.teamh.huddleout;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button groupBtn = (Button)findViewById(R.id.groupBtn);

        final TabHost myth = (TabHost) findViewById(R.id.tabHost);
        myth.setup();
        TabHost.TabSpec tabSpec;

        setTitle("Main Menu");

        // groups tab
        tabSpec = myth.newTabSpec("Groups");
        tabSpec.setContent(R.id.groupsTag);
        tabSpec.setIndicator("groups");
        myth.addTab(tabSpec);

        // friends tab
        tabSpec = myth.newTabSpec("Friends");
        tabSpec.setContent(R.id.friendsTag);
        tabSpec.setIndicator("friends");
        myth.addTab(tabSpec);

        // profile tab
        tabSpec = myth.newTabSpec("Profile");
        tabSpec.setContent(R.id.profileTag);
        tabSpec.setIndicator("profile");
        myth.addTab(tabSpec);

        // settings tab
        tabSpec = myth.newTabSpec("Settings");
        tabSpec.setContent(R.id.settingsTag);
        tabSpec.setIndicator("settings");
        myth.addTab(tabSpec);


        groupBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivitySwap.swapToNextActivity(MainMenu.this, GroupMenu.class);
                        finish();
                    }
                }
        );

        myth.setOnTabChangedListener(
                new OnTabChangeListener() {
                    public void onTabChanged(String s) {
                        setTitle(myth.getCurrentTabTag());
//                        if (myth.getCurrentTabTag().equalsIgnoreCase("profileTag")) {
//                            setTitle("enda the road");
//                        }
                    }
                }
        );
    }
}
