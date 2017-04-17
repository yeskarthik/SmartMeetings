package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public static class GetRequester extends AsyncTask<Map, Void, ArrayList<HashMap<String, String>>> {

        Context context;
        URL url;

        public interface TaskListener{
            public void onFinished(ArrayList<HashMap<String, String>> result, Context context);
        }

        private final TaskListener taskListener;

        GetRequester(Context context, String path, TaskListener listener) throws MalformedURLException {
            this.context = context;
            this.url = new URL(API_URL + path);
            this.taskListener = listener;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Map... maps) {
            Map<String, String> params = maps[0];
            StringBuilder queryParams = new StringBuilder("?");
            for (Map.Entry<String, String> e: params.entrySet()) {
                queryParams.append(String.format(e.getKey() +"=%s", URLEncoder.encode(e.getValue())));
                queryParams.append("&");
            }
            String query = queryParams.toString();
            System.out.println(url.toString() + query);
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url.toString() + query).build();
            Response response = null;
            String body = null;

            try {
                response = httpClient.newCall(request).execute();
                body = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Received response: "+body);

            try {
                JSONObject jsonObject = new JSONObject(body);
                JSONArray items = jsonObject.getJSONArray("items");
                ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
                for(int i=0; i<items.length(); i++) {
                    HashMap<String, String> itResult = new HashMap<String, String>();
                    JSONObject c = items.getJSONObject(i);
                    Iterator iter = c.keys();
                    while(iter.hasNext()) {
                        String key = (String)iter.next();
                        itResult.put(key, c.getString(key));
                    }
                    result.add(itResult);
                }
                return result;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> s) {
            super.onPostExecute(s);
            if (this.taskListener != null) {
                this.taskListener.onFinished(s, context);
            }
        }
    }
}
