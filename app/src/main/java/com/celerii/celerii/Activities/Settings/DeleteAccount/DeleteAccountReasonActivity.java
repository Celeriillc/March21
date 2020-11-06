package com.celerii.celerii.Activities.Settings.DeleteAccount;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DeleteAccountReasonActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    RadioGroup reportingGroup;
    RadioButton loginSignUp, classFeed, assignmentFeed, notification, other;
    TextView name;
    ImageView profilePicture;
    LinearLayout profilePictureClipper;
    EditText otherEditText;
    Button continueButton;

    String reasonForDelete = "";

    String featureUseKey = "";
    String featureName = "Delete Account Reason For Delete";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_reason);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delete Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        reportingGroup = (RadioGroup) findViewById(R.id.reportinggroup);
        loginSignUp = (RadioButton) findViewById(R.id.loginsignup);
        classFeed = (RadioButton) findViewById(R.id.classfeed);
        assignmentFeed = (RadioButton) findViewById(R.id.assignmentfeed);
        notification = (RadioButton) findViewById(R.id.notifications);
        other = (RadioButton) findViewById(R.id.other);
        name = (TextView) findViewById(R.id.name);
        profilePicture = (ImageView) findViewById(R.id.profilepicture);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        otherEditText = (EditText) findViewById(R.id.otheredittext);
        continueButton = (Button) findViewById(R.id.continuebutton);

        name.setText(sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName());

        Drawable textDrawable;
        if (!sharedPreferencesManager.getMyFirstName().isEmpty() && !sharedPreferencesManager.getMyLastName().isEmpty()) {
            String[] nameArray = (sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName()).split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
            }
            profilePicture.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            Glide.with(this)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(profilePicture);
        }

        loginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginSignUp.isChecked() && otherEditText.getVisibility() == View.VISIBLE) {
                    collapse(otherEditText);
                }
            }
        });

        classFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classFeed.isChecked() && otherEditText.getVisibility() == View.VISIBLE) {
                    collapse(otherEditText);
                }
            }
        });

        assignmentFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assignmentFeed.isChecked() && otherEditText.getVisibility() == View.VISIBLE) {
                    collapse(otherEditText);
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isChecked() && otherEditText.getVisibility() == View.VISIBLE) {
                    collapse(otherEditText);
                }
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (other.isChecked() && otherEditText.getVisibility() == View.GONE) {
                    expand(otherEditText);
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReasonForDelete();

                if (reasonForDelete.trim().isEmpty()) {
                    String message = "If you selected " + "<b>" + "Other" + "</b>" + ", you need to specify a reason for deleting your account in the textbox";
                    showDialogWithMessage(Html.fromHtml(message));
                    return;
                }

                Bundle bundle = new Bundle();
                Intent intent = new Intent(DeleteAccountReasonActivity.this, DeleteAccountConfirmPasswordActivity.class);
                bundle.putString("Reason For Delete", reasonForDelete);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setReasonForDelete() {
        if (loginSignUp.isChecked()){ reasonForDelete = loginSignUp.getText().toString(); }
        else if (classFeed.isChecked()){ reasonForDelete = classFeed.getText().toString(); }
        else if (assignmentFeed.isChecked()){ reasonForDelete =  assignmentFeed.getText().toString(); }
        else if (notification.isChecked()){ reasonForDelete =  notification.getText().toString(); }
        else if (other.isChecked()){ reasonForDelete =  otherEditText.getText().toString(); }
    }

    void showDialogWithMessage (Spanned messageString) {
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void expand(EditText editText) {
        //set Visible
        editText.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        editText.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, editText.getMeasuredHeight(), editText);
        mAnimator.start();
    }

    private void collapse(final EditText editText) {
        int finalHeight = editText.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, editText);

        mAnimator.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //set Visible
                editText.setVisibility(View.GONE);
            }
        }, 500);
    }

    private ValueAnimator slideAnimator(int start, int end, final EditText editText) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
                layoutParams.height = value;
                editText.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
