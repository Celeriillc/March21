package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditTeacherProfileActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherAttendanceActivity;
import com.celerii.celerii.Activities.StudentPerformance.EnterResultsActivity;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Settings.SettingsActivityTeacher;
import com.celerii.celerii.Activities.StudentPerformance.History.StudentAcademicHistoryActivity;
import com.celerii.celerii.Activities.Utility.SwitchActivityTeacherParent;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherPerformanceRowActivity;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherTakeAttendanceActivity;
import com.celerii.celerii.Activities.Timetable.TeacherTimetableActivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.models.MoreTeacherHeaderModel;
import com.celerii.celerii.models.MoreTeachersModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/8/2017.
 */

public class MoreTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MoreTeachersModel> moreTeachersModelList;
    private Context context;
    private MoreTeacherHeaderModel moreTeacherHeaderModel;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;
    private int lastSelectedPosition;
    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    ArrayList<String> childrenFirebase = new ArrayList<>();
    ArrayList<String> childIdsFirebase = new ArrayList<>();
    ArrayList<String> childNameFirebase = new ArrayList<>();
    ArrayList<String> childURLFirebase = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className;
        public ImageView classPic;
        public RadioButton selectedClass;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            classPic = (ImageView) view.findViewById(R.id.classpic);
            selectedClass = (RadioButton) view.findViewById(R.id.selectedclass);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView teacherName, editMyProfile, noClassLabel;
        ImageView teacherProfilePic;

        public HeaderViewHolder(View view) {
            super(view);
            teacherName = (TextView) view.findViewById(R.id.myprofilename);
            editMyProfile = (TextView) view.findViewById(R.id.editmyprofile);
            noClassLabel = (TextView) view.findViewById(R.id.noclasslabel);
            teacherProfilePic = (ImageView) view.findViewById(R.id.myprofileimage);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout attendanceRecordsLayout, attendanceLayout, timetableLayout, enterClassResultLayout, personalPerformanceLayout, studentPerformanceLayout, eventsLayout, newslettersLayout,
            settingsLayout, switchAccountLayout, logoutLayout;
        TextView attendanceRecords, attendance, timetable, enterClassResult, personalPerformance, studentPerformance, events, newsletters, settings, switchAccount, logout;
        TextView attendanceRecordsMarker, attendanceMarker, timetableMarker, enterClassResultMarker, personalPerformanceMarker, studentPerformanceMarker, eventsMarker, newslettersMarker,
                settingsMarker, switchAccountMarker, logoutMarker;

        public FooterViewHolder(View view) {
            super(view);
            attendanceLayout = (LinearLayout) view.findViewById(R.id.attendanceLayout);
            attendanceRecordsLayout = (LinearLayout) view.findViewById(R.id.attendancerecordsLayout);
            timetableLayout = (LinearLayout) view.findViewById(R.id.timetableLayout);
            enterClassResultLayout = (LinearLayout) view.findViewById(R.id.enterclassresultLayout);
            personalPerformanceLayout = (LinearLayout) view.findViewById(R.id.personalperformanceLayout);
            studentPerformanceLayout = (LinearLayout) view.findViewById(R.id.studentperformanceLayout);
            eventsLayout = (LinearLayout) view.findViewById(R.id.eventsLayout);
            newslettersLayout = (LinearLayout) view.findViewById(R.id.newslettersLayout);
            settingsLayout = (LinearLayout) view.findViewById(R.id.settingsLayout);
            switchAccountLayout = (LinearLayout) view.findViewById(R.id.switchaccountLayout);
            logoutLayout = (LinearLayout) view.findViewById(R.id.logoutLayout);

            attendance = (TextView) view.findViewById(R.id.attendance);
            attendanceRecords = (TextView) view.findViewById(R.id.attendancerecords);
            timetable = (TextView) view.findViewById(R.id.timetable);
            enterClassResult = (TextView) view.findViewById(R.id.enterclassresult);
            personalPerformance = (TextView) view.findViewById(R.id.personalperformance);
            studentPerformance = (TextView) view.findViewById(R.id.studentperformance);
            events = (TextView) view.findViewById(R.id.events);
            newsletters = (TextView) view.findViewById(R.id.newsletters);
            settings = (TextView) view.findViewById(R.id.settings);
            switchAccount = (TextView) view.findViewById(R.id.switchaccount);
            logout = (TextView) view.findViewById(R.id.logout);

            attendanceMarker = (TextView) view.findViewById(R.id.attendancemarker);
            attendanceRecordsMarker = (TextView) view.findViewById(R.id.attendancerecordsmarker);
            timetableMarker = (TextView) view.findViewById(R.id.timetablemarker);
            enterClassResultMarker = (TextView) view.findViewById(R.id.enterclassresultmarker);
            personalPerformanceMarker = (TextView) view.findViewById(R.id.personalperformancemarker);
            studentPerformanceMarker = (TextView) view.findViewById(R.id.studentperformancemarker);
            eventsMarker = (TextView) view.findViewById(R.id.eventsmarker);
            newslettersMarker = (TextView) view.findViewById(R.id.newslettersmarker);
            settingsMarker = (TextView) view.findViewById(R.id.settingsmarker);
            switchAccountMarker = (TextView) view.findViewById(R.id.switchaccountmarker);
            logoutMarker = (TextView) view.findViewById(R.id.logoutmarker);
        }
    }

    public MoreTeacherAdapter(List<MoreTeachersModel> moreTeachersModelList, MoreTeacherHeaderModel moreTeacherHeaderModel,
                        Context context) {
        this.moreTeachersModelList = moreTeachersModelList;
        this.moreTeacherHeaderModel = moreTeacherHeaderModel;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        sharedPreferencesManager = new SharedPreferencesManager(context);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(context);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(context);
        if (moreTeachersModelList.size() == 0){
            lastSelectedPosition = -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_body_teacher, parent, false);
                return new MoreTeacherAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_header_teacher, parent, false);
                return new MoreTeacherAdapter.HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_footer_teacher, parent, false);
                return new MoreTeacherAdapter.FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_body_teacher, parent, false);
                return new MoreTeacherAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).teacherName.setText(moreTeacherHeaderModel.getTeacherName());

            if (moreTeachersModelList.size() <= 2){
                ((HeaderViewHolder) holder).noClassLabel.setVisibility(View.VISIBLE);
            } else {
                ((HeaderViewHolder) holder).noClassLabel.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(moreTeacherHeaderModel.getTeacherImageURL())
                    .centerCrop()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((HeaderViewHolder) holder).teacherProfilePic);

            ((HeaderViewHolder) holder).editMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, EditTeacherProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putString("id", auth.getCurrentUser().getUid());
                    context.startActivity(I);
                }
            });

            ((HeaderViewHolder) holder).teacherName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", auth.getCurrentUser().getUid());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });

            ((HeaderViewHolder) holder).teacherProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherProfileOneActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", auth.getCurrentUser().getUid());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });
        }
        else if (holder instanceof FooterViewHolder) {

            String att = "Attendance";
            String atr = "Attendance Records";
            String ttb = "My TimeTable";
            String ecr = "Enter Class Results";
            String mpef = "My Performance";
            String cpef = "Class Performance";
            String evt = "Events";
            String nws = "Newsletters";
            String set = "Settings";
            String swt = "Switch to Parenting";
            String lgt = "Logout";

            String activeClass = null;
            activeClass = sharedPreferencesManager.getActiveClass();
            if (activeClass != null){
                String className = activeClass.split(" ")[1];
                att = className + "'s Attendance";
                atr = className + "'s Attendance Records";
                ecr = "Enter Results for " + className ;
                cpef = className + "'s Performance";
            }

            ((FooterViewHolder) holder).attendance.setText(att);
            ((FooterViewHolder) holder).attendanceRecords.setText(atr);
            ((FooterViewHolder) holder).timetable.setText(ttb);
            ((FooterViewHolder) holder).enterClassResult.setText(ecr);
            ((FooterViewHolder) holder).personalPerformance.setText(mpef);
            ((FooterViewHolder) holder).studentPerformance.setText(cpef);
            ((FooterViewHolder) holder).events.setText(evt);
            ((FooterViewHolder) holder).newsletters.setText(nws);
            ((FooterViewHolder) holder).settings.setText(set);
            ((FooterViewHolder) holder).switchAccount.setText(swt);
            ((FooterViewHolder) holder).logout.setText(lgt);

            ((FooterViewHolder) holder).attendanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent I = new Intent(context, TeacherAttendanceActivity.class);
                    Intent I = new Intent(context, TeacherTakeAttendanceActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).attendanceRecordsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherAttendanceActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).timetableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherTimetableActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).enterClassResultLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, EnterResultsActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).personalPerformanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, TeacherPerformanceRowActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).studentPerformanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, StudentAcademicHistoryActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).eventsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, EventsRowActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).newslettersLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, NewsletterRowActivity.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).settingsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, SettingsActivityTeacher.class);
                    context.startActivity(I);
                }
            });
            ((FooterViewHolder) holder).switchAccountLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, SwitchActivityTeacherParent.class);
                    context.startActivity(I);
                    ((Activity)context).finish();
