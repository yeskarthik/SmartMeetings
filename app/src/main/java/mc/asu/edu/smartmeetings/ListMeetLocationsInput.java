package mc.asu.edu.smartmeetings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    TextView textView;

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";

    private SwitchDateTimeDialogFragment fromDateTimeFragment;
    private SwitchDateTimeDialogFragment toDateTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_meet_locations_input);

        textView = (TextView)findViewById(R.id.findlabel);
        fromDate = (EditText) findViewById(R.id.from_meeting_date);
        toDate = (EditText) findViewById(R.id.to_meeting_date);


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


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current textView
        savedInstanceState.putCharSequence(STATE_TEXTVIEW, textView.getText());
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void findMeetingRooms(View view) throws Exception {
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

        Utils.GetRequester getRequester = new Utils.GetRequester(
            this.getApplication(), "meetingrooms", new Utils.GetRequester.TaskListener() {
            @Override
            public void onFinished(ArrayList<HashMap<String, String>> result, Context context) {
                ArrayList<String> locationNames = new ArrayList<String>();
                ArrayList<String> locationIds = new ArrayList<String>();

                for(HashMap<String, String> res: result) {
                    locationNames.add(res.get("name"));
                    locationIds.add(res.get("id"));
                }
                Intent intent = new Intent(context, ListMeetLocations.class);
                Bundle extras = new Bundle();
                extras.putStringArrayList("locationNames", locationNames);
                extras.putStringArrayList("locationIds", locationIds);
                extras.putString("from_date", df.format(fromDa8));
                extras.putString("to_date", df.format(toDa8));
                extras.putString("meeting_name", meeting_name);
                intent.putExtras(extras);
                //intent.putExtra(EXTRA_MESSAGE, locationNames.toArray(new String[locationNames.size()]));

                startActivity(intent);
            }
        });

        getRequester.execute(params);
    }
}
