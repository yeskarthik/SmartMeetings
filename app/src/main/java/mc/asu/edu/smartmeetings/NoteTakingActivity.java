package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoteTakingActivity extends AppCompatActivity {

    SharedPreferences preferences;
    String name;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        preferences = getSharedPreferences("SmartMeetings",Context.MODE_PRIVATE);
        name = preferences.getString("name","");
        password = preferences.getString("password","");

    }

    protected  void save_note(View view) throws MalformedURLException {
        EditText note_title =(EditText)findViewById(R.id.notetitle);
        EditText note = (EditText)findViewById(R.id.notepad);
        EditText email = (EditText)findViewById(R.id.email);

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "somethin");
        params.put("email", email.getText().toString());
        params.put("note_title", note_title.getText().toString());
        params.put("note_text",note.getText().toString());

        System.out.println("hello");
        Utils.PostRequester requester = new Utils.PostRequester(this.getApplicationContext(), "note", null);
        requester.execute(params);
    }

    protected void get_notes(View view) throws MalformedURLException {
        EditText note_title =(EditText)findViewById(R.id.notetitle);

        Utils.GetRequester requester = new Utils.GetRequester(this, "note", new Utils.GetRequester.TaskListener() {
            @Override
            public void onFinished(ArrayList<HashMap<String, String>> result, Context context) {

                if (result.size() > 0) {
                    HashMap<String, String> res = result.get(0);
                    System.out.println("hello");
                    Intent email = new Intent(Intent.ACTION_SEND);

                    String to = res.get("email");
                    email.setType("application/text");
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    email.putExtra(Intent.EXTRA_SUBJECT, res.get("note_title"));
                    email.putExtra(Intent.EXTRA_TEXT, res.get("note_text"));
                    email.setType("message/rfc822");
                    email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(email, "Choose an email client"));
                }
            }
        });
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", name);

        requester.execute(params);
    }

}
