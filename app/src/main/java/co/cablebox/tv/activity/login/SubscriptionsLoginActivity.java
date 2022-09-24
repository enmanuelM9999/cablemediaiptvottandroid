package co.cablebox.tv.activity.login;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.IpmuxActivity;

public class SubscriptionsLoginActivity extends SmartphoneLoginActivity implements IpmuxActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_landscape);
        ButterKnife.bind(this);

        /* Hide loading screen */
        hideLoading();

        /* App state configs */
        setMessageOnAppState();

        /* Set onClick events*/
        initOnClickEvents();

        /*Check if user already logged in*/
        if (AppState.getUser().isLoggedIn()){
            showLoading();
            AppState.getSocketConnection().socketEmitConnect();
        }
        else{
            etUsername.requestFocus();
        }



    }

    @Override
    void loginExtraSteps(){
        showLoading();
    }

    void showLoading(){
        /*Show loading screen*/
        LinearLayout llLoadingChannels=findViewById(R.id.llLoadingChannels);
        llLoadingChannels.setVisibility(View.VISIBLE);

        /*Hide view with data for no autofocus in edit text in android tv*/
        LinearLayout llLoginData= findViewById(R.id.llLoginData);
        llLoginData.setVisibility(View.GONE);
    }
    void hideLoading(){
        /*Hide loading screen*/
        LinearLayout llLoadingChannels=findViewById(R.id.llLoadingChannels);
        llLoadingChannels.setVisibility(View.INVISIBLE);

        /*Show view with data*/
        LinearLayout llLoginData= findViewById(R.id.llLoginData);
        llLoginData.setVisibility(View.VISIBLE);
    }
}