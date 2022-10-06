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

        ivSettings.setVisibility(View.GONE);
//        ivLogout.setVisibility(View.GONE);
//        ivTypeNum.setVisibility(View.GONE);
//        ivList.setVisibility(View.GONE);
        ivAdvanceSettings.setVisibility(View.GONE);
//        ivExitApp.setVisibility(View.GONE);
        ivLock.setVisibility(View.GONE);
//        ivUnLock.setVisibility(View.GONE);
    }

    @Override
    public void fixLogoutDialogStyle(AlertDialog ad){
    }
}
