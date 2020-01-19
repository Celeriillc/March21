package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.StartAChatAdapter;
import com.celerii.celerii.models.StartAChatModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherStartAChatFragment extends Fragment {

    private ArrayList<StartAChatModel> startAChatModelList;
    public RecyclerView recyclerView;
    public StartAChatAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    public TeacherStartAChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_start_achat, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        startAChatModelList = new ArrayList<>();
        //yeah();
        mAdapter = new StartAChatAdapter(startAChatModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    void yeah(){
        StartAChatModel ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://travel.jumia.com/blog/ng/wp-content/uploads/2015/10/oma-4-660x400.png");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "http://beautifulng.com/wp-content/uploads/2016/04/Natural-hair.jpg");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://i0.wp.com/otownloaded.com/wp-content/uploads/2016/01/rita.jpeg");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://i0.wp.com/otownloaded.com/wp-content/uploads/2016/01/rita.jpeg");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://travel.jumia.com/blog/ng/wp-content/uploads/2015/10/oma-4-660x400.png");
        startAChatModelList.add(ml);

        ml = new StartAChatModel("Esther Oriabure", "@estheroriabure", "https://i.onthe.io/vllkyt4c8haautcss.d62e45bb.jpg");
        startAChatModelList.add(ml);
    }
}
