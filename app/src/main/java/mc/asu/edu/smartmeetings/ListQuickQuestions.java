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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListQuickQuestions extends AppCompatActivity {
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
            Utils.GetRequester getRequester = new Utils.GetRequester(this.getApplicationContext(), "allquickquestions", new Utils.GetRequester.TaskListener() {
                @Override
                public void onFinished(ArrayList<HashMap<String, String>> result, final Context context) {
                    globalResult = result;
                    idList = new ArrayList<>();
                    questionList = new ArrayList<>();
                    for (HashMap<String, String> res : result) {
                        idList.add(res.get("id"));
                        questionList.add(res.get("question"));
                    }
                    createQuickQuestionsList();

                }});
            getRequester.execute(params);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    void createQuickQuestionsList(){
        setContentView(R.layout.activity_list_quick_questions);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.poll_item, questionList.toArray(new String[questionList.size()]));

        listView = (ListView) findViewById(R.id.quick_questions_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent inc = new Intent(context, ViewQuickQuestion.class);
                Bundle extras = new Bundle();
                extras.putString("id", idList.get(questionList.indexOf(((TextView) view).getText())));
                HashMap<String, String> res = globalResult.get(questionList.indexOf(((TextView) view).getText()));
                extras.putString("question", res.get("question"));
                System.out.println(res.get("answers"));
                System.out.println(Arrays.toString(res.get("answers").split(",")));
                extras.putStringArray("answers", res.get("answers").split(","));
                inc.putExtras(extras);
                startActivity(inc);
            }

        });
    }
}
