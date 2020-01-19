package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ManageClassesAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ManageClassesModel;

import java.util.ArrayList;
import java.util.Set;

public class ManageMyClassesActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    Toolbar toolbar;
    private ArrayList<ManageClassesModel> manageClassesModelsList;
    public RecyclerView recyclerView;
    public ManageClassesAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_classes);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage My Kids");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        manageClassesModelsList = new ArrayList<>();
        mAdapter = new ManageClassesAdapter(manageClassesModelsList, this);
        recyclerView.setAdapter(mAdapter);
        loadFromSharedPreferences();
    }

    private void loadFromSharedPreferences() {
        Set<String> classSet = sharedPreferencesManager.getMyClasses();
        ArrayList<String> classes = new ArrayList<>();

        if (classSet != null){ classes = new ArrayList<>(classSet); }

        if (classes.size() > 0) {
            for (int i = 0; i < classes.size(); i++) {
                String[] classInfo = classes.get(i).split(" ");
                ManageClassesModel manageClassesModel = new ManageClassesModel(classInfo[1], classInfo[2], classInfo[0]);
                manageClassesModelsList.add(manageClassesModel);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void yeah(){
        ManageClassesModel manageClassesModel = new ManageClassesModel("Toulouse", "", "");
        manageClassesModelsList.add(manageClassesModel);

        manageClassesModel = new ManageClassesModel("Minsk", "", "");
        manageClassesModelsList.add(manageClassesModel);

        manageClassesModel = new ManageClassesModel("Fukushima", "", "");
        manageClassesModelsList.add(manageClassesModel);

        manageClassesModel = new ManageClassesModel("Pyongyang", "", "");
        manageClassesModelsList.add(manageClassesModel);
    }
}
