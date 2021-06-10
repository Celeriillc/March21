package com.celerii.celerii.Activities.ELibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.twitter.sdk.android.core.models.Image;

public class ELibraryListenToAudioActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    ProgressBar loadingProgressBar;
    SeekBar playbackSeekBar;
    TextView errorText, title, author, currentTime, totalTime;
    LinearLayout audioFileThumbnailBackground;
    ImageView audioFileThumbnail;
    ImageButton playPause, replay, forward;

    Bundle bundle;
    String materialID, materialTitle, materialAuthor, materialURL;

    MediaPlayer mediaPlayer;
    Boolean isPlaying, isPrepared;
    int current = 0;
    int duration = 0;
    int videoPausedPosition;

    String featureUseKey = "";
    String featureName = "E Library Audio Player";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_listen_to_audio);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        materialID = bundle.getString("materialID");
        materialTitle = bundle.getString("materialTitle");
        materialAuthor = bundle.getString("materialAuthor");
        materialURL = bundle.getString("materialURL");
//        materialURL = "https://firebasestorage.googleapis.com/v0/b/altarii-aa0e5.appspot.com/o/ELibrary%2FwAKoSvpuADThnif48BcfcXRrPhx1%2FATB%2C%20Topic%20%26%20A7S%20-%20Your%20Love%20(Lyrics).mp3?alt=media&token=490587b4-8e80-474a-b7b4-62f597efe0ae" ;//bundle.getString("materialURL");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(materialTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingprogressbar);
        playbackSeekBar = (SeekBar) findViewById(R.id.playbackseekbar);
        errorText = (TextView) findViewById(R.id.errortext);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        currentTime = (TextView) findViewById(R.id.currenttime);
        totalTime = (TextView) findViewById(R.id.totaltime);
        audioFileThumbnail = (ImageView) findViewById(R.id.audiofilethumbnail);
        audioFileThumbnailBackground = (LinearLayout) findViewById(R.id.audiofilethumbnailbackground);
        playPause = (ImageButton) findViewById(R.id.playpause);
        replay = (ImageButton) findViewById(R.id.replay);
        forward = (ImageButton) findViewById(R.id.forward);

        title.setText(materialTitle);
        author.setText(materialAuthor);

        errorText.setVisibility(View.GONE);
        audioFileThumbnail.setVisibility(View.GONE);
        audioFileThumbnailBackground.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        isPlaying = false;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(materialURL);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            errorText.setVisibility(View.VISIBLE);
        }

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == mp.MEDIA_INFO_BUFFERING_START) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    audioFileThumbnail.setVisibility(View.INVISIBLE);
                    audioFileThumbnailBackground.setVisibility(View.INVISIBLE);
                    playPause.setEnabled(false);
                } else if (what == mp.MEDIA_INFO_BUFFERING_END) {
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    audioFileThumbnail.setVisibility(View.INVISIBLE);
                    audioFileThumbnailBackground.setVisibility(View.INVISIBLE);
                    playPause.setEnabled(true);
                }

                return false;
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                isPlaying = true;
                duration = mediaPlayer.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration / 60, duration % 60);
                totalTime.setText(durationString);
                playbackSeekBar.setMax(duration);
                audioFileThumbnail.setVisibility(View.VISIBLE);
                audioFileThumbnailBackground.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.GONE);
                errorText.setVisibility(View.GONE);
                playPause.setEnabled(true);
                playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_24));
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_filled_black_24dp));
                isPlaying = false;
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                loadingProgressBar.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
                playPause.setEnabled(false);
                return false;
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    isPlaying = false;
                    mediaPlayer.pause();
                    playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_filled_black_24dp));
                } else {
                    isPlaying = true;
                    mediaPlayer.start();
                    playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_24));
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = mediaPlayer.getCurrentPosition();
                int replayTime = currentTime - 10000;

                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(Math.max(0, replayTime));
//                    playbackSeekBar.setProgress(Math.min(0, replayTime / 1000));
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                int forwardTime = currentTime + 10000;

                if (mediaPlayer != null){
                    mediaPlayer.seekTo(Math.min(duration, forwardTime));
//                    playbackSeekBar.setProgress(Math.min(duration, forwardTime) / 1000);
                }
            }
        });

        playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });

        Handler mHandler = new Handler();
        ELibraryListenToAudioActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null) {
                    try {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        playbackSeekBar.setProgress(mCurrentPosition);
                        String currentString = String.format("%02d:%02d", mCurrentPosition / 60, mCurrentPosition % 60);
                        currentTime.setText(currentString);
                    } catch (Exception e) {
                        Log.d("Media Player", e.toString());
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });

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
    public void onPause() {
        super.onPause();
        videoPausedPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        isPlaying = false;
    }
    @Override
    public void onRestart() {
        super.onRestart();
        mediaPlayer.seekTo(videoPausedPosition);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}