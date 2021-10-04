package com.celerii.celerii.Activities.Utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class OpenPDFActivity extends AppCompatActivity implements DownloadFile.Listener{
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    MyCountDownTimer countDownTimer;

    RelativeLayout errorLayout;
    LinearLayout progressLayout;
    TextView errorLayoutText;
    ProgressBar loadingProgressBar;

    LinearLayout pagesContainer;
    TextView currentPage, totalPages;
    private RemotePDFViewPager remotePDFViewPager;
    private PDFPagerAdapter pdfPagerAdapter;

    Toolbar toolbar;
    LinearLayout pdfLayout;

    Bundle bundle;
    String pdfTitle, pdfURL;
    long startTime = 3 * 1000; // 3 SECS IDLE TIME
    private final long interval = 1000;

    String featureUseKey = "";
    String featureName = "PDF Reader";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdfactivity);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        pdfTitle = bundle.getString("PDFTitle");
        pdfURL = bundle.getString("PDFURL");
//        materialURL = "https://firebasestorage.googleapis.com/v0/b/altarii-aa0e5.appspot.com/o/ELibrary%2FwAKoSvpuADThnif48BcfcXRrPhx1%2FCELERII%20NEW.pdf?alt=media&token=bbbe3e63-078c-4d24-a87e-4200f520ed3d" ;//bundle.getString("materialURL");

        countDownTimer = new MyCountDownTimer(startTime, interval);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(pdfTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        pagesContainer = (LinearLayout) findViewById(R.id.pagescontainer);
        currentPage = (TextView) findViewById(R.id.currentpage);
        totalPages = (TextView) findViewById(R.id.totalpages);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingprogressbar);
//        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfLayout = (LinearLayout) findViewById(R.id.pdflayout);

        pdfLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        remotePDFViewPager = new RemotePDFViewPager(this, pdfURL, this);

        remotePDFViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    String currentPageString = String.valueOf(position + 1) + " / ";
                    totalPages.setText(String.valueOf(remotePDFViewPager.getAdapter().getCount()));
                    currentPage.setText(currentPageString);
                    animatePagesContainerOn();
                } catch (Exception e) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            CustomToast.blueBackgroundToast(this, "There's no internet to open newsletter");
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        pdfPagerAdapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(pdfPagerAdapter);
        updateLayout();

        try {
            String currentPageString = (remotePDFViewPager.getCurrentItem() + 1) + " / ";
            totalPages.setText(String.valueOf(remotePDFViewPager.getAdapter().getCount()));
            currentPage.setText(currentPageString);
        } catch (Exception e) {
            Log.d(OpenPDFActivity.class.getSimpleName(), e.toString());
        }

        pagesContainer.setVisibility(View.VISIBLE);
        countDownTimer.start();
        pdfLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void updateLayout() {
        pdfLayout.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onFailure(Exception e) {
        pdfLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        errorLayoutText.setText("An error has occurred while trying to render this pdf document. Please try again.");
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        loadingProgressBar.setProgress(progress * 100 / total);
    }

    private void animatePagesContainerOff() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        pagesContainer.startAnimation(animation);
        pagesContainer.setVisibility(View.GONE);
    }

    private void animatePagesContainerOn() {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(300);
        animation.setFillAfter(true);
        pagesContainer.startAnimation(animation);
        pagesContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserInteraction(){
        super.onUserInteraction();

        countDownTimer.cancel();
        countDownTimer.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfPagerAdapter != null) {
            pdfPagerAdapter.close();
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            animatePagesContainerOff();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}