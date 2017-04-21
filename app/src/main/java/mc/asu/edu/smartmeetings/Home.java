package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;
    gpsService service;
    pollService poll_service;
    SharedPreferences preferences;
    String username;
    int mId = 5;
    Context context;
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

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        System.out.println("Seeking Permissions");
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("WRITE Calendar permission denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else
        {
            System.out.println("Permissions were granted, starting service");
            try {
                startPolling();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //service = new gpsService(this);
           // service.startService();

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

                    //service = new gpsService(this);
                    //service.startService();
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
                return true;
            case R.id.list_meet_locations:
                Intent inc = new Intent(this, ListMeetLocationsInput.class);
                startActivity(inc);
                return true;
            case R.id.view_meetings:
                Intent inte = new Intent(this, ViewMeetings.class);
                startActivity(inte);
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
}
