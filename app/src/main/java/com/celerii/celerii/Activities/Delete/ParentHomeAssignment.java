package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassAssignmentAdapter;
import com.celerii.celerii.models.ClassAssignment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentHomeAssignment extends Fragment {

    private ArrayList<ClassAssignment> classAssignmentList;
    public RecyclerView recyclerView;
    public ClassAssignmentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Integer mCurCheckPosition = 0;

    public ParentHomeAssignment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home_assignment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        classAssignmentList = new ArrayList<>();
        yeah();
        mAdapter = new ClassAssignmentAdapter(classAssignmentList, getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
    }

    public void yeah()
    {
        ClassAssignment classAssignment = new ClassAssignment("This is the first assignment post, lets see how many of you will default :)",
                "3 hours ago", "20 June 2018", "Due", "Esther Oriabure", "https://static.giantbomb.com/uploads/original/0/4018/440698-r168707_630368.jpg",
                19, 34, "SSS 3 Alpha", "", "https://static.giantbomb.com/uploads/original/0/5668/387994-cristiano_ronaldo_biography_2.jpg");
        classAssignmentList.add(classAssignment);

        classAssignment = new ClassAssignment("", "5 hours ago", "20 June 2018", "Due", "Esther Oriabure", "https://static.giantbomb.com/uploads/original/0/4018/440698-r168707_630368.jpg", 19, 34, "Toulouse", "", "https://static.giantbomb.com/uploads/original/0/5668/387994-cristiano_ronaldo_biography_2.jpg");
        classAssignmentList.add(classAssignment);

        classAssignment = new ClassAssignment("This is the first assignment post, lets see how many of you will default :)", "7 hours ago", "20 June 2018", "Due", "Esther Oriabure", "", 19, 34, "Toulouse", "", "");
        classAssignmentList.add(classAssignment);

        classAssignment = new ClassAssignment("This is the first assignment post, lets see how many of you will default :)", "9 hours ago", "20 June 2018", "Due", "Esther Oriabure", "", 19, 34, "Toulouse", "", "");
        classAssignmentList.add(classAssignment);
    }
}
