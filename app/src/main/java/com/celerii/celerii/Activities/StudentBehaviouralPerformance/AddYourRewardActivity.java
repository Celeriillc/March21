package com.celerii.celerii.Activities.StudentBehaviouralPerformance;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddYourRewardActivity extends AppCompatActivity {
    Context context = this;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;

    Bundle bundle;
    String tag, target;

    TextView header, maxCharacters;
    EditText rewardOrPunishment;
    Button add;
    int maxNumberOfCharacters = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_your_reward);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        tag = bundle.getString("Tag");
        target = bundle.getString("Target");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        header = (TextView) findViewById(R.id.header);
        maxCharacters = (TextView) findViewById(R.id.maxcharacters);
        rewardOrPunishment = (EditText) findViewById(R.id.rewardorpunishment);
        add = (Button) findViewById(R.id.add);

        rewardOrPunishment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxNumberOfCharacters)});

        if (tag.equals("Reward")) {
            header.setText("Add Your Reward");
            rewardOrPunishment.setHint("New Reward");
            getSupportActionBar().setTitle("New Reward");
            add.setText("Add Reward");
//            add.setBackgroundColor(getResources().getColor(R.color.colorButtonBlue));
        } else if (tag.equals("Punishment")) {
            header.setText("Add Your Punishment");
            rewardOrPunishment.setHint("New Punishment");
            getSupportActionBar().setTitle("New Punishment");
            add.setText("Add Punishment");
//            add.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rewardOrPunishmentString = rewardOrPunishment.getText().toString().trim();
                if (rewardOrPunishmentString.isEmpty()) {
                    return;
                }

                rewardOrPunishmentString = rewardOrPunishmentString.substring(0, 1).toUpperCase() + rewardOrPunishmentString.substring(1);

                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                if (tag.equals("Reward")) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherBehaviouralRewardsCustom").child(auth.getCurrentUser().getUid()).child("Rewards").push();
                    String pushKey = mDatabaseReference.getKey();
                    Map<String, Object> updater = new HashMap<String, Object>();
                    updater.put("TeacherBehaviouralRewardsCustom/" + auth.getCurrentUser().getUid() + "/Rewards/" + pushKey, rewardOrPunishmentString);
                    mDatabaseReference = mFirebaseDatabase.getReference();
                    mDatabaseReference.updateChildren(updater);
                    finish();
                } else {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherBehaviouralRewardsCustom").child(auth.getCurrentUser().getUid()).child("Punishments").push();
                    String pushKey = mDatabaseReference.getKey();
                    Map<String, Object> updater = new HashMap<String, Object>();
                    updater.put("TeacherBehaviouralRewardsCustom/" + auth.getCurrentUser().getUid() + "/Punishments/" + pushKey, rewardOrPunishmentString);
                    mDatabaseReference = mFirebaseDatabase.getReference();
                    mDatabaseReference.updateChildren(updater);
                    finish();
                }
            }
        });

        rewardOrPunishment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int presentNumOfChars = rewardOrPunishment.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int presentNumOfChars = rewardOrPunishment.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }

            @Override
            public void afterTextChanged(Editable s) {
                int presentNumOfChars = rewardOrPunishment.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
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
