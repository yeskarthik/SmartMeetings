package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sairaj on 4/6/2017.
 */

public class gpsService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    LocationManager locationManager;
    private Context context;
    private PendingIntent pendingIntent;
        LocationRequest mLocationRequest;
        GoogleApiClient mGoogleApiClient;
    Location location;
    TextView weather;
    public gpsService(Context context)
    {
        this.context = context;
        Intent intent = new Intent(this.context, gpsLocation.class);
        pendingIntent = PendingIntent.getService(this.context, 0, intent, 0);
        //weather = (TextView) viewById;
    }

    public  void startService()
    {

        /*System.out.println("Started Service");
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


        }*/

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        //return location;
    }
    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Intent mRequestLocationUpdatesIntent = new Intent(context, gpsLocation.class);
        pendingIntent = PendingIntent.getService(context, 0,
                mRequestLocationUpdatesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    pendingIntent);

        }
        catch(SecurityException e)
        {
            System.out.println(e);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed");
    }
}
