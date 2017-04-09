package mc.asu.edu.smartmeetings;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by yeskarthik on 4/9/2017.
 */

public class Utils {

    public static class PostRequester extends AsyncTask<Map, Void, Void> {

        URL url;
        PostRequester(String path)  {
            try {
                this.url = new URL("API_URL" + path);
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
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                out.close();
                System.out.println("RESPONSE" + urlConnection.getResponseCode());
                urlConnection.disconnect();

            }
            catch(Exception e)
            {
                System.out.println(e);
            }

            return null;
        }
    }
}
