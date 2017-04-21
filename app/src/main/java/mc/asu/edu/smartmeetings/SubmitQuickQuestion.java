package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class SubmitQuickQuestion extends AppCompatActivity {

    private Context appContext;
    private String question_id;
    private TextView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setContentView(R.layout.activity_submit_quick_question);
        question_id = extras.getString("question_id");
        String question = extras.getString("question");

        questionView = (TextView) findViewById(R.id.question_text_sqq);
        questionView.setText(question);

        Button submitButton = (Button) findViewById(R.id.quick_ques_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitQuickQuestions();
            }
        });

    }

    protected void submitQuickQuestions() {

        Map<String, String> params = new HashMap<>();
        params.put("question_id", question_id);
        EditText answerText = (EditText) findViewById(R.id.quick_q_answer);
        params.put("answer", answerText.getText().toString());
        try {
            Utils.PostRequester poster = new Utils.PostRequester(this.getApplicationContext(), "submitquickquestion", new Utils.PostRequester.TaskListener() {
                @Override
                public void onFinished(HashMap<String, String> result, Context context) {
                    System.out.println("reached here");
                    Intent intent = new Intent(appContext, Home.class);
                    startActivity(intent);
                }
            });
            poster.execute(params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
