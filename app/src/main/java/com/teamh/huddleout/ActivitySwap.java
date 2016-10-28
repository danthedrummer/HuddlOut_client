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

}
