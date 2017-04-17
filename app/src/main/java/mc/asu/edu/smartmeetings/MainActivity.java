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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected void create_user(View view) throws IOException {

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
        request.execute(params);

    }

    protected void get_users(View view) throws MalformedURLException, IOException {
        GetRequester request = new GetRequester(this, "users");
        System.out.println("CHOWMEIN");
        String data = request.execute().toString();
        System.out.println(data);
    }

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


            catch(Exception e) {
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
