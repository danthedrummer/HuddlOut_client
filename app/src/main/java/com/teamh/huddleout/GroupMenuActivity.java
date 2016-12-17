package com.teamh.huddleout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMenuActivity extends AppCompatActivity implements GroupWelcomeFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener, VotingFragment.OnFragmentInteractionListener, OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private PlaceDetectionApi mPlaceDetectionApi;
    private GoogleMap myMap;
    private MapView mapView;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = "DevMsg";

    //Group info
    private int groupId; //Group ID
    private JSONObject thisGroup;//

    private final int PLACE_PICKER_REQUEST = 1;
    private final String[] requestLocation = {Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupId = getIntent().getIntExtra("GROUP_ID", 0);
        User currentUser = User.getInstance(getApplicationContext());
        for (JSONObject group : currentUser.getGroupsList()) {
            try {
                if (group.getInt("group_id") == groupId) {
                    thisGroup = group;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Set the text at the top of the activity - Populate it with the generated group name
        try {
            getSupportActionBar().setTitle(thisGroup.getString("group_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Request permission to use location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(requestLocation, 2);
        }
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Swap
                    dialog.dismiss();
                    ActivitySwap.swapToNextActivityNoHistory(GroupMenuActivity.this, LoginActivity.class);
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
        return super.onOptionsItemSelected(item);
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
                    return GroupWelcomeFragment.newInstance();
                case 1:
                    return MapFragment.newInstance();
                case 2:
                    return VotingFragment.newInstance();
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
                    return "WELCOME";
                case 1:
                    return "MAP";
                case 2:
                    return "VOTING";
            }
            return null;
        }
    }

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    /*public void onShowLocationsClicked(View v) {
        //If location request was allowed, then launch the map
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            //callPlaceDetectionApi();

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesNotAvailableException e1) {

            } catch (GooglePlayServicesRepairableException e2) {

            }
        }*
        Log.i("DevMsg", "Grabbing from XML");
        Resources res = getResources();
        String locationsString = "";
        TextView locationsTextView = (TextView)findViewById(R.id.locationTextView);
        String[] locationArray = res.getStringArray(R.array.FastFood);
        for(int j = 0; j < 4; j++) {
            switch (j){
                case 0:
                    locationArray = res.getStringArray(R.array.Bars);
                    break;
                case 1:
                    locationArray = res.getStringArray(R.array.FastFood);
                    break;
                case 2:
                    locationArray = res.getStringArray(R.array.Restaurants);
                    break;
                case 3:
                    locationArray = res.getStringArray(R.array.Cafes);
                    break;
                default:
                    locationArray = new String[0];
                    break;
            }
            for (int i = 0; i < locationArray.length; i += 3) {
                locationsString = locationsString + "Name: " + locationArray[i]
                        + "\nLat: " + locationArray[i + 1]
                        + "  Lon: " + locationArray[i + 2] + "\n\n";
            }
        }
        locationsTextView.setText(locationsString);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.myMap = googleMap;
        Log.i("DevMsg", "Reached map ready in activity");
    }

    private void setUpMap(GoogleMap map) {
        myMap = map;
        Log.i("DevMsg", "Reached map setup");
    }

}

    //Prevent back nav
//    @Override
//    public void onBackPressed() {
//    }
