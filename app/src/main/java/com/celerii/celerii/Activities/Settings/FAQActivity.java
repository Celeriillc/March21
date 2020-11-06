package com.celerii.celerii.Activities.Settings;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.celerii.celerii.R;

public class FAQActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView header, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Bundle bundle = getIntent().getExtras();
        String headerString = bundle.getString("header");
        String bodyString = bundle.getString("body");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FAQ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        header = (TextView) findViewById(R.id.faqheader);
        body = (TextView) findViewById(R.id.faqbody);

        header.setText(headerString);
        body.setText(bodyString);
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
