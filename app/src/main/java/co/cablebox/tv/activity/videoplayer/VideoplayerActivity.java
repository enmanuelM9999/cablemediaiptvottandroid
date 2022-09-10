package co.cablebox.tv.activity.videoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.BaseInputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.CustomVolumeManager;
import co.cablebox.tv.R;
import co.cablebox.tv.ToastManager;
import co.cablebox.tv.activity.AppsListActivity;
import co.cablebox.tv.activity.ChannelListActivityBox;
import co.cablebox.tv.activity.ServiceProgramActivity;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.bean.MensajeBean;
import co.cablebox.tv.utils.ConexionSQLiteHelper;
import co.cablebox.tv.utils.NetWorkUtils;
import co.cablebox.tv.utils.OnSwipeTouchListener;
import co.cablebox.tv.utils.StorageUtils;
import co.cablebox.tv.utils.Utilidades;


public abstract class VideoplayerActivity extends Activity implements IVLCVout.OnNewVideoLayoutListener {
    Activity activity=this;

    public static final String TAG = VideoplayerActivity.class.getName();

    public static boolean canCloseSocketConnectionPauseVideoPlayer =true;

    public static final String MESSAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyApp/MessageInfo";
    public static final String LOCAL_MESSAGES_FILE = "messageList.xml";

    public AudioManager audio = (AudioManager) AppState.getAppContext().getSystemService(AppState.getAppContext().AUDIO_SERVICE);

    //Listas de Canales, Favoritos y Todos los Canales
        public static Channels channels;
        public static MensajeBean mensajeBean;
        public static ArrayList<Channels.Channel> canalesFavoritos;
        public static ArrayList<Channels.Channel> canalesAux;


        @BindView(R.id.tv_channel_number_change)
        TextView tvChannelNumberChange;
        @BindView(R.id.tv_channel_number)
        TextView tvChannelNumber;
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;
        @BindView(R.id.tv_channel_logo)
        ImageView tvChannelLogo;
        @BindView(R.id.tv_classification)
        ImageView tvClassification;
        @BindView(R.id.rl_panel_down)
        RelativeLayout rlDisplayDown;
        @BindView(R.id.ll_panel_up)
        LinearLayout llDisplayUp;
        @BindView(R.id.rl_panel_up)
        RelativeLayout rlPanelUp;
        @BindView(R.id.panel_down_channel_info_2)
        LinearLayout panelDownChannelInfo2;
        @BindView(R.id.rl_logo)
        RelativeLayout rlLogo;
        @BindView(R.id.ll_channel_name)
        LinearLayout llChannelName;


        SurfaceView surfaceview;

        @BindView(R.id.pb_error)
        TextView pbError;
        @BindView(R.id.tv_quality)
        TextView tvQuality;
        @BindView(R.id.tv_system_time)
        TextView tvSystemTime;

        @BindView(R.id.rl_volumenA)
        RelativeLayout rlVolumenA;
        @BindView(R.id.sb_volumenA)
        public SeekBar sbVolumenA;
        @BindView(R.id.tv_volume_indicator)
        public TextView volumeIndicator;



        @BindView(R.id.iv_mute)
        ImageView ivMute;

        @BindView(R.id.iv_nav)
        ImageView ivNav;

        @BindView(R.id.rl_opciones)
        RelativeLayout rlOpciones;
        @BindView(R.id.ll_smartphone_buttons)
        LinearLayout llSmartphoneButtons;
        @BindView(R.id.ll_options)
        RelativeLayout llOptions;
        @BindView(R.id.iv_informativo)
        ImageView ivInformation;
        @BindView(R.id.iv_favorite)
        ImageView ivFavorite;
        @BindView(R.id.iv_list)
        ImageView ivList;
        @BindView(R.id.ivLogout)
        ImageView ivLogout;
        @BindView(R.id.iv_exit_app)
        ImageView ivExitApp;
        @BindView(R.id.iv_lock)
        ImageView ivLock;
        @BindView(R.id.iv_unlock)
        ImageView ivUnLock;
        @BindView(R.id.iv_type_num)
        ImageView ivTypeNum;
        @BindView(R.id.ivSettings)
        ImageView ivSettings;
        @BindView(R.id.ivAdvanceSettings)
        ImageView ivAdvanceSettings;



        //Panel de Numeros
        @BindView(R.id.rl_panel_num)
        RelativeLayout rlPanelNum;
        @BindView(R.id.tv_num_one)
        TextView numOne;
        @BindView(R.id.tv_num_two)
        TextView numTwo;
        @BindView(R.id.tv_num_three)
        TextView numThree;
        @BindView(R.id.tv_num_four)
        TextView numFour;
        @BindView(R.id.tv_num_five)
        TextView numFive;
        @BindView(R.id.tv_num_six)
        TextView numSix;
        @BindView(R.id.tv_num_seven)
        TextView numSeven;
        @BindView(R.id.tv_num_eight)
        TextView numEight;
        @BindView(R.id.tv_num_nine)
        TextView numNine;
        @BindView(R.id.tv_num_zero)
        TextView numZero;
        @BindView(R.id.iv_enter_num)
        ImageView ivEnterNum;

        //Lista de Canales
        @BindView(R.id.ll_list)
        LinearLayout llList;
        @BindView(R.id.lv_canales)
        ListView lvCanales;

        //View CPU RAM RED
        @BindView(R.id.ll_cpu_ram)
        LinearLayout llCpuRam;
        @BindView(R.id.tv_cpu)
        TextView tvCpu;
        @BindView(R.id.tv_ram)
        TextView tvRam;
        @BindView(R.id.tv_red)
        TextView tvRed;

        @BindView(R.id.tv_net_state)
        TextView tvNetState;
        @BindView(R.id.tv_black)
        TextView tvBlack;

        @BindView(R.id.ll_actualizando)
        LinearLayout llActualizando;

        @BindView(R.id.rl_mensajes)
        RelativeLayout fondoNot;
        @BindView(R.id.mensaje_not)
        TextView mensajeNot;

    // Variables de control de tiempo actual
        int hour=0, minute =0, second = 0, year = 0, month = 0, day = 0;
        Thread initClock = null;
        Runnable re;
        String sec, min, hor;
        String curTime;

    //Medidor de Banda Ancha
        public String mDownloadSpeedOutput;
        public String mUnits;
        public boolean meterOn = false;

    // Eventos
        public final static int CODE_SHOWLOADING = 1;

        public final static int CODE_STOP_SHOWLOADING = 2;

        public final static int CODE_NET_STATE = 3;

        public final static int CODE_HIDE_BLACK = 4;

        public final static int CODE_CHANGE_BY_NUM = 5;

        public final static int CODE_HIDE_ERROR = 6;

        public final static int CODE_SALIR_APP = 7;

        public final static int CODE_CLEAR_SCREEN = 8;

        public final static int CODE_HIDE_VOLUMEN = 9;

        public final static int CODE_HIDE_OPTION = 10;

        public final static int CODE_DEG_LOCK = 11;

        public final static int CODE_COLOR_NUM = 12;

        public final static int CODE_CHANGE_CHANNEL = 13;

        public final static int CODE_HIDE_NOT = 14;

        public final static int CODE_RESTART_APP = 15;

        public final static int CODE_NOTF_VIEW = 16;

        public final static int CODE_NOTF_VIEW_OPC = 17;

        public final static int CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW = 18;

        public final static int CODE_MEDIA_PLAYER_UNMUTE = 19;


    // Variable guardar No. de canal
        public final static String PROGRAM_KEY = "lastProIndex";

    // Variables para el correcto funcionamiento del reproductor con VLC
        public SurfaceHolder surfaceHolder;
        public LibVLC libvlc = null;
         MediaPlayer mediaPlayer = null;
        public IVLCVout ivlcVout;
        public Media media;
        public MediaController controller;
        public SurfaceView mSubtitlesSurface = null;

    //fix contar que se mutee y desmutee una sola vez cuando el buffer es 100%
        public boolean wasMuted=false;
        public boolean wasUnmuted=false;

        public Reproduccion reproduccion = null;

    // Variables para bloquear la el tactil y barras de notificacion y navegacion
        WindowManager wmanager;
        LinearLayout llayout;



    // Claves
    public static final String KEY_VIEW_DEVICE_STATS = "99999"; // Visualizar Consumo de CPU y RAM
    public static final String KEY_OPEN_APP_TECHNICIAN_MODE = "54321"; // Ajustes avanzados de la app
    public static final String KEY_OPEN_USER_MODE = "12345"; // Ajustes de la app
    public static final String KEY_TOGGLE_TOP_BUTTONS = "11111"; // Show or hide top buttons
    public String wordKey = "";

    // Variables de Canal actual y programa actual
         static int channelIndex = 0;
         static int idProgramCurrent = 0;
         static int lastChannelIndex=0; //el último canal que se reprodujo, esto para volver al canal anterior con el botón "atrás"
         boolean isScreenLocked = false; // true significa Bloqueada, la pantalla no recibe gestos ni eventos touch

    public static boolean failNet = false; // Controla cualquier fallo de conexion

    // Eventos
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_SHOWLOADING:
                    showLoading();
                    handler.sendEmptyMessageDelayed(CODE_SHOWLOADING, 1000);
                    break;

                case CODE_STOP_SHOWLOADING:
                    hideLoading();
                    handler.removeMessages(CODE_SHOWLOADING);
                    break;

                case CODE_NET_STATE:
                    tvNetState.setVisibility(View.INVISIBLE);
                    break;

                case CODE_HIDE_BLACK:
                    tvBlack.setVisibility(View.INVISIBLE);
                    delayExitInfoChannel = 3000;
                    //handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, delayExitInfoChannel);
                    break;

