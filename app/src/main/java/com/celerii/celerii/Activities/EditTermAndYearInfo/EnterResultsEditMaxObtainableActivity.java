package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;

public class EnterResultsEditMaxObtainableActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_max_obtainable);

        Bundle bundle = getIntent().getExtras();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter Maximum Score Obtainable");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        captionView = (TextView) findViewById(R.id.caption);
        descriptionView = (TextView) findViewById(R.id.description);
        editItem = (EditText) findViewById(R.id.edititem);
        editItem.setText(bundle.getString("Maximum Obtainable"));
        editItem.setSelectAllOnFocus(true);

        //Show keyboard by default
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editItem.getWindowToken(), 0);
            finish();
        }
        else if (id == R.id.action_send){
            String maxScore = editItem.getText().toString().trim();
            if (!validateMaxScore(maxScore))
                return false;

            Intent intent = new Intent();
            intent.putExtra("Max Obtainable", maxScore);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateMaxScore(String maxScore) {
        if (maxScore.isEmpty()) {
            String messageString = "You need to enter the maximum score obtainable for this test.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        if (!isNumeric(maxScore)) {
            String messageString = "You need to enter only numeric values.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
