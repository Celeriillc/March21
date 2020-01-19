package com.celerii.celerii.Activities.Search.Teacher;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchHistoryAdapter;
import com.celerii.celerii.models.SearchHistoryHeader;
import com.celerii.celerii.models.SearchHistoryRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private Toolbar mToolbar;
    private ArrayList<SearchHistoryRow> searchHistoryRowList;
    private SearchHistoryHeader searchHistoryHeader;
    public RecyclerView recyclerView;
    public SearchHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        SearchView searchView;

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView = new SearchView(this);
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(15);
        searchView.setBackgroundColor(Color.TRANSPARENT);
        getSupportActionBar().setCustomView(searchView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Removes default cancel button (x)
        searchView.onActionViewExpanded();

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        //Removes margin between back action bar button and searchView
        LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 0;

        //Sets hint for search
        searchView.setQueryHint("Search location, school or teacher"); //Todo: Use @string resource

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        loading = false;
        searchHistoryHeader = new SearchHistoryHeader("Recent Search History", loading);
        searchHistoryRowList = new ArrayList<>();
        searchHistoryRowList.add(new SearchHistoryRow());
        loadDetailsFromFirebase();
        mAdapter = new SearchHistoryAdapter(searchHistoryRowList, searchHistoryHeader, this);
        recyclerView.setAdapter(mAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Query", query);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        loadDetailsFromFirebase();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadDetailsFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference("MySearchHistory").child("Teachers").child(auth.getCurrentUser().getUid());
        mDatabaseReference.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    searchHistoryRowList.clear();
                    searchHistoryRowList.add(new SearchHistoryRow());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        SearchHistoryRow searchHistoryRow = postSnapshot.getValue(SearchHistoryRow.class);
                        searchHistoryRowList.add(1, searchHistoryRow);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
