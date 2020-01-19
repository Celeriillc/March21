package com.celerii.celerii.Activities.Profiles.SchoolProfile;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.celerii.celerii.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class GalleryDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.gallery_detail_transition));
            ((ImageView) findViewById(R.id.fullimage)).setTransitionName("imageTransition");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.imageloadprogress);
        progressBar.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        String url = b.getString("URL");

        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into((ImageView) findViewById(R.id.fullimage));

    }
}
