package com.celerii.celerii.Activities.Timetable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditClassActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditDayActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.TeacherTimetableModel;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewTimetableActivity extends AppCompatActivity implements TimePickerDialogFragment.TimePickerDialogHandler {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    CustomProgressDialogOne customProgressDialogOne;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private EditText className, subject, day, time, duration;
    Button save;

    ArrayList<String> subjectList = new ArrayList<>();

    String activeClassID = "";
    String activeClassName = "";
    String activeClass;
    Class activeClassModel;
    String hourOfTheDay;
    String minute;
    String zone;

    String featureUseKey = "";
    String featureName = "Add New Timetable Item";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_timetable);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        customProgressDialogOne = new CustomProgressDialogOne(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Timetable Activity");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        className = (EditText) findViewById(R.id.classname);
        subject = (EditText) findViewById(R.id.subject);
        day = (EditText) findViewById(R.id.dayoftheweek);
        time = (EditText) findViewById(R.id.timeoftheday);
        duration = (EditText) findViewById(R.id.duration);
        save = (Button) findViewById(R.id.save);

        activeClass = sharedPreferencesManager.getActiveClass();

        if (activeClass == null) {
            Gson gson = new Gson();
            ArrayList<Class> myClasses = new ArrayList<>();
            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            Type type = new TypeToken<ArrayList<Class>>() {}.getType();
            myClasses = gson.fromJson(myClassesJSON, type);

            if (myClasses != null) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                    gson = new Gson();
                    type = new TypeToken<Class>() {}.getType();
                    activeClassModel = gson.fromJson(activeClass, type);
                    activeClassID = activeClassModel.getID();
                    activeClassName = activeClassModel.getClassName();
                    className.setText(activeClassName);
                } else {
                    showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            } else {
                showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                return;
            }
        } else {
            Boolean activeClassExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Class>() {}.getType();
            activeClassModel = gson.fromJson(activeClass, type);

            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);

            for (Class classInstance: myClasses) {
                if (activeClassModel.getID().equals(classInstance.getID())) {
                    activeClassExist = true;
                    activeClassModel = classInstance;
                    activeClass = gson.toJson(activeClassModel);
                    sharedPreferencesManager.setActiveClass(activeClass);
                    break;
                }
            }

            if (!activeClassExist) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                } else {
                    showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            }

            type = new TypeToken<Class>() {}.getType();
            activeClassModel = gson.fromJson(activeClass, type);
            activeClassID = activeClassModel.getID();
            activeClassName = activeClassModel.getClassName();
            className.setText(activeClassName);
        }

        Gson gson = new Gson();
        subjectList = new ArrayList<>();
        String subjectJSON = sharedPreferencesManager.getSubjects();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        subjectList = gson.fromJson(subjectJSON, type);

        if (subjectList == null) {
            subjectList = new ArrayList<>();
            showDialogWithMessageAndDisconnect("There are no subjects to assign to your timetable. If you're not connected to a school, use the search feature to search for a school and send a request.");
            return;
        } else {
            subject.setText(subjectList.get(0));
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
        day.setText(getDayOfTheWeek(dayOfTheWeek));

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hourOfTheDay = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        minute = String.valueOf(Date.makeTwoDigits(calendar.get(Calendar.MINUTE)));
        if (hour < 13) {
            time.setText((hourOfTheDay) + ":" + (minute) + " AM");
            zone = "AM";
        } else {
            time.setText((hour - 12) + ":" + (minute) + " PM");
            zone = "PM";
        }

        duration.setText("45 Minutes");

        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTimetableActivity.this, EditClassActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Class", activeClass);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTimetableActivity.this, EnterResultsEditSubjectsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Activity", "AddNewTimetable");
                bundle.putString("Subject", subject.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTimetableActivity.this, EditDayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Day", day.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerBuilder tpb = new TimePickerBuilder().setFragmentManager(getSupportFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment);
                tpb.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    showDialogWithMessage("Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                if (day.getText().toString().equals("Saturday") || day.getText().toString().equals("Sunday")) {
                    showDialogWithMessage("Timetable is not able to show entries for Saturdays and Sundays. Select a day from Monday to Friday.");
                    return;
                }

                customProgressDialogOne.show();

                mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Timetable").child(mFirebaseUser.getUid()).push();
                String pushKey = mDatabaseReference.getKey();

                String hour = "";
                if (Integer.parseInt(hourOfTheDay) < 13) {
                    hour = hourOfTheDay;
                } else {
                    hour = String.valueOf(Integer.parseInt(hourOfTheDay) - 12);
                }
                hour = Date.makeTwoDigits(hour);
                minute = Date.makeTwoDigits(minute);

                TeacherTimetableModel teacherTimetableModel = new TeacherTimetableModel(mFirebaseUser.getUid(), activeClassID, activeClassName,
                        pushKey, subject.getText().toString(), day.getText().toString(), hourOfTheDay + ":" + minute,
                        duration.getText().toString().replace(" Minutes", ""), zone);

                HashMap<String, Object> timetableMap = new HashMap<>();
                timetableMap.put("Teacher Timetable/" + mFirebaseUser.getUid() + "/" + pushKey, teacherTimetableModel);
                timetableMap.put("Class Timetable/" + activeClassID + "/" + pushKey, teacherTimetableModel);

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(timetableMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            customProgressDialogOne.dismiss();
                            String message = "Your timetable has been updated";
                            showDialogWithMessageAndDisconnect(message);
                        } else {
                            customProgressDialogOne.dismiss();
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
            }
        });

        duration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!duration.getText().toString().endsWith(" Minutes")) {
                        String durationString = duration.getText().toString() + " Minutes";
                        duration.setText(durationString);
                    }
                } else {
                    duration.setText(duration.getText().toString().replace(" Minutes", ""));
                }
            }
        });
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        int hour = hourOfDay;
        hourOfTheDay = String.valueOf(hourOfDay);
        this.minute = String.valueOf(minute);
        if (hour < 13) {
            time.setText((hourOfTheDay) + ":" + Date.makeTwoDigits(minute) + " AM");
            zone = "AM";
        } else {
            time.setText((hour - 12) + ":" + (minute) + " PM");
            zone = "PM";
        }
    }

    String getDayOfTheWeek(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Sunday";
        }
    }

    void showDialogWithMessageAndDisconnect(String messageString) {
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
                finish();
                dialog.dismiss();
            }
        });
    }

    void showDialogWithMessage(String messageString) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                activeClass = data.getStringExtra("Selected Class");
                Gson gson = new Gson();
                Type type = new TypeToken<Class>() {}.getType();
                activeClassModel = gson.fromJson(activeClass, type);
                activeClassID = activeClassModel.getID();
                activeClassName = activeClassModel.getClassName();
                className.setText(activeClassName);
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                subject.setText(data.getStringExtra("Selected Subject"));
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                day.setText(data.getStringExtra("Selected Day"));
            }
        }
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
