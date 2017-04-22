package mc.asu.edu.smartmeetings;

/**
 * Created by ridges on 4/17/17.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.widget.Toast;

public class batteryChecker extends BroadcastReceiver {
    int batteryLevel;
    boolean batteryNotificationSent;
    @Override

    public void onReceive(Context context, Intent intent) {

        float batteryPercentage = intent.getIntExtra("level", 0);

        if(batteryPercentage <= 30){ //if lower than thirty send a toast, only shows once

            if (batteryNotificationSent == false){
                //action payload if it drops
                Toast toast = Toast.makeText(context, "Battery is below 30%", Toast.LENGTH_SHORT);
                toast.show();



                batteryNotificationSent = true; //so we dont keep getting a notifcation
            }
        }
        else{
            batteryNotificationSent = false;
        }
    }
}