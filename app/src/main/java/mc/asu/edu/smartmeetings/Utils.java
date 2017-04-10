package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yeskarthik on 4/9/2017.
 */

public class Utils {

    public static final String API_URL = "http://smartmeetings-server.jgscuqju3c.us-west-2.elasticbeanstalk.com/";
    //public static final String API_URL = "http://10.0.2.2:8080/";
    public static class PostRequester extends AsyncTask<Map, Void, Void> {

        Context context;
        URL url;
        PostRequester(Context context, String path)  {
            this.context = context;
            try {
                this.url = new URL(API_URL + path);
                System.out.println(this.url.toString());
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Map... params) {

            Map<String, String> param = params[0];

            Uri.Builder builder = new Uri.Builder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            String query = builder.build().getEncodedQuery();

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setFixedLengthStreamingMode(query.length());
                urlConnection.setDoOutput(true);
                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                out.close();
                System.out.println("RESPONSE " + urlConnection.getResponseCode());
                urlConnection.disconnect();

            }
            catch(Exception e)
            {
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast toast = Toast.makeText(context, "Save complete", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
