package mc.asu.edu.smartmeetings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShowMeetingDetails extends AppCompatActivity {

    String meeting_id;
    String meeting_name;
    String creator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meeting_details);
        Intent in = getIntent();
        Bundle extras = in.getExtras();
        this.meeting_id = extras.getString("meeting_id");
        this.meeting_name = extras.getString("meeting_name");
        this.creator = extras.getString("creator");

        TextView meeting_name_view = (TextView)findViewById(R.id.meeting_name_detail);
        TextView creator_view = (TextView)findViewById(R.id.creator_name_detail);
        meeting_name_view.setText(meeting_name);
        creator_view.setText(creator);

    }

    protected void createPoll(View view) {
        Intent in = new Intent(this, CreatePoll.class);
        Bundle extras = new Bundle();
        extras.putString("meeting_id", meeting_id);
        in.putExtras(extras);
        startActivity(in);
    }
}
