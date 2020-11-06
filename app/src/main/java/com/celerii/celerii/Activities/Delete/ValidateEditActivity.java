package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.MyTextWatcher;
import com.celerii.celerii.helperClasses.Validate;

public class ValidateEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;
    Validate validate;
    TextInputLayout editItemLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_edit);

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
        editItemLayout = (TextInputLayout) findViewById(R.id.edititemlayout);

        validate = new Validate(this);
        editItem.addTextChangedListener(new MyTextWatcher(validate, editItem, "name", editItemLayout));

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
            if (!validate.validateName(editItem, editItemLayout))
            {
                return false;
            }
            Intent intent = new Intent();
            String text = editItem.getText().toString();
            intent.putExtra("Caption", text);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