                case CODE_CHANGE_BY_NUM:
                    if (numChange != "") {
                        cambiarPorNumero(Integer.parseInt(numChange));
                    }
                    numChange = "";
                    writingNum = false;
                    delayBusNum = 3000;
                    handler.removeMessages(CODE_CHANGE_BY_NUM);
                    break;

                case CODE_HIDE_ERROR:
                    hideNetworkInfo();
                    break;

                case CODE_SALIR_APP:
                     if (wordKey.equals(KEY_VIEW_DEVICE_STATS)) {
                        viewCpuRam();
                        delayBusNum = 3000;
                        wordKey = "";
                    } else if (wordKey.equals(KEY_OPEN_APP_TECHNICIAN_MODE)){
                         openSttingsActivityAsTechnician();
                    }
                     else if (wordKey.equals(KEY_OPEN_USER_MODE)){
                         openSettingsActivityAsNormalUser();
                     }
                     else if (wordKey.equals(KEY_TOGGLE_TOP_BUTTONS)){
                         boolean areVisiblesTopButtons= llSmartphoneButtons.getVisibility()==View.VISIBLE;
                         if (areVisiblesTopButtons) hideTopButtons();
                         else showTopButtons();
                     }
                    delayBusNum = 3000;
                    wordKey = "";
                    handler.removeMessages(CODE_SALIR_APP);
                    break;

                case CODE_CLEAR_SCREEN:
                     clearScreen();
                    break;

                case CODE_HIDE_VOLUMEN:
                    volumeManager.hideVolumeIndicator();
                    break;

                case CODE_HIDE_OPTION:
                    exitOptions();

