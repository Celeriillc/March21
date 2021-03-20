package com.celerii.celerii.Activities.Settings;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ManageKidsAdapter;
import com.celerii.celerii.adapters.RecyclerItemTouchHelper;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ManageKidsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageKidsRowActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private ArrayList<ManageKidsModel> manageKidsModelsList;
    public RecyclerView recyclerView;
    public ManageKidsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout coordinatorLayout;

    String featureUseKey = "";
    String featureName = "Manage Kids Setting";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_kids_row);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        coordinatorLayout = (LinearLayout) findViewById(R.id.coordinator_layout);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage My Kids");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        manageKidsModelsList = new ArrayList<>();
        mAdapter = new ManageKidsAdapter(manageKidsModelsList, this);
        loadFromSharedPreferrences();
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    private void loadFromSharedPreferrences() {
//        Set<String> childrenSet = sharedPreferencesManager.getMyChildren();
//        ArrayList<String> children = new ArrayList<>();
//        if (childrenSet != null) {children = new ArrayList<>(childrenSet); }
//
//        manageKidsModelsList.clear();
//
//        if (children.size() > 0) {
//            for (int i = 0; i < children.size(); i++) {
//                String[] childrenInfo = children.get(i).split(" ");
//                ManageKidsModel manageKidsModel = new ManageKidsModel(childrenInfo[1] + " " + childrenInfo[2], childrenInfo[3], childrenInfo[0]);
//                manageKidsModelsList.add(manageKidsModel);
//            }
//        }
//
//        mAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void yeah(){
        ManageKidsModel manageKidsModel = new ManageKidsModel("Kyrie Irving", "http://images.parents.mdpcdn.com/sites/parents.com/files/images/550_102272359.jpg", "0002");
        manageKidsModelsList.add(manageKidsModel);

        manageKidsModel = new ManageKidsModel("Brandon Jennings", "https://s-media-cache-ak0.pinimg.com/736x/7c/af/28/7caf28d3112d4a9885d932610f51727a--beautiful-black-babies-beautiful-children.jpg", "00087");
        manageKidsModelsList.add(manageKidsModel);

        manageKidsModel = new ManageKidsModel("Tobias Harris", "https://s-media-cache-ak0.pinimg.com/originals/71/e5/c1/71e5c1bff4e96da4951b0d873d34cf35.jpg", "000235");
        manageKidsModelsList.add(manageKidsModel);

        manageKidsModel = new ManageKidsModel("Marcus Smart", "https://s-media-cache-ak0.pinimg.com/736x/90/eb/06/90eb0654409f7116dbe4bb839afd8ab5--cutest-babies-adorable-babies.jpg", "000365");
        manageKidsModelsList.add(manageKidsModel);
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ManageKidsAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = manageKidsModelsList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final ManageKidsModel deletedItem = manageKidsModelsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Disconnect from  " + name, Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
