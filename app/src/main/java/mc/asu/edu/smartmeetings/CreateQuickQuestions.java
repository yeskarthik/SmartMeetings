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

public class CreateQuickQuestions extends AppCompatActivity {

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
        preferences = getSharedPreferences("SmartMeetings", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        this.username = name;
        setContentView(R.layout.activity_create_quick_questions);

        Button button = (Button)findViewById(R.id.submit_quick_question);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQuickQuestion();
            }
        });

    }

    protected void createQuickQuestion() {
        EditText question = (EditText)findViewById(R.id.quick_question_input);

        Map<String, String> params = new HashMap<>();
        params.put("meeting_id",meeting_id);
        params.put("username", this.username);
        params.put("question", question.getText().toString());
        System.out.println(question.getText().toString());
        try {
            Utils.PostRequester postRequester = new Utils.PostRequester(this.getApplicationContext(), "quickquestions", null);
            postRequester.execute(params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
