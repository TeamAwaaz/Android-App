package teamawaazdtu.com.awaaz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG="Calendar Activity";
    private CalendarView mCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int a;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarView=(CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                String date=(i2) +"/" +(i1+1) +"/" +i;
                Log.d(TAG,"onSelectedDayChange:dd/mm/yyyy: "+ date);
                Intent in=new Intent(CalendarActivity.this,SignUpActivity.class);
                in.putExtra("DATE: ",date);
                startActivity(in);
            }
        });
    }
}
