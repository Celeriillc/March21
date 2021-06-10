package com.celerii.celerii.Activities.ELibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class ELibraryViewImageActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    WebView webView;
    ProgressBar progressBar;

    Bundle bundle;
    String materialID, materialTitle, materialURL;

    String featureUseKey = "";
    String featureName = "E Library Image Viewer";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_view_image);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        materialID = bundle.getString("materialID");
        materialTitle = bundle.getString("materialTitle");
//        materialURL = bundle.getString("materialURL");
        materialURL = "https://firebasestorage.googleapis.com/v0/b/altarii-aa0e5.appspot.com/o/Chat%2FJHy5AFycTaY3vnkQKJQkHdByxZJ2%2FCmp44epgTqR30ocQ2Y0jTLxXOJF3%2F20200718220959942.jpg?alt=media&token=1ac5c80e-77d5-4250-a718-0f033a2f297f";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(materialTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
//        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
//        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        initializeWebView();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

//        Glide.with(context)
//                .load(materialURL)
//                .placeholder(R.drawable.ic_baseline_image_purple_24)
//                .error(R.drawable.ic_baseline_image_purple_24)
//                .into(imageView);

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Materials").child(materialID).child("numberOfReads");
        mDatabaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = Integer.parseInt(mutableData.getValue(String.class));
                if (currentValue == null) {
                    mutableData.setValue(String.valueOf(1));
                } else {
                    mutableData.setValue(String.valueOf(currentValue + 1));
                }

                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private void initializeWebView() {
        webView.loadUrl(materialURL);

        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        webView.setBackgroundColor(ContextCompat.getColor(context, R.color.bpTransparent));
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
}