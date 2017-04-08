package mc.asu.edu.smartmeetings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;

import java.util.Arrays;

/**
 * Created by yeskarthik on 25/03/17.
 */

public class DisplayUsers extends Activity {


   // String[] userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String[] userList = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);

        setContentView(R.layout.activity_main);
        System.out.println(Arrays.toString(userList));
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.content_display_users, userList);

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

    }
}
