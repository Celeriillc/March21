package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ManageKidsModel;
import com.celerii.celerii.models.Student;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/14/2017.
 */

public class ManageKidsAdapter extends RecyclerView.Adapter<ManageKidsAdapter.MyViewHolder>{

    private List<ManageKidsModel> manageKidsModelList;
    private Context context;
    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    SharedPreferencesManager sharedPreferencesManager;
    ArrayList<String> childrenFirebase = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kidName;
        public ImageView kidPic;
        public View clickableView;
        public RelativeLayout backGround, foreGround;

        public MyViewHolder(final View view) {
            super(view);
            kidName = (TextView) view.findViewById(R.id.kidname);
            kidPic = (ImageView) view.findViewById(R.id.kidpic);
            clickableView = view;
            backGround = (RelativeLayout) view.findViewById(R.id.view_background);
            foreGround = (RelativeLayout) view.findViewById(R.id.view_foreground);
        }
    }

    public ManageKidsAdapter(List<ManageKidsModel> manageKidsModelList, Context context) {
        this.manageKidsModelList = manageKidsModelList;
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_kids_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ManageKidsModel manageKidsModel = manageKidsModelList.get(position);

        holder.kidName.setText(manageKidsModel.getName());
        if (!manageKidsModel.getPicURL().isEmpty()) {
            Glide.with(context)
                    .load(manageKidsModel.getPicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.kidPic);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.kidPic);
        }
        final String childID = manageKidsModel.getID();


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("childID", childID);
                Intent I = new Intent(context, StudentProfileActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return manageKidsModelList.size();
    }

    public void removeItem(int position) {
        if (manageKidsModelList.size() == 1){
            sharedPreferencesManager.deleteActiveKid();
        }
        ManageKidsModel manageKidsModel = manageKidsModelList.get(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()

        mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid()).child(manageKidsModel.getID()).setValue(null);
        manageKidsModelList.remove(position);

        sharedPreferencesManager.deleteMyChildren();

        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
//                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        notifyItemRemoved(position);
    }

    public void restoreItem(ManageKidsModel manageKidsModel, int position) {
        mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid()).child(manageKidsModel.getID()).setValue(true);
        manageKidsModelList.add(position, manageKidsModel);

        sharedPreferencesManager.deleteMyChildren();

        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
//                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // notify item added by position
        notifyItemInserted(position);
    }
}
