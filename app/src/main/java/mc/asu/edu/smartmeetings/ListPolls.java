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
import java.util.List;
import java.util.Map;

public class ListPolls extends AppCompatActivity {

    private SharedPreferences preferences;
    private String username;
    private List<String> idList, questionList;
    private List<List<String>> optionsList;
    private ListView listView;
    private Context context;
    private List<HashMap<String, String>> globalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        preferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        this.username = name;

        try {
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            Utils.GetRequester getRequester = new Utils.GetRequester(this.getApplicationContext(), "allpolls", new Utils.GetRequester.TaskListener() {
                @Override
                public void onFinished(ArrayList<HashMap<String, String>> result, final Context context) {
                    globalResult = result;
                    idList = new ArrayList<>();
                    questionList = new ArrayList<>();
                    for (HashMap<String, String> res : result) {
                        idList.add(res.get("id"));
                        questionList.add(res.get("question"));
                    }
                    createPollsList();

                }});
                getRequester.execute(params);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    void createPollsList(){
        setContentView(R.layout.activity_list_polls);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.poll_item, questionList.toArray(new String[questionList.size()]));

        listView = (ListView) findViewById(R.id.polls_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent inc = new Intent(context, ViewPoll.class);
                Bundle extras = new Bundle();
                extras.putString("id", idList.get(questionList.indexOf(((TextView) view).getText())));
                HashMap<String, String> res = globalResult.get(questionList.indexOf(((TextView) view).getText()));
                extras.putString("question", res.get("question"));
                extras.putString("option1", res.get("option1"));
                extras.putString("option2", res.get("option2"));
                extras.putString("option3", res.get("option3"));
                extras.putString("option4", res.get("option4"));
                extras.putString("count_option1", res.get("count_option1"));
                extras.putString("count_option2", res.get("count_option2"));
                extras.putString("count_option3", res.get("count_option3"));
                extras.putString("count_option4", res.get("count_option4"));

                inc.putExtras(extras);
                startActivity(inc);
            }

        });
    }
}
