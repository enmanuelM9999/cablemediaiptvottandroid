package co.cablebox.tv.activity.error;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
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

        /* Recover props*/
        String errorType= getIntent().getStringExtra("errorType");
        String errorMsg= getIntent().getStringExtra("errorMsg");

        System.out.println("------------------ErrorActivity: "+errorMsg);

        /* Set Error type and error msg*/
        txtErrorType.setText(errorType);
        txtErrorMsg.setText(errorMsg);

        /* Set user info and server info*/
        tvErrorUserId.setText(AppState.getUser().getUserId());
        tvErrorServerInfo.setText(AppState.getUrlService().generateAndReturnSocketUriWithoutProtocol());

        /*If exist an error, reset user credentials info for user can login again*/
        //AppState.getUser().resetUserCredentials();

        loadComponents();

    }

    public void loadComponents(){}


    private static final String KEY_OPEN_APP_TECHNICIAN_MODE = "55555";
    private static final String KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE = "02468";
    private String wordKey = "";
    private final static int CODE_SALIR_APP = 3;
    private int delayBusNum = 3000;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_SALIR_APP:
                    if (wordKey.equals(KEY_OPEN_APP_TECHNICIAN_MODE)) {
                        ActivityLauncher.launchSettingsActivityAsNormalUser();
                    } else if(wordKey.equals(KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE)){
                        ActivityLauncher.launchSettingsActivityAsTechnician();
                    }
                    wordKey = "";
                    break;

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_1:
                pressNumber("1");
                break;

            case KeyEvent.KEYCODE_2:
                pressNumber("2");
                break;

            case KeyEvent.KEYCODE_3:
                pressNumber("3");
                break;

            case KeyEvent.KEYCODE_4:
                pressNumber("4");
                break;

            case KeyEvent.KEYCODE_5:
                pressNumber("5");
                break;

            case KeyEvent.KEYCODE_6:
                pressNumber("6");
                break;
            case KeyEvent.KEYCODE_7:
                pressNumber("7");
                break;

            case KeyEvent.KEYCODE_8:
                pressNumber("8");
                break;

            case KeyEvent.KEYCODE_9:
                pressNumber("9");
                break;

            case KeyEvent.KEYCODE_BOOKMARK:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                pressNumber(KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE);
                break;

            case KeyEvent.KEYCODE_MENU:
                //"return true" evita comportamientos por defecto del S.O. para el botón presionado
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void pressNumber(String number){
        wordKey += number;
        handler.removeMessages(CODE_SALIR_APP);
        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, delayBusNum);
    }
}