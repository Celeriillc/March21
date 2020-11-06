package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;

public class AvailableForHomeLessonsEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RadioButton available, notAvailable;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_for_home_lessons_edit);

        Bundle b = getIntent().getExtras();
        String availableForHomeLessons = b.getString("availableforhomelessons");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change your availability status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        available = (RadioButton) findViewById(R.id.available);
        notAvailable = (RadioButton) findViewById(R.id.notavailable);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        if (availableForHomeLessons.equals("Available")){
            available.setChecked(true);
        } else if (availableForHomeLessons.equals("Not Available")){
            notAvailable.setChecked(true);
        } else {
            notAvailable.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send){
            //TODO: Validate against null values
            Intent intent = new Intent();
            String selectedAvailableForHomeLessons;

            if (available.isChecked()){
                selectedAvailableForHomeLessons = "Available";
            } else {
                selectedAvailableForHomeLessons = "Not Available";
            }

            intent.putExtra("selectedavailableforhomelessons", selectedAvailableForHomeLessons);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
