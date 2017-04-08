package mc.asu.edu.smartmeetings;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;

import java.io.IOException;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Sairaj on 4/5/2017.
 */

public class gpsLocation extends IntentService{
    double latitude;
    double longitude;
    Location location;
public gpsLocation()
{
    super("LocationService");
}
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        //sendLocation to our AWS server over network using sendLocation function.
        gpsService mService = new gpsService(getApplicationContext());
        sendLocation(getApplicationContext(), mService.startService());
        try
        {
            getWeatherUpdates(getApplicationContext());
        }
        catch(IOException e)
        {
            System.out.println(e);
        }

    }

    void sendLocation(Context context, Location location)
    {

        long timestamp;

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        timestamp = location.getTime(); // this is going to give us time in milliseconds.

        System.out.println(timestamp + " latitude " + latitude + " longitude " + longitude );
    }

    void getWeatherUpdates(Context context) throws IOException
    {
        OkHttpClient httpClient = new OkHttpClient();
        String url="http://api.openweathermap.org/data/2.5/weather?"+"lat="+latitude+"&lon="+longitude+"&appid=a785c4493ca77c844a469d079ef76754";
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        String body = response.body().string();
        System.out.println("Received response: "+body);

    }

}
