package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import java.util.Date;

public class viewSchedule extends AppCompatActivity {

    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;
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

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.out.println("Permission denied da thambi");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        System.out.println("Begin");
        eventsTask task = new eventsTask();
        task.execute();
        System.out.println("Done");

    }


    private class eventsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params)
        {
            // Retrieve Calendar first
            Cursor cur = null;
            ContentResolver res = getContentResolver();

            String[] projection={CalendarContract.Events._ID,CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String query = "((" + CalendarContract.Calendars._ID + "=?) AND (" +CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                    + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
            //String[] select = {"pon@volteo.com"};
            Long calID;
            ContentValues values = new ContentValues();
            try
            {
                //might throw an exception if no permission has been provided
                cur = res.query(uri, projection, null, null, null);

                if(cur!=null && cur.moveToFirst())
                {
                    System.out.println("get values");
                    while(cur.moveToNext())
                    {
                        String displayName = null;
                        String accountName = null;
                        String ownerName = null;

                        // Get the field values
                        calID = cur.getLong(0);
                        displayName = cur.getString(1);
                        Date start = new Date(cur.getLong(2));
                        Date end = new Date(cur.getLong(3));
                        System.out.println(calID + " "+ displayName + " " + start + " "+ end);
                    }
                }
                else
                {
                    //no calendar exists. Let us add one.

                }

                cur.close();
            }
            catch(Exception e)
            {
                System.out.println("Exception occured  "+ e);
            }

            return null;
        }

        protected Void onProgressUpdate()
        {

            return null;
        }
        protected Void onPostExecute()
        {
            return null;
        }
    }
}
