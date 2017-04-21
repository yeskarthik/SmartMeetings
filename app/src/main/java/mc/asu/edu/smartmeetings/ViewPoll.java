package mc.asu.edu.smartmeetings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewPoll extends AppCompatActivity {

    private TextView questionView, opt1, opt2, opt3, opt4, opt1q, opt2q, opt3q, opt4q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String question = extras.getString("question");
        String count1 = extras.getString("count_option1");
        String count2 = extras.getString("count_option2");
        String count3 = extras.getString("count_option3");
        String count4 = extras.getString("count_option4");
        String option1 = extras.getString("option1");
        String option2 = extras.getString("option2");
        String option3 = extras.getString("option3");
        String option4 = extras.getString("option4");
        setContentView(R.layout.activity_view_poll);
        questionView = (TextView) findViewById(R.id.qView);
        questionView.setText(question);
        opt1 = (TextView) findViewById(R.id.opt1view);
        opt1.setText(count1);
        opt2 = (TextView) findViewById(R.id.opt2view);
        opt2.setText(count2);
        opt3 = (TextView) findViewById(R.id.opt3view);
        opt3.setText(count3);
        opt4 = (TextView) findViewById(R.id.opt4view);
        opt4.setText(count4);

        opt1q = (TextView) findViewById(R.id.opt1);
        opt1q.setText(option1);
        opt2q = (TextView) findViewById(R.id.opt2);
        opt2q.setText(option2);
        opt3q = (TextView) findViewById(R.id.opt3);
        opt3q.setText(option3);
        opt4q = (TextView) findViewById(R.id.opt4);
        opt4q.setText(option4);


    }
}
