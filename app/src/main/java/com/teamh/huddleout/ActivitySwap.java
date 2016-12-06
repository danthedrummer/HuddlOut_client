 /**
 * Created by Dan on 28/10/2016.
 */
package com.teamh.huddleout;
import android.app.Activity;
import android.content.Intent;

public class ActivitySwap {


    //Swaps between the currently active Activity and the target activity class
    public static void swapToNextActivity(Activity current, Class target){
        current.startActivity(new Intent(current, target));
    }

    public static void swapToNextActivityNoHistory(Activity current, Class target) {
        Intent i = new Intent(current, target);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        current.startActivity(i);
    }
}
