package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class CreatePoll extends AppCompatActivity {

    private Context context;
    private SharedPreferences preferences;
    private String username;
    private String meeting_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        this.meeting_id = (String)extras.get("meeting_id");
        context = this.getApplicationContext();
        setContentView(R.layout.activity_view_meetings);
        preferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        this.username = name;
        System.out.println("hi");
        setContentView(R.layout.activity_create_poll);
        Button button = (Button) findViewById(R.id.submit_poll);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPoll(view);
            }
        });

    }

    protected void createPoll(View view) {
        EditText question = (EditText)findViewById(R.id.question_poll_create);
        EditText option_1 = (EditText)findViewById(R.id.option_1_create);
        EditText option_2 = (EditText)findViewById(R.id.option_2_create);
        EditText option_3 = (EditText)findViewById(R.id.option_3_create);
        EditText option_4 = (EditText)findViewById(R.id.option_4_create);

        Map<String, String> params = new HashMap<>();
        params.put("meeting_id",meeting_id);
        params.put("username", this.username);
        params.put("question", question.getText().toString());
        params.put("option1", option_1.getText().toString());
        params.put("option2", option_2.getText().toString());
        params.put("option3", option_3.getText().toString());
        params.put("option4", option_4.getText().toString());

        try {
            Utils.PostRequester postRequester = new Utils.PostRequester(this.getApplicationContext(), "polls", null);
            postRequester.execute(params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
