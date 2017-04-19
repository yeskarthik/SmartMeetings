package mc.asu.edu.smartmeetings;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Sairaj on 4/17/2017.
 */

public class pollService {
    Context context;
    private PendingIntent pendingIntent;
    SharedPreferences preferences;
    public pollService(Context context)
    {
        this.context = context;
        Intent intent = new Intent(this.context, mainService.class);

    }

    public String getUsername()
    {

        //endingIntent = PendingIntent.getService(context, 0, intent, 0);
        System.out.println("getting username");
        preferences = context.getSharedPreferences("SmartMeetings",Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");

        return name;
    }
}
