package ca.com.androidbinnersproject.activities.pickup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import ca.com.androidbinnersproject.R;

/**
 * Created by jonathan_campos on 02/03/2016.
 */
public class SelectDateActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtDayOfWeek;
    private TextView txtDayOfMonth;
    private TextView txtMonth;

    private ImageButton btnPreviousDay;
    private ImageButton btnNextDay;
    private ImageButton btnPreviousMonth;
    private ImageButton btnNextMonth;
    private CalendarView mCalendarView;

    private String[] daysOfWeek;
    private String[] months;

    private Calendar superCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_calendar);

        txtDayOfWeek = (TextView) findViewById(R.id.select_date_activity_txtDayOfWeek);
        txtDayOfMonth = (TextView) findViewById(R.id.select_date_activity_txtDayOfMonth);
        txtMonth = (TextView) findViewById(R.id.select_date_activity_txtMonth);
        mCalendarView = (CalendarView) findViewById(R.id.select_date_activity_calendar_view);

        btnPreviousDay = (ImageButton) findViewById(R.id.select_date_activity_btnPreviousDay);
        btnNextDay = (ImageButton) findViewById(R.id.select_date_activity_btnNextDay);
        btnPreviousMonth = (ImageButton) findViewById(R.id.select_date_activity_btnPreviousMonth);
        btnNextMonth = (ImageButton) findViewById(R.id.select_date_activity_btnNextMonth);
        daysOfWeek = getResources().getStringArray(R.array.day_of_week);
        months = getResources().getStringArray(R.array.months);

        btnPreviousDay.setOnClickListener(this);
        btnNextDay.setOnClickListener(this);
        btnPreviousMonth.setOnClickListener(this);
        btnNextMonth.setOnClickListener(this);

        initializeCalendar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateViews(superCalendar);
    }

    private void initializeCalendar() {
        ViewGroup vg = (ViewGroup) mCalendarView.getChildAt(0);

        View subView = vg.getChildAt(0);

        if(subView instanceof TextView) {
            ((TextView)subView).setVisibility(View.GONE);
        }

        mCalendarView.setShowWeekNumber(false);
        mCalendarView.setFirstDayOfWeek(2);

        setMinDate();

        setListener();
    }

    private void setListener() {
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                if (isValid(year, month, day)) {
                    superCalendar.set(year, month, day);
                    updateViews(superCalendar);

                } else {
                    calendarView.setDate(superCalendar.getTime().getTime());
                    Toast.makeText(SelectDateActivity.this, "Not Allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateViews(Calendar calendar) {
        int day          = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        String month     = months[calendar.get(Calendar.MONTH)];

        txtDayOfWeek.setText(dayOfWeek);
        txtDayOfMonth.setText(String.valueOf(day));
        txtMonth.setText(month);
    }

    public void setMinDate() {

    }

    public boolean isValid(int year, int month, int day) {
        Calendar calNow = Calendar.getInstance();
        Calendar calUser = Calendar.getInstance();
        calUser.set(year, month, day);

        return calUser.compareTo(calNow) >=0;
    }

    @Override
    public void onClick(View view) {
        Calendar calClone = (Calendar) superCalendar.clone();

        switch(view.getId()) {
            case R.id.select_date_activity_btnPreviousDay:{
                calClone.add(Calendar.DAY_OF_MONTH, -1);
            } break;
            case R.id.select_date_activity_btnNextDay:{
                calClone.add(Calendar.DAY_OF_MONTH, 1);
            } break;
            case R.id.select_date_activity_btnPreviousMonth:{
                calClone.add(Calendar.MONTH, -1);
            } break;
            case R.id.select_date_activity_btnNextMonth:{
                calClone.add(Calendar.MONTH, 1);
            } break;
        }

        if(isValid(calClone.get(Calendar.YEAR), calClone.get(Calendar.MONTH), calClone.get(Calendar.DAY_OF_MONTH))) {
            superCalendar = calClone;
            mCalendarView.setDate(superCalendar.getTime().getTime());
        } else {
            mCalendarView.setDate(superCalendar.getTime().getTime());
            Toast.makeText(SelectDateActivity.this, "Not Allowed", Toast.LENGTH_SHORT).show();
        }
    }

}
