package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.celerii.celerii.R;

public class BrowserActivityForInfo extends AppCompatActivity {

    private String postUrl = "https://help.instagram.com/478745558852511";
    private WebView webView;
    private ProgressBar progressBar;
    Toolbar mToolbar;
    Bundle bundle;
    String header = "", URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_for_info);

        bundle = getIntent().getExtras();
        header = bundle.getString("Header");
        URL = bundle.getString("URL");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(header);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        loadWebViewContent();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
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

//        webView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                progressBar.setVisibility(View.GONE);
//            }
//        });

    }

    private void loadWebViewContent() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL);
        webView.setHorizontalScrollBarEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
