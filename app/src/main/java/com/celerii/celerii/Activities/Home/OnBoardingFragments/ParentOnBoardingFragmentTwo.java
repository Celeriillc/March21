package com.celerii.celerii.Activities.Home.OnBoardingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentOnBoardingFragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentOnBoardingFragmentTwo extends Fragment {

    public ParentOnBoardingFragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_on_boarding_two, container, false);
    }
}