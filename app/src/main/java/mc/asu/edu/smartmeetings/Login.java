package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static mc.asu.edu.smartmeetings.Login.API_URL;

public class Login extends AppCompatActivity {

    EditText username, password;
    SharedPreferences sharedPreferences;
    public static final String API_URL = "http://smartmeetings-server.jgscuqju3c.us-west-2.elasticbeanstalk.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void performLogin(View view)
    {
        username = (EditText)findViewById(R.id.editText6);
        password = (EditText)findViewById(R.id.editText7);

        if(validate(username.getText().toString(), password.getText().toString()))
        {
            sharedPreferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", username.toString());
            editor.putString("password", password.toString());
            editor.commit();
            System.out.println("Starting Activity");
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        }
        else
        {
            System.out.println("false password");
        }
    }

    public boolean validate(String username, String password)
    {
        networkAcess network = new networkAcess();
        System.out.println("Username: "+username + " password "+ password);
        String body = network.execute(username,password).toString();
        System.out.println("returned body "+ body);
            if(body.equals("true"))
            {
                return true;
            }
        return false;
    }

    class networkAcess extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient httpClient = new OkHttpClient();
            System.out.println("Querying with "+params[0] + " " +params[1]);
            String url=API_URL+"auth"+"?username="+params[0]+"&password="+params[1];
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = httpClient.newCall(request).execute();
                String body = response.body().string();
                System.out.println("Received response: " + body);
                return body;
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return null;
        }
    }



}
