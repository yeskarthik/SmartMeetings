package mc.asu.edu.smartmeetings;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        try
        {
            getWeatherUpdates(getApplicationContext());
            checkPolls(getApplicationContext());
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        gpsService mService = new gpsService(getApplicationContext());
        sendLocation(getApplicationContext(), mService.startService());


    }

    void sendLocation(Context context, Location location)
    {

        long timestamp;

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        timestamp = location.getTime(); // this is going to give us time in milliseconds.
        System.out.println(timestamp + " latitude " + latitude + " longitude " + longitude );
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "something");
        params.put("latitude", Double.toString(location.getLatitude()));
        params.put("longitude", Double.toString(location.getLongitude()));
        Utils.PostRequester postRequest = new Utils.PostRequester(this.getApplicationContext(), "location");
        postRequest.execute(params);
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

    void checkPolls(Context context) throws MalformedURLException
    {
        OkHttpClient httpClient = new OkHttpClient();
        String url = "";
        Utils.GetRequester getRequester = new Utils.GetRequester(
                this.getApplicationContext()
                ,"polls",
                new Utils.GetRequester.TaskListener() {
                    @Override
                    public void onFinished(ArrayList<HashMap<String, String>> result, Context context) {
                        HashMap<String, String> res = result.get(0);
                        System.out.println("status");
                        System.out.println(res.get("status"));
                        if(res.get("status").equals("true")) {
                            String[] bundle = {res.get("question"),res.get("a"),res.get("b"),res.get("c"), res.get("d")};
                            Intent in = new Intent(context, Home.class); //change to Poll.class
                            in.putExtra("bundle",bundle);
                            startActivity(in);
                        }
                    }
                });
    }

}
