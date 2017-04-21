package mc.asu.edu.smartmeetings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

public class ViewQuickQuestion extends AppCompatActivity {

    String question;
    String[] answers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        question = extras.getString("question");
        answers = extras.getStringArray("answers");
        System.out.println(Arrays.toString(answers));
        setContentView(R.layout.activity_view_quick_question);
        TextView questionText = (TextView)findViewById(R.id.vqq_question_text);
        questionText.clearFocus();
        questionText.setTextKeepState(question);


        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.poll_item, answers);

        ListView listView = (ListView) findViewById(R.id.view_quick_question_list);
        listView.setAdapter(adapter);
    }
}
