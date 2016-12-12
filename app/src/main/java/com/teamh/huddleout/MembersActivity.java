package com.teamh.huddleout;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;

public class MembersActivity extends AppCompatActivity {

    //Application context
    private Context context;

    //HuddlOut API
    final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(context);

    //Group Variables
    private int groupId = -1;
    private ArrayList<JSONObject> groupMembers;

    //UI Elements
    private RelativeLayout loadLayout;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        this.setTitle("Group Members");

        //Set context
        context = getApplicationContext();

        //Get UI Elements
        loadLayout = (RelativeLayout)findViewById(R.id.loadLayout);
        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);

        //Set Group ID
        if(groupId == -1)
            groupId = getIntent().getExtras().getInt("group_id");

        getGroupMembers();
    }

    private void getGroupMembers()
    {
        loadLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.INVISIBLE);

        hAPI.getGroupMembers(groupId, this);
    }

    public void getGroupMembersCallback(String response)
    {
        Popup.show(response, context);

        loadLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }
}
