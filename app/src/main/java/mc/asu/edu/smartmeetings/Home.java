package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;
    gpsService service;
    pollService poll_service;
    SharedPreferences preferences;
    String username;
    ImageView newImage;

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
            //service = new gpsService(this, findViewById(R.id.weather_box));
            //service.startService();
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

                    //service = new gpsService(this, findViewById(R.id.weather_box));
                    //service.startService();

                    Intent intent = new Intent(Intent.ACTION_SYNC, null, this, gpsLocation.class);
                    startService(intent);
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
                    if (remoteMessage != null) {
                        System.out.println("Received from server " + remoteMessage.getNotification().getBody() + " " + remoteMessage.getNotification().getTitle());
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
}
