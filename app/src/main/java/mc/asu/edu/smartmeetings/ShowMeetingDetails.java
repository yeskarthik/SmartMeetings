package mc.asu.edu.smartmeetings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowMeetingDetails extends AppCompatActivity {

    String meeting_id;
    String meeting_name;
    String creator;
    String from;
    String to;
    String locationName;
    Context appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = this;
        setContentView(R.layout.activity_show_meeting_details);
        Intent in = getIntent();
        Bundle extras = in.getExtras();
        this.meeting_id = extras.getString("meeting_id");
        this.meeting_name = extras.getString("meeting_name");
        this.creator = extras.getString("creator");
        this.from = extras.getString("from_date");
        this.to = extras.getString("to_date");
        this.locationName = extras.getString("location_name");

        System.out.println("hellasdjas sdas");
        System.out.println(from);
        System.out.println(to);


        TextView meeting_name_view = (TextView)findViewById(R.id.meeting_name_detail);
        TextView creator_view = (TextView)findViewById(R.id.creator_name);
        TextView from_view = (TextView)findViewById(R.id.from_time_det);
        TextView to_view = (TextView)findViewById(R.id.to_time_det);
        TextView locationName_view = (TextView)findViewById(R.id.location_det);
        Button createPollButton = (Button) findViewById(R.id.create_poll_button);
        Button createQQButton = (Button) findViewById(R.id.create_qq_button);
        Button navigateButton = (Button) findViewById(R.id.navigate_meeting);

        createPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(appContext, CreatePoll.class);
                Bundle extras = new Bundle();
                extras.putString("meeting_id", meeting_id);
                in.putExtras(extras);
                startActivity(in);
            }
        });

        createQQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(appContext, CreateQuickQuestions.class);
                Bundle extras = new Bundle();
                extras.putString("meeting_id", meeting_id);
                in.putExtras(extras);
                startActivity(in);
            }
        });
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=33.424564, -111.928001");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        meeting_name_view.setText(meeting_name);
        creator_view.setText(creator);
        from_view.setText(from);
        to_view.setText(to);
        locationName_view.setText(locationName);

    }

}
