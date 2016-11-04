package com.teamh.huddleout;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TabHost;

public class GroupMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        Button mainMenuBtn = (Button)findViewById(R.id.mainMenuBtn);

        final TabHost myth = (TabHost) findViewById(R.id.tabHost);
        myth.setup();
        TabHost.TabSpec tabSpec;

        setTitle("Team Hayes");

        // chat tab
        tabSpec = myth.newTabSpec("Chat");
        tabSpec.setContent(R.id.chatTag);
        tabSpec.setIndicator("chat");
        myth.addTab(tabSpec);

        // map tab
        tabSpec = myth.newTabSpec("Map");
        tabSpec.setContent(R.id.mapTag);
        tabSpec.setIndicator("map");
        myth.addTab(tabSpec);

        // voting tab
        tabSpec = myth.newTabSpec("Voting");
        tabSpec.setContent(R.id.votingTag);
        tabSpec.setIndicator("voting");
        myth.addTab(tabSpec);

        // rules tab
        tabSpec = myth.newTabSpec("Rules");
        tabSpec.setContent(R.id.rulesTag);
        tabSpec.setIndicator("rules");
        myth.addTab(tabSpec);


        mainMenuBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivitySwap.swapToNextActivity(GroupMenu.this, MainMenuActivity.class);
                        finish();
                    }
                }
        );

        myth.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    public void onTabChanged(String s) {
                        setTitle(myth.getCurrentTabTag());
                        if (myth.getCurrentTabTag().equalsIgnoreCase("rTag")) {
                            setTitle("enda the road");
                        }
                    }
                }
        );
    }
}
