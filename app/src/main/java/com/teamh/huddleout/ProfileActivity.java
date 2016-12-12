package com.teamh.huddleout;

import android.app.ProgressDialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/*
 * ProfileActivity.java
 * By Aaron Meaney
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "DevMsg";
    private int friendId = -1; //ID of current friend
    private User currentUser; //Current User
    private ArrayList<JSONObject> friends; //User's friends
    private Button removeBtn;

    private Context context;

    //HuddlOut API
    final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(context);

    //Friend Data
    private int friendProfile = -1;
    private String friendRelationshipType = "";
    private String friendFirstName = "";
    private String friendLastName = "";
    private String friendDesc = "";
    private String friendProfilePicture = "";
    private int friendProfilePictureId = -1;

    //Loading progress
    private ProgressDialog loadingProgress;

    //UI Stuff
    private TextView name;
    private TextView about;
    private ImageView profileImage;

    //Friend location
    private boolean searchOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = getApplicationContext();

        //Show loading dialog
        loadingProgress = new ProgressDialog(ProfileActivity.this);
        loadingProgress.setTitle("Loading");
        loadingProgress.setMessage("Please wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        //Get UI elements
        name = (TextView)findViewById(R.id.nameTextView);                //Name
        about = (TextView)findViewById(R.id.aboutContentTextView);       //About
        profileImage = (ImageView)findViewById(R.id.profileImageView);  //Profile Pic

        //Get the current user singleton
        currentUser = User.getInstance(this.getApplicationContext());

        //Get the current user's friends
        friends = currentUser.getFriends();

        //Get Friend ID param
        Bundle b = getIntent().getExtras();
        friendId = -1;
        if(b != null)
            friendId = b.getInt("friendId");

        if(b.getBoolean("searchOnline"))
            searchOnline = b.getBoolean("searchOnline");

        if(!searchOnline) {
            //Update friend info
            try {
                JSONObject friend = friends.get(friendId);
                Log.i(TAG, "JSON OBJ: " + friend.toString(1));
                friendProfile = friend.getInt("profile");
                friendRelationshipType = friend.getString("relationship_type");
                friendFirstName = friend.getString("first_name");
                friendLastName = friend.getString("last_name");
                friendDesc = friend.getString("desc");
                friendProfilePicture = friend.getString("profile_picture");
                friendProfilePictureId = getApplicationContext().getResources().getIdentifier(friendProfilePicture, "drawable", "com.teamh.huddleout");
                Log.i(TAG, "ID: " + friendProfilePictureId);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            removeBtn = (Button)findViewById(R.id.removeBtn);
            if(friendRelationshipType.equals("FRIEND") || friendRelationshipType.equals("BEST FRIEND")) {
                // Remove friend button
                removeBtn.setOnClickListener( new Button.OnClickListener(){
                    public void onClick(View v){
                        HuddlOutAPI.getInstance(getApplicationContext()).deleteFriend(friendProfile);
                    }
                });
            } else if (friendRelationshipType.equals("Invite")) {
                // Invite Pending
                removeBtn.setText("Invite Pending");
                removeBtn.setEnabled(false);
            } else if (friendRelationshipType.equals("Blocked")) {
                // Remove friend button
                removeBtn.setText("BLOCKED...?");
                removeBtn.setEnabled(false);
                removeBtn.setOnClickListener( new Button.OnClickListener(){
                    public void onClick(View v){
                        HuddlOutAPI.getInstance(getApplicationContext()).deleteFriend(friendProfile);
                    }
                });
            }

            //Update UI elements
            name.setText(friendFirstName + " " + friendLastName);
            about.setText(friendDesc);
            profileImage.setImageResource(friendProfilePictureId);

            //Stop loading
            loadingProgress.hide();
        } else {
            hAPI.getProfile(friendId, this);
        }
    }

    public void getProfileCallback(String response) {
        Log.i(TAG, "RES: " + response);

        try {
            JSONObject friend = new JSONObject(response);
            Log.i(TAG, "JSON OBJ: " + friend.toString(1));
            String friendPrivacy = friend.getString("privacy");
            friendProfile = friend.getInt("profile_id");
            friendFirstName = friend.getString("first_name");
            friendLastName = friend.getString("last_name");
            if(friendPrivacy.equals("PRIVATE")) {
                friendDesc = "[PRIVATE DESCRIPTION]";
                friendProfilePicture = "chess";
            } else {
                friendDesc = friend.getString("description");
                friendProfilePicture = friend.getString("profile_picture");
            }

            friendProfilePictureId = getApplicationContext().getResources().getIdentifier(friendProfilePicture, "drawable", "com.teamh.huddleout");

            Log.i(TAG, "ID: " + friendProfilePictureId);

            removeBtn = (Button)findViewById(R.id.removeBtn);
            if(friendRelationshipType.equals("Friend") || friendRelationshipType.equals("Best Friend")) {
                // Remove friend button
                removeBtn.setOnClickListener( new Button.OnClickListener(){
                    public void onClick(View v){
                        HuddlOutAPI.getInstance(getApplicationContext()).deleteFriend(friendProfile);
                    }
                });
            } else  {
                // Remove friend button
                removeBtn.setVisibility(View.GONE);
                removeBtn.setEnabled(false);
            }

            //Update UI elements
            name.setText(friendFirstName + " " + friendLastName);
            about.setText(friendDesc);
            profileImage.setImageResource(friendProfilePictureId);

            //Stop loading
            loadingProgress.hide();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
