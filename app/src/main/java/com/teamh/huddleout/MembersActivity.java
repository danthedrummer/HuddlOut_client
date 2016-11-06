package com.teamh.huddleout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        this.setTitle("Group Members");

        ImageButton memberButton1 = (ImageButton)findViewById(R.id.memberButton1);
        ImageButton memberButton2 = (ImageButton)findViewById(R.id.memberButton2);
        ImageButton memberButton3 = (ImageButton)findViewById(R.id.memberButton3);
        ImageButton memberButton4 = (ImageButton)findViewById(R.id.memberButton4);

        memberButton1.setOnClickListener(
            new ImageButton.OnClickListener(){
                public void onClick(View v){
                    ActivitySwap.swapToNextActivity(MembersActivity.this, OtherProfileActivity.class);
                }
            }
        );

        memberButton2.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivity(MembersActivity.this, OtherProfileActivity.class);
                    }
                }
        );

        memberButton3.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivity(MembersActivity.this, OtherProfileActivity.class);
                    }
                }
        );

        memberButton4.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        ActivitySwap.swapToNextActivity(MembersActivity.this, OtherProfileActivity.class);
                    }
                }
        );
    }
}
