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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMeetings extends AppCompatActivity {

    SharedPreferences preferences;
    String username;
    List<String> meeting_ids;
    List<String> meeting_names;
    List<String> froms;
    List<String> tos;
    List<String> locationNames;
    Context context;
    List<String> creators;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_view_meetings);
        preferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        this.username = name;
        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            Utils.GetRequester getRequester = new Utils.GetRequester(this.getApplicationContext(), "meetings", new Utils.GetRequester.TaskListener() {
                @Override
                public void onFinished(ArrayList<HashMap<String, String>> result, final Context context) {
                    meeting_names = new ArrayList<>();
                    meeting_ids = new ArrayList<>();
                    creators = new ArrayList<>();
                    froms = new ArrayList<>();
                    tos = new ArrayList<>();
                    locationNames = new ArrayList<>();
                    for(HashMap<String, String> res: result) {
                        meeting_ids.add(res.get("id"));
                        meeting_names.add(res.get("name"));
                        creators.add(res.get("creator"));
                        froms.add(res.get("from"));
                        tos.add(res.get("to"));
                        locationNames.add(res.get("location_name"));
                    }
                    createList();
                }
            });
            getRequester.execute(params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



    }

    private void createList() {
        setContentView(R.layout.activity_view_meetings);
        ArrayAdapter adapter = new ArrayAdapter<String>
                (this, R.layout.view_meeting_item, meeting_names.toArray(new String[meeting_names.size()]));

        ListView listView = (ListView) findViewById(R.id.meeting_names_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(),
                    meeting_ids.get(meeting_names.indexOf(((TextView) view).getText())),
                    Toast.LENGTH_SHORT).show();

            Intent inc = new Intent(context, ShowMeetingDetails.class);
            Bundle extras = new Bundle();
            extras.putString("meeting_name", ((TextView) view).getText().toString());
            extras.putString("creator", creators.get(meeting_names.indexOf(((TextView) view).getText())));
            extras.putString("meeting_id", meeting_ids.get(meeting_names.indexOf(((TextView) view).getText())));
            extras.putString("location_name", locationNames.get(meeting_names.indexOf(((TextView) view).getText())));
            extras.putString("from", froms.get(meeting_names.indexOf(((TextView) view).getText())));
            extras.putString("to", tos.get(meeting_names.indexOf(((TextView) view).getText())));
            inc.putExtras(extras);
            startActivity(inc);
            }
        });
    }
}
