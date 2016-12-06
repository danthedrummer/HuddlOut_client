package com.teamh.huddleout;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Paul on 29/11/2016.
 * Updated by Aaron ;) on 05/12/2016.
 */

public class Popup {
    //Creates a toast with the specified message STR under CONTEXT
    public static void show(String str, Context context) {
        CharSequence message = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
