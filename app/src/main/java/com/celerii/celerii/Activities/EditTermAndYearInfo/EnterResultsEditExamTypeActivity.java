package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;

public class EnterResultsEditExamTypeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RadioButton ca, exam, other;
    RadioGroup testtypeGroup;

    String selectedTestType;
    String testType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_exam_type);

        Bundle b = getIntent().getExtras();
        testType = b.getString("Test Type");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Test Type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ca = (RadioButton) findViewById(R.id.ca);
        exam = (RadioButton) findViewById(R.id.exam);
        other = (RadioButton) findViewById(R.id.other);
        testtypeGroup = (RadioGroup) findViewById(R.id.testtypegroup);

        if (testType.equals("Continous Assessment")){
            ca.setChecked(true);
            selectedTestType = "Continous Assessment";
        } else if (testType.equals("Examination")){
            exam.setChecked(true);
            selectedTestType = "Examination";
        } else {
            other.setChecked(true);
            selectedTestType = "Other";
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

            if (selectedTestType == null) {return false;}

            if (ca.isChecked()){
                selectedTestType = "Continous Assessment";
            } else if (exam.isChecked()){
                selectedTestType = "Examination";
            } else {
                selectedTestType = "Other";
            }

            intent.putExtra("Selected Test Type", selectedTestType);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
