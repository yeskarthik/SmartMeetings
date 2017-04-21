package mc.asu.edu.smartmeetings;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
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

            gpsService mService = new gpsService(getApplicationContext());

            sendLocation(getApplicationContext(), mService.startService());

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    void getWeatherUpdates(Context context) throws IOException
    {
        OkHttpClient httpClient = new OkHttpClient();
        String url="http://api.openweathermap.org/data/2.5/weather?"+"lat="+latitude+"&lon="+longitude+"&appid=a785c4493ca77c844a469d079ef76754";
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        String body = response.body().string();
        System.out.println("Received response: "+body);

        String weatherMain = "";
        String weatherTemp = "";
        try {
            JSONObject jsonObject = new JSONObject(body);
            weatherMain = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherTemp = jsonObject.getJSONObject("main").getString("temp");
            System.out.println("weather: " + weatherMain + " " + weatherTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void sendLocation(Context context, Location location) throws MalformedURLException
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
        Utils.PostRequester postRequest = new Utils.PostRequester(this.getApplicationContext(), "location", null);
        postRequest.execute(params);
    }



}
