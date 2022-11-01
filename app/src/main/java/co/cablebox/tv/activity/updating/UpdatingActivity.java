package co.cablebox.tv.activity.updating;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.settings.SettingsActivity;
import co.cablebox.tv.activity.videoplayer.VideoplayerActivity;
import co.cablebox.tv.actualizacion.MyReceiver;
import co.cablebox.tv.bean.Channels;

import android.app.DownloadManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class UpdatingActivity extends AppCompatActivity {
    @BindView(R.id.cablebox_title)
    TextView tvCableboxTitle;

    MyReceiver myReceiver;
    private String wordKey = "";
    private final static int CODE_SALIR_APP = 3;

    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updating);
        ButterKnife.bind(this);

        setFontOnTitle();//Fix para que el título tenga una fuente personalizada

        /*Recover props*/
        String fileName = ""+getIntent().getStringExtra("fileName");
        String host= ""+getIntent().getStringExtra("host");

        //Necessary for start apk downloading
        initDescarga();



        //Start download and apk installing
        if(!AppState.isNetDisponible()){
            showNonetMessage();
        }else{
            hideNonetMessage();
            downloadManager=myReceiver.download(host,fileName);
            //myReceiver.download(AppState.getUrlService().generateAndReturnApkDownloadUri(),fileName);
        }
    }

    private void showNonetMessage(){
        try {
            View rlNoNet=findViewById(R.id.rl_mensaje_wifi);
            rlNoNet.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void hideNonetMessage(){
        try {
            View rlNoNet=findViewById(R.id.rl_mensaje_wifi);
            rlNoNet.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFontOnTitle(){
        Typeface segoe;
        String fontPath="fonts/segoe_ui_bold.ttf";
        segoe= Typeface.createFromAsset(getAssets(),fontPath);
        tvCableboxTitle.setTypeface(segoe);
    }

    private void initDescarga(){
        myReceiver = new MyReceiver(UpdatingActivity.this);
        myReceiver.Registrar(myReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


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

            case KeyEvent.KEYCODE_DPAD_UP:
                pressNumber("0");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                pressNumber("2");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                pressNumber("4");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                pressNumber("6");
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                pressNumber("8");
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void pressNumber(String number){
        wordKey += number;
        handler.removeMessages(CODE_SALIR_APP);
        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, 3000);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_SALIR_APP:
                    if (wordKey.equals(AppState.KEY_OPEN_APP_TECHNICIAN_MODE)) {
                         stopDownload();
                        ActivityLauncher.launchSettingsActivityAsNormalUser();
                    } else if(wordKey.equals(AppState.KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE)){
                        stopDownload();
                        ActivityLauncher.launchSettingsActivityAsTechnician();
                    }
                    wordKey = "";
                    break;

            }
        }
    };

    void stopDownload(){
        try {
            downloadManager.remove(MyReceiver.downloadedId);
            myReceiver.abortBroadcast();
        }catch(Exception e){
            System.out.println(e);
        }finally {

            myReceiver.borrarRegistro(myReceiver);
        }

    }


}