package mc.asu.edu.smartmeetings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListMeetLocationsInput extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "mc.asu.edu.smartmeetings.passMessage";

    DatePickerDialog datePickerDialog;
    EditText fromDate;
    EditText toDate;
    Date fromDateFormatted;
    Date toDateFormatted;
    List<String> userList;
    String participantsString;


    TextView textView;

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";

    private SwitchDateTimeDialogFragment fromDateTimeFragment;
    private SwitchDateTimeDialogFragment toDateTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_meet_locations_input);

        Intent in = getIntent();
        Bundle extras = in.getExtras();
        userList = extras.getStringArrayList("userList");

        textView = (TextView)findViewById(R.id.findlabel);
        fromDate = (EditText) findViewById(R.id.from_meeting_date);
        toDate = (EditText) findViewById(R.id.to_meeting_date);

        String[] partArray = userList.toArray(new String[userList.size()]);
        participantsString = Arrays.toString(partArray).substring(1, Arrays.toString(partArray).length() - 1);

        // Construct SwitchDateTimePicker
        fromDateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(fromDateTimeFragment == null) {
            fromDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(R.string.positive_button_datetime_picker),
                    getString(R.string.negative_button_datetime_picker)
            );
        }

        // Assign values we want
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        fromDateTimeFragment.startAtCalendarView();
        fromDateTimeFragment.set24HoursMode(false);
        fromDateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        fromDateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        fromDateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());

        // Define new day and month format
        try {
            fromDateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {

        }

        // Set listener for date
        fromDateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                fromDateFormatted = date;
                fromDate.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });


        // Construct SwitchDateTimePicker
        toDateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(toDateTimeFragment == null) {
            toDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(R.string.positive_button_datetime_picker),
                    getString(R.string.negative_button_datetime_picker)
            );
        }

        // Assign values we want
        toDateTimeFragment.startAtCalendarView();
        toDateTimeFragment.set24HoursMode(false);
        toDateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        toDateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        toDateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());

        // Define new day and month format
        try {
            toDateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {

        }

        // Set listener for date
        toDateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                toDateFormatted = date;
                toDate.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });

        Button findButton = (Button) findViewById(R.id.button3);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    findMeetingRooms(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current textView
        savedInstanceState.putCharSequence(STATE_TEXTVIEW, textView.getText());
        super.onSaveInstanceState(savedInstanceState);
    }

    void findMeetingRooms(View view) throws Exception {
        EditText fromDate = (EditText) findViewById(R.id.from_meeting_date);
        EditText toDate = (EditText) findViewById(R.id.to_meeting_date);
        EditText meetingName = (EditText) findViewById(R.id.meetingName);

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        final Date fromDa8 = fromDateFormatted;
        final Date toDa8 = toDateFormatted;
        final String meeting_name = meetingName.getText().toString();

        System.out.println(df.format(fromDa8));
        System.out.println(df.format(toDa8));

        Map<String, String> params = new HashMap<String, String>();
        params.put("from_date", df.format(fromDa8));
        params.put("to_date", df.format(toDa8));
        params.put("participants", participantsString);
        Utils.GetRequester getRequester = new Utils.GetRequester(
            this.getApplication(), "checkclash", new Utils.GetRequester.TaskListener() {
            @Override
            public void onFinished(ArrayList<HashMap<String, String>> result, final Context context) {
                HashMap<String, String> res = result.get(0);
                if(res.get("status").equals("false")) {
                    Bundle extras = new Bundle();
                    extras.putString("result", res.get("result"));
                    Toast toast = Toast.makeText(context, "The meeting time you created is clashing for some of the participants", Toast.LENGTH_LONG);
                    toast.show();
                    Intent in = new Intent(context, DisplayUsers.class);
                    in.putExtras(extras);
                    startActivity(in);
                } else {
                    System.out.println(res.get("location_list"));
                    String[] locAndIds = res.get("location_list").substring(2, res.get("location_list").length() - 2).replace("\"", "").split(",");
                    System.out.println(Arrays.toString(locAndIds));
                    ArrayList<String> locationNames = new ArrayList<String>();
                    ArrayList<String> locationIds = new ArrayList<String>();
                    System.out.println(locAndIds.length);
                    for(int i = 0; i<locAndIds.length; i+=2) {
                        locationNames.add(locAndIds[i+1]);
                        locationIds.add(locAndIds[i]);
                    }
                    Intent intent = new Intent(context, ListMeetLocations.class);
                    Bundle extras = new Bundle();
                    extras.putStringArrayList("locationNames", locationNames);
                    extras.putStringArrayList("locationIds", locationIds);
                    extras.putString("from_date", df.format(fromDa8));
                    extras.putString("to_date", df.format(toDa8));
                    extras.putString("meeting_name", meeting_name);
                    extras.putString("participants", participantsString);
                    intent.putExtras(extras);
                    //intent.putExtra(EXTRA_MESSAGE, locationNames.toArray(new String[locationNames.size()]));

                    startActivity(intent);

                }

                /**/
            }
        });

        getRequester.execute(params);
    }
}
