package mc.asu.edu.smartmeetings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class NoteTakingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    protected  void save_note(View view) {
        EditText note_title =(EditText)findViewById(R.id.notetitle);
        EditText note = (EditText)findViewById(R.id.notepad);

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "something");
        params.put("note_title", note_title.getText().toString());
        params.put("note_text",note.getText().toString());

        System.out.println("hello");
        Utils.PostRequester requester = new Utils.PostRequester(this.getApplicationContext(), "note");
        requester.execute(params);

    }

}
