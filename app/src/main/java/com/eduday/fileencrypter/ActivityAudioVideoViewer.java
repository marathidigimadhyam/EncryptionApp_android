package com.eduday.fileencrypter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eduday.fileencrypter.Utils.App_Constant;
import com.eduday.fileencrypter.Utils.ResponseDialog;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;


public class ActivityAudioVideoViewer extends AppCompatActivity implements VideoRendererEventListener {


    private RelativeLayout activity_exovideoplayer;
    private PlayerView simpleExoPlayerView;
    private ProgressBar loading;

    private MediaSource videoSource;
    private ResponseDialog resDialog;
    private SimpleExoPlayer player;
    private Context mContext;
    private Animation anim;
    private static final String TAG = "AudioVideoViewer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App_Constant.disabledScreenshot(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_audiovideoviewer);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mContext = this;

        activity_exovideoplayer = (RelativeLayout) findViewById(R.id.activity_exovideoplayer);
        simpleExoPlayerView = (PlayerView) findViewById(R.id.player_view);
        loading = (ProgressBar) findViewById(R.id.loading);

        resDialog = new ResponseDialog(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        anim = (Animation) AnimationUtils.loadAnimation(mContext,R.anim.fade);
        anim.setDuration(4000);
        anim.setRepeatCount(0);

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        //Set media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        //Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        //Produces DataSource instances through which media data is loaded.
       // DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);


        String userAgent = Util.getUserAgent(mContext, "exoplayer2example");

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                mContext,
                null /* listener */,
                httpDataSourceFactory
        );


        //Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        String urimp4 = getIntent().getExtras().getString("filePath");

        Log.d("ddddddd",urimp4);
        Uri fileuri = Uri.parse(urimp4);


        if(urimp4 == null){
            Toast.makeText(mContext,"There is problem playing video.",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        videoSource = new ExtractorMediaSource(fileuri, dataSourceFactory, extractorsFactory, null, null);

        // This is the MediaSource representing the media to be played:
        // FOR SD CARD SOURCE:
        // MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

        //FOR LIVESTREAM LINK:
       // MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);


      //  final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.v(TAG, "Listener-onTimelineChanged...");

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged..."+isLoading);

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        loading.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        loading.setVisibility(View.VISIBLE);
                        break;
                    case ExoPlayer.STATE_ENDED:
                        finish();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.v(TAG, "Listener-onShuffleModeEnabledChanged...");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.v(TAG, "Listener-onPositionDiscontinuity...");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }

            @Override
            public void onSeekProcessed() {
                Log.v(TAG, "Listener-onSeekProcessed...");
            }
        });



        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
        registerReceiver(onMemoryCardChange, new IntentFilter("ON_MEMORY_CARD_STATUS_CHANGE")); // MKN
        registerReceiver(onReleaseSDCard, new IntentFilter("RELEASE_SD_CARD"));

    }


    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged ["  + " width: " + width + " height: " + height + "]");
       // resolutionTextView.setText("RES:(WxH):"+width+"X"+height +"\n           "+height+"p");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();

        try{
            unregisterReceiver(onMemoryCardChange);//MKN
        }catch (Exception e){
            e.printStackTrace();
        }


        try{
            mContext.unregisterReceiver(onReleaseSDCard);//MKN
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /*
     * @auther - MKN
     * @purpose - SD Card Activation workflow  - Receiver - Call from OnMemoryCardChanges Listener
     * @date - 06/03/2019
     * */
    BroadcastReceiver onMemoryCardChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {    // internet lost alert dialog method call from here...
            try{
                String MEDIA_BAD_REMOVAL="android.intent.action.MEDIA_BAD_REMOVAL";
                String MEDIA_MOUNTED="android.intent.action.MEDIA_MOUNTED";
                String action = intent.getExtras().getString("action");
                if(action.equals(MEDIA_BAD_REMOVAL)){

                    Toast.makeText(context, "sd card unexpectedly removed.", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /*
     * @auther - MKN
     * @purpose - SD Card Activation workflow  - Receiver - Call from firebase message Listener
     * @date - 06/03/2019
     * */
    BroadcastReceiver onReleaseSDCard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {    // internet lost alert dialog method call from here...
            try{

                App_Constant.uri = null;//MKN - Reset instance
                App_Constant.externalSDCard = "";//MKN - Reset ID
                Toast.makeText(context, "sd card unexpectedly removed.", Toast.LENGTH_SHORT).show();
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
