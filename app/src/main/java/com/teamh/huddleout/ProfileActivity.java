package com.teamh.huddleout;

import android.app.ProgressDialog;
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

    //Friend Data
    private int friendProfile = -1;
    private String friendRelationshipType = "";
    private String friendFirstName = "";
    private String friendLastName = "";
    private String friendDesc = "";
    private String friendProfilePicture = "";
    private int friendProfilePictureId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Show loading dialog
        final ProgressDialog loadingProgress = new ProgressDialog(ProfileActivity.this);
        loadingProgress.setTitle("Loading");
        loadingProgress.setMessage("Please wait...");
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        //Get UI elements
        final TextView name = (TextView)findViewById(R.id.nameTextView);                //Name
        final TextView about = (TextView)findViewById(R.id.aboutContentTextView);       //About
        final ImageView profileImage = (ImageView)findViewById(R.id.profileImageView);  //Profile Pic

        //Get the current user singleton
        currentUser = User.getInstance(this.getApplicationContext());

        //Get the current user's friends
        friends = currentUser.getFriends();

        //Get Friend ID param
        Bundle b = getIntent().getExtras();
        friendId = -1;
        if(b != null)
            friendId = b.getInt("friendId");

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

        // Remove friend button
        removeBtn = (Button)findViewById(R.id.removeBtn);
        removeBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){
                Log.i(TAG, "friend to be deleted: " + friendProfile);
                HuddlOutAPI.getInstance(getApplicationContext()).deleteFriend(friendProfile);
            }
        });

        //Update UI elements
        name.setText(friendFirstName + " " + friendLastName);
        about.setText(friendDesc);
        profileImage.setImageResource(friendProfilePictureId);

        //Stop loading
        loadingProgress.hide();
    }
}