//                    Toast.makeText(context, "switchAccountLayout clicked", Toast.LENGTH_SHORT).show();
//                    loginParent("Parent");
                }
            });
            ((FooterViewHolder) holder).logoutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferencesManager.deleteActiveAccount();
                    sharedPreferencesManager.deleteMyUserID();
                    sharedPreferencesManager.deleteMyFirstName();
                    sharedPreferencesManager.deleteMyLastName();
                    sharedPreferencesManager.deleteMyPicURL();
                    sharedPreferencesManager.deleteActiveKid();
                    sharedPreferencesManager.deleteActiveClass();
                    sharedPreferencesManager.deleteMyClasses();
                    sharedPreferencesManager.deleteMyChildren();
                    Intent I = new Intent(context, IntroSlider.class);
                    context.startActivity(I);
                    auth.signOut();
                    ((Activity)context).finish();
                }
            });

        }
        else if (holder instanceof MyViewHolder) {
            final MoreTeachersModel moreTeachersModel = moreTeachersModelList.get(position);

            if (moreTeachersModelList.size() == 0){
                return;
            }

            String activeClass = null;
            activeClass = sharedPreferencesManager.getActiveClass();
            if (activeClass != null) {
                activeClass = activeClass.split(" ")[0];
            }

            if (activeClass == null){
                lastSelectedPosition = 1;
            }

            if (activeClass != null) {
                if (activeClass.equals(moreTeachersModel.getClassId())) {
                    lastSelectedPosition = position;
                }
            }

            ((MyViewHolder) holder).className.setText(moreTeachersModel.getClassName());
            if (lastSelectedPosition == position) {
                ((MyViewHolder) holder).selectedClass.setChecked(true);
                sharedPreferencesManager.setActiveClass(moreTeachersModel.getClassId() + " " + moreTeachersModel.getClassName());
            } else {
                ((MyViewHolder) holder).selectedClass.setChecked(false);
            }

            Glide.with(context)
                    .load(moreTeachersModel.getClassPicUrl())
                    .centerCrop()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).classPic);

            ((MyViewHolder) holder).selectedClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = position;

                    String id = moreTeachersModel.getClassId();
                    String name = moreTeachersModel.getClassName();
                    String clas = id + " " + name;

                    sharedPreferencesManager.setActiveClass(clas);
                    teacherTakeAttendanceSharedPreferences.deleteSubject();
                    teacherEnterResultsSharedPreferences.deleteSubject();

                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return moreTeachersModelList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader (position)) {
            return Header;
        } else if(isPositionFooter (position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == moreTeachersModelList.size() - 1;
    }

    void loginParent(final String activeRole){
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    final String activeAccount = activeRole;
                    final String activeUserId = mFirebaseUser.getUid();
                    final String firstName = parent.getFirstName();
                    final String lastName = parent.getLastName();
                    final String activeUserURL = parent.getProfilePicURL();

                    //Clear Shared Preferences
                    sharedPreferencesManager.deleteActiveAccount();
                    sharedPreferencesManager.deleteMyUserID();
                    sharedPreferencesManager.deleteMyFirstName();
                    sharedPreferencesManager.deleteMyLastName();
                    sharedPreferencesManager.deleteMyPicURL();
                    sharedPreferencesManager.deleteActiveKid();
                    sharedPreferencesManager.deleteActiveClass();
                    sharedPreferencesManager.deleteMyClasses();
                    sharedPreferencesManager.deleteMyChildren();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                                    final String childKey = postSnapShot.getKey();

                                    mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Student childInstance = dataSnapshot.getValue(Student.class);
                                                childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                                sharedPreferencesManager.deleteMyChildren();
                                                sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                sharedPreferencesManager.setActiveAccount(activeAccount);
                                sharedPreferencesManager.setMyUserID(activeUserId);
                                sharedPreferencesManager.setMyFirstName(firstName);
                                sharedPreferencesManager.setMyLastName(lastName);
                                sharedPreferencesManager.setMyPicURL(activeUserURL);
                                Intent intent = new Intent(context, ParentMainActivityTwo.class);
                                ((Activity)context).startActivity(intent);
                                ((Activity)context).finish();
                            } else {
                                sharedPreferencesManager.setActiveAccount(activeAccount);
                                sharedPreferencesManager.setMyUserID(activeUserId);
                                sharedPreferencesManager.setMyFirstName(firstName);
                                sharedPreferencesManager.setMyLastName(lastName);
                                sharedPreferencesManager.setMyPicURL(activeUserURL);
                                Intent intent = new Intent(context, ParentMainActivityTwo.class);
                                ((Activity)context).startActivity(intent);
                                ((Activity)context).finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(mFirebaseUser.getUid());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                Parent parent = new Parent(teacher.getFirstName(), teacher.getLastName(), teacher.getEmail(), teacher.getGender());
                                parent.setOccupation("School Teacher");
                                Map<String, Object> parentObject = new HashMap<String, Object>();
                                parentObject.put("Parent/" + mFirebaseUser.getUid(), parent);
                                mDatabaseReference = mFirebaseDatabase.getReference();
                                mDatabaseReference.updateChildren(parentObject, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        CustomToast.whiteBackgroundBottomToast(context, "Parent Account Created");
                                        loginParent("Parent");
                                    }
                                });
                            } else {
                                CustomToast.whiteBackgroundBottomToast(context, "Teacher account doesn't exist!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
