package co.cablebox.tv.activity.updating;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.settings.SettingsActivity;
import co.cablebox.tv.activity.videoplayer.VideoplayerActivity;
import co.cablebox.tv.actualizacion.MyReceiver;
import co.cablebox.tv.bean.Channels;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class UpdatingActivity extends AppCompatActivity {
    @BindView(R.id.cablebox_title)
    TextView tvCableboxTitle;

    MyReceiver myReceiver;

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
        myReceiver.download(host,fileName);
        //myReceiver.download(AppState.getUrlService().generateAndReturnApkDownloadUri(),fileName);
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


}