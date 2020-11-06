package com.celerii.celerii.Activities.Delete;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.GalleryAdapter;
import com.celerii.celerii.models.GalleryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class SchoolGalleryFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private ArrayList<GalleryModel> galleryModelList;
    public RecyclerView recyclerView;
    public GalleryAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    String entityID = "Grac0001";

    public SchoolGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_gallery, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(view.getContext(), 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        galleryModelList = new ArrayList<>();
        yeah();
        mAdapter = new GalleryAdapter(galleryModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    void yeah() {
        GalleryModel model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/03/toolzo.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Leonora-Okine.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3746832/0355563074-w644-h429/Sharon-Ezeamaka.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3800133/5805561420-w644-h429/Toolz-WCW.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Dorcas-Shola-Fapson.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://i1.wp.com/thenet.ng/wp-content/uploads/2016/06/tiwa-savage-mavin-e1468221794971-600x589.jpg?resize=600%2C589", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://i0.wp.com/thenet.ng/wp-content/uploads/2012/09/Toolz-photoshoot-with-Moussa-Moussa-10.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3458474/1636366867-w644-h960/Sharon-Chisom-Ezeamaka-plays-Princess-in-Shuga-Pulse.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("https://1.bp.blogspot.com/-vh7RR6T056I/V0NEHawIf5I/AAAAAAAAGvs/rjnJZugf6DQv0idI_T13a958a8dkcN3LQCKgB/s640/tolu.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://dev.mtvshuga.com/wp-content/uploads/2015/06/Sophie-760x760.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://stargist.com/wp-content/uploads/2016/03/Tiwa-Savage-8-fashionpheeva.png", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://1.bp.blogspot.com/-bGfxgmKs1cE/VTgJULVOBEI/AAAAAAAFBoA/CBpuRSLdM7I/s1600/4.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://www.authorityngr.com/app/views/images/uploads/content_images/2016_08_26_22587.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://3.bp.blogspot.com/-c7dgWpRNPHo/VTgJU3F1FyI/AAAAAAAFBoM/3jA867FTxQY/s1600/5.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://cdn1.dailypost.ng/wp-content/uploads/2015/12/Ini-Edo1.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://www.tori.ng/userfiles/image/2017/feb/06/actress-Rita-Dominic-5.jpg", "");
        galleryModelList.add(model);

        model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/04/Ini-Edo1.jpg", "");
        galleryModelList.add(model);
    }
}
