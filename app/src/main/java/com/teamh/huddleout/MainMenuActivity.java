package com.teamh.huddleout;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity implements GroupListFragment.OnFragmentInteractionListener, FriendListFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;

    private static final String TAG = "DevMsg";

    private View addGroupView;
    private ArrayList<String> groupTypes;
    private ArrayAdapter<String> groupSpinnerAdapter;
    private Spinner groupTypeSpinner;
    private EditText groupNameText;

    private ArrayList<String> profilePicOptions;
    private ArrayAdapter<String> profilePicSpinnerAdapter;
    private Spinner profilePicSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Main Menu");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(this.getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        addGroupView = inflater.inflate(R.layout.group_dialog_box, null);
        groupNameText = (EditText)addGroupView.findViewById(R.id.groupNameEditText);

        groupTypes = new ArrayList<String>();
        groupTypes.add("Drinking");
        groupTypes.add("Social Meet");

        groupTypeSpinner = (Spinner)addGroupView.findViewById(R.id.groupActivitySpinner);
        groupSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupTypes);
        groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        groupTypeSpinner.setAdapter(groupSpinnerAdapter);

        // Create spinner for drop down in edit profile menu
        profilePicOptions = new ArrayList<String>();
//        profilePicOptions.add(User.getInstance(getApplicationContext()).getProfilePicture());
        profilePicOptions.add("airplane");
        profilePicOptions.add("astronaut");
        profilePicOptions.add("ball");
        profilePicOptions.add("beach");
        profilePicOptions.add("butterfly");
        profilePicOptions.add("car");
        profilePicOptions.add("cat");
        profilePicOptions.add("chess");
        profilePicOptions.add("dirt_bike");
        profilePicOptions.add("dog");
        profilePicOptions.add("drip");
        profilePicOptions.add("duck");
        profilePicOptions.add("fish");
        profilePicOptions.add("frog");
        profilePicOptions.add("guest");
        profilePicOptions.add("guitar");
        profilePicOptions.add("horses");
        profilePicOptions.add("kick");
        profilePicOptions.add("lift_off");
        profilePicOptions.add("palm_tree");
        profilePicOptions.add("pink_flower");
        profilePicOptions.add("red_flowed");
        profilePicOptions.add("skater");
        profilePicOptions.add("snowflake");

        profilePicSpinner = new Spinner(this);
        profilePicSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profilePicOptions);
        profilePicSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        profilePicSpinner.setAdapter(profilePicSpinnerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ActivitySwap.swapToNextActivity(MainMenuActivity.this, SettingsActivity.class);
            return true;
        }

        if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Swap
                    dialog.dismiss();
                    ActivitySwap.swapToNextActivityNoHistory(MainMenuActivity.this, LoginActivity.class);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        //TODO: This is a debug command! Remove once group navigation is implemented!
        if (id == R.id.action_debug_groups) {
            Intent intent = new Intent(getBaseContext(), GroupMenuActivity.class);
            intent.putExtra("GROUP_ID", 1); //Day Drinkers 4 Life
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = GroupListFragment.newInstance();

            switch (position) {
                case 0:
                    return GroupListFragment.newInstance();
                case 1:
                    return FriendListFragment.newInstance();
                case 2:
                    return ProfileFragment.newInstance();
            }
            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "GROUPS";
                case 1:
                    return "FRIENDS";
                case 2:
                    return "PROFILE";
            }
            return null;
        }
    }



    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    //#####################################################################################################################
    //Listener for the add group floating action button
    public void addGroup(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final Handler HANDLER = new Handler();

        alert.setTitle("Create New Group");

        // Set an EditText view to get user input
        final EditText input = new EditText(v.getContext());
        alert.setView(addGroupView);

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                HuddlOutAPI.getInstance(getApplicationContext()).createGroup(groupNameText.getText().toString(), groupTypeSpinner.getSelectedItem().toString());
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.setCancelable(true);

        alert.show();
    }


    // Listener for edit profile
    public void editProfile(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final AlertDialog alertd = alert.create();

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText firstNameEdit = new EditText(v.getContext());
        firstNameEdit.setHint(User.getInstance(getApplicationContext()).getFirstName());
        layout.addView(firstNameEdit);

        final EditText lastNameEdit = new EditText(v.getContext());
        lastNameEdit.setHint(User.getInstance(getApplicationContext()).getLastName());
        layout.addView(lastNameEdit);

        profilePicSpinner = new Spinner(this);
        profilePicSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, profilePicOptions);
        profilePicSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        profilePicSpinner.setAdapter(profilePicSpinnerAdapter);

        layout.addView(profilePicSpinner);

        final EditText descriptionEdit = new EditText(v.getContext());
        descriptionEdit.setHint(User.getInstance(getApplicationContext()).getDescription());
        layout.addView(descriptionEdit);

        alert.setView(layout);

        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String firstName = "";
                String lastName = "";
                String profilepic = profilePicSpinner.getSelectedItem().toString();
                String description = "";
                int age = User.getInstance(getApplicationContext()).getAge();
                String privacy = User.getInstance(getApplicationContext()).getPrivacy();

                if(firstNameEdit.getText().toString().equalsIgnoreCase("")){
                    firstName = User.getInstance(getApplicationContext()).getFirstName();
                }else{
                    firstName = firstNameEdit.getText().toString();
                }

                if(lastNameEdit.getText().toString().equalsIgnoreCase("")){
                    lastName = User.getInstance(getApplicationContext()).getLastName();
                }else{
                    lastName = lastNameEdit.getText().toString();
                }

                if(descriptionEdit.getText().toString().equalsIgnoreCase("")){
                    description = User.getInstance(getApplicationContext()).getDescription();
                }else{
                    description = descriptionEdit.getText().toString();
                }

                HuddlOutAPI.getInstance(getApplicationContext()).editProfile(firstName, lastName, profilepic, age, description, privacy);
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                alertd.dismiss();
            }
        });

        alert.setCancelable(true);

        alert.show();

    }


    //Listener for the add friend floating action button
    public void addFriend(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

        alert.setTitle("Add New Friend");
        alert.setMessage("Enter friend name:");

        // Set an EditText view to get user input
        final EditText input = new EditText(v.getContext());
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                HuddlOutAPI.getInstance(getApplicationContext()).sendFriendRequest(input.getText().toString());
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.setCancelable(true);

        alert.show();
    }

    public void openGroupMenu(int groupId){
        Intent intent = new Intent(getBaseContext(), GroupMenuActivity.class);
        intent.putExtra("GROUP_ID", groupId);
        startActivity(intent);
    }


}
