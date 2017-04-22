package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;
    gpsService service;
    pollService poll_service;
    SharedPreferences preferences;
    String username;
    ImageView newImage;

    int mId = 5;
    Context context;
    private PendingIntent pendingIntent;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String[] temp = new String[2];
    Location loc;
    String weather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // REQUEST ALL PERMISSIONS HERE

        preferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        context = this.getApplicationContext();
        this.username = name;
        System.out.println("username " + name);
        weather = "";
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        System.out.println("Seeking Permissions");
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("WRITE Calendar permission denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else
        {
            System.out.println("Permissions were granted, starting service");
            try {
                startPolling();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Intent intent = new Intent(Intent.ACTION_SYNC, null, this, gpsLocation.class);
            //startService(intent);

            buildGoogleApiClient();
            System.out.println("Connecting google api");
            mGoogleApiClient.connect();
            setTemperature();

        }

        TextView view = (TextView) findViewById(R.id.username);
        view.append("Welcome, "+name);

        NotificationCompat.Builder appBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("New Sign-In")
                .setContentText("Hi you have signed in successfully :)");
        Intent resultIntent = new Intent(this, NoteTakingActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NoteTakingActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        appBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

        mNotificationManager.notify(mId, appBuilder.build());
        //(BEGIN) Camera code begin
        Button picButton = (Button) findViewById(R.id.launch_camera_button);
        newImage = (ImageView) findViewById(R.id.imageView);

        //if user's phone has a camera
        if(!hasCamera())
            picButton.setEnabled(false);
        //(END)


        batteryChecker mBatteryLevelReceiver = new batteryChecker();
        registerReceiver(mBatteryLevelReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        Log.i("mystuff", "I started...");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.ACCESS_NOTIFICATION_POLICY}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    System.out.println("Permission granted da thambi");

                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    setTemperature();
                    //Intent intent = new Intent(Intent.ACTION_SYNC, null, this, gpsLocation.class);
                    //startService(intent);
                    try {
                        startPolling();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    //If permission has been denied, create a pop-up to quit the app.
                    System.out.println("Permission denied da thambi");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @TargetApi(23)
    public void setTemperature()
    {
        System.out.println("I got called");
        final TextView view = (TextView) findViewById(R.id.weather_box);
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        loc= null;
        try
        {
            System.out.println("I am getting location now");
            loc=mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc == null)
            {
                Criteria criteria = new Criteria();
                String bestprovider = String.valueOf(mlocManager.getBestProvider(criteria, true)).toString();
                mlocManager.requestLocationUpdates(bestprovider, 1000, 0, this);
            }

        }
        catch(SecurityException e)
        {
            System.out.println(e);
        }
        try
        {
            if(loc != null)
            {
                locationM locate = new locationM();
                locate.execute(loc);

                System.out.println("Weather and temperature" +" "+ loc.getLatitude());

                if (loc.getLatitude() > 33.42 && loc.getLatitude() < 33.44 && loc.getLongitude() <= -111.8 && loc.getLatitude() > -111.10)
                {
                    System.out.println("I need the IF condition");
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.setInterruptionFilter(mNotificationManager.INTERRUPTION_FILTER_NONE);
                }
                //view.setText("Weather : "+weather);
            }


        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void startPolling () throws Exception
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Registered with token "+token);
        System.out.println("username : " + username);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token_id", token);
        params.put("username", this.username);
        Utils.PostRequester postRequester = new Utils.PostRequester(this.getApplicationContext(), "token", null);
        postRequester.execute(params);

        try
        {
            PusherAndroid pusher = new PusherAndroid("bdf3e36a58647e366f38");
            PushNotificationRegistration nativePusher = pusher.nativePusher();
            nativePusher.registerFCM(this, new PushNotificationRegistrationListener() {
                @Override
                public void onSuccessfulRegistration() {
                    System.out.println("Successfully registered with Pusher server");
                }

                @Override
                public void onFailedRegistration(int statusCode, String response) {
                    System.out.println(
                            "Registration with Pusher failure " + statusCode +
                                    " " + response
                    );
                }
            });
            nativePusher.subscribe("polls", new InterestSubscriptionChangeListener() {
                @Override
                public void onSubscriptionChangeSucceeded() {
                    System.out.println("Success! Ready to receive polls");
                }

                @Override
                public void onSubscriptionChangeFailed(int statusCode, String response) {
                    System.out.println("poll failure " + statusCode + " with" + response);
                }
            });
            nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
                @Override
                public void onMessageReceived(RemoteMessage remoteMessage) {
                    // do something magical
                    if(remoteMessage != null)
                    {
                        System.out.println("Received from server "+ remoteMessage.getNotification().getBody() + " "+remoteMessage.getNotification().getTitle());
                        Map<String, String> data = remoteMessage.getData();
                        Intent submitPoll = new Intent(context, SubmitPoll.class);
                        Bundle extras = new Bundle();
                        extras.putString("question", data.get("question"));
                        extras.putString("option1", data.get("option1"));
                        extras.putString("option2", data.get("option2"));
                        extras.putString("option3", data.get("option3"));
                        extras.putString("option4", data.get("option4"));
                        extras.putString("id", data.get("poll_id"));
                        submitPoll.putExtras(extras);
                        startActivity(submitPoll);
                    }
                }
            });


            PushNotificationRegistration nativePusher2 = pusher.nativePusher();
            nativePusher2.registerFCM(this, new PushNotificationRegistrationListener() {
                @Override
                public void onSuccessfulRegistration() {
                    System.out.println("Successfully registered with Pusher server");
                }

                @Override
                public void onFailedRegistration(int statusCode, String response) {
                    System.out.println(
                            "Registration with Pusher failure " + statusCode +
                                    " " + response
                    );
                }
            });
            nativePusher2.subscribe("quickquestions", new InterestSubscriptionChangeListener() {
                @Override
                public void onSubscriptionChangeSucceeded() {
                    System.out.println("Success! Ready to receive polls");
                }

                @Override
                public void onSubscriptionChangeFailed(int statusCode, String response) {
                    System.out.println("poll failure " + statusCode + " with" + response);
                }
            });
            nativePusher2.setFCMListener(new FCMPushNotificationReceivedListener() {
                @Override
                public void onMessageReceived(RemoteMessage remoteMessage) {
                    // do something magical
                    if (remoteMessage != null) {
                        System.out.println("Received from server " + remoteMessage.getNotification().getBody() + " " + remoteMessage.getNotification().getTitle());
                        Map<String, String> data = remoteMessage.getData();
                        Intent submitQuickQuestion = new Intent(context, SubmitQuickQuestion.class);
                        Bundle extras = new Bundle();
                        extras.putString("question", data.get("question"));
                        extras.putString("id", data.get("question_id"));
                        submitQuickQuestion.putExtras(extras);
                        startActivity(submitQuickQuestion);
                    }
                }
            });
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_meeting:
                //Intent in = new Intent(this,)
                Intent inc = new Intent(this, DisplayUsers.class);
                startActivity(inc);
                return true;
            case R.id.camera:
                //showHelp();
                return true;
            case R.id.note_taking:
                Intent in = new Intent(this, NoteTakingActivity.class);
                startActivity(in);
                return true;
            case R.id.polls:
                Intent ink = new Intent(this, ListPolls.class);
                startActivity(ink);
                return true;
            case R.id.quick_questions:
                Intent inq = new Intent(this, ListQuickQuestions.class);
                startActivity(inq);
                return true;
            case R.id.view_meetings:
                Intent inte = new Intent(this, ViewMeetings.class);
                startActivity(inte);
                return true;
            case R.id.list_meet_locations:
                Intent ino = new Intent(this, ListMeetLocationsInput.class);
                startActivity(ino);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void startNavigation(View view)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=33.424564, -111.928001");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    //(BEGIN) All the picture capture code is here
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int REQUEST_IMAGE_CAPTURE = 1;
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_IMAGE_CAPTURE = 1;
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //image take, retrieve photo
            Bundle extr = data.getExtras();
            Bitmap photo = (Bitmap) extr.get("data");
            newImage.setImageBitmap(photo);
            //Do whatever we want with the photo: variable name: photo << its a bitmap

        }
    }
    //(END)

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        System.out.println("connected to google api");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000*60);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Intent mRequestLocationUpdatesIntent = new Intent(this, gpsLocation.class);

        pendingIntent = PendingIntent.getService(getApplicationContext(), 0,
                mRequestLocationUpdatesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try
        {
            PendingResult pendingResult =  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
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
        System.out.println("Connection failure");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed ");
        loc = location;

        setTemperature();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class locationM extends AsyncTask<Location, Void , String>
    {

        @Override
        protected String doInBackground(Location... params) {

            String latitude = "" + loc.getLatitude();
            String longitude = ""+ loc.getLongitude();

            OkHttpClient httpClient = new OkHttpClient();
            String url="http://api.openweathermap.org/data/2.5/weather?"+"lat="+latitude+"&lon="+longitude+"&appid=a785c4493ca77c844a469d079ef76754";
            Request request = new Request.Builder().url(url).build();
            String body="";
            try
            {
                Response response = httpClient.newCall(request).execute();
                body = response.body().string();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            System.out.println("Received response: "+body);

            String weatherMain = "";
            String weatherTemp = "";
            try {
                JSONObject jsonObject = new JSONObject(body);
                weatherMain = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                weatherTemp = jsonObject.getJSONObject("main").getString("temp");
                temp[0] = weatherMain;
                temp[1] = weatherTemp;
                System.out.println("Weather1: " + weatherMain + " " + weatherTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weatherMain + " " +weatherTemp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            weather = s;
            System.out.println("Weather2 "+weather);
            String[] weatherArr = weather.split(" ");
            Double kelvin = Double.parseDouble(weatherArr[weatherArr.length - 1]);
            Double fahrenheit = (kelvin * 9 / 5.0) - 459.67;
            String weatherString = Arrays.toString(Arrays.copyOf(weatherArr, weatherArr.length-1));

            TextView view = (TextView) findViewById(R.id.weather_box);
            view.setText("Weather: " + weatherString.substring(1, weatherString.length()-1) + " " + String.format( "%.2f", fahrenheit )+"F");

        }
    }
}
