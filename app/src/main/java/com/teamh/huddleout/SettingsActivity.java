package com.teamh.huddleout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.setTitle("HuddlOut Settings");

        final Switch showNotifications = (Switch)findViewById(R.id.showNotificationsSwitch);
        final Switch playNotifications = (Switch)findViewById(R.id.notificationsSoundSwitch);
        if(!showNotifications.isChecked()){
            playNotifications.setChecked(false);
        }

        showNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton switchView, boolean isChecked){
                if(isChecked){

                }else{
                    playNotifications.setChecked(false);
                }
            }
        });

        playNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton switchView, boolean isChecked){
                //If notifications are turned off then sounds are disabled by default
                if(showNotifications.isChecked()){

                }else{
                    playNotifications.setChecked(false);
                }
            }
        });

        //User Privacy Spinner
        Spinner userPrivacy = (Spinner)findViewById(R.id.userPrivacySpinner);
        final String[] privacyPreferences = new String[]{"Public(Anyone can view profile)", "Friends(Only friends can view profile)", "Private(Nobody can view profile)"};
        ArrayAdapter<String> userPrivacyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, privacyPreferences);
        userPrivacy.setAdapter(userPrivacyAdapter);

        //Theme Spinner
        Spinner themePicker = (Spinner)findViewById(R.id.themeSpinner);
        final String[] themes = new String[]{"Deep Purple", "Purple", "Light Green", "Lime"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, themes);
        themePicker.setAdapter(themeAdapter);

        themePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean initialDisplay = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int themeIndex, long id) {
                if(initialDisplay){
                    //Listener fires on creation of the spinner
                    //Ignore first event
                   initialDisplay = false;
                }
                else{
                    switch(themeIndex){
                        case 0:
                            setTheme(R.style.DeepPurpleTheme);
                            break;
                        case 1:
                            setTheme(R.style.PurpleTheme);
                            break;
                        case 2:
                            setTheme(R.style.LightGreenTheme);
                            break;
                        case 3:
                            setTheme(R.style.LimeTheme);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
