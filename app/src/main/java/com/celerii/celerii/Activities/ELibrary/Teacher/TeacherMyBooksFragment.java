package com.celerii.celerii.Activities.ELibrary.Teacher;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TeacherMyBooksFragment extends Fragment {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;

    Button uploadABook, myBooksErrorLayoutButton;
    LinearLayout myBooksErrorLayout, recommendedBooksErrorLayout, myBooksLayout, recommendedBooksLayout;
    LinearLayout myBooksLayoutOne, myBooksLayoutTwo, myBooksLayoutThree, myBooksLayoutFour, myBooksLayoutFive, myBooksLayoutSix, myBooksLayoutSeven, 
            myBooksLayoutEight, myBooksLayoutNine, myBooksLayoutTen;
    LinearLayout recommendedBooksLayoutOne, recommendedBooksLayoutTwo, recommendedBooksLayoutThree, recommendedBooksLayoutFour, 
            recommendedBooksLayoutFive, recommendedBooksLayoutSix, recommendedBooksLayoutSeven, recommendedBooksLayoutEight, 
            recommendedBooksLayoutNine, recommendedBooksLayoutTen;
    ImageView myBooksImageOne, myBooksImageTwo, myBooksImageThree, myBooksImageFour, myBooksImageFive, myBooksImageSix, myBooksImageSeven,
            myBooksImageEight, myBooksImageNine, myBooksImageTen;
    ImageView recommendedBooksImageOne, recommendedBooksImageTwo, recommendedBooksImageThree, recommendedBooksImageFour,
            recommendedBooksImageFive, recommendedBooksImageSix, recommendedBooksImageSeven, recommendedBooksImageEight,
            recommendedBooksImageNine, recommendedBooksImageTen;
    TextView myBooksTitleOne, myBooksTitleTwo, myBooksTitleThree, myBooksTitleFour, myBooksTitleFive, myBooksTitleSix, myBooksTitleSeven,
            myBooksTitleEight, myBooksTitleNine, myBooksTitleTen;
    TextView recommendedBooksTitleOne, recommendedBooksTitleTwo, recommendedBooksTitleThree, recommendedBooksTitleFour,
            recommendedBooksTitleFive, recommendedBooksTitleSix, recommendedBooksTitleSeven, recommendedBooksTitleEight,
            recommendedBooksTitleNine, recommendedBooksTitleTen;
    TextView myBooksAuthorOne, myBooksAuthorTwo, myBooksAuthorThree, myBooksAuthorFour, myBooksAuthorFive, myBooksAuthorSix, myBooksAuthorSeven,
            myBooksAuthorEight, myBooksAuthorNine, myBooksAuthorTen;
    TextView recommendedBooksAuthorOne, recommendedBooksAuthorTwo, recommendedBooksAuthorThree, recommendedBooksAuthorFour,
            recommendedBooksAuthorFive, recommendedBooksAuthorSix, recommendedBooksAuthorSeven, recommendedBooksAuthorEight,
            recommendedBooksAuthorNine, recommendedBooksAuthorTen;
    FloatingActionButton addABookFAB;

    ArrayList<LinearLayout> myBooksLinearLayoutList = new ArrayList<>();
    ArrayList<LinearLayout> recommendedBooksLinearLayoutList = new ArrayList<>();
    ArrayList<ImageView> myBooksImageViewList = new ArrayList<>();
    ArrayList<ImageView> recommendedBooksImageViewList = new ArrayList<>();
    ArrayList<TextView> myBooksTitleTextViewList = new ArrayList<>();
    ArrayList<TextView> recommendedBooksTitleTextViewList = new ArrayList<>();
    ArrayList<TextView> myBooksAuthorTextViewList = new ArrayList<>();
    ArrayList<TextView> recommendedBooksAuthorTextViewList = new ArrayList<>();

    String featureUseKey = "";
    String featureName = "Teacher My Books";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public TeacherMyBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_my_books, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        superLayout = (ScrollView) view.findViewById(R.id.superlayout);

        uploadABook = (Button) view.findViewById(R.id.mybookserrorlayoutbutton);
        myBooksErrorLayoutButton = (Button) view.findViewById(R.id.mybookserrorlayoutbutton);
        myBooksErrorLayout = (LinearLayout) view.findViewById(R.id.mybookserrorlayout);
        recommendedBooksErrorLayout = (LinearLayout) view.findViewById(R.id.recommendedbookserrorlayout);
        myBooksLayout = (LinearLayout) view.findViewById(R.id.mybookslayout);
        recommendedBooksLayout = (LinearLayout) view.findViewById(R.id.recommendedbookslayout);

        myBooksLayoutOne = (LinearLayout) view.findViewById(R.id.mybookslayoutone);
        myBooksLayoutTwo = (LinearLayout) view.findViewById(R.id.mybookslayouttwo);
        myBooksLayoutThree = (LinearLayout) view.findViewById(R.id.mybookslayoutthree);
        myBooksLayoutFour = (LinearLayout) view.findViewById(R.id.mybookslayoutfour);
        myBooksLayoutFive = (LinearLayout) view.findViewById(R.id.mybookslayoutfive);
        myBooksLayoutSix = (LinearLayout) view.findViewById(R.id.mybookslayoutsix);
        myBooksLayoutSeven = (LinearLayout) view.findViewById(R.id.mybookslayoutseven);
        myBooksLayoutEight = (LinearLayout) view.findViewById(R.id.mybookslayouteight);
        myBooksLayoutNine = (LinearLayout) view.findViewById(R.id.mybookslayoutnine);
        myBooksLayoutTen = (LinearLayout) view.findViewById(R.id.mybookslayoutten);

        recommendedBooksLayoutOne = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutone);
        recommendedBooksLayoutTwo = (LinearLayout) view.findViewById(R.id.recommendedbookslayouttwo);
        recommendedBooksLayoutThree = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutthree);
        recommendedBooksLayoutFour = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutfour);
        recommendedBooksLayoutFive = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutfive);
        recommendedBooksLayoutSix = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutsix);
        recommendedBooksLayoutSeven = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutseven);
        recommendedBooksLayoutEight = (LinearLayout) view.findViewById(R.id.recommendedbookslayouteight);
        recommendedBooksLayoutNine = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutnine);
        recommendedBooksLayoutTen = (LinearLayout) view.findViewById(R.id.recommendedbookslayoutten);

        myBooksImageOne = (ImageView) view.findViewById(R.id.mybooksimageone);
        myBooksImageTwo = (ImageView) view.findViewById(R.id.mybooksimagetwo);
        myBooksImageThree = (ImageView) view.findViewById(R.id.mybooksimagethree);
        myBooksImageFour = (ImageView) view.findViewById(R.id.mybooksimagefour);
        myBooksImageFive = (ImageView) view.findViewById(R.id.mybooksimagefive);
        myBooksImageSix = (ImageView) view.findViewById(R.id.mybooksimagesix);
        myBooksImageSeven = (ImageView) view.findViewById(R.id.mybooksimageseven);
        myBooksImageEight = (ImageView) view.findViewById(R.id.mybooksimageeight);
        myBooksImageNine = (ImageView) view.findViewById(R.id.mybooksimagenine);
        myBooksImageTen = (ImageView) view.findViewById(R.id.mybooksimageten);

        recommendedBooksImageOne = (ImageView) view.findViewById(R.id.recommendedbooksimageone);
        recommendedBooksImageTwo = (ImageView) view.findViewById(R.id.recommendedbooksimagetwo);
        recommendedBooksImageThree = (ImageView) view.findViewById(R.id.recommendedbooksimagethree);
        recommendedBooksImageFour = (ImageView) view.findViewById(R.id.recommendedbooksimagefour);
        recommendedBooksImageFive = (ImageView) view.findViewById(R.id.recommendedbooksimagefive);
        recommendedBooksImageSix = (ImageView) view.findViewById(R.id.recommendedbooksimagesix);
        recommendedBooksImageSeven = (ImageView) view.findViewById(R.id.recommendedbooksimageseven);
        recommendedBooksImageEight = (ImageView) view.findViewById(R.id.recommendedbooksimageeight);
        recommendedBooksImageNine = (ImageView) view.findViewById(R.id.recommendedbooksimagenine);
        recommendedBooksImageTen = (ImageView) view.findViewById(R.id.recommendedbooksimageten);

        myBooksTitleOne = (TextView) view.findViewById(R.id.mybookstitleone);
        myBooksTitleTwo = (TextView) view.findViewById(R.id.mybookstitletwo);
        myBooksTitleThree = (TextView) view.findViewById(R.id.mybookstitlethree);
        myBooksTitleFour = (TextView) view.findViewById(R.id.mybookstitlefour);
        myBooksTitleFive = (TextView) view.findViewById(R.id.mybookstitlefive);
        myBooksTitleSix = (TextView) view.findViewById(R.id.mybookstitlesix);
        myBooksTitleSeven = (TextView) view.findViewById(R.id.mybookstitleseven);
        myBooksTitleEight = (TextView) view.findViewById(R.id.mybookstitleeight);
        myBooksTitleNine = (TextView) view.findViewById(R.id.mybookstitlenine);
        myBooksTitleTen = (TextView) view.findViewById(R.id.mybookstitleten);

        recommendedBooksTitleOne = (TextView) view.findViewById(R.id.recommendedbookstitleone);
        recommendedBooksTitleTwo = (TextView) view.findViewById(R.id.recommendedbookstitletwo);
        recommendedBooksTitleThree = (TextView) view.findViewById(R.id.recommendedbookstitlethree);
        recommendedBooksTitleFour = (TextView) view.findViewById(R.id.recommendedbookstitlefour);
        recommendedBooksTitleFive = (TextView) view.findViewById(R.id.recommendedbookstitlefive);
        recommendedBooksTitleSix = (TextView) view.findViewById(R.id.recommendedbookstitlesix);
        recommendedBooksTitleSeven = (TextView) view.findViewById(R.id.recommendedbookstitleseven);
        recommendedBooksTitleEight = (TextView) view.findViewById(R.id.recommendedbookstitleeight);
        recommendedBooksTitleNine = (TextView) view.findViewById(R.id.recommendedbookstitlenine);
        recommendedBooksTitleTen = (TextView) view.findViewById(R.id.recommendedbookstitleten);

        myBooksAuthorOne = (TextView) view.findViewById(R.id.mybooksauthorone);
        myBooksAuthorTwo = (TextView) view.findViewById(R.id.mybooksauthortwo);
        myBooksAuthorThree = (TextView) view.findViewById(R.id.mybooksauthorthree);
        myBooksAuthorFour = (TextView) view.findViewById(R.id.mybooksauthorfour);
        myBooksAuthorFive = (TextView) view.findViewById(R.id.mybooksauthorfive);
        myBooksAuthorSix = (TextView) view.findViewById(R.id.mybooksauthorsix);
        myBooksAuthorSeven = (TextView) view.findViewById(R.id.mybooksauthorseven);
        myBooksAuthorEight = (TextView) view.findViewById(R.id.mybooksauthoreight);
        myBooksAuthorNine = (TextView) view.findViewById(R.id.mybooksauthornine);
        myBooksAuthorTen = (TextView) view.findViewById(R.id.mybooksauthorten);

        recommendedBooksAuthorOne = (TextView) view.findViewById(R.id.recommendedbooksauthorone);
        recommendedBooksAuthorTwo = (TextView) view.findViewById(R.id.recommendedbooksauthortwo);
        recommendedBooksAuthorThree = (TextView) view.findViewById(R.id.recommendedbooksauthorthree);
        recommendedBooksAuthorFour = (TextView) view.findViewById(R.id.recommendedbooksauthorfour);
        recommendedBooksAuthorFive = (TextView) view.findViewById(R.id.recommendedbooksauthorfive);
        recommendedBooksAuthorSix = (TextView) view.findViewById(R.id.recommendedbooksauthorsix);
        recommendedBooksAuthorSeven = (TextView) view.findViewById(R.id.recommendedbooksauthorseven);
        recommendedBooksAuthorEight = (TextView) view.findViewById(R.id.recommendedbooksauthoreight);
        recommendedBooksAuthorNine = (TextView) view.findViewById(R.id.recommendedbooksauthornine);
        recommendedBooksAuthorTen = (TextView) view.findViewById(R.id.recommendedbooksauthorten);
        
        addABookFAB = (FloatingActionButton) view.findViewById(R.id.uploadbookfab);

        myBooksLinearLayoutList.add(myBooksLayoutOne);
        myBooksLinearLayoutList.add(myBooksLayoutTwo);
        myBooksLinearLayoutList.add(myBooksLayoutThree);
        myBooksLinearLayoutList.add(myBooksLayoutFour);
        myBooksLinearLayoutList.add(myBooksLayoutFive);
        myBooksLinearLayoutList.add(myBooksLayoutSix);
        myBooksLinearLayoutList.add(myBooksLayoutSeven);
        myBooksLinearLayoutList.add(myBooksLayoutEight);
        myBooksLinearLayoutList.add(myBooksLayoutNine);
        myBooksLinearLayoutList.add(myBooksLayoutTen);

        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutOne);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutTwo);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutThree);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutFour);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutFive);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutSix);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutSeven);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutEight);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutNine);
        recommendedBooksLinearLayoutList.add(recommendedBooksLayoutTen);

        myBooksImageViewList.add(myBooksImageOne);
        myBooksImageViewList.add(myBooksImageTwo);
        myBooksImageViewList.add(myBooksImageThree);
        myBooksImageViewList.add(myBooksImageFour);
        myBooksImageViewList.add(myBooksImageFive);
        myBooksImageViewList.add(myBooksImageSix);
        myBooksImageViewList.add(myBooksImageSeven);
        myBooksImageViewList.add(myBooksImageEight);
        myBooksImageViewList.add(myBooksImageNine);
        myBooksImageViewList.add(myBooksImageTen);

        recommendedBooksImageViewList.add(recommendedBooksImageOne);
        recommendedBooksImageViewList.add(recommendedBooksImageTwo);
        recommendedBooksImageViewList.add(recommendedBooksImageThree);
        recommendedBooksImageViewList.add(recommendedBooksImageFour);
        recommendedBooksImageViewList.add(recommendedBooksImageFive);
        recommendedBooksImageViewList.add(recommendedBooksImageSix);
        recommendedBooksImageViewList.add(recommendedBooksImageSeven);
        recommendedBooksImageViewList.add(recommendedBooksImageEight);
        recommendedBooksImageViewList.add(recommendedBooksImageNine);
        recommendedBooksImageViewList.add(recommendedBooksImageTen);

        myBooksTitleTextViewList.add(myBooksTitleOne);
        myBooksTitleTextViewList.add(myBooksTitleTwo);
        myBooksTitleTextViewList.add(myBooksTitleThree);
        myBooksTitleTextViewList.add(myBooksTitleFour);
        myBooksTitleTextViewList.add(myBooksTitleFive);
        myBooksTitleTextViewList.add(myBooksTitleSix);
        myBooksTitleTextViewList.add(myBooksTitleSeven);
        myBooksTitleTextViewList.add(myBooksTitleEight);
        myBooksTitleTextViewList.add(myBooksTitleNine);
        myBooksTitleTextViewList.add(myBooksTitleTen);

        recommendedBooksTitleTextViewList.add(recommendedBooksTitleOne);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleTwo);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleThree);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleFour);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleFive);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleSix);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleSeven);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleEight);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleNine);
        recommendedBooksTitleTextViewList.add(recommendedBooksTitleTen);

        myBooksAuthorTextViewList.add(myBooksAuthorOne);
        myBooksAuthorTextViewList.add(myBooksAuthorTwo);
        myBooksAuthorTextViewList.add(myBooksAuthorThree);
        myBooksAuthorTextViewList.add(myBooksAuthorFour);
        myBooksAuthorTextViewList.add(myBooksAuthorFive);
        myBooksAuthorTextViewList.add(myBooksAuthorSix);
        myBooksAuthorTextViewList.add(myBooksAuthorSeven);
        myBooksAuthorTextViewList.add(myBooksAuthorEight);
        myBooksAuthorTextViewList.add(myBooksAuthorNine);
        myBooksAuthorTextViewList.add(myBooksAuthorTen);

        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorOne);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorTwo);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorThree);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorFour);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorFive);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorSix);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorSeven);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorEight);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorNine);
        recommendedBooksAuthorTextViewList.add(recommendedBooksAuthorTen);

        uploadABook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addABookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        loadFromFirebase();

        return view;
    }

    private void loadFromFirebase() {

    }

    @Override
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public void onResume() {
        UpdateDataFromFirebase.populateEssentials(context);
        super.onResume();
    }
}