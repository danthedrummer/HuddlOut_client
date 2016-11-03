package com.teamh.huddleout;

        import android.net.Uri;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TabHost;
        import android.widget.TabHost.OnTabChangeListener;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.appindexing.Thing;
        import com.google.android.gms.common.api.GoogleApiClient;


public class GroupMenu extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
                        ActivitySwap.swapToNextActivity(GroupMenu.this, MainMenu.class);
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MainMenu Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
