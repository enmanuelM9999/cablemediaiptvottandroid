/*
* This class is created to manipulate a view element (volume bar)
*
* */

package co.cablebox.tv;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import co.cablebox.tv.activity.videoplayer.VideoplayerActivity;
import co.cablebox.tv.utils.StorageUtils;

public class CustomVolumeManager {

    /**
     * View element to show volume progress
    * */
    public SeekBar seekBarVolume;

    public TextView volumeIndicator;

    /**
     * Container to show and hide volume bar
     * */
    public RelativeLayout containerOfSeekBarVolume;

    /**
     * Handler of activity to send delayed messages (sample: hide volume bar after 2 seconds)
     * */
    public Handler handler;


    /**
    * Current progress of volume bar
    * */
    public int progress=2;


    /**
     * Max progress of volume bar
     * */
    public final int maxProgress=15;


    /**
     * Min progress of volume bar
     * */
    public final int minProgress=0;


    /**
     * When the volume up or down, is necesary use steps
     *
     * Sample:
     * If volume=30 and steps=6, when user press volume up, the progress is setted to 36
     * If volume=97 and steps=3, when user press volume down, the progress is setted to 94
     * */
    public final int steps=1;

    /**
     * Android audio manager
     * */
    public AudioManager audioManager;


    public static final String KEY_VOLUME="STORAGE_KEY_VOLUME_PROGRESS";


    public CustomVolumeManager(SeekBar seekBarVolume, Handler handler, RelativeLayout containerOfSeekBarVolume,TextView volumeIndicator) {
        //set handler
        this.handler=handler;

        //set container of volume bar
        this.containerOfSeekBarVolume=containerOfSeekBarVolume;

        this.volumeIndicator=volumeIndicator;

        //default visibility for volume bar
        hideVolumeBar();

        //Check local storage for saved volume progress
        //progress=StorageUtils.getInt(KEY_VOLUME,progress);

        //init android audio manager
        Context context= AppState.getAppContext();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //set view element
        this.seekBarVolume=seekBarVolume;
        this.seekBarVolume.setMax(getMaxProgress());
        this.seekBarVolume.setProgress(getProgress());
        this.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                //setProgress(progress);
                //tvVolumen.setText(""+progress);
            }
        });
    }

    public void up(){
        showVolumeIndicatorThenHideWithDelay();
        //showVolumeBarThenHideWithDelay();
        upVolume();
    }

    public void down(){
        showVolumeIndicatorThenHideWithDelay();
        //showVolumeBarThenHideWithDelay();
        downVolume();
    }

    private void upVolume(){
        int possibleNewVolume= progress + steps;

        //verify that it does not exceed max
        if (possibleNewVolume<=maxProgress) setProgress(possibleNewVolume);
    }

    private void downVolume(){
        int possibleNewVolume= progress - steps;

        //verify that it does not exceed min
        if (possibleNewVolume>=minProgress) setProgress(possibleNewVolume);
    }

    public int getProgress() {
        //return this.progress;
        return StorageUtils.getInt(KEY_VOLUME,progress);
    }

    public void setProgress(int progress) {
        StorageUtils.setInt(KEY_VOLUME,progress);
        this.progress = progress;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.progress, 0);
        volumeIndicator.setText(""+progress+"/"+maxProgress);
        //this.seekBarVolume.setProgress(getProgress());
        //ToastManager.toast("volume "+getProgress());
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getMinProgress() {
        return minProgress;
    }

    public int getSteps() {
        return steps;
    }

    public void hideVolumeBar(){
        containerOfSeekBarVolume.setVisibility(View.INVISIBLE);
    }
    public void showVolumeBar(){
        containerOfSeekBarVolume.setVisibility(View.VISIBLE);
    }

    public void showVolumeBarThenHideWithDelay(){
        handler.removeMessages(VideoplayerActivity.CODE_HIDE_VOLUMEN);
        showVolumeBar();
        handler.sendEmptyMessageDelayed(VideoplayerActivity.CODE_HIDE_VOLUMEN,3000);
    }


    public void showVolumeIndicator(){
        volumeIndicator.setVisibility(View.VISIBLE);
    }
    public void hideVolumeIndicator(){
        volumeIndicator.setVisibility(View.INVISIBLE);
    }
    void showVolumeIndicatorThenHideWithDelay(){
        handler.removeMessages(VideoplayerActivity.CODE_HIDE_VOLUMEN);
        showVolumeIndicator();
        handler.sendEmptyMessageDelayed(VideoplayerActivity.CODE_HIDE_VOLUMEN,3000);
    }
}

