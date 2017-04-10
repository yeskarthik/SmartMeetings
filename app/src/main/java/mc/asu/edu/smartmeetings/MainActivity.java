package mc.asu.edu.smartmeetings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import static android.R.attr.name;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "mc.asu.edu.smartmeetings.passMessage";
    public String[] passMessage = null;
    public static final String API_URL = "http://smartmeetings-server.jgscuqju3c.us-west-2.elasticbeanstalk.com/";
    EditText name, username, password, email, phone;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;
    gpsService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // REQUEST ALL PERMISSIONS HERE

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
            service = new gpsService(this);
            service.startService();
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

                    service = new gpsService(this);
                    service.startService();
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
    protected void create_user(View view) throws MalformedURLException, IOException {

        name =(EditText)findViewById(R.id.editText4);
        username =(EditText)findViewById(R.id.editText5);
        password =(EditText)findViewById(R.id.editText);
        email =(EditText)findViewById(R.id.editText3);
        phone =(EditText)findViewById(R.id.editText2);
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name.getText().toString());
        params.put("username", username.getText().toString());
        params.put("email", email.getText().toString());
        params.put("phone", phone.getText().toString());

        Utils.PostRequester request = new Utils.PostRequester(this.getApplicationContext(), "user");
        //PostRequester request = new PostRequester("user");
        request.execute(params);

    }

    protected void get_users(View view) throws MalformedURLException, IOException {
        GetRequester request = new GetRequester(this, "users");
        System.out.println("CHOWMEIN");
        String data = request.execute().toString();
        System.out.println(data);
    }

//    private class PostRequester extends AsyncTask<Map, Void, Void> {
//
//        URL url;
//        PostRequester(String path)  {
//            try {
//                this.url = new URL("API_URL" + path);
//            } catch(MalformedURLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Map... params) {
//
//            Map<String, String> param = params[0];
//
//            Uri.Builder builder = new Uri.Builder();
//            for (Map.Entry<String, String> entry : param.entrySet()) {
//                builder.appendQueryParameter(entry.getKey(), entry.getValue());
//            }
//
//            String query = builder.build().getEncodedQuery();
//
//            try {
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoOutput(true);
//                urlConnection.setChunkedStreamingMode(0);
//
//                OutputStream out = urlConnection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                out.close();
//                System.out.println("RESPONSE" + urlConnection.getResponseCode());
//                urlConnection.disconnect();
//
//            }
//            catch(Exception e)
//            {
//                System.out.println(e);
//            }
//
//            return null;
//        }
//    }

    private class GetRequester extends AsyncTask<String, Void, String> {

        Context context;
        URL url;

        private GetRequester(Context context, String path) {
            this.context = context.getApplicationContext();
            try {
                this.url = new URL(API_URL+path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String...params) {

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                char[] buffer = new char[1024];

                String jsonString = new String();

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line+"\n");
                }
                reader.close();
                in.close();
                jsonString = sb.toString();
                System.out.println("JSON: " + jsonString);


                System.out.println("RESPONSE" + urlConnection.getResponseCode());
                urlConnection.disconnect();
                return jsonString;


            }


            catch(Exception e)
            {
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data){
            Intent intent = new Intent(context, DisplayUsers.class);
            intent.putExtra(EXTRA_MESSAGE,data.split("\\], \\["));
            startActivity(intent);
        }


    }
}
