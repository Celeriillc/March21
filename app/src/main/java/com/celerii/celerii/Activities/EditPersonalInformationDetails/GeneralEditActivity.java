package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;

public class GeneralEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_edit);

        Bundle b = getIntent().getExtras();
        String caption = b.getString("Caption");
        String description = b.getString("Description");
        String editHint = b.getString("EditHint");
        String edit = b.getString("EditItem");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit " + caption);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        captionView = (TextView) findViewById(R.id.caption);
        descriptionView = (TextView) findViewById(R.id.description);
        editItem = (EditText) findViewById(R.id.edititem);

        captionView.setText(caption);
        descriptionView.setText(description);
        editItem.setHint(editHint);
        editItem.setText(edit);

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
            String text = editItem.getText().toString();
            intent.putExtra("Caption", text);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
