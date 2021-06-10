package com.celerii.celerii.Activities.ELibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

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

public class ELibraryWatchVideoActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    MyCountDownTimer countDownTimer;

    Toolbar toolbar;
    RelativeLayout background;
    LinearLayout mediaControl, playControl;
    VideoView videoView;
    ProgressBar loadingProgressBar;
    SeekBar playbackSeekBar;
    TextView errorText, currentTime, totalTime;
    ImageView fullScreen;
    ImageButton playPause, replay, forward;

    MediaPlayer mediaPlayer;
    Boolean isPlaying, isPrepared, isFlipped;

    Bundle bundle;
    String materialID, materialTitle, materialURL;
    int current = 0;
    int duration = 0;
    int videoPausedPosition;
    long startTime = 3 * 1000; // 3 SECS IDLE TIME
    private final long interval = 1000;

    String featureUseKey = "";
    String featureName = "E Library Video Player";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_e_library_watch_video);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        materialID = bundle.getString("materialID");
        materialTitle = bundle.getString("materialTitle");
        materialURL = bundle.getString("materialURL");
//        materialURL = "https://firebasestorage.googleapis.com/v0/b/altarii-aa0e5.appspot.com/o/ELibrary%2FwAKoSvpuADThnif48BcfcXRrPhx1%2Fvideoplayback.mp4?alt=media&token=0294a670-0cec-4fbe-91ff-35e8dd8e9b7d" ;//bundle.getString("materialURL");

        countDownTimer = new MyCountDownTimer(startTime, interval);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(materialTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        background = (RelativeLayout) findViewById(R.id.background);
        mediaControl = (LinearLayout) findViewById(R.id.mediacontrol);
        playControl = (LinearLayout) findViewById(R.id.playcontrol);
        videoView = (VideoView) findViewById(R.id.videoview);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingprogressbar);
        playbackSeekBar = (SeekBar) findViewById(R.id.playbackseekbar);
        errorText = (TextView) findViewById(R.id.errortext);
        currentTime = (TextView) findViewById(R.id.currenttime);
        totalTime = (TextView) findViewById(R.id.totaltime);
        fullScreen = (ImageView) findViewById(R.id.fullscreen);
        playPause = (ImageButton) findViewById(R.id.playpause);
        replay = (ImageButton) findViewById(R.id.replay);
        forward = (ImageButton) findViewById(R.id.forward);

        playbackSeekBar.setPadding(0, 0, 0, 0);

        errorText.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        playControl.setVisibility(View.GONE);

        isPlaying = false;
        isFlipped = false;

        videoView.setVideoURI(Uri.parse(materialURL));
        videoView.requestFocus();
        videoView.start();
        isPlaying = true;

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == mp.MEDIA_INFO_BUFFERING_START) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                } else if (what == mp.MEDIA_INFO_BUFFERING_END) {
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPlaying = true;
                duration = videoView.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration / 60, duration % 60);
                totalTime.setText(durationString);
                playbackSeekBar.setMax(duration);
                loadingProgressBar.setVisibility(View.GONE);
                countDownTimer.start();
                playControl.setVisibility(View.VISIBLE);
                playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_white_24));
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_filled_white_24));
                isPlaying = false;
                animatePlayControlOn();
                animateMediaControlUp();
                animateToolBarDown();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                loadingProgressBar.setVisibility(View.GONE);
                playControl.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
                return false;
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playControl.getVisibility() == View.GONE) {
                    animatePlayControlOn();
                    animateMediaControlUp();
                    animateToolBarDown();
                } else if (playControl.getVisibility() == View.VISIBLE) {
                    animatePlayControlOff();
                    animateMediaControlDown();
                    animateToolbarUp();
                }
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    isPlaying = false;
                    videoView.pause();
                    playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_circle_filled_white_24));
                } else {
                    isPlaying = true;
                    videoView.start();
                    playPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_white_24));
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = videoView.getCurrentPosition();
                int replayTime = currentTime - 10000;

                if (videoView != null) {
                    videoView.seekTo(Math.max(0, replayTime));
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = videoView.getCurrentPosition();
                int duration = videoView.getDuration();
                int forwardTime = currentTime + 10000;

                if (videoView != null){
                    videoView.seekTo(Math.min(duration, forwardTime));
                }
            }
        });

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlipped) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isFlipped = false;
                    fullScreen.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_24));
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isFlipped = true;
                    fullScreen.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_exit_24));
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
                if (videoView != null && fromUser){
                    videoView.seekTo(progress * 1000);
                }
            }
        });

        Handler mHandler = new Handler();
        ELibraryWatchVideoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(videoView != null){
                    int mCurrentPosition = videoView.getCurrentPosition() / 1000;
                    playbackSeekBar.setProgress(mCurrentPosition);
                    String currentString = String.format("%02d:%02d", mCurrentPosition / 60, mCurrentPosition % 60);
                    currentTime.setText(currentString);
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

    private void animateMediaControlDown() {
        Animation animation = new TranslateAnimation(0, 0,0, 500);
        animation.setDuration(500);
        animation.setFillAfter(true);
        mediaControl.startAnimation(animation);
        mediaControl.setVisibility(View.GONE);
        fullScreen.setVisibility(View.GONE);
        playbackSeekBar.setVisibility(View.GONE);
    }

    private void animateMediaControlUp() {
        Animation animation = new TranslateAnimation(0, 0,500, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        mediaControl.startAnimation(animation);
        mediaControl.setVisibility(View.VISIBLE);
        fullScreen.setVisibility(View.VISIBLE);
        playbackSeekBar.setVisibility(View.VISIBLE);
    }

    private void animateToolbarUp() {
        Animation animation = new TranslateAnimation(0, 0,0, -500);
        animation.setDuration(300);
        animation.setFillAfter(true);
        toolbar.startAnimation(animation);
        toolbar.setVisibility(View.GONE);
    }

    private void animateToolBarDown() {
        Animation animation = new TranslateAnimation(0, 0,-500, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        toolbar.startAnimation(animation);
        toolbar.setVisibility(View.GONE);
    }

    private void animatePlayControlOff() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        playControl.startAnimation(animation);
        playControl.setVisibility(View.GONE);
        playPause.setVisibility(View.GONE);
        replay.setVisibility(View.GONE);
        forward.setVisibility(View.GONE);
    }

    private void animatePlayControlOn() {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(300);
        animation.setFillAfter(true);
        playControl.startAnimation(animation);
        playControl.setVisibility(View.VISIBLE);
        playPause.setVisibility(View.VISIBLE);
        replay.setVisibility(View.VISIBLE);
        forward.setVisibility(View.VISIBLE);
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

        isPlaying = false;
        videoView.stopPlayback();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public void onPause() {
        super.onPause();
        videoPausedPosition = videoView.getCurrentPosition();
        videoView.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        videoView.seekTo(videoPausedPosition);
        videoView.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            animateMediaControlDown();
            animatePlayControlOff();
            animateToolbarUp();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}