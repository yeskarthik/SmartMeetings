package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class SubmitPoll extends AppCompatActivity {

    private TextView questionView;
    private RadioButton r1, r2, r3, r4;
    private String poll_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setContentView(R.layout.activity_submit_poll);
        System.out.println(intent.getStringExtra("question"));
        System.out.println(extras.getString("question"));
        poll_id = extras.getString("poll_id");
        String question = extras.getString("question");
        String option1 = extras.getString("option1");
        String option2 = extras.getString("option2");
        String option3 = extras.getString("option3");
        String option4 = extras.getString("option4");

        questionView = (TextView) findViewById(R.id.questionTextSubmitPoll);
        questionView.setText(question);
        r1 = (RadioButton) findViewById(R.id.radioButton1);
        r1.setText("1. "+option1);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
        r2.setText("2. "+option2);
        r3 = (RadioButton) findViewById(R.id.radioButton3);
        r3.setText("3. "+option3);
        r4 = (RadioButton) findViewById(R.id.radioButton4);
        r4.setText("4. "+option4);

    }

    protected void submitPoll(View view) {

        Map<String, String> params = new HashMap<>();
        params.put("poll_id", poll_id);
        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
        int answer = group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) group.findViewById(answer);
        String radioId = (String) radioButton.getText();
        System.out.println("ANSWER " + answer + "\n");
        params.put("option", String.valueOf(radioId.substring(0,1)));
        try {
            Utils.PostRequester poster = new Utils.PostRequester(this.getApplicationContext(), "submitpoll", new Utils.PostRequester.TaskListener() {
                @Override
                public void onFinished(HashMap<String, String> result, Context context) {
                    Intent intent = new Intent(context, Home.class);
                    startActivity(intent);
                }
            });
            poster.execute(params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