                    posOpcion = 1;
                    ivList.setBackground(getDrawable(R.drawable.borde_volumen));
                    ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));
                    ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                    break;

                case CODE_DEG_LOCK:
                    ivUnLock.setAlpha(0.5f);
                    break;

                case CODE_COLOR_NUM:
                    tvChannelNumber.setTextColor(Color.rgb(255,255,255));
                    break;

                case CODE_CHANGE_CHANNEL:
                    change = false;
                    setIdProgramaActual();
                    showProgramInfo();
                    changeChannel();
                    break;

                case CODE_HIDE_NOT:
                    exitOptions();
                    break;

                case CODE_RESTART_APP:
                    ServiceProgramActivity.openLiveB(VideoplayerActivity.this);
                    finish();
                    break;

                case CODE_NOTF_VIEW:
                    mensajeNot.setVisibility(View.INVISIBLE);
                    fondoNot.setVisibility(View.INVISIBLE);

                    break;

                case CODE_NOTF_VIEW_OPC:
                    mensajeNot.setVisibility(View.INVISIBLE);
                    fondoNot.setVisibility(View.INVISIBLE);
                    break;

                case CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW:
                    tvChannelNumberChange.setVisibility(View.INVISIBLE);
                    break;

                case CODE_MEDIA_PLAYER_UNMUTE:
                    unMuteAudio();
                    break;

            }
        }
    };

    //Audio manager
    public AudioManager audioManager;
    public CustomVolumeManager volumeManager;

    //Animaciones para cada panel o grupo de items en la interfaz
        public TranslateAnimation animIn;
        public TranslateAnimation exitAnim;
        public TranslateAnimation animInBot;
        public TranslateAnimation exitAnimBot;
        public TranslateAnimation animInOptions;
        public TranslateAnimation exitAnimOptions;

        public TranslateAnimation animInPanelNum;
        public TranslateAnimation exitAnimPanelNum;
        public TranslateAnimation animInList;
        public TranslateAnimation exitAnimList;
        public TranslateAnimation animNotif;
        public TranslateAnimation animInListNot;
        public TranslateAnimation exitAnimListNot;

    public NetworkReceiver networkReceiver;

    // Variables de control y administracion de eventos
        public boolean change = false;
        public boolean onVolumen = false;
        public boolean controlError = false;
        public int numCurrent = 0;

        public String numChange = "";
        public boolean writingNum = false;
        public boolean viewInfo = false;
        public int delayBusNum = 3000;
        public int delayExitInfoChannel = 3000;

        public boolean acFavoritos = false;
        public boolean enAnimacion = false;
        public int posOpcion = 1;

        public boolean cambCanal = false;


    //Canal Informativo
    public String numCanalInformativo = "-1";

    //Timer Canal
    Timer tiempo_canal = new Timer();
    boolean nuevoCanal = true;

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

                //All nav actions like control app with fingers or with a remote control
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
                        clearScreen(20000);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                lvCanales.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        clearScreen(20000);
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
                configTopButtons();

                //Fullscreen
                System.out.println("-----------------------------------------video oncreate");
                hideSystemUI(this);

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
                                Intent intent=new Intent();
                                intent.setClass(VideoplayerActivity.this, VideoplayerActivity.this.getClass());
                                VideoplayerActivity.this.startActivity(intent);
                                VideoplayerActivity.this.finish();
                            }
                        })
                        .show();
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void translateNotf() {
        if(mensajeBean.getData() != null) {
            if (mensajeNot.getVisibility() == View.INVISIBLE) {
                System.out.println("Translate ON");
                float x = fondoNot.getWidth() + mensajeNot.getWidth() * 10;
                if (animNotif == null) {
                    System.out.println("animNotif == null");
                    animNotif = new TranslateAnimation(x, 0f, 0f, 0f);
                    animNotif.setDuration(10000);
                }

                animNotif.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fondoNot.setVisibility(View.VISIBLE);
                        mensajeNot.setVisibility(View.VISIBLE);
                        System.out.println("Mensaje Actual "+mensajeBean.getData().get(0).getTexto());
                        mensajeNot.setText(mensajeBean.getData().get(0).getTexto());
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        handler.sendEmptyMessageDelayed(CODE_NOTF_VIEW, 1000);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mensajeNot.startAnimation(animNotif);
            }
        }
    }
    public void translateNotfOp(final String mensaje) {
        if(mensajeBean.getData() != null) {
            if (mensajeNot.getVisibility() == View.INVISIBLE) {
                float x = fondoNot.getWidth() + mensajeNot.getWidth() * 10;
                if (animNotif == null) {
                    animNotif = new TranslateAnimation(x, 0f, 0f, 0f);
                    animNotif.setDuration(10000);
                }

                animNotif.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fondoNot.setVisibility(View.VISIBLE);
                        mensajeNot.setVisibility(View.VISIBLE);
                        mensajeNot.setText(mensaje);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        handler.sendEmptyMessageDelayed(CODE_NOTF_VIEW_OPC, 1000);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mensajeNot.startAnimation(animNotif);
            }
        }
    }


    // Oculta Barras de Navegacion y Notificaciones
    public void toggleHideyBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reinicar IPTV")
                .setMessage("Estás seguro?")
                .setNegativeButton(android.R.string.cancel, null)// sin listener
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Salir

                        if (reproduccion != null)
                            reproduccion.cancel(true);

                        mediaPlayer.stop();
                        mediaPlayer.getVLCVout().detachViews();
                    }
                })
                .show();*/

        System.out.println("OnStop......");
    }

    public void registerNetReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, filter);
    }

    public void getCodec(){
        System.out.println("AspectRatio: "+mediaPlayer.getAspectRatio()+"");
        System.out.println("Id Original Video: "+mediaPlayer.getVideoTrack());
        System.out.println("Id Original Audio: "+mediaPlayer.getAudioTrack());
    }

    // Cambiar lista de Canales en la Interfaz
    public void changeChannelList() {
        int programBack = 0;
        int programNext = 0;

        if(channelIndex > (channels.getChannels().size() - 1))
            channelIndex = 0;
        else if(channelIndex < 0)
            channelIndex = channels.getChannels().size() - 1;

        programNext = channelIndex + 1;
        programBack = channelIndex - 1;

        if(programNext > (channels.getChannels().size() - 1))
            programNext = 0;
        else if(programNext < 0)
            programNext = channels.getChannels().size() - 1;

        if(programBack > (channels.getChannels().size() - 1))
            programBack = 0;
        else if(programBack < 0)
            programBack = channels.getChannels().size() - 1;

        /*tv_program_name_list_top.setText(channels.getData().get(programBack).getNum() + "    " +
                channels.getData().get(programBack).getName());
        selecImg(tv_program_logo_list_top, channels.getData().get(programBack).getLogo());

        tv_program_name_list_center.setText(channels.getData().get(channelIndex).getNum() + "    " +
                channels.getData().get(channelIndex).getName());
        selecImg(tv_program_logo_list_center, channels.getData().get(channelIndex).getLogo());

        tv_program_name_list_bot.setText(channels.getData().get(programNext).getNum() + "    " +
                channels.getData().get(programNext).getName());
        selecImg(tv_program_logo_list_bot, channels.getData().get(programNext).getLogo());*/
    }

    // Buscar imagen en la drawable y actualizar imagen en la interfaz
    public void selecImg(ImageView imageView, String nombre) {
        boolean aux = false;
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            try {
                if (f.getName().startsWith(nombre)) {
                    aux = true;
                    int resourceID = this.getResources().getIdentifier(nombre, "drawable",this.getPackageName());
                    imageView.setImageResource(f.getInt(resourceID));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!aux) {
            imageView.setImageResource(R.drawable.plantilla);
        }
    }

    /* Iniciar Variables necesarias para la actividad
     * se comprueba el ultimo canal elegido, esto es guardado en la cache de la aplicacion*/
    void initData() {

            channelIndex = StorageUtils.getInt(VideoplayerActivity.this, PROGRAM_KEY, 0);
            if (channelIndex == (channels.getChannels().size()-1))
                channelIndex = 0;


        if (channelIndex >= channels.getChannels().size() || channelIndex < 0)
            channelIndex = 1;

        numCurrent = channelIndex;
        changeChannelList();


        rlDisplayDown.setVisibility(View.VISIBLE);
        //llProgramList.setVisibility(View.VISIBLE);
        canalesAux = (ArrayList<Channels.Channel>) channels.getChannels();
        organizarFavoritos();



        lvCanales.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        try
        {


            //tvVolumen.setText(""+sbVolumenA.getProgress());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        rlVolumenA.setVisibility(View.VISIBLE); //show volume bar seekbar
        ivMute.setVisibility(View.INVISIBLE);


    }

    // Verificar si los favoritos guardados en la base SQLite existe en la lista de canales actual
    public void organizarFavoritos(){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoplayerActivity.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor c = db.query(
                Utilidades.TABLA_FAVORITO,
                null,
                null,
                null,
                null,
                null,
                null);

        canalesFavoritos = new ArrayList<>();
        System.out.println("Numero de Favoritos "+c.getCount());
        while(c.moveToNext()){
            boolean encontro = false;
            String name = c.getString(c.getColumnIndex(Utilidades.CAMPO_NOMBRE));
            System.out.println("Favorito "+name);
            // Acciones...
            for(int i = 0; i < channels.getChannels().size(); i++){
                if(name.equals(channels.getChannels().get(i).getName())){
                    encontro = true;
                    canalesFavoritos.add(channels.getChannels().get(i));
                }
            }
            if(!encontro)
                eliminarFavorito(name);
        }
        c.close();
        db.close();
    }

    /*Para el codigo de la fecha en la pagina https://www.epochconverter.com/ usando local time
     * Se obtiene el id del programa actual del canal actual, esto se actualiza cada que se cambia de canal*/
    void setIdProgramaActual() {
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < channels.getChannels().get(channelIndex).getProgramas().size(); i++) {
            long horaActual = c.getTimeInMillis();
            long horaA = channels.getChannels().get(channelIndex).getProgramas().get(i).getCalendarInit().getTimeInMillis();
            long horaB = channels.getChannels().get(channelIndex).getProgramas().get(i).getCalendarFinish().getTimeInMillis();

            if (horaActual >= horaA && horaActual <= horaB) {
                idProgramCurrent = i;
                break;
            }
        }

    }

    // Inicializa todas las variables requeridas para la reproduccion y carga el canal actual
    void initPlayer() {
        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("-vvv");
        libvlc = new LibVLC(VideoplayerActivity.this, options);

        ViewStub stub = (ViewStub) findViewById(R.id.surface_stub);
        surfaceview = (SurfaceView) stub.inflate();
        stub = (ViewStub) findViewById(R.id.subtitles_surface_stub);
        mSubtitlesSurface = (SurfaceView) stub.inflate();

        controller = new MediaController(this);
        controller.setAnchorView(surfaceview);

        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mediaPlayer = new MediaPlayer(libvlc);
        showLoading();
        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        Log.i(TAG, "event.getBuffering(): " + event.getBuffering());
                        if (event.getBuffering() >= 100f ) {

                            if(!wasMuted){
                                muteAudio();
                                wasMuted=true;
                            }

                            if(!controlError || numCurrent != channelIndex){
                                hideLoading();
                                Log.i(TAG, "onEvent: buffer success...");
                                handler.sendEmptyMessageDelayed(CODE_HIDE_BLACK, 1500); // Desaparecer la pantalla negra con retraso despues de que el buffer del canal está al 100%
                                numCurrent = channelIndex;
                                getCodec();
                            }else {
                                playerInterface.seekTo(0);
                                controlError = false;
                            }

                            if (!wasUnmuted){
                                handler.removeMessages(CODE_MEDIA_PLAYER_UNMUTE);
                                handler.sendEmptyMessageDelayed(CODE_MEDIA_PLAYER_UNMUTE,1500);
                                wasUnmuted=true;
                            }
                            //mediaPlayer.play();
                        } else {
                            if(numCurrent != channelIndex)
                                showLoading();
                            //System.out.println("Cargando: " + Math.floor(event.getBuffering()) + "%");
                        }

                        break;

                    case MediaPlayer.Event.Playing:
                        Log.i(TAG, "onEvent: playing...");
                        controlError = true;
                        break;

                    case MediaPlayer.Event.EncounteredError:
                        Log.i(TAG, "onEvent: error...");
                        //mediaPlayer.stop();
                        play(0);
                        //Toast.makeText(VideoPlayerActivity.this, "Canal Inactivo!", Toast.LENGTH_LONG).show();
                        tvBlack.setVisibility(View.VISIBLE);
                        if (!failNet){
                            pbError.setText("Problemas técnicos en el canal");
                            pbError.setVisibility(View.VISIBLE);
                        }
                        break;

                    case MediaPlayer.Event.PausableChanged:
                        System.out.println("PausableChanged..............");
                        break;

                    case MediaPlayer.Event.EndReached:
                        System.out.println("EndReached..............");
                        break;

                    case MediaPlayer.Event.MediaChanged:
                        System.out.println("MediaChanged..............");
                        break;

                    case MediaPlayer.Event.Opening:
                        System.out.println("Opening..............");
                        break;


                    case MediaPlayer.Event.Paused:
                        System.out.println("Paused..............");
                        break;


                    case MediaPlayer.Event.Stopped:
                        //System.out.println("Stopped..............");
                        play(0);
                        break;


                    case MediaPlayer.Event.TimeChanged:
                        //System.out.println("TimeChanged..............");
                        break;

                    case MediaPlayer.Event.PositionChanged:
                        //System.out.println("PositionChanged..............");
                        break;

                    case MediaPlayer.Event.SeekableChanged:
                        System.out.println("SeekableChanged..............");
                        break;

                    case MediaPlayer.Event.LengthChanged:
                        System.out.println("LengthChanged..............");
                        break;

                    case MediaPlayer.Event.Vout:
                        System.out.println("Vout..............");
                        break;


                    case MediaPlayer.Event.ESAdded:
                        System.out.println("ESAdded..............");
                        break;

                    case MediaPlayer.Event.ESDeleted:
                        System.out.println("ESDeleted..............");
                        break;

                    case MediaPlayer.Event.ESSelected:
                        System.out.println("ESSelected..............");
                        break;
                }
            }
        });

        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(surfaceview);
        if (mSubtitlesSurface != null)
            ivlcVout.setSubtitlesView(mSubtitlesSurface);
        ivlcVout.attachViews(this);

        ivlcVout.addCallback(new IVLCVout.Callback() {
            @Override
            public void onSurfacesCreated(IVLCVout vlcVout) {
                int sw = getWindow().getDecorView().getWidth();
                int sh = getWindow().getDecorView().getHeight();

                if (sw * sh == 0) {
                    Log.e(TAG, "Invalid surface size");
                    return;
                }

                mediaPlayer.getVLCVout().setWindowSize(sw, sh);
                mediaPlayer.setAspectRatio("16:9");
                mediaPlayer.setScale(0);
            }

            @Override
            public void onSurfacesDestroyed(IVLCVout vlcVout) {

            }
        });

        media = new Media(libvlc, Uri.parse(channels.getChannels().get(channelIndex).getUrl()));
        mediaPlayer.setMedia(media);
        changeChannel();

    }


    // Iniciar actividad VideoPlayerActivity desde la vista de Categorias
    public static void openLiveB(Context context, Channels channels, MensajeBean mensajeBean, String num, String IMEI) {
        VideoplayerActivity.mensajeBean = mensajeBean;
        VideoplayerActivity.channels = channels;
        for (int i = 0; i < VideoplayerActivity.channels.getChannels().size(); i++) {
            if (channels.getChannels().get(i).getNum().equals(num)) {
                VideoplayerActivity.channelIndex = i;
                break;
            }
        }
        context.startActivity(new Intent(context, VideoplayerActivity.class));
    }

    // Iniciar actividad VideoPlayerActivity desde la vista de Categorias pero sin errores como openLiveB.
    public static void openLiveC(Context context, Channels channels, MensajeBean mensajeBean, String IMEI, String num) {
        VideoplayerActivity.mensajeBean = mensajeBean;
        VideoplayerActivity.channels = channels;

        //encontrar el index del canal elegido
        int indexOfChannel=0;
        for (int i = 0; i < VideoplayerActivity.channels.getChannels().size(); i++) {
            if (channels.getChannels().get(i).getNum().equals(num)) {
                indexOfChannel=i;
                VideoplayerActivity.channelIndex = indexOfChannel;
                break;
            }
        }
        //cargar el activity
        StorageUtils.setInt(context, PROGRAM_KEY, indexOfChannel);
        context.startActivity(new Intent(context, VideoplayerActivity.class));
    }

    // Reproduce canal actual
    public void play(int position) {
        reproduccion = (Reproduccion) new Reproduccion().execute();

        /*Uri parse = Uri.parse(channels.getData().get(position).getUrl());
        media = new Media(libvlc, parse);
        mediaPlayer.setMedia(media);
        ivlcVout.setVideoView(surfaceview);
        if (mSubtitlesSurface != null)
            ivlcVout.setSubtitlesView(mSubtitlesSurface);
        ivlcVout.attachViews();
        mediaPlayer.play();*/
    }

    // Escribiendo el numero del canal
    public void canalNum(String num) {
        if (numChange.length() <= 3) { //permite 3 digitos antes de agregar otro, max 4 digitos
            handler.removeMessages(CODE_CHANGE_BY_NUM);
            numChange += num;

            tvChannelNumberChange.setVisibility(View.VISIBLE);
            tvChannelNumberChange.setText(numChange);
            handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, HUD_HIDE_TIME);
        }
    }

    // Acumulado de numeros y comprobar claves
    public void claveExit(String letra) {
        wordKey += letra;
        handler.removeMessages(CODE_SALIR_APP);
        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, delayBusNum);
    }

    // Inicializa la actividad AppsListActivity para ver la lista de aplicaciones instaladas en el dispositivo
    public void viewListApps() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){}

        surfaceview.setVisibility(View.INVISIBLE);
        mSubtitlesSurface.setVisibility(View.INVISIBLE);

        AppsListActivity.openLive(this, channels);
        finish();
    }

    // Mostrar u ocultar el consumo de Ram y CPU
    public void viewCpuRam() {
        if (llCpuRam.getVisibility() == View.INVISIBLE) {
            //Mostrar CPU y RAM
            llCpuRam.setVisibility(View.VISIBLE);
            handler.postDelayed(r, 1000);
            handler.postDelayed(c, 1000);
            handler.postDelayed(d, 1000);
        } else {
            //Detener
            llCpuRam.setVisibility(View.INVISIBLE);
            handler.removeCallbacks(r);
            handler.removeCallbacks(c);
            handler.removeCallbacks(d);
        }
    }

    public void viewRed(){
        if (llCpuRam.getVisibility() == View.INVISIBLE) {
            llCpuRam.setVisibility(View.VISIBLE);
            handler.postDelayed(d, 1000);
        } else {
            llCpuRam.setVisibility(View.INVISIBLE);
            handler.removeCallbacks(d);
        }
    }

    //Actualizar los datos de descarga
    public void getDownloadSpeed() {


        long mRxBytesPrevious = TrafficStats.getTotalRxBytes();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long mRxBytesCurrent = TrafficStats.getTotalRxBytes();

        long mDownloadSpeed = mRxBytesCurrent - mRxBytesPrevious;

        float mDownloadSpeedWithDecimals;
        System.out.println("Bajada: "+mDownloadSpeed);

        if (mDownloadSpeed >= 1000000000) {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000000;
            mUnits = "GB/s";
        } else if (mDownloadSpeed >= 1000000) {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000;
            mUnits = "MB/s";

        } else {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000;
            mUnits = "KB/s";
        }


        if (!mUnits.equals("KB/s") && mDownloadSpeedWithDecimals < 100) {
            System.out.println("Es igual KB/s");
            mDownloadSpeedOutput = String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals);
        } else {
            mDownloadSpeedOutput = String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals);
            //mDownloadSpeedOutput = Float.toString((float) mDownloadSpeedWithDecimals);
        }

    }

    public void stopMeter(){
        meterOn = false;
    }

    /* Inicializa la actividad ChannelListActivity en esta vista se listan los canales
     * por categorias y muestra la programacion de cada canal*/
    public void getChannelListActivity() {
        if (channels != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }catch (Exception e){}

            surfaceview.setVisibility(View.INVISIBLE);
            mSubtitlesSurface.setVisibility(View.INVISIBLE);

            handler.removeMessages(CODE_SHOWLOADING);
            handler.removeMessages(CODE_STOP_SHOWLOADING);
            handler.removeMessages(CODE_HIDE_BLACK);
            handler.removeMessages(CODE_CHANGE_BY_NUM);
            handler.removeMessages(CODE_HIDE_ERROR);
            handler.removeMessages(CODE_SALIR_APP);
            handler.removeMessages(CODE_CLEAR_SCREEN);
            handler.removeMessages(CODE_HIDE_VOLUMEN);
            handler.removeMessages(CODE_HIDE_OPTION);
            handler.removeMessages(CODE_DEG_LOCK);
            handler.removeMessages(CODE_COLOR_NUM);
            handler.removeMessages(CODE_CHANGE_CHANNEL);
            handler.removeMessages(CODE_HIDE_NOT);
            handler.removeMessages(CODE_RESTART_APP);

            //ChannelListActivityBox.channelIndex = channelIndex;
            ChannelListActivityBox.channelIndex = 0;
            StorageUtils.setInt(VideoplayerActivity.this, PROGRAM_KEY, channelIndex);
            //ChannelListActivityBox.openLive(this, channels, IMEI, mensajeBean, ipmuxIP);
            finish();
        }
    }

    public void openServiceActivity() {
        prepareForCloseVideoPlayerActivityBox();
        ServiceProgramActivity.openLive(VideoplayerActivity.this);
        finish();
    }

    // Llenar Lista de Canales en interfaz
    void adaptarListaCanales() {
        final ArrayList<Channels.Channel> canales = new ArrayList<>();
        for(Channels.Channel item: channels.getChannels() ){
            if(!item.getNum().equals(numCanalInformativo)){
                canales.add(item);
            }
        }


        ArrayAdapter<Channels.Channel> cheeseAdapterA = new ArrayAdapter<Channels.Channel>(this,
                R.layout.lv_list_item,
                canales) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.lv_list_item, null);
                }
                if(!canales.get(position).getName().equals("N/D")){
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.tv_program_logo);
                    selecImg(imageView, canales.get(position).getLogo());

                    TextView textView = (TextView) convertView.findViewById(R.id.tv_program_name);
                    textView.setText(canales.get(position).getNum() + "    " +
                            canales.get(position).getName());
                }else{
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.tv_program_logo);
                    imageView.setImageResource(R.drawable.ic_mantenimiento);

                    TextView textView = (TextView) convertView.findViewById(R.id.tv_program_name);
                    textView.setText(canales.get(position).getNum() + "    No disponible");
                }

                return convertView;
            }
        };
        lvCanales.setAdapter(cheeseAdapterA);
        lvCanales.setSelection(channelIndex);

    }

    // Comprobar si existe Favoritos guardados en la base de datos SQLite
    public boolean existenFavs(){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoplayerActivity.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor c = db.query(
                Utilidades.TABLA_FAVORITO,
                null,
                null,
                null,
                null,
                null,
                null);
        System.out.println("Numero de Favoritos "+c.getCount());
        db.close();

        return c.getCount() > 0;
    }



    //when a view is touched with fingers or is pressed with a remote control

    //First, declare actions on pressed. Then, use that actions to declare input methods

    //actions of buttons:
    void pressExitAppButton(){
        new AlertDialog.Builder(VideoplayerActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("¿Seguro que desea cerrar la app?")
                .setMessage("")
                .setNegativeButton(R.string.no, null)// sin listener
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeApp();

                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hideSystemUI(activity);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hideSystemUI(activity);
                    }
                })
                .show();
    }
    void pressOptionButton(){
        showChannelList();
        clearScreen(HUD_HIDE_TIME);
    }
    void pressLogoutButton(){
        showLogoutDialog();
    }
    void pressLockScreenButton(){
        if(!isScreenLocked){
            removeHudDelayedMessages(); //evitar eventos programados que ocuten el hud
            clearScreen();
            isScreenLocked = true;
            ivNav.setVisibility(View.INVISIBLE); //ivNav recibe todos los toques en pantalla, si es invisible no se reciben eventos touch
            ivUnLock.setVisibility(View.VISIBLE); //boton para volver a activar los eventos touch en pantalla
            handler.sendEmptyMessageDelayed(CODE_DEG_LOCK, 2000); // hace semitransparente el boton de desbloquear para que no moleste al usuario

        }
    }
    void pressUnlockScreenButton(){
        if(isScreenLocked){
            isScreenLocked = false;
            ivNav.setVisibility(View.VISIBLE);
            ivUnLock.setAlpha(1.0f);
            ivUnLock.setVisibility(View.INVISIBLE);

            clearAndShowChannelInfo();
        }
    }
    void pressTypeNumberButton(){
        clearScreen();
        showPanelNum();
        clearScreen(HUD_HIDE_TIME);
    }

    void pressSettingsButton(){
        ActivityLauncher.launchSettingsActivityAsNormalUser();
    }

    void pressAdvanceSettingsButton(){
        ActivityLauncher.launchSettingsActivityAsTechnician();
    }



    void onActionTouch(){

        //otro boton que abre lista de canales
        rlOpciones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        pressOptionButton();
                        break;
                }
                return true;
            }

        });

        // Boton para cerrar sesion
        ivLogout.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivLogout.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        ivLogout.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        break;
                    case MotionEvent.ACTION_UP:
                        ivLogout.setBackground(originalBackground);
                        pressLogoutButton();
                        break;
                }
                return true;
            }

        });


        // Boton para cerra la app por completo
        ivExitApp.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivExitApp.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivExitApp.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        break;
                    case MotionEvent.ACTION_UP:
                        ivExitApp.setBackground(originalBackground);
                        pressExitAppButton();

                        break;
                }
                return true;
            }

        });

        // Boton para ver la lista de todos los canales en un panel izquierdo
        ivList.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivList.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivList.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivList.setBackground(originalBackground);
                        pressOptionButton();
                        break;
                }
                return true;
            }

        });


        // Boton Bloquear Pantalla
        ivLock.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivLock.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                            ivLock.setBackground(getDrawable(R.drawable.bordes_suave_act));

                    case MotionEvent.ACTION_UP:
                            ivLock.setBackground(originalBackground);
                            pressLockScreenButton();

                        break;
                }
                return true;
            }

        });

        // Boton Desbloquear Pantalla
        ivUnLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:
                        pressUnlockScreenButton();
                        break;
                }
                return true;
            }

        });

        // Boton para expandir el panel de numeros para digitar el numero de un canal
        ivTypeNum.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivTypeNum.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivTypeNum.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivTypeNum.setBackground(originalBackground);
                        pressTypeNumberButton();
                        break;
                }
                return true;
            }

        });


        ivInformation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!enAnimacion)
                            ivInformation.setBackground((getDrawable(R.drawable.bordes_suave_act)));
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!enAnimacion) {
                            ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                            exitOptions();
                            channelIndex = channels.getChannels().size()-1;
                            changeChannel();
                        }
                        break;
                }
                return true;
            }
        });

        // Boton para agregar o eliminar de favoritos el canal actual
        ivFavorite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivFavorite.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));

                        if(consultarFavorito(channels.getChannels().get(channelIndex).getName())){
                            //Remover Fav por SQLite
                            eliminarFavorito(channels.getChannels().get(channelIndex).getName());
                            organizarFavoritos();

                            if(acFavoritos){
                                if(existenFavs()){
                                    channelIndex = 0;
                                    channels.setChannels(canalesFavoritos);


                                    setIdProgramaActual();
                                    showProgramInfo();
                                    changeChannel();

                                }else{
                                    acFavoritos = false;

                                    channels.setChannels(canalesAux);


                                }
                            }
                        }else{
                            //Agregar Fav por SQLite
                            registrarFavorito(channels.getChannels().get(channelIndex).getName());
                            organizarFavoritos();


                        }

                        break;
                }
                return true;
            }

        });

        // Panel que controla los toques en pantalla para cambiar canal o mostrar la interfaz
        ivNav.setOnTouchListener(new OnSwipeTouchListener(VideoplayerActivity.this) {
            public void onClick() {
                clearAndShowChannelInfo();
            }

            public void onSwipeTop() {
                nextChannelInScreen();
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
                previousChannelInScreen();

            }

        });

        // Cada uno de los numeros del panel para digitar el numero del canal buscado
        numOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numOne.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("1");
                        break;
                    case MotionEvent.ACTION_UP:
                        numOne.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numTwo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numTwo.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("2");
                        break;
                    case MotionEvent.ACTION_UP:
                        numTwo.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numThree.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numThree.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("3");
                        break;
                    case MotionEvent.ACTION_UP:
                        numThree.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numFour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numFour.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("4");
                        break;
                    case MotionEvent.ACTION_UP:
                        numFour.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numFive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numFive.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("5");
                        break;
                    case MotionEvent.ACTION_UP:
                        numFive.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numSix.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numSix.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("6");
                        break;
                    case MotionEvent.ACTION_UP:
                        numSix.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numSeven.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numSeven.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("7");
                        break;
                    case MotionEvent.ACTION_UP:
                        numSeven.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numEight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numEight.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("8");
                        break;
                    case MotionEvent.ACTION_UP:
                        numEight.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numNine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numNine.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("9");
                        break;
                    case MotionEvent.ACTION_UP:
                        numNine.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numZero.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numZero.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("0");
                        break;
                    case MotionEvent.ACTION_UP:
                        numZero.setBackground(null);
                        break;
                }
                return true;
            }

        });
        ivEnterNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivEnterNum.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        if (writingNum) {
                            delayBusNum = 0;
                            handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ivEnterNum.setBackground(null);
                        break;
                }
                return true;
            }

        });
    }

    // Guardar en base de datos SQLite un canal favorito
    public void registrarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoplayerActivity.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_NOMBRE, name);

        Long idResultante = db.insert(Utilidades.TABLA_FAVORITO, null, values);
        db.close();

        System.out.println("Agregado aaa "+idResultante);
        Toast.makeText(getApplicationContext(), "Id Registro: "+idResultante, Toast.LENGTH_LONG);
    }

    // Eliminar de la base de datos SQLite un canal
    public void eliminarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoplayerActivity.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        if(db != null){
            db.execSQL("DELETE FROM "+Utilidades.TABLA_FAVORITO+" WHERE "+Utilidades.CAMPO_NOMBRE+"" +
                    "='"+name+"'");
        }
        db.close();
    }

    // Consultar si el nombre de un canal se encuentra registrado en los favoritos
    public boolean consultarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoplayerActivity.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        String[] columns = new String[]{Utilidades.CAMPO_NOMBRE};
        String selection = Utilidades.CAMPO_NOMBRE + " LIKE ?";
        String[] selectionArgs = new String []{name};

        try {

            Cursor cursor = db.query(
                    Utilidades.TABLA_FAVORITO,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            System.out.println("Encontrado "+cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_NOMBRE)));
            cursor.close();
            db.close();
            return true;

        }catch (Exception e){
            //No existe
            return false;
        }
    }

    // Metodo para cerrar la aplicacion
    public void closeApp() {
        releaseResourcesAndFinish();
        System.exit(0);
    }

    void releaseResourcesAndFinish(){
        releaseResources();
        finish();
    }

    public void releaseResources(){
        reproduccion.cancel(true);

        mediaPlayer.stop();
        mediaPlayer.getVLCVout().detachViews();
        mediaPlayer.release();
        libvlc.release();


        llCpuRam.setVisibility(View.INVISIBLE);
        handler.removeCallbacks(r);
        handler.removeCallbacks(c);
        handler.removeCallbacks(d);
        handler.removeMessages(CODE_MEDIA_PLAYER_UNMUTE);

        tiempo_canal.cancel();


        StorageUtils.setInt(VideoplayerActivity.this, PROGRAM_KEY, channelIndex);

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        /*Socket connection*/
        if (canCloseSocketConnectionPauseVideoPlayer)
            AppState.restartSocketConnection();
    }

    // Metodos para reconocer cuando se oprime una tecla desde controles compatibles con la TvBox


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
                if (writingNum) {
                    System.out.println("Escribiendo Enter");
                    delayBusNum = 0;
                    handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
                } else if(llOptions.getVisibility() == View.VISIBLE){
                    System.out.println("Opcion Enter");
                    selecOpcion();
                }else {
                    System.out.println("Informacion Enter");
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        clearAndShowChannelInfo();
                    }else{
                        clearScreen();
                    }
                    setIdProgramaActual();
                    showProgramInfo();

                    //Pausar Canal Actual y Reanudar al presente
                    /*if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        controlError = true;
                    } else
                        playerInterface.seekTo(0);*/
                }
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:

            case KeyEvent.KEYCODE_DPAD_UP:

            case KeyEvent.KEYCODE_DPAD_DOWN:

            case KeyEvent.KEYCODE_DPAD_RIGHT:

            case KeyEvent.KEYCODE_DPAD_LEFT:

                if(isSomeHudActive()){
                    extendClearScreenTimeout();
                }
                break;


            case KeyEvent.KEYCODE_MENU:
                /*
                if(isOptionsActive() || isChannelListActive()){
                    clearScreen();
                }
                else{
                    rlOpciones.setBackground(getDrawable(R.drawable.bordes_suave_act));
                    clearAndShowOptionsAndChannelInfo();
                }
                return true;
                //break;
                 */
                if(isOptionsActive() || isChannelListActive()){
                    clearScreen();
                }
                else{
                    clearAndShowChannelList();
                }
                return true;

            case KeyEvent.KEYCODE_0:
                pressNumber("0");
                break;

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
                pressNumber("7");;
                break;

            case KeyEvent.KEYCODE_8:
                pressNumber("8");
                break;

            case KeyEvent.KEYCODE_9:
                pressNumber("9");
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                break;

            case KeyEvent.KEYCODE_VOLUME_MUTE:
                if(ivMute.getVisibility() == View.INVISIBLE){
                    ivMute.setVisibility(View.VISIBLE);

                    rlVolumenA.setVisibility(View.INVISIBLE);
                }else{
                    ivMute.setVisibility(View.INVISIBLE);
                }
                break;

            case KeyEvent.KEYCODE_BACK:
                int tempLastChannelIndex= lastChannelIndex;
                lastChannelIndex=channelIndex;
                channelIndex=tempLastChannelIndex;
                changeChannelInScreen();

                return true;
            case KeyEvent.KEYCODE_SETTINGS:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                System.out.println("++++++++++++++++++++++++SETTINGS");
                return true;

            case KeyEvent.KEYCODE_W:
                try {
                    releaseResources();
                    Process proc = Runtime.getRuntime()
                            .exec(new String[]{ "su", "-c", "reboot -p" });
                    proc.waitFor();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case KeyEvent.KEYCODE_Q:
                try {
                    openSettingsActivityAsNormalUser();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//
//            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                if(llOptions.getVisibility() == View.INVISIBLE){
//                    onVolumen = false;
//                    handler.sendEmptyMessageDelayed(CODE_HIDE_VOLUMEN, 3000);
//                }
//                break;
//
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    // Animaciones para navegar entre las opciones(Solo para TvBox)
    public void navOpciones(){
        if(posOpcion > 2)
            posOpcion  = 1;
        else if(posOpcion < 1)
            posOpcion = 2;
        System.out.println("posOpcion "+posOpcion);
        ivList.setBackground(getDrawable(R.drawable.borde_volumen));
        ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));
        ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));

        switch (posOpcion){
            case 1:
                ivList.setBackground(getDrawable(R.drawable.bordes_suave_act));
                break;
            /*case 2:
                ivFavorite.setBackground(getDrawable(R.drawable.bordes_suave_act));
                break;

             */
            case 2:
                ivInformation.setBackground(getDrawable(R.drawable.bordes_suave_act));
        }
    }

    // Seleccionar la opcion, se maneja por casos en una variable posOpcion (Solo para TvBox)
    public void selecOpcion(){

        switch (posOpcion){
            case 1:
                adaptarListaCanales();
                toggleList();
                exitOptions();

                posOpcion = 1;
                ivList.setBackground(getDrawable(R.drawable.borde_volumen));
                ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));
                ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                break;

            /*
            case 2:
                if(consultarFavorito(channels.getData().get(channelIndex).getName())){
                    //Remover Fav por SQLite
                    eliminarFavorito(channels.getData().get(channelIndex).getName());
                    organizarFavoritos();

                    if(acFavoritos){
                        if(existenFavs()){
                            channelIndex = 0;
                            channels.setData(canalesFavoritos);
                            setIdProgramaActual();
                            showProgramInfo();
                            changeChannel();
                            ivFavorite.setImageResource(R.drawable.button_fav_b);
                            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                        }else{
                            acFavoritos = false;

                            channels.setData(canalesAux);

                            ivFavorite.setImageResource(R.drawable.button_fav);
                            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));

                        }
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }
                }else{
                    //Agregar Fav por SQLite
                    registrarFavorito(channels.getData().get(channelIndex).getName());
                    organizarFavoritos();

                    ivFavorite.setImageResource(R.drawable.button_fav_b);
                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                }
                break;


             */
            case 2:
                ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                exitOptions();
                channelIndex = channels.getChannels().size()-1;
                changeChannel();
                break;
        }
    }

    // Actualiza en la interfaz toda la informacion del canal actual
    void showProgramInfo() {
        pbError.setText("");
        pbError.setVisibility(View.INVISIBLE);
        tvQuality.setText("");
        //tvHorarioIni.setText("");


        tvChannelName.setText(channels.getChannels().get(channelIndex).getName());
        tvQuality.setText(channels.getChannels().get(channelIndex).getCalidad());
        tvChannelNumber.setText(channels.getChannels().get(channelIndex).getNum());
        tvChannelNumber.setTextColor(Color.rgb(241,96,96));
        handler.removeMessages(CODE_COLOR_NUM);
        handler.sendEmptyMessageDelayed(CODE_COLOR_NUM, 2000);

        if(!channels.getChannels().get(channelIndex).getName().equals("N/D")){
            tvChannelName.setText(channels.getChannels().get(channelIndex).getName());
            tvChannelNumber.setText(channels.getChannels().get(channelIndex).getNum());
            tvChannelNumber.setTextColor(Color.rgb(241,96,96));
            handler.removeMessages(CODE_COLOR_NUM);
            handler.sendEmptyMessageDelayed(CODE_COLOR_NUM, 2000);

            selecImg(tvChannelLogo, channels.getChannels().get(channelIndex).getLogo());
        }else{
            tvChannelName.setText("No disponible");
            tvChannelNumber.setText(channels.getChannels().get(channelIndex).getNum());
            tvChannelLogo.setImageResource(R.drawable.ic_mantenimiento);
        }

        if(idProgramCurrent < (channels.getChannels().get(channelIndex).getProgramas().size() - 1)){

            selecImg(tvClassification, channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getClasificacion());

            int h = channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarInit().get(Calendar.HOUR_OF_DAY);
            String hora = Integer.toString(h);
            if(h == 0)
                hora = "00";
            int m = channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarInit().get(Calendar.MINUTE);
            String minuto = Integer.toString(m);
            if(m == 0)
                minuto = "00";

            tvQuality.setText(channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getNombreProgram());
            //tvHorarioIni.setText("Inicio "+hora+":"+minuto);

            h = channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().get(Calendar.HOUR_OF_DAY);
            hora = Integer.toString(h);
            if(h == 0)
                hora = "00";
            m = channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().get(Calendar.MINUTE);
            minuto = Integer.toString(m);
            if(m == 0)
                minuto = "00";

           // tvHorarioFin.setText("Fin "+hora+":"+minuto);

            if (idProgramCurrent < (channels.getChannels().get(channelIndex).getProgramas().size() - 1)) {
                /*h = channels.getData().get(channelIndex).getProgramas().get(idProgramCurrent + 1).getCalendarInit().get(Calendar.HOUR_OF_DAY);
                hora = Integer.toString(h);
                if(h == 0)
                    hora = "00";
                m = channels.getData().get(channelIndex).getProgramas().get(idProgramCurrent + 1).getCalendarInit().get(Calendar.MINUTE);
                minuto = Integer.toString(m);
                if(m == 0)
                    minuto = "00";*/


            } else {

            }
        }

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        /*ServiceProgramActivity.openLive(VideoPlayerActivity.this);
        finish();*/
    }

    // Al oprimir el boton BACK
    @Override
    public void onBackPressed() {
        if(rlPanelNum.getVisibility() == View.VISIBLE) {
            hidePanelNum();
        }else if(llList.getVisibility() == View.VISIBLE) {
            hideList();
        }else if(viewInfo){
            exitProgramList();
            exitInfoChannel();
            viewInfo = false;
        }else if(llOptions.getVisibility() == View.VISIBLE){
            exitProgramList();
            exitInfoChannel();
            exitOptions();

            posOpcion = 1;
            ivList.setBackground(getDrawable(R.drawable.borde_volumen));
            ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));
            ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
        }
    }

    //Animaciones de todos los paneles
    //Animacion de Panel Opciones
    public void toggleOptions() {
        if(llOptions.getVisibility() == View.INVISIBLE){
            if (animInOptions == null) {
                animInOptions = new TranslateAnimation(0f, 0f, -llOptions.getHeight(), 0f);
                animInOptions.setDuration(300);
            }
            animInOptions.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    llOptions.setVisibility(View.VISIBLE);
                    enAnimacion = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    enAnimacion = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llOptions.startAnimation(animInOptions);
        }
    }

    public void exitOptions() {

        if(llOptions.getVisibility() == View.VISIBLE && !enAnimacion){
            if (exitAnimOptions == null) {
                exitAnimOptions = new TranslateAnimation(0f, 0f, 0f, -llOptions.getHeight());
                exitAnimOptions.setDuration(1000);

            }
            exitAnimOptions.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    llOptions.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llOptions.startAnimation(exitAnimOptions);
        }
    }

    //Animacion de Panel de Numeros
    public void showPanelNum() {
        rlPanelNum.setVisibility(View.VISIBLE);
    }

    public void hidePanelNum() {

        rlPanelNum.setVisibility(View.INVISIBLE);

    }

    //Animacion de Lista de Canales
    public void toggleList() {
        if(llList.getVisibility() == View.INVISIBLE){
            //llProgramList.setVisibility(View.INVISIBLE);
            if (animInList == null) {
                animInList = new TranslateAnimation(-llList.getWidth(), 0f, 0f, 0f);
                animInList.setDuration(300);
            }
            animInList.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    llList.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llList.startAnimation(animInList);
        }
    }

    public void hideList() {


            if (exitAnimList == null) {
                exitAnimList = new TranslateAnimation(0f, -llList.getWidth(), 0f, 0f);
                exitAnimList.setDuration(1000);

            }
            exitAnimList.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    llList.setVisibility(View.INVISIBLE);
                    //TvBox
                    togglePlaylist();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llList.startAnimation(exitAnimList);

    }


    public void exitInfoChannel() {
        if(!change){
            if(rlDisplayDown.getVisibility() == View.VISIBLE){
                if (exitAnimBot == null) {
                    exitAnimBot = new TranslateAnimation(0f, 0f, 0f, rlDisplayDown.getHeight());
                    exitAnimBot.setDuration(1000);
                }

                exitAnimBot.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //tvChannelNumber.setVisibility(View.INVISIBLE);
                        rlDisplayDown.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rlDisplayDown.startAnimation(exitAnimBot);
            }
        }
    }

    public void exitProgramList() {
        //Desactivados para Celular
        /*if(!change){
            if(llProgramList.getVisibility() == View.VISIBLE){
                if (exitAnim == null) {
                    exitAnim = new TranslateAnimation(0f, -llProgramList.getWidth(), 0f, 0f);
                    exitAnim.setDuration(1000);

                }
                exitAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        llProgramList.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                llProgramList.startAnimation(exitAnim);
            }
        }*/
    }



    public void togglePlaylist() {
        //Desactivados para Celular
        /**if(llProgramList.getVisibility() == View.INVISIBLE && llList.getVisibility() == View.INVISIBLE){
            if (animIn == null) {
                animIn = new TranslateAnimation(-llProgramList.getWidth(), 0f, 0f, 0f);
                animIn.setDuration(300);
            }
            animIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    llProgramList.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llProgramList.startAnimation(animIn);
        }*/
    }

    public String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd    HH:mm");
        return sDateFormat.format(new Date());
    }

    // Pone una pantalla negra sobre el SurfaceView para ocultar el canal anterior  mientras carga el actual
    public void showLoading() {
        if (tvBlack.getVisibility() == View.INVISIBLE) {
            tvBlack.setVisibility(View.VISIBLE);
            pbError.setText("");
            pbError.setVisibility(View.INVISIBLE);
        }
    }

    // Iniciar actividad ChannelListActivity
    public void hideLoading() {
        if (tvBlack.getVisibility() == View.VISIBLE) {
            pbError.setText("");
            pbError.setVisibility(View.INVISIBLE);
        }
    }

    // Ejecuta el play del canal actual
    public void changeChannel() {
        /*
        //ivlcVout.detachViews();

        if(channels.getData().get(channelIndex).getId().equals(numCanalInformativo)){
            pbError.setText("Seccion Informativo");
            getChannelListActivity();
        }else{
            pbError.setText("Problemas técnicos en el canal");
            play(channelIndex);
        }

        socket.emit("vivo_channel", IMEI,channels.getData().get(channelIndex).getName(),channels.getData().get(channelIndex).getNum(),channels.getData().get(channelIndex).getId());
        nuevoCanal = true;

         */
        //ivlcVout.detachViews();



        if(channels.getChannels().get(channelIndex).getNum().equals(numCanalInformativo)){
            channelIndex=0;
        }

        pbError.setText("Problemas técnicos en el canal");
        muteAudio();
        restartMuteVariables();
        play(channelIndex);

        socketEmitPlayingChannel();
        nuevoCanal = true;
    }

    // Metodo para cambiar canal por numero
    public void cambiarPorNumero(int channelNumber) {
        //Agregar el último canal visitado
        lastChannelIndex=channelIndex;

        for(int i = 0; i < channels.getChannels().size(); i++){
            if(channelNumber == Integer.parseInt(channels.getChannels().get(i).getNum())){
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                tvBlack.setVisibility(View.VISIBLE);
                channelIndex = i;
                changeChannelInScreen();
                handler.sendEmptyMessageDelayed(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW,0);
                return;
            }
        }

        //showNetworkInfo("No existe el canal " + i);
        //handler.sendEmptyMessageDelayed(CODE_HIDE_ERROR, 2000);
        tvChannelNumberChange.setText("No existe el canal " + channelNumber);
        handler.sendEmptyMessageDelayed(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW,HUD_HIDE_TIME);
    }

    // Canal siguiente
    public void next() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");}

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex++;

        if (channelIndex >= channels.getChannels().size()) {
            channelIndex = 0;
        }
    }

    // Canal Anterior
    public void previous() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");
        }

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex--;

        if (channelIndex < 0) {
            channelIndex = channels.getChannels().size() - 1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");

        if (networkReceiver == null) {
            registerNetReceiver();
        }

        //mediaPlayer.play();

        playerInterface.seekTo(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        releaseResourcesAndFinish();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        System.out.println("onRestart");

        ivlcVout.setVideoView(surfaceview);
        if (mSubtitlesSurface != null)
            ivlcVout.setSubtitlesView(mSubtitlesSurface);
        ivlcVout.attachViews(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    public void showNetworkInfo(String text) {
        if (tvNetState.getVisibility() == View.INVISIBLE) {
            tvNetState.setVisibility(View.VISIBLE);
        }

        tvNetState.setText(text);
    }

    public void hideNetworkInfo() {
        if (tvNetState.getVisibility() == View.VISIBLE) {
            tvNetState.setVisibility(View.INVISIBLE);
        }

        tvNetState.setText("");
    }

    class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetWorkUtils.getNetState(context)) {
                //showNetworkInfo("Fallo de conexión!");
                tvBlack.setVisibility(View.VISIBLE);
                pbError.setText("Fallo de conexión!");
                pbError.setVisibility(View.VISIBLE);
                failNet = true;

                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }catch ( Exception e){}


                /*
                BUG IMPORTANTE: El usuario puede evadir el control del servidor.
                Si el usuario está reproduciendo canales, se queda sin conexión, luego en el servidor suspenden al usuario,
                luego el usuario recupera la conexión, la app no recibió ningun evento socket mientras estaba desconectada.

                La siguiente linea de código asegura que la app vuelva a preguntar por el estado del usuario (Activo o Suspendido)
                 */
                openServiceActivity(); // NO BORRAR, leer arriba
            } else {
                failNet = false;
                hideNetworkInfo();
                tvBlack.setVisibility(View.INVISIBLE);
                pbError.setText("");
                pbError.setVisibility(View.INVISIBLE);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.play();
                }
            }
        }
    }

    // Comprobar si la conexion esta disponible
    boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    // Hacer Ping, este metodo demora demasiado
    public Boolean isOnlineNet() {

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
    }

    /* Metodos Boquear Barras */
    @SuppressLint("WrongConstant")
    public void bloquearBarras() {
        this.wmanager = (WindowManager) getSystemService("window");
        this.llayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        this.llayout.setBackgroundColor(Color.argb(0, 240, 24, 24));
        this.llayout.setLayoutParams(layoutParams);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(-1, barraEstado(), 2010, 264, -3);
        layoutParams2.gravity = Gravity.FILL;
        layoutParams2.x = 0;
        layoutParams2.y = 0;
        this.wmanager.addView(this.llayout, layoutParams2);
    }

    public int barraEstado() {
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return getResources().getDimensionPixelSize(identifier) + 12;
        }
        return 0;
    }
    /*                          */

    public void activarBarras() {
        this.wmanager.removeViewImmediate(this.llayout);
    }

    //CPU y RAM
    public long getMemorySize() {

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long j = mi.totalMem / 1048576;
        long j2 = ((j - (mi.availMem / 1048576)) * 100) / j;
        return j2;
    }
    final Runnable r = new Runnable() {
        public void run() {
            int a = (int) getMemorySize();
            tvRam.setText("RAM: " + a + "%");
            handler.postDelayed(this, 500);
        }
    };
    public static float getCpuUsage() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/stat", "r");
            String[] split = randomAccessFile.readLine().split(" ");
            long parseLong = Long.parseLong(split[5]);
            long parseLong2 = Long.parseLong(split[2]) + Long.parseLong(split[3]) + Long.parseLong(split[4]) + Long.parseLong(split[6]) + Long.parseLong(split[7]) + Long.parseLong(split[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }
            randomAccessFile.seek(0);
            String readLine = randomAccessFile.readLine();
            randomAccessFile.close();
            String[] split2 = readLine.split(" ");
            long parseLong3 = Long.parseLong(split2[5]);
            long parseLong4 = Long.parseLong(split2[8]) + Long.parseLong(split2[2]) + Long.parseLong(split2[3]) + Long.parseLong(split2[4]) + Long.parseLong(split2[6]) + Long.parseLong(split2[7]);
            return ((float) (parseLong4 - parseLong2)) / ((float) ((parseLong4 + parseLong3) - (parseLong + parseLong2)));
        } catch (IOException e2) {
            e2.printStackTrace();
            return 0.0f;
        }
    }
    final Runnable c = new Runnable() {
        public void run() {
            int a = (int) (getCpuUsage() * 100.0f);
            tvCpu.setText("CPU: " + a + "%");
            handler.postDelayed(this, 500);
        }
    };

    final Runnable d = new Runnable() {
        public void run() {
            getDownloadSpeed();
            System.out.println("BANDA:    "+mDownloadSpeedOutput+""+mUnits);
            tvRed.setText("RED: "+mDownloadSpeedOutput+""+mUnits);
            handler.postDelayed(this, 1000);
        }
    };

    //Uso de Banda Ancha del dispositivo


    //Control de tiempos
    public void updateTime(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        setZeroClock();
    }

    public void setZeroClock(){
        if(hour >= 0 && hour <= 9){
            hor = "0";
        }else{
            hor = "";
        }

        if(minute >= 0 && minute <= 9){
            min = ":0";
        }else{
            min = ":";
        }

        if(second >= 0 && second <= 9){
            sec = ":0";
        }else{
            sec = ":";
        }
    }

    public void initClock(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateTime();

                    curTime = hor + hour + min + minute;
                    tvSystemTime.setText(curTime);

                    Calendar c = Calendar.getInstance();
                    long horaActual = c.getTimeInMillis();
                    long horaB = channels.getChannels().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().getTimeInMillis();

                    if (horaActual >= horaB) {
                        idProgramCurrent++;
                        showProgramInfo();
                    }

                }catch (Exception e){}
            }
        });
    }

    //
    class RefresfClock implements Runnable{
        @SuppressWarnings("unused")
        public void run(){
            while(!Thread.currentThread().isInterrupted()){
                try {
                    initClock();
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }catch (Exception e){}
            }
        }
    }

    public class Reproduccion extends AsyncTask<Void, Integer, Media> {
        @Override
        protected Media doInBackground(Void... params) {
            Uri parse = Uri.parse(channels.getChannels().get(channelIndex).getUrl());
            media = new Media(libvlc, parse);
            System.out.println("SET MEDIAAAAAAAAAAAAA");
            mediaPlayer.setMedia(media);
            System.out.println("PLAYYYYYY");
            mediaPlayer.play();
            return media;
        }
        @Override
        protected void onPostExecute(Media media) {

        }

        @Override
        protected void onCancelled() {
            Toast.makeText(VideoplayerActivity.this, "",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl() {
        public int getBufferPercentage() {
            return 0;
        }

        public int getCurrentPosition() {
            float pos = mediaPlayer.getPosition();
            return (int)(pos * getDuration());
        }

        public int getDuration() {
            return (int)mediaPlayer.getLength();
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public void pause() {
            mediaPlayer.pause();
        }

        public void seekTo(int pos) {
            //mediaPlayer.setPosition((float)pos / getDuration());
            mediaPlayer.setPosition(1);
        }

        public void start() {
            mediaPlayer.play();
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    };

    //-----enmanuel

    /*Cantidad de tiempo en milisegundos que debe pasar antes de que se oculte todo el hud */
    public int HUD_HIDE_TIME=4000;
    /*Cantidad de tiempo en milisegundos que debe durar las animaciones de mostrar y ocultar elementos del hud*/
    public int ANIM_TIME=1000;

    /*Métodos de alto nivel: son métodos más orientados al "qué hacer", no al "cómo hacerlo". Son los que se deben usar para interactuar con la vista, no usar los métodos de bajo nivel en la vista. Los métodos de alto nivel usan los métodos de bajo nivel
     */


    /**
     *Este método limpia toda la pantalla y hace aparecer la barra inferior con la información del canal. Debe desaparecer en un tiempo definido
     */
    public void clearAndShowChannelInfo(){
        clearScreen();
        showChannelInfo();
        clearScreen(HUD_HIDE_TIME);
    }


    /**
     *Este método limpia toda la pantalla y hace aparecer el menú superior de opciones y la barra inferior con la información del canal. Debe desaparecer en un tiempo definido
     */
    public void clearAndShowOptionsAndChannelInfo(){
        clearScreen();
        showChannelInfo();
        showOptions();
        clearScreen(HUD_HIDE_TIME);

    }

    /**
     *Este método limpia toda la pantalla y hace aparecer el menú izquierdo con la lista de canales y la barra inferior con la información del canal. Debe desaparecer en un tiempo definido
     */
    public void clearAndShowListAndChannelInfo(){
        clearScreen();
        showChannelInfo();
        showChannelList();
        clearScreen(HUD_HIDE_TIME);

    }

    /**
     *Este método limpia toda la pantalla y hace aparecer la lista de canales a la izquierda. Debe desaparecer en un tiempo definido
     */
    public void clearAndShowChannelList(){
        clearScreen();
        showChannelList();
        clearScreen(HUD_HIDE_TIME);
    }

    // Canal siguiente
    public void showNextChannel() {
        //pausar la reproduccion
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");}

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex++;

        if (channelIndex >= channels.getChannels().size()) {
            channelIndex = 0;
        }
    }

    // Canal Anterior
    public void showPreviousChannel() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");
        }

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex--;

        if (channelIndex < 0) {
            channelIndex = channels.getChannels().size() - 1;
        }
    }

    //Abrir el activity principal
    public void openSettingsActivityAsNormalUser(){
        prepareForCloseVideoPlayerActivityBox();
        ActivityLauncher.launchSettingsActivityAsNormalUser();
        finish();
    }

    //Abrir el activity principal
    public void openSttingsActivityAsTechnician(){
        prepareForCloseVideoPlayerActivityBox();
        ActivityLauncher.launchSettingsActivityAsTechnician();
        finish();
    }

    /**
     * Este método prepara la activity para ser cerrada por completo y que no hayan eventos programados pendientes
     */
    public void prepareForCloseVideoPlayerActivityBox(){
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }catch (Exception e){}

            surfaceview.setVisibility(View.INVISIBLE);
            mSubtitlesSurface.setVisibility(View.INVISIBLE);


            handler.removeMessages(CODE_SHOWLOADING);
            handler.removeMessages(CODE_STOP_SHOWLOADING);
            handler.removeMessages(CODE_HIDE_BLACK);
            handler.removeMessages(CODE_CHANGE_BY_NUM);
            handler.removeMessages(CODE_HIDE_ERROR);
            handler.removeMessages(CODE_SALIR_APP);
            handler.removeMessages(CODE_CLEAR_SCREEN);
            handler.removeMessages(CODE_HIDE_VOLUMEN);
            handler.removeMessages(CODE_HIDE_OPTION);
            handler.removeMessages(CODE_DEG_LOCK);
            handler.removeMessages(CODE_COLOR_NUM);
            handler.removeMessages(CODE_CHANGE_CHANNEL);
            handler.removeMessages(CODE_HIDE_NOT);
            handler.removeMessages(CODE_RESTART_APP);

    }


// Métodos de bajo nivel: Métodos más orientados al "cómo hacer" y no al "qué hacer". Dan soporte a los métodos de alto nivel

    /**
     *Este método limpia toda la pantalla para que solo se visualice el video
     */
    public void clearScreen(){
        removeHudDelayedMessages();
        hideOptions();
        hideChannelInfo();
        hideChannelList();
        hidePanelNum();
        //tvChannelNumberChange.setVisibility(View.INVISIBLE);
    }

    /**
     *Método que limpia toda la pantalla en un tiempo definido para que solo se visualice el video.
     *@param millis es la cantidad de tiempo en milisegundos que deben pasar antes de que se limpie toda la pantalla
     */
    public void clearScreen(int millis){
        handler.removeMessages(CODE_CLEAR_SCREEN);
        handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, millis);
    }



    /**
     * Método que muestra la barra inferior con la información del canal
     */
    public void showChannelInfo(){

        rlDisplayDown.setVisibility(View.VISIBLE);
        rlPanelUp.setVisibility(View.VISIBLE);

    }

    /**
     * Método que oculta la barra inferior con la información del canal
     */
    public void hideChannelInfo(){
        rlDisplayDown.setVisibility(View.INVISIBLE);
        rlPanelUp.setVisibility(View.INVISIBLE);
    }

    /**
     *Método que muestra la barra superior de opciones
     */
    public void showOptions(){

            if (animInOptions == null) {
                animInOptions = new TranslateAnimation(0f, 0f, -llOptions.getHeight(), 0f);
                animInOptions.setDuration(300);
            }
            animInOptions.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rlOpciones.setBackground(null);
                    posOpcion = 1;
                    ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                    ivList.setBackground(getDrawable(R.drawable.borde_volumen));
                    llOptions.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llOptions.startAnimation(animInOptions);


    }


    /**
     *Método que oculta la barra superior con las opciones
     */
    public void hideOptions(){
        llOptions.setVisibility(View.INVISIBLE);
    }

    /**
     * Método que muestra la barra izquierda con la lista de canales
     */
    public void showChannelList(){
        adaptarListaCanales();
        llList.setVisibility(View.VISIBLE);
    }

    /**
     *Método que oculta la barra izquierda con la lista de canales
     */
    public void hideChannelList(){
        llList.setVisibility(View.INVISIBLE);

    }

    /**
     *Método para saber si hay algún elemento visual (HUD) activo
     * @return true o false
     */
    public boolean isSomeHudActive(){
        boolean isActive=false;
        if(
                rlDisplayDown.getVisibility() == View.VISIBLE ||
                llOptions.getVisibility() == View.VISIBLE ||
                llList.getVisibility() == View.VISIBLE ||
                rlPanelNum.getVisibility() == View.VISIBLE
        ){
            isActive= true;
        }
        return isActive;

    }

    /**
     *Método para saber si las opciones estan visibles
     * @return true o false
     */
    public boolean isOptionsActive(){
        boolean isActive=false;
        if(
                llOptions.getVisibility() == View.VISIBLE
        ){
            isActive= true;
        }
        return isActive;

    }

    /**
     *Método para saber si la lista izquierda de canales está visible
     * @return true o false
     */
    public boolean isChannelListActive(){
        return llList.getVisibility() == View.VISIBLE;
    }

    /**
     *Método para saber si la barra inferior con la informacion del canal está visible y ningun otro elemento hud más
     * @return true o false
     */
    public boolean isOnlyChannelInfoActive(){
        return rlDisplayDown.getVisibility() == View.VISIBLE &&
                llOptions.getVisibility() == View.INVISIBLE &&
                llList.getVisibility() == View.INVISIBLE;
    }

    /**
     *Método para decir a la app que se presionó un número
     * @return void
     */
    public void pressNumber(String number){
        claveExit(number);
        writingNum = true;
        canalNum(number);
        extendClearScreenTimeout();

        //extender el tiempo en que se oculta el textview del canal en marcación
        handler.removeMessages(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW);
        handler.sendEmptyMessageDelayed(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW,HUD_HIDE_TIME*2);
    }


    /**
     *Método que elimina los metodos con demora (delayed messages) que ocultan elementos hud
     * @return void
     */
    public void removeHudDelayedMessages(){
        handler.removeMessages(CODE_CLEAR_SCREEN);
    }



    /**
     *Método que cambia el canal modificando todos los elementos de la vista
     * @return void
     */
    public void changeChannelInScreen(){
        changeChannelList();
        showProgramInfo();
        setIdProgramaActual();
        changeChannel();

        clearAndShowChannelInfo();
    }

    public void nextChannelInScreen(){
        next();
        changeChannelInScreen();

    }

    public void previousChannelInScreen(){
        previous();
        changeChannelInScreen();

    }



    public void socketEmitPlayingChannel(){
        AppState.getSocketConnection().socketEmitPlayingChannel(channels,channelIndex);
    }


    /*
    * Sirve para extender el tiempo en que se ocultan los elementos en pantalla. Ejm: Cuando se presiona un boton del panel numérico,
    * es necesario agregar más tiempo para que el usuario pueda presionar más números
    * */
    public void extendClearScreenTimeout(){
        handler.removeMessages(CODE_CLEAR_SCREEN);
        handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN,HUD_HIDE_TIME);
    }


    public void muteAudio(){
        /*
        AudioManager amanager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,true);

         */
        mediaPlayer.setVolume(0);
    }

    public void unMuteAudio(){
         /*
        AudioManager amanager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,false);
          */
        mediaPlayer.setVolume(100);
    }

    public void restartMuteVariables(){
        wasMuted=false;
        wasUnmuted=false;
    }

    void initVolumeControl(){
      audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      volumeManager= new CustomVolumeManager(sbVolumenA,handler,rlVolumenA, volumeIndicator);
    }

    public void upVolume(){
        //v1
        //simulateKeyPress(KeyEvent.KEYCODE_VOLUME_UP);

        //v2
        //audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
          //      AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        //v3
        volumeManager.up();
    }
    public void downVolume(){
        //v1
        //simulateKeyPress(KeyEvent.KEYCODE_VOLUME_DOWN);

        //v2
        //audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
          //      AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

        //v3
        volumeManager.down();
    }

    public void simulateKeyPress(int key){
        Activity a = (Activity) VideoplayerActivity.this;
        a.getWindow().getDecorView().getRootView();
        BaseInputConnection inputConnection = new BaseInputConnection(a.getWindow().getDecorView().getRootView(),
                true);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, key);
        inputConnection.sendKeyEvent(downEvent);
    }

    public void showLogoutDialog(){

            AlertDialog.Builder builder= new AlertDialog.Builder(VideoplayerActivity.this);
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
                    hideSystemUI(activity);
                }
            });

            builder
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    hideSystemUI(activity);
                }
            })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hideSystemUI(activity);
                        }
                    });

            //Create Dialog
            AlertDialog ad= builder.create();
            ad.show();

            //dont add in smartphone
            fixLogoutDialogStyle(ad);
        }

    public static void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }



    /*Top buttons configs*/
    public void configTopButtons(){
        hideTopButtons();
    }
    public void hideTopButtons(){
        llSmartphoneButtons.setVisibility(View.INVISIBLE);
    }
    public void showTopButtons(){
        llSmartphoneButtons.setVisibility(View.VISIBLE);
    }





    /*Fix for logout dialog*/
    public void fixLogoutDialogStyle(AlertDialog ad){
        ad.getWindow().setLayout(300, 180); //Controlling width and height.
    }
}
