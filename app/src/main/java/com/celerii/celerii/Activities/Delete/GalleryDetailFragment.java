package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.celerii.celerii.R;
import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryDetailFragment extends Fragment {

    public GalleryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_detail, container, false);
        Bundle b = getArguments();
        String url = b.getString("URL");

        Glide.with(getContext())
                .load(url)
                .crossFade()
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.fullimage));

        return view;
    }
}
