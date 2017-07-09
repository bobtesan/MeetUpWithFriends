package com.example.intern05.meetup.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.intern05.meetup.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EventCreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private  FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = db.getReference().child("Events");
    private DatabaseReference myRef2 = db.getReference().child("Events");
    private DatabaseReference myRef3 = db.getReference().child("Events");
    private Button calendarB;
    private EditText dateText;
    private EditText timeStart;
    private Button createEventB;
    private EditText eventName;
    private TimePickerDialog mTimePicker;
    private DatePickerDialog mDatePicker;
    private TextView location;
    private String eventAddress;
    private String eventAddressLatitude;
    private String eventAddressLongitude;

    private int day, year, month;

    private Calendar mCurrentTime = Calendar.getInstance();
    private int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
    private int minute = mCurrentTime.get(Calendar.MINUTE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        Intent i=getIntent();

        eventAddress=i.getStringExtra(MapsActivity.KEY_EVENT_ADDRESS);
        eventAddressLatitude=i.getStringExtra(MapsActivity.KEY_EVENT_LATITUDE);
        eventAddressLongitude=i.getStringExtra(MapsActivity.KEY_EVENT_LONGITUDE);

        eventName = (EditText) findViewById(R.id.event_name);
        dateText = (EditText) findViewById(R.id.dateText2);
        calendarB = (Button) findViewById(R.id.calendarB);
        timeStart = (EditText) findViewById(R.id.timeStart);
        location=(TextView)findViewById(R.id.textView6);

        location.setText(eventAddress);
        dateText.setShowSoftInputOnFocus(false);


        createEventB = (Button) findViewById(R.id.button3);
        createEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventName.getText().toString().isEmpty() || dateText.getText().toString().isEmpty() || timeStart.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        MapsActivity ma=new MapsActivity();
                        myRef = db.getReference("Events").child(eventName.getText().toString()); //child(temp_key.toString()).
                        Map<String, Object> map2 = new HashMap<String, Object>();
                        map2.put("EventDate", dateText.getText().toString());
                        map2.put("StartTime", timeStart.getText().toString());
                        map2.put("Location", "");
                        map2.put("Chat", "");
                        myRef.updateChildren(map2);

                        myRef2 = db.getReference("Events").child(eventName.getText().toString()).child("Location").child("Latitude");
                        myRef2.setValue(String.valueOf(eventAddressLatitude));
                        myRef3 = db.getReference("Events").child(eventName.getText().toString()).child("Location").child("Longitude");
                        myRef3.setValue(String.valueOf(eventAddressLongitude));

                        Toast.makeText(getApplicationContext(), "Event created successfully.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EventCreateActivity.this, SlideBarActivity.class);
                        startActivity(i);
                        finish();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Oops. There is an error.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        //dateText.setEnabled(false);
       calendarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTimePicker = new TimePickerDialog(EventCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHours, int selectedMinutes) {
                        timeStart.setText(selectedHours + ":" + selectedMinutes);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }

        });
    }

    private Calendar calander = Calendar.getInstance();
    private int yearC = calander.get(Calendar.YEAR);
    private int monthC = calander.get(Calendar.MONTH);
    private int dayC = calander.get(Calendar.DAY_OF_MONTH);

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, yearC, monthC, dayC);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            dateText.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
