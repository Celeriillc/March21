package com.celerii.celerii.Activities.StudentPerformance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceFutureAdapter;
import com.celerii.celerii.models.PerformanceFutureModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FutureFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private ArrayList<PerformanceFutureModel> performanceFutureModelList;
    public RecyclerView recyclerView;
    public PerformanceFutureAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    public FutureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        performanceFutureModelList = new ArrayList<>();
        yeah();
        mAdapter = new PerformanceFutureAdapter(performanceFutureModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
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
