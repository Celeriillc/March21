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
import com.celerii.celerii.helperClasses.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterResultsEditPercentageOfTotalScoreActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;
    Double previousPercentageOfTotal = 0.0;
    String subjectYearTerm, classSubjectYearTerm, activeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_percentage_of_total_score);

        Bundle bundle = getIntent().getExtras();
        previousPercentageOfTotal = bundle.getDouble("PreviousPercentageOfTotal");
        subjectYearTerm = bundle.getString("SubjectYearTerm");
        classSubjectYearTerm = bundle.getString("ClassSubjectYearTerm");
        activeClass = bundle.getString("ClassID");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(classSubjectYearTerm).child("percentageOfTotal");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = Double.valueOf(dataSnapshot.getValue(String.class));
                } else {
                    previousPercentageOfTotal = 0.0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter Percentage of Total");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        captionView = (TextView) findViewById(R.id.caption);
        descriptionView = (TextView) findViewById(R.id.description);
        editItem = (EditText) findViewById(R.id.edititem);
        editItem.setText(bundle.getString("PercentageOfTotal"));
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
            //TODO: Validate against null values
            Intent intent = new Intent();
            String percentageOfTotal = editItem.getText().toString().trim();

            if (!validatePercentageOfTotal(percentageOfTotal))
                return false;

            if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
            }

            if ((Double.valueOf(percentageOfTotal) + previousPercentageOfTotal) > 100.0){
                CustomToast.whiteBackgroundBottomToast(EnterResultsEditPercentageOfTotalScoreActivity.this, "Error: The percentage of total is more than 100");
                return false;
            }

            intent.putExtra("PercentageOfTotal", percentageOfTotal);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validatePercentageOfTotal(String percentageOfTotal) {
        if (percentageOfTotal.isEmpty()) {
            String messageString = "You need to enter the percentage this test constitutes of the total.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        if (!isNumeric(percentageOfTotal)) {
            String messageString = "You need to enter only numeric values.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        if (Integer.valueOf(percentageOfTotal) < 0 || Integer.valueOf(percentageOfTotal) > 100) {
            String messageString = "The Percentage of Total must be a whole number between 0 and 100 (it is a percentage)";
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
