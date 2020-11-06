package com.celerii.celerii.Activities.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Settings.BrowserActivityForInfo;
import com.celerii.celerii.Activities.Settings.SettingsActivityParent;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherHomeClassAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class RemoteCampaignActivity extends AppCompatActivity {

    Context context;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    ImageView campaignBackground, campaignIcon;
    TextView campaignTitle, campaignText;
    Button campaignAction;
    Bundle bundle;
    String campaignID, campaignURL, campaignBackgroundURL, campaignIconURL, campaignTitleString, campaignTextString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_campaign);

        context = this;
        bundle = getIntent().getExtras();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        campaignBackground = (ImageView) findViewById(R.id.campaignbackground);
        campaignIcon = (ImageView) findViewById(R.id.campaignicon);
        campaignTitle = (TextView) findViewById(R.id.campaigntitle);
        campaignText = (TextView) findViewById(R.id.campaigntext);
        campaignAction = (Button) findViewById(R.id.campaignaction);

        campaignID = bundle.getString("ID");
        campaignURL = bundle.getString("URL");
        campaignBackgroundURL = bundle.getString("BackgroundURL");
        campaignIconURL = bundle.getString("IconURL");
        campaignTitleString = bundle.getString("Title");
        campaignTextString = bundle.getString("Text");

        Glide.with(context)
                .load(campaignBackgroundURL)
                .centerCrop()
                .into(campaignBackground);

        Glide.with(context)
                .load(campaignIconURL)
                .centerCrop()
                .into(campaignIcon);

        campaignTitle.setText(campaignTitleString);
        campaignText.setText(campaignTextString);

        DatabaseReference remoteCampaignUpdate = mFirebaseDatabase.getReference();
        Map<String, Object> remoteCampaignMap = new HashMap<String, Object>();
        remoteCampaignMap.put("Campaigns/User Campaigns/" + mFirebaseUser.getUid() + "/" + campaignID, true);
        remoteCampaignMap.put("Campaigns/Campaign Recipients/" + campaignID + "/" + mFirebaseUser.getUid(), true);
        remoteCampaignUpdate.updateChildren(remoteCampaignMap);

        campaignAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference remoteCampaignUpdate = mFirebaseDatabase.getReference();
                Map<String, Object> remoteCampaignMap = new HashMap<String, Object>();
                remoteCampaignMap.put("Campaigns/Engaged Campaigns User/" + mFirebaseUser.getUid() + "/" + campaignID, true);
                remoteCampaignMap.put("Campaigns/Engaged Campaigns/" + campaignID + "/" + mFirebaseUser.getUid(), true);
                remoteCampaignUpdate.updateChildren(remoteCampaignMap);

                Intent I = new Intent(RemoteCampaignActivity.this, BrowserActivityForInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Header", campaignTitleString);
                bundle.putString("URL", campaignURL);
                I.putExtras(bundle);
                startActivity(I);
            }
        });
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