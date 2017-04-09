package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sairaj on 4/6/2017.
 */

public class gpsService {
    LocationManager locationManager;
    private Context context;
    private PendingIntent pendingIntent;
    Location location;
    public gpsService(Context context)
    {
        this.context = context;
        Intent intent = new Intent(this.context, gpsLocation.class);
        pendingIntent = PendingIntent.getService(this.context, 0, intent, 0);
    }

    public Location startService()
    {
        System.out.println("Started Service");
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps)
        {
            //show some dialog that says gps disabled...
            //we don't use network provider here. can be added if needed...
            System.out.println("GPS Disabled");
        }
        else
        {
            Intent intent = new Intent(context, gpsLocation.class);
            pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    // for sample, I have set it to 1 minute. When deploying the application change it to 30 minutes.
                    // distance is 1000 meters. The value is updated only if the distance moved is greater than 1000 meters.
                    // for testing purposes it has been set to 1 now.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1*1000*60,1, pendingIntent);
                    if(location == null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("We don't have permission to obtain GPS from user " + e);
                }
            }


        }

        return location;
    }
}
