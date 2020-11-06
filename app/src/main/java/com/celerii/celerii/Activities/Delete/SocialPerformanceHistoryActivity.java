package com.celerii.celerii.Activities.Delete;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SocialPerformanceHistoryAdapter;
import com.celerii.celerii.models.SocialPerformanceHistoryHeader;
import com.celerii.celerii.models.SocialPerformanceHistoryRow;

import java.util.ArrayList;

public class SocialPerformanceHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ArrayList<SocialPerformanceHistoryRow> socialPerformanceHistoryRowList;
    private SocialPerformanceHistoryHeader socialPerformanceHistoryHeader;
    public RecyclerView recyclerView;
    public SocialPerformanceHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_performance_history);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Martin Ikubese"); //Todo: Make dynamic.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        socialPerformanceHistoryHeader = new SocialPerformanceHistoryHeader("567", "0", "567", "Melancholic", "Phlegmatic");
        socialPerformanceHistoryRowList = new ArrayList<>();
        yeah();
        mAdapter = new SocialPerformanceHistoryAdapter(socialPerformanceHistoryRowList, socialPerformanceHistoryHeader, this);
        recyclerView.setAdapter(mAdapter);
    }

    void yeah(){

    }
}
