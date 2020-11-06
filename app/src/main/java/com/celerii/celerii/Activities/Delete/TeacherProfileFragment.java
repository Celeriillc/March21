package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.celerii.celerii.R;
import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherProfileFragment extends Fragment {



    public TeacherProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        String smallImageURL = "http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg";
        String largeImageURL = "http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg";

        ImageView smallImage = (ImageView) view.findViewById(R.id.profilepic);
        ImageView largeImage = (ImageView) view.findViewById(R.id.backgroundimage);

        if (!smallImageURL.isEmpty()) {
            Glide.with(getContext())
                    .load(smallImageURL)
                    .crossFade()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(smallImage);
        }
        else {
            Glide.with(getContext())
                    .load(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(smallImage);
        }

        if (!largeImageURL.isEmpty()) {
            Glide.with(getContext())
                    .load(largeImageURL)
                    .centerCrop()
                    .bitmapTransform(new BlurTransformation(getContext(), 50))
                    .into(largeImage);
        }
        else {
            Glide.with(getContext())
                    .load(R.drawable.materialdesignwallpapericebasic)
                    .centerCrop()
                    .bitmapTransform(new BlurTransformation(getContext(), 50))
                    .into(largeImage);
        }
        return view;
    }

}
