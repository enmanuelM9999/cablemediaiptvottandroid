package co.cablebox.tv.activity.videoplayer;

import static co.cablebox.tv.utils.ToolBox.convertDpToPx;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.bean.Channels;

public class SmartphoneVideoPlayerActivity extends VideoplayerActivity{

    @Override
    public void configTopButtons() {
        showTopButtons();
    }

    @Override
    public void fixLogoutDialogStyle(AlertDialog ad){
    }
}
