package co.cablebox.tv.activity.error;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;

public abstract class ErrorActivity extends AppCompatActivity {

    @BindView(R.id.txtErrorType)
    TextView txtErrorType;

    @BindView(R.id.txtErrorMsg)
    TextView txtErrorMsg;

    @BindView(R.id.tvErrorUserId)
    TextView tvErrorUserId;

    @BindView(R.id.tvErrorServerInfo)
    TextView tvErrorServerInfo;

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
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        /*Set app context*/
        AppState.setAppContext(this);

        /* Recover props*/
        String errorType= getIntent().getStringExtra("errorType");
        String errorMsg= getIntent().getStringExtra("errorMsg");

        System.out.println("------------------ErrorActivity: "+errorMsg);

        /* Set Error type and error msg*/
        txtErrorType.setText(errorType);
        txtErrorMsg.setText(errorMsg);

        /* Set user info and server info*/
        tvErrorUserId.setText(AppState.getUser().getUserId());
        tvErrorServerInfo.setText(AppState.getUrlService().generateAndReturnSocketUri());

        /*If exist an error, reset user credentials info for user can login again*/
        //AppState.getUser().resetUserCredentials();

    }

    public void loadComponents(){}
}