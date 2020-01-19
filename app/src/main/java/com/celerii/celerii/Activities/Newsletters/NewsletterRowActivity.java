package com.celerii.celerii.Activities.Newsletters;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewsletterRowAdapter;
import com.celerii.celerii.models.NewsletterRow;

import java.util.ArrayList;

public class NewsletterRowActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ArrayList<NewsletterRow> newsletterRowList;
    public RecyclerView recyclerView;
    public NewsletterRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String string = "Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor  Lorem ipsum dolor sit amet, nsectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor" +
            "Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor  Lorem ipsum dolor sit amet, nsectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter_row);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Newsletter");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        newsletterRowList = new ArrayList<>();
        yeah();
        mAdapter = new NewsletterRowAdapter(newsletterRowList, this);
        recyclerView.setAdapter(mAdapter);
    }

    void yeah(){
        NewsletterRow newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "https://si.wsj.net/public/resources/images/MI-BX240_DWEEK_G_20130716135136.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);

        newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "http://www.foreverlawn.com/wp-content/uploads/2015/11/DSC9083.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);

        newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "http://www.foreverlawn.com/wp-content/uploads/2015/11/DSC9084.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);
    }
}
