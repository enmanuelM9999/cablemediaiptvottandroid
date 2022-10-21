package co.cablebox.tv.activity.login;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.R;
import co.cablebox.tv.ToastManager;

public abstract class LoginActivity extends AppCompatActivity {
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

            case KeyEvent.FLAG_EDITOR_ACTION:
                System.out.println("Oprimio Enter");
                break;

            case KeyEvent.KEYCODE_BOOKMARK:
            case KeyEvent.KEYCODE_MENU:
                ActivityLauncher.launchSettingsActivityAsTechnician();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void pressNumber(String number){
        wordKey += number;
        handler.removeMessages(CODE_SALIR_APP);
        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, delayBusNum);
    }


    // Comprobar si la conexion esta disponible
    public boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public void showNoInternet(){
        try {
            View messageBox = findViewById(R.id.rl_mensaje_wifi);
            messageBox.setVisibility(View.VISIBLE);
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
    public void hideNoInternet(){
        try {
            View messageBox = findViewById(R.id.rl_mensaje_wifi);
            messageBox.setVisibility(View.INVISIBLE);
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public void checkNetwork(){
        if(!isNetDisponible()){
            showNoInternet();
        }else{
            hideNoInternet();
        }
    }



}
