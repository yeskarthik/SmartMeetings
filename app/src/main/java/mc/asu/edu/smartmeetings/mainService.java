package mc.asu.edu.smartmeetings;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sairaj on 4/17/2017.
 */

public class mainService extends IntentService {
    public mainService()
    {
        super("AllServices");
        //setIntentRedelivery(true);
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        try
        {
            //System.out.println("Started Poll service");
            pollService service = new pollService(getApplicationContext());
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fDate = df.format(c.getTime());
            //checkPolls(getApplicationContext(),service.getUsername(),fDate);
            restart(getApplicationContext());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void restart(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, mainService.class);
        startService(intent);
    }

    void checkPolls(Context context, String username, String date) throws MalformedURLException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("time", date);
        Utils.GetRequester getRequester = new Utils.GetRequester(
                this.getApplicationContext()
                ,"polls",
                new Utils.GetRequester.TaskListener() {
                    @Override
                    public void onFinished(ArrayList<HashMap<String, String>> result, final Context context) {
                        System.out.println("Polling");
                        HashMap<String, String> res = result.get(0);
                        System.out.println("status");
                        System.out.println(res.get("status"));
                        if(res.get("status").equals("true")) {
                            String[] bundle = {res.get("question"),res.get("a"),res.get("b"),res.get("c"), res.get("d")};
                            Intent in = new Intent(context, Home.class); //change to Poll.class
                            in.putExtra("bundle",bundle);
                            startActivity(in);
                        }
                    }
                });
        getRequester.execute(params);
    }
}
