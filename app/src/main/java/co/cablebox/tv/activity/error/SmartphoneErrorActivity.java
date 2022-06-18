package co.cablebox.tv.activity.error;

import android.view.View;
import android.widget.Button;

import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;

public class SmartphoneErrorActivity extends ErrorActivity{

    @Override
    public void loadComponents(){
        /*Button to login*/
        Button btnErrorLaunchLogin=findViewById(R.id.btnErrorLaunchLogin);
        btnErrorLaunchLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                AppState.getUser().resetUserCredentials();
                ActivityLauncher.launchMainActivity();
            }
        });
    }
}
