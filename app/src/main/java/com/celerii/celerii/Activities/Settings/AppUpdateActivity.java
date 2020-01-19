package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;

public class AppUpdateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RadioButton autoUpdate, notify, notAutoUpdate;
    RadioGroup updateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Updates");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        autoUpdate = (RadioButton) findViewById(R.id.autoupdate);
        notify = (RadioButton) findViewById(R.id.notify);
        notAutoUpdate = (RadioButton) findViewById(R.id.notautoupdate);
        updateGroup = (RadioGroup) findViewById(R.id.updategroup);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
