package co.cablebox.tv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import co.cablebox.tv.AppState;
import co.cablebox.tv.R;

public class TvboxLoadingChannelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
    }

    private void start(){
        /*Set app context*/
        AppState.setAppContext(this);

        /*Connect socket as tvbox*/
        loginWithDeviceId(AppState.getUser().getUserId());

        /*Set GUI*/
        setContentView(R.layout.activity_tvbox_loading_channels);
    }

    private void loginWithDeviceId(String userId){
        AppState.getSocketConn();
    }
}