package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GeneralEditActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;
    Button editButton;

    String featureUseKey = "";
    String featureName = "Edit General";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_edit);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        final String caption = b.getString("Caption");
        String description = b.getString("Description");
        String editHint = b.getString("EditHint");
        String edit = b.getString("EditItem");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit " + caption);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        captionView = (TextView) findViewById(R.id.caption);
        descriptionView = (TextView) findViewById(R.id.description);
        editItem = (EditText) findViewById(R.id.edititem);
        editButton = (Button) findViewById(R.id.edit);

        captionView.setText(caption);
        descriptionView.setText(description);
        editItem.setHint(editHint);
        editItem.setText(edit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Validate against null values
                if (caption.equals("First Name") || caption.equals("Last Name") || caption.equals("Middle Name")) {
                    if (!validateName(editItem.getText().toString().trim())) {
                        return;
                    }
                } else if (caption.equals("Phone Number") || caption.equals("Occupation")) {
                    if (!validateEmpty(editItem.getText().toString().trim())) {
                        return;
                    }
                }

                Intent intent = new Intent();
                String text = editItem.getText().toString().trim();
                intent.putExtra("Caption", text);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean validateName(String nameString) {
        if (nameString.isEmpty()) {
            String messageString = "You need to enter a valid name in Name field";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            return false;
        }

        String[] nameArray = nameString.split(" ");
        if (nameArray.length > 1) {
            String messageString = "You should enter only one name in this field. If you have a double name, you can separate them with a hyphen (-). E.g. Ava-Grace.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    private boolean validateEmpty(String nameString) {
        if (nameString.isEmpty()) {
            String messageString = "Entry is invalid";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            return false;
        }

        return true;
    }

    void showDialogWithMessage (String messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
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
