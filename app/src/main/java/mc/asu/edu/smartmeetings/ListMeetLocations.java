package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListMeetLocations extends AppCompatActivity {

    private SharedPreferences preferences;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("SmartMeetings",Context.MODE_PRIVATE);
        username = preferences.getString("name","");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //String[] msgList = intent.getStringArrayExtra(ListMeetLocationsInput.EXTRA_MESSAGE);
        final ArrayList<String> locationNames = extras.getStringArrayList("locationNames");
        final ArrayList<String> locationIds = extras.getStringArrayList("locationIds");
        final String from_date = extras.getString("from_date");
        final String to_date = extras.getString("to_date");
        final String meeting_name = extras.getString("meeting_name");

        System.out.println(Arrays.toString(locationNames.toArray(new String[locationNames.size()])));
        System.out.println(Arrays.toString(locationIds.toArray(new String[locationIds.size()])));
        System.out.println(from_date);
        System.out.println(to_date);

        setContentView(R.layout.activity_list_meet_locations);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.location_item, locationNames);

        ListView listView = (ListView) findViewById(R.id.meet_locations_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        locationIds.get(locationNames.indexOf(((TextView) view).getText())),
                        Toast.LENGTH_SHORT).show();

                Map<String, String> params = new HashMap<String, String>();
                params.put("from_date", from_date);
                params.put("to_date", to_date);
                params.put("name", meeting_name);
                params.put("creator", username);
                params.put("location_id", locationIds.get(locationNames.indexOf(((TextView) view).getText())));

                try {
                    Utils.PostRequester postRequester = new Utils.PostRequester
                    (getApplicationContext(), "meeting", new Utils.PostRequester.TaskListener() {
                        @Override
                        public void onFinished(HashMap<String, String> result, final Context context) {
                            System.out.println(result.keySet().toString());
                        }
                    });
                    postRequester.execute(params);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
