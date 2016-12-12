package com.teamh.huddleout;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MembersActivity extends AppCompatActivity {

    //TAG
    private static final String TAG = "DevMsg";

    //Application context
    private Context context;

    //HuddlOut API
    final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(context);

    //Array List of Members
    private ArrayList<String> memberNameList;
    private ArrayList<String> memberInfoList;

    //Group Variables
    private int groupId = -1;
    private JSONArray groupMembers;
    private JSONObject thisMember;
    private JSONObject selectedMember;

    //UI Elements
    private ArrayAdapter<String> memberAdapter;
    private RelativeLayout loadLayout;
    private RelativeLayout mainLayout;
    private ListView memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        this.setTitle("Group Members");

        //Set context
        context = getApplicationContext();

        //ArrayList of Members
        memberNameList = new ArrayList<>();
        memberInfoList = new ArrayList<>();

        //Get UI Elements
        loadLayout = (RelativeLayout)findViewById(R.id.loadLayout);
        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        memberList = (ListView)findViewById(R.id.memberListView);
        memberAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, memberNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(memberNameList.get(position));
                text2.setText(memberInfoList.get(position));
                return view;
            }
        };

        memberList.setAdapter(memberAdapter);
        //TODO: Set onClick for memberAdapter

        //Set Group ID
        if(groupId == -1)
            groupId = getIntent().getExtras().getInt("group_id");

        getGroupMembers();
    }

    //INIT CODE
    private void getGroupMembers()
    {
        loadLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.INVISIBLE);

        hAPI.getGroupMembers(groupId, this);
    }

    public void getGroupMembersCallback(String response)
    {
        try {
            groupMembers = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get this member
        //TODO: ADD THIS MEMBER FIRST
        for(int i = 0; i < groupMembers.length(); i++) {
            try {
                JSONObject member = groupMembers.getJSONObject(i);
                String name = member.getString("first_name") + " " + member.getString("last_name");
                memberNameList.add(name);
                memberInfoList.add(member.getString("group_role"));

                if(member.getInt("profile_id") == User.getInstance(getApplicationContext()).getProfileID()) {
                    thisMember = member;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        memberList.setAdapter(memberAdapter);

        loadLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }
    //END OF INIT CODE
}
