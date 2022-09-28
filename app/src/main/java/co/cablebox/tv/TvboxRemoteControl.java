package co.cablebox.tv;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class TvboxRemoteControl extends Activity {


    private static final String KEY_OPEN_APP_TECHNICIAN_MODE = "55555";
    private static final String KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE = "666666";
    private String wordKey = "";
    private final static int CODE_OPEN_SETTINGS = 3;
    private int delayBusNum = 3000;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_OPEN_SETTINGS:
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

            case KeyEvent.KEYCODE_MENU:
                //"return true" evita comportamientos por defecto del S.O. para el botón presionado
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void pressNumber(String number){
        wordKey += number;
        handler.removeMessages(CODE_OPEN_SETTINGS);
        handler.sendEmptyMessageDelayed(CODE_OPEN_SETTINGS, delayBusNum);
    }
}
