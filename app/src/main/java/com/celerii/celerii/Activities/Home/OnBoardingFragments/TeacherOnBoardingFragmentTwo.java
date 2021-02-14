package com.celerii.celerii.Activities.Home.OnBoardingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherOnBoardingFragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherOnBoardingFragmentTwo extends Fragment {

    public TeacherOnBoardingFragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_on_boarding_two, container, false);
    }
}