package mc.asu.edu.smartmeetings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class createMeeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
    }

    protected void addParticipants(View view)
    {
        Intent in = new Intent(this, DisplayUsers.class);
        startActivity(in);
    }
}
