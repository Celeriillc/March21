package com.celerii.celerii.Activities.Delete;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ReviewAdapter;
import com.celerii.celerii.models.RatingSummary;
import com.celerii.celerii.models.Review;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {

    private ArrayList<Review> reviewList;
    private RatingSummary ratingSummary;
    public RecyclerView recyclerView;
    public ReviewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        mLayoutManager = new LinearLayoutManager(view.getContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//
//        reviewList = new ArrayList<>();
//        yeah();
//        ratingSummary = new RatingSummary(2316, "http://d19lga30codh7.cloudfront.net/wp-content/uploads/2016/07/Dija-300x272.jpg", 100, 67, 45, 23, 54, 5);
//        mAdapter = new ReviewAdapter(reviewList, ratingSummary, getContext());
//        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void yeah(){
        Review review = new Review("Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, con adipisc elit, sed do eiusm Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet", "https://www.36ng.ng/wp-content/uploads/2015/11/Chidinma-Looking-Gorgeous-In-New-Selfie.png", "Clara Ikubese");
        reviewList.add(review);

        review = new Review("Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, con adipisc elit, sed do eiusm Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet", "https://www.36ng.ng/wp-content/uploads/2015/11/Chidinma-Looking-Gorgeous-In-New-Selfie.png", "Clara Ikubese");
        reviewList.add(review);

        review = new Review("Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, con adipisc elit, sed do eiusm Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet", "https://www.36ng.ng/wp-content/uploads/2015/11/Chidinma-Looking-Gorgeous-In-New-Selfie.png", "Clara Ikubese");
        reviewList.add(review);

        review = new Review("Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, con adipisc elit, sed do eiusm Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet", "https://www.36ng.ng/wp-content/uploads/2015/11/Chidinma-Looking-Gorgeous-In-New-Selfie.png", "Clara Ikubese");
        reviewList.add(review);
    }
}
