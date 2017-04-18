package mc.asu.edu.smartmeetings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yeskarthik on 25/03/17.
 */

public class DisplayUsers extends Activity {

    ListView listView;

   String[] userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        userList = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);

        setContentView(R.layout.activity_display_users);
        System.out.println(Arrays.toString(userList));
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, userList);

        listView = (ListView) findViewById(R.id.mobile_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setAdapter(adapter);

    }

    protected void addParticipants(View view) {
        SparseBooleanArray selectedItems = listView.getCheckedItemPositions();
        ArrayList<String> selectedUsers = new ArrayList<String>();
        for (int i=0; i<selectedItems.size(); i++) {
            if(selectedItems.valueAt(i)) {
                selectedUsers.add(userList[selectedItems.keyAt(i)]);
            }
        }

        System.out.println("reached");
        System.out.println(Arrays.toString(selectedUsers.toArray()));

    }
}
