package co.cablebox.tv.activity.videoplayer;

import static co.cablebox.tv.utils.ToolBox.convertDpToPx;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.bean.Channels;

public class SmartphoneVideoPlayerActivity extends VideoplayerActivity{


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_box);

        try {
            /*Default*/
            canCloseSocketConnectionPauseVideoPlayer=true;

            /*Recover props*/
            VideoplayerActivity.channels = (Channels) getIntent().getSerializableExtra("channels");

            if (isNetDisponible()) {
                //Conectado a internet
                ButterKnife.bind(this);

                toggleHideyBar(); // Oculta Barras de Navegacion y Notificaciones
                //inicio();
                initData();
                initPlayer();

                // Control de tiempo, la app muentras constantemente la hora y fecha configurada en el dispositivo
                re = new RefresfClock();
                initClock = new Thread(re);
                initClock.start();

                setIdProgramaActual();
                showProgramInfo();
                clearAndShowChannelInfo();

                adaptarListaCanales();
                //Celular
                onActionTouch();

                // Elegir un canal en la lista de canales
                lvCanales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        lastChannelIndex=channelIndex;
                        channelIndex = position;
                        try {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            }
                        }catch (Exception e){
                            System.out.println("ERROR PAUSE");
                        }

                        tvBlack.setVisibility(View.VISIBLE);
                        changeChannelInScreen();


                    }
                });
                lvCanales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        clearScreen(10000);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                lvCanales.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        clearScreen(10000);
                        return false;
                    }
                });

                /*Catch screen off event*/
                IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                BroadcastReceiver mReceiver = new BroadcastReceiver() {

                    public void onReceive(Context context, Intent intent) {
                        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                            /*Catch event screen off*/
                            Log.e("estado", "----------------------Pantalla Apagada");
                            releaseResourcesAndFinish();

                        }
                        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                            /*Catch event screen on*/
                            Log.e("estado", "--------------------------Pantalla Encendida");
                        }
                    }
                };
                registerReceiver(mReceiver, filter);

                /*Volume control*/
                initVolumeControl();

                //si es modo smartphone, habilitar los botones del panel superior
                llSmartphoneButtons.setVisibility(View.VISIBLE);


                //fix para que las barras superiores e inferiores no ocupen tanto espacio para telefonos. Hice que ocuparan mucho espacio porque
                //   los televisores recortan la imagen en los extremos.
                rlPanelUp.setPadding(convertDpToPx(40,this),convertDpToPx(5,this),convertDpToPx(40,this),convertDpToPx(5,this));
                rlDisplayDown.setMinimumHeight(convertDpToPx(70,this));
                rlLogo.setMinimumHeight(convertDpToPx(70,this));
                llChannelName.setMinimumHeight(convertDpToPx(70,this));
                panelDownChannelInfo2.setMinimumHeight(convertDpToPx(70,this));





            } else {
                //no conectado a internet
                ButterKnife.bind(this);
                Toast.makeText(this, "Sin conexión", Toast.LENGTH_LONG).show();
                pbError.setVisibility(View.VISIBLE);
                tvBlack.setVisibility(View.VISIBLE);
                pbError.setText("Sin conexión");

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Fallo de conexión!")
                        .setMessage("")
                        .setNegativeButton(null, null)
                        .setPositiveButton(R.string.reintentar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Reintentar
                                Context context= AppState.getAppContext();
                                Intent intent=new Intent();
                                intent.setClass(context, this.getClass());
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showLogoutDialog(){

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("¿Cerrar sesión?");
        //builder.setMessage(""); //Mensaje además del titulo

        //Set positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityLauncher.logout();
            }
        });

        //Set negative button
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        //Create Dialog
        AlertDialog ad= builder.create();
        ad.show();
    }

}
