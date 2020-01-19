package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;

public class MaritalStatusEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RadioButton single, married, widowed, divorced, separated, registeredPartnership;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marital_status_edit);

        Bundle b = getIntent().getExtras();
        String maritalstatus = b.getString("maritalstatus");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change your marital status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        single = (RadioButton) findViewById(R.id.single);
        married = (RadioButton) findViewById(R.id.married);
        widowed = (RadioButton) findViewById(R.id.widowed);
        divorced = (RadioButton) findViewById(R.id.divorced);
        separated = (RadioButton) findViewById(R.id.separated);
        registeredPartnership = (RadioButton) findViewById(R.id.registeredpartnership);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        if (maritalstatus.equals("Single")){
            single.setChecked(true);
        } else if (maritalstatus.equals("Married")){
            married.setChecked(true);
        } else if (maritalstatus.equals("Widowed")){
            widowed.setChecked(true);
        } else if (maritalstatus.equals("Divorced")){
            divorced.setChecked(true);
        } else if (maritalstatus.equals("Separated")){
            separated.setChecked(true);
        } else if (maritalstatus.equals("Registered Partnership")){
            registeredPartnership.setChecked(true);
        } else {
            single.setChecked(true);
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
            String selectedMaritalStatus;

            if (single.isChecked()){
                selectedMaritalStatus = "Single";
            } else if (married.isChecked()){
                selectedMaritalStatus = "Married";
            } else if (widowed.isChecked()){
                selectedMaritalStatus = "Widowed";
            } else if (divorced.isChecked()){
                selectedMaritalStatus = "Divorced";
            } else if (separated.isChecked()){
                selectedMaritalStatus = "Separated";
            } else {
                selectedMaritalStatus = "Registered Partnership";
            }

            intent.putExtra("selectedmaritalstatus", selectedMaritalStatus);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
