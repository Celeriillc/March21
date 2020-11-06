package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

public class SelectDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_date);

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.show(getSupportFragmentManager(), "Material Calendar Example");

        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                Intent intent = new Intent();
                intent.putExtra("Year", String.valueOf(year));
                intent.putExtra("Month", String.valueOf(monthOfYear));
                intent.putExtra("Day", String.valueOf(dayOfMonth));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
