package com.celerii.celerii.Activities.StudentPerformance;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceFutureAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.PerformanceFutureModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FutureFragment extends Fragment {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<PerformanceFutureModel> performanceFutureModelList;
    public RecyclerView recyclerView;
    public PerformanceFutureAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String featureUseKey = "";
    String featureName = "Predicted Academic Results";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public FutureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        context = getActivity();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        performanceFutureModelList = new ArrayList<>();
        yeah();
        mAdapter = new PerformanceFutureAdapter(performanceFutureModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    public void yeah() {
        PerformanceFutureModel model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);

        model = new PerformanceFutureModel("English Language", 80, 90);
        performanceFutureModelList.add(model);
    }
}
