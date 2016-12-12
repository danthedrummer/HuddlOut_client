package com.teamh.huddleout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Member;
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
    private TextView memberInfo;
    private Button kickButton;
    private Button viewButton;

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
        loadLayout = (RelativeLayout) findViewById(R.id.loadLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        memberList = (ListView) findViewById(R.id.memberListView);
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
        memberInfo = (TextView) findViewById(R.id.memberInfoTextView);
        kickButton = (Button) findViewById(R.id.kickButton);
        viewButton = (Button) findViewById(R.id.viewButton);

        //Kick Button action listener
        kickButton.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){

                }
            }
        );

        //View Button action listener
        viewButton.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){
                    try {
                        Intent intent = new Intent(MembersActivity.this, ProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("friendId", selectedMember.getInt("profile_id")); //Group ID
                        b.putBoolean("searchOnline", true);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        );

        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                selectMember(id);
            }
        });

        //Set Group ID
        if (groupId == -1)
            groupId = getIntent().getExtras().getInt("group_id");

        getGroupMembers();
    }

    //Selects the member to perform actions on
    private void selectMember(int index) {
        //Select Member
        try {
            selectedMember = groupMembers.getJSONObject(index);
            memberInfo.setText(selectedMember.getString("first_name") + " " + selectedMember.getString("last_name"));

            //Reset Buttons
            viewButton.setEnabled(false);
            kickButton.setEnabled(false);

            //View Profile Button
            if(selectedMember.getInt("profile_id") != thisMember.getInt("profile_id")) {
                viewButton.setEnabled(true);
            }

            //Kick Member Button
            if(selectedMember.getInt("profile_id") != thisMember.getInt("profile_id") && !selectedMember.getString("group_role").equals("ADMIN") && !selectedMember.getString("group_role").equals("KICKED")) {
                kickButton.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
