package co.cablebox.tv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.socket.TvboxSocketConnection;
import co.cablebox.tv.user.TvboxUser;

public class TvboxLoadingChannelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void start(){
        /*Set app context*/
        AppState.setAppContext(this);

        /*Set GUI*/
        setContentView(R.layout.activity_tvbox_loading_channels);

        /*Active full screen*/
        setFullScreenMode();

        /*Connect socket*/
        connectSocket();
    }

    private void connectSocket(){
        AppState.getSocketConnection().socketEmitConnect();
    }

    public void setFullScreenMode() {
        String TAG = TvboxLoadingChannelsActivity.class.getName();
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}