package mc.asu.edu.smartmeetings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yeskarthik on 25/03/17.
 */

public class DisplayUsers extends Activity {

    ListView listView;

    List<String> userList;
    List<String> idList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string

        try {
            Utils.GetRequester getRequester = new Utils.GetRequester(getApplicationContext(), "users", new Utils.GetRequester.TaskListener() {
                @Override
                public void onFinished(ArrayList<HashMap<String, String>> result, Context context) {
                    userList = new ArrayList<>();
                    idList = new ArrayList<>();
                    for(HashMap<String, String> res : result) {
                        userList.add(res.get("username"));
                        idList.add(res.get("id"));
                        createParticipantsList();
                    }
                }
            });
            getRequester.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();;
        }



    }

    void createParticipantsList() {

        System.out.println("users list " + Arrays.toString(userList.toArray()));
        setContentView(R.layout.activity_display_users);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, this.userList);

        listView = (ListView) findViewById(R.id.mobile_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setAdapter(adapter);

        Button addParticipantsButton = (Button) findViewById(R.id.add_participants_button);
        addParticipantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParticipants();
            }
        });
    }

    void addParticipants() {
        SparseBooleanArray selectedItems = listView.getCheckedItemPositions();
        ArrayList<String> selectedUsers = new ArrayList<String>();
        for (int i=0; i<selectedItems.size(); i++) {
            if(selectedItems.valueAt(i)) {
                selectedUsers.add(userList.get(selectedItems.keyAt(i)));
            }
        }
        System.out.println("reached");
        System.out.println(Arrays.toString(selectedUsers.toArray()));

        Bundle extras = new Bundle();
        extras.putStringArrayList("userList", (ArrayList)userList);

        Intent in = new Intent(this, ListMeetLocationsInput.class);
        in.putExtras(extras);
        startActivity(in);

    }
}
