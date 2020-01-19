package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.Activities.Home.Parent.ParentHomeClassFeed;
import com.celerii.celerii.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeFragment extends Fragment {

    private FragmentTabHost mTabHost;

    public TeacherHomeFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentclassfeed").setIndicator("Class Feed"),
                ParentHomeClassFeed.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentassignments").setIndicator("Assignments"),
                ParentHomeAssignment.class, null);

        return rootView;
    }
}
