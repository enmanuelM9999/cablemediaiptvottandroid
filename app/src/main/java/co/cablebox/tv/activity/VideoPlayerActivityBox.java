package co.cablebox.tv.activity;

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
import android.text.TextUtils;
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

import com.android.volley.VolleyError;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

//import butterknife.Bind;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.R;
import co.cablebox.tv.bean.LiveBean;
import co.cablebox.tv.bean.MensajeBean;
import co.cablebox.tv.utils.ConexionSQLiteHelper;
import co.cablebox.tv.utils.IResult;
import co.cablebox.tv.utils.MCrypt;
import co.cablebox.tv.utils.NetWorkUtils;
import co.cablebox.tv.utils.OnSwipeTouchListener;
import co.cablebox.tv.utils.PreUtils;
import co.cablebox.tv.utils.Utilidades;

import co.cablebox.tv.utils.VolleyService;


public class VideoPlayerActivityBox extends Activity implements IVLCVout.OnNewVideoLayoutListener {
    private static final String TAG = VideoPlayerActivityBox.class.getName();

    private static String direcPag = "51.161.73.204";
    //Volley
    private Gson gson;

    MCrypt mc = new MCrypt();
    IResult mResultCallbackTK = null;
    VolleyService mVolleyServiceTK;

    IResult mResultCallbackMS = null;
    IResult mResultChangeMS = null;
    VolleyService mVolleyServiceMS;

    private static String tk = "";

    private static final String MESSAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyApp/MessageInfo";
    private static final String LOCAL_MESSAGES_FILE = "messageList.xml";

    //Listas de Canales, Favoritos y Todos los Canales
        public static LiveBean liveBean;
        public static MensajeBean mensajeBean;
        public static ArrayList<LiveBean.DataBean> canalesFavoritos;
        public static ArrayList<LiveBean.DataBean> canalesAux;

    // Variables de Interfaz
        /*@BindView(R.id.ll_program_list)
        LinearLayout llProgramList;
        @BindView(R.id.tv_program_logo_list_top)
        ImageView tv_program_logo_list_top;
        @BindView(R.id.tv_program_name_list_top)
        TextView tv_program_name_list_top;
        @BindView(R.id.tv_program_logo_list_center)
        ImageView tv_program_logo_list_center;
        @BindView(R.id.tv_program_name_list_center)
        TextView tv_program_name_list_center;
        @BindView(R.id.tv_program_logo_list_bot)
        ImageView tv_program_logo_list_bot;
        @BindView(R.id.tv_program_name_list_bot)
        TextView tv_program_name_list_bot;*/

        @BindView(R.id.tv_channel_number_change)
        TextView tvChannelNumberChange;
        @BindView(R.id.tv_channel_number)
        TextView tvChannelNumber;
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;
        @BindView(R.id.rl_channel_name)
        RelativeLayout rlChannelName;
        @BindView(R.id.tv_channel_logo)
        ImageView tvChannelLogo;
        @BindView(R.id.tv_classification)
        ImageView tvClassification;
        @BindView(R.id.rl_panel_down)
        RelativeLayout rlDisplayDown;

        SurfaceView surfaceview;

        @BindView(R.id.pb_error)
        TextView pbError;
        @BindView(R.id.tv_program_a)
        TextView tvProgramA;
        @BindView(R.id.tv_horario_ini)
        TextView tvHorarioIni;
        @BindView(R.id.tv_horario_fin)
        TextView tvHorarioFin;
        @BindView(R.id.tv_program_b)
        TextView tvProgramB;
        @BindView(R.id.tv_system_time)
        TextView tvSystemTime;

        @BindView(R.id.rl_volumenA)
        RelativeLayout rlVolumenA;
        @BindView(R.id.sb_volumenA)
        SeekBar sbVolumenA;
        /*@BindView(R.id.tv_volumen)
        TextView tvVolumen;*/

        @BindView(R.id.iv_mute)
        ImageView ivMute;

        @BindView(R.id.iv_nav)
        ImageView ivNav;

        @BindView(R.id.rl_opciones)
        RelativeLayout rlOpciones;
        @BindView(R.id.ll_options)
        RelativeLayout llOptions;
        @BindView(R.id.iv_informativo)
        ImageView ivInformation;
        @BindView(R.id.iv_favorite)
        ImageView ivFavorite;
        @BindView(R.id.iv_list)
        ImageView ivList;

        private int numNot = 0;
        private int posNot = 0;

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
        private String mDownloadSpeedOutput;
        private String mUnits;
        private boolean meterOn = false;

    // Eventos
        private final static int CODE_SHOWLOADING = 1;

        private final static int CODE_STOP_SHOWLOADING = 2;

        private final static int CODE_NET_STATE = 3;

        private final static int CODE_HIDE_BLACK = 4;

        private final static int CODE_CHANGE_BY_NUM = 5;

        private final static int CODE_HIDE_ERROR = 6;

        private final static int CODE_SALIR_APP = 7;

        private final static int CODE_CLEAR_SCREEN = 8;

        private final static int CODE_HIDE_VOLUMEN = 9;

        private final static int CODE_HIDE_OPTION = 10;

        private final static int CODE_DEG_LOCK = 11;

        private final static int CODE_COLOR_NUM = 12;

        private final static int CODE_CHANGE_CHANNEL = 13;

        private final static int CODE_HIDE_NOT = 14;

        private final static int CODE_ACT_PLAN = 15;

        private final static int CODE_NOTF_VIEW = 16;

        private final static int CODE_NOTF_VIEW_OPC = 17;


    // Variable guardar No. de canal
        private final static String PROGRAM_KEY = "lastProIndex";

    // Variables para el correcto funcionamiento del reproductor con VLC
        private SurfaceHolder surfaceHolder;
        private LibVLC libvlc = null;
        private MediaPlayer mediaPlayer = null;
        private IVLCVout ivlcVout;
        private Media media;
        private MediaController controller;
        private SurfaceView mSubtitlesSurface = null;

        private Reproduccion reproduccion = null;

    // Variables para bloquear la el tactil y barras de notificacion y navegacion
        WindowManager wmanager;
        LinearLayout llayout;

    // Claves
    private static final String Q15QSFD = "55555"; // Lista de Programas instalados en el dispositivo (En el celular no funciona)
    private static final String Q12FAFD = "99999"; // Visualisar Consumo de CPU y RAM
    private static final String Q84ASNV = "12345"; //Cambiar IP
    private static final String Q_LOAD_MAIN_ACTIVITY = "11111";
    private String wordKey = "";

    // Variables de Canal actual y programa actual
        private static int channelIndex = 0;
        private static int idProgramCurrent = 0;

    private static boolean deMosaico = false; // Controla si la Actividad es iniciada al elegir un canal desde el mosaico de Categorias o no
    private static boolean failNet = false; // Controla cualquier fallo de conexion

    // Eventos
    private Handler handler = new Handler() {
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
                    handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, delayExitInfoChannel);
                    break;

                case CODE_CHANGE_BY_NUM:
                    if (numChange != "") {
                        cambiarPorNumero(Integer.parseInt(numChange));
                        if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                            toggleInfoChannel();
                            togglePlaylist();
                        }
                        setIdProgramaActual();
                        showProgramInfo();
                        changeChannelList();

                        tvChannelNumberChange.setVisibility(View.INVISIBLE);
                        exitPanelNum();
                    }
                    numChange = "";
                    writingNum = false;
                    delayBusNum = 3000;
                    break;

                case CODE_HIDE_ERROR:
                    hideNetworkInfo();
                    break;

                case CODE_SALIR_APP:
                    if (wordKey.equals(Q15QSFD)) {
                        viewListApps();
                        delayBusNum = 3000;
                        wordKey = "";
                    } else if (wordKey.equals(Q12FAFD)) {
                        viewCpuRam();
                        delayBusNum = 3000;
                        wordKey = "";
                    } else if (wordKey.equals(Q84ASNV)){
                        openServiceActivityAsTechnician();
                    }
                    delayBusNum = 3000;
                    wordKey = "";
                    break;

                case CODE_CLEAR_SCREEN:
                     clearScreen();
                    break;

                case CODE_HIDE_VOLUMEN:
                    if (!onVolumen){
                        rlVolumenA.setVisibility(View.INVISIBLE);
                    }
                    break;

                case CODE_HIDE_OPTION:
                    exitOptions();

                    posOpcion = 1;
                    ivList.setBackground(getDrawable(R.drawable.borde_volumen));
                    ivFavorite.setBackground(getDrawable(R.drawable.borde_volumen));
                    ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                    break;

                case CODE_DEG_LOCK:
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

                case CODE_ACT_PLAN:
                    ServiceProgramActivity.openLiveB(VideoPlayerActivityBox.this);
                    finish();
                    break;

                case CODE_NOTF_VIEW:
                    mensajeNot.setVisibility(View.INVISIBLE);
                    fondoNot.setVisibility(View.INVISIBLE);
                    setServiceMessage(mensajeBean.getData().get(0).getId_dispositivo(), mensajeBean.getData().get(0).getId_mensaje());
                    break;

                case CODE_NOTF_VIEW_OPC:
                    mensajeNot.setVisibility(View.INVISIBLE);
                    fondoNot.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    };

    //Animaciones para cada panel o grupo de items en la interfaz
        private TranslateAnimation animIn;
        private TranslateAnimation exitAnim;
        private TranslateAnimation animInBot;
        private TranslateAnimation exitAnimBot;
        private TranslateAnimation animInOptions;
        private TranslateAnimation exitAnimOptions;

        private TranslateAnimation animInPanelNum;
        private TranslateAnimation exitAnimPanelNum;
        private TranslateAnimation animInList;
        private TranslateAnimation exitAnimList;
        private TranslateAnimation animNotif;
        private TranslateAnimation animInListNot;
        private TranslateAnimation exitAnimListNot;

    private NetworkReceiver networkReceiver;

    // Variables de control y administracion de eventos
        private boolean change = false;
        private boolean onVolumen = false;
        private boolean controlError = false;
        private int numCurrent = 0;

        private String numChange = "";
        private boolean writingNum = false;
        private boolean viewInfo = false;
        private int delayBusNum = 3000;
        private int delayExitInfoChannel = 3000;

        private boolean acFavoritos = false;
        private boolean enAnimacion = false;
        private int posOpcion = 1;

        private boolean cambCanal = false;

    //Socket
        private Socket socket;
        public static String Nickname;
        public static String IMEI = "";

    //Canal Informativo
    private String numCanalInformativo = "-1";

    //Timer Canal
    Timer tiempo_canal = new Timer();
    boolean nuevoCanal = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_box);

        if (isNetDisponible()) {
            //Conectado a internet
            ButterKnife.bind(this);

            toggleHideyBar(); // Oculta Barras de Navegacion y Notificaciones
            inicio();
            initData();
            initPlayer();

            // Control de tiempo, la app muentras constantemente la hora y fecha configurada en el dispositivo
            re = new RefresfClock();
            initClock = new Thread(re);
            initClock.start();

            setIdProgramaActual();
            showProgramInfo();

            adaptarListaCanales();
            //Celular
            onActionTouch();

            // Elegir un canal en la lista de canales
            lvCanales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    channelIndex = position;
                    try {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                    }catch (Exception e){
                        System.out.println("ERROR PAUSE");
                    }

                    tvBlack.setVisibility(View.VISIBLE);
                    toggleInfoChannel();
                    exitList();
                    setIdProgramaActual();
                    showProgramInfo();

                    changeChannel();

                    if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                        ivFavorite.setImageResource(R.drawable.button_fav_b);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }
                }
            });
            lvCanales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN,10000);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
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
                        intent.setClass(VideoPlayerActivityBox.this, VideoPlayerActivityBox.this.getClass());
                        VideoPlayerActivityBox.this.startActivity(intent);
                        VideoPlayerActivityBox.this.finish();
                    }
                })
                .show();
        }
    }


    //Volley
    private void inicio() {
        initVolleyCallback();
        mVolleyServiceTK = new VolleyService(mResultCallbackTK,this);
        Date date= new Date();
        long time = date.getTime();
        System.out.println("IMEI: "+IMEI);
        StringBuilder aux = new StringBuilder();
        aux.append(IMEI);
        aux.append("___");
        aux.append(time);
        //mVolleyService.getDataVolley("GETCALL","http://"+direcPag+":5509/api/RestController.php?q=client&tk=cf4da3d85afbe06c32828ac371a7c036f31f0e55ac4a4515e630baf56096a085");
        JSONObject sendObj = null;
        try {
            String code = MCrypt.bytesToHex(mc.encrypt(aux.toString()));
            sendObj = new JSONObject("{'q':'client','tk':'"+code+"'}");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVolleyServiceTK.postDataVolley("POSTCALL", "http://"+direcPag+":5509/api/RestController.php", sendObj);
    }

    void initVolleyCallback(){
        mResultCallbackTK = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester TK " + requestType);
                Log.d(TAG, "Volley JSON post TK " + response);
                try {
                    String res = response.getString("output");
                    //Log.d("resultado: ",""+res);
                    tk=res;
                    getServiceMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
            }
        };

        mResultCallbackMS = new IResult() {
            @Override
            public void notifySuccess(String requestType, final JSONObject response) {
                Log.d(TAG, "Volley requester MS " + requestType);
                Log.d(TAG, "Volley JSON post MS " + response);

                try {
                    String s=response.toString();
                    if (!TextUtils.isEmpty(s)) {
                        mensajeBean = gson.fromJson(s, MensajeBean.class);

                        //----
                        translateNotf();
                        //----

                        File dirFile = new File(MESSAGE_DIR);

                        if (dirFile.exists()) {
                            dirFile.delete();
                        }
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }

                        File file = new File(MESSAGE_DIR, LOCAL_MESSAGES_FILE);
                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(file, false);
                            stream.write(s.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!TextUtils.isEmpty(s)) {
                    }
                    String res = response.getString("data");
                    //Log.d("resultado: ",""+res);
                    //tk=res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester MS GET" + requestType);
                Log.d(TAG, "Volley JSON post MS GET" + error);
            }
        };


        mResultChangeMS = new IResult() {
            @Override
            public void notifySuccess(String requestType, final JSONObject response) {
                Log.d(TAG, "Volley requester MS Change" + requestType);
                Log.d(TAG, "Volley JSON post MS Change" + response);

                getServiceMessage();
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester MS Change" + requestType);
                Log.d(TAG, "Volley JSON post MS Change" + "That didn't work!");
            }
        };

    }

    public void getServiceMessage() {
        mVolleyServiceMS = new VolleyService(mResultCallbackMS,this);
        JSONObject sendObj = null;
        try {
            sendObj = new JSONObject("{'q':'messages','tk':'"+tk+"'}");
        } catch (JSONException e) {
            System.out.println("Error MS 1 "+e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error MS 2 "+e);
            e.printStackTrace();
        }
        mVolleyServiceMS.postDataVolley("POSTCALL", "http://"+direcPag+":5509/api/RestController.php", sendObj);
    }


    public void setServiceMessage(final int idDispo, final int idMensaje) {
        mVolleyServiceMS = new VolleyService(mResultChangeMS,this);
        JSONObject sendObj = null;
        try {
            sendObj = new JSONObject("{'q':'messagesview','tk':'"+tk+"','idmensaje':'"+idMensaje+"', 'iddispo':'"+idDispo+"'}");
        } catch (JSONException e) {
            System.out.println("Error MS 1 "+e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error MS 2 "+e);
            e.printStackTrace();
        }
        mVolleyServiceMS.postDataVolley("POSTCALL", "http://"+direcPag+":5509/api/RestController.php", sendObj);
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
        if (reproduccion != null)
            reproduccion.cancel(true);

        mediaPlayer.stop();
        mediaPlayer.getVLCVout().detachViews();
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, filter);
    }

    private void getCodec(){
        System.out.println("AspectRatio: "+mediaPlayer.getAspectRatio()+"");
        System.out.println("Id Original Video: "+mediaPlayer.getVideoTrack());
        System.out.println("Id Original Audio: "+mediaPlayer.getAudioTrack());
    }

    // Cambiar lista de Canales en la Interfaz
    private void changeChannelList() {
        int programBack = 0;
        int programNext = 0;

        if(channelIndex > (liveBean.getData().size() - 1))
            channelIndex = 1;
        else if(channelIndex < 0)
            channelIndex = liveBean.getData().size() - 1;

        programNext = channelIndex + 1;
        programBack = channelIndex - 1;

        if(programNext > (liveBean.getData().size() - 1))
            programNext = 0;
        else if(programNext < 0)
            programNext = liveBean.getData().size() - 1;

        if(programBack > (liveBean.getData().size() - 1))
            programBack = 0;
        else if(programBack < 0)
            programBack = liveBean.getData().size() - 1;

        /*tv_program_name_list_top.setText(liveBean.getData().get(programBack).getNum() + "    " +
                liveBean.getData().get(programBack).getName());
        selecImg(tv_program_logo_list_top, liveBean.getData().get(programBack).getLogo());

        tv_program_name_list_center.setText(liveBean.getData().get(channelIndex).getNum() + "    " +
                liveBean.getData().get(channelIndex).getName());
        selecImg(tv_program_logo_list_center, liveBean.getData().get(channelIndex).getLogo());

        tv_program_name_list_bot.setText(liveBean.getData().get(programNext).getNum() + "    " +
                liveBean.getData().get(programNext).getName());
        selecImg(tv_program_logo_list_bot, liveBean.getData().get(programNext).getLogo());*/
    }

    // Buscar imagen en la drawable y actualizar imagen en la interfaz
    private void selecImg(ImageView imageView, String nombre) {
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
     * Se comprueba si la actividad es iniciada desde el panel de categorias o no,
     * se comprueba el ultimo canal elegido, esto es guardado en la cache de la aplicacion*/
    private void initData() {
        //Socket
        socketNoti();

        if (!deMosaico){
            channelIndex = PreUtils.getInt(VideoPlayerActivityBox.this, PROGRAM_KEY, 0);
            if (channelIndex == (liveBean.getData().size()-1))
                channelIndex = 0;
        }

        if (channelIndex >= liveBean.getData().size() || channelIndex < 0)
            channelIndex = 1;

        numCurrent = channelIndex;
        changeChannelList();
        deMosaico = false;

        rlDisplayDown.setVisibility(View.VISIBLE);
        //llProgramList.setVisibility(View.VISIBLE);
        canalesAux = (ArrayList<LiveBean.DataBean>) liveBean.getData();
        organizarFavoritos();

        if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
            ivFavorite.setImageResource(R.drawable.button_fav_b);
            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
        }else{
            ivFavorite.setImageResource(R.drawable.button_fav);
            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
        }

        lvCanales.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        try
        {
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            sbVolumenA.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            sbVolumenA.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            sbVolumenA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    //tvVolumen.setText(""+progress);
                }
            });

            //tvVolumen.setText(""+sbVolumenA.getProgress());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        rlVolumenA.setVisibility(View.INVISIBLE);
        ivMute.setVisibility(View.INVISIBLE);

        socket.emit("vivo_channel", IMEI,liveBean.getData().get(channelIndex).getName(),liveBean.getData().get(channelIndex).getNum(),liveBean.getData().get(channelIndex).getId());

        /*tiempo_canal.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(nuevoCanal) {
                    try {
                        Thread.sleep(1*1000);
                        nuevoCanal = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!nuevoCanal)
                    socket.emit("time_channel", IMEI,liveBean.getData().get(channelIndex).getName());
            }
        },0,1000);*/
    }

    //Notifaciones por Socket
    private void socketNoti(){
        //connect you socket client to the server
        try {
            Nickname = IMEI;
            System.out.println("NickSocket "+Nickname);
            socket = IO.socket("http://"+direcPag+":4010/");
            socket.connect();
            socket.emit("join", Nickname);

            socket.on("nuevoplan", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String id = data.getString("receptorNickname");
                                System.out.println("Actualizar Plan "+id);
                                if(id.equals(IMEI)) {
                                    //Actualizar
                                    llActualizando.setVisibility(View.VISIBLE);

                                    handler.removeMessages(CODE_ACT_PLAN);
                                    handler.sendEmptyMessageDelayed(CODE_ACT_PLAN, 3000);
                                }
                            } catch (Exception e) {
                                Log.d("error socket ", ""+e.toString());
                                //e.printStackTrace();
                            }

                        }
                    });
                }
            });


            socket.on("updateapp", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                //Iniciar Update
                                openServiceActivity();
                            } catch (Exception e) {
                                Log.d("error socket ", ""+e.toString());
                                //e.printStackTrace();
                            }

                        }
                    });
                }
            });

            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String message = data.getString("message");
                                translateNotfOp(message);
                                getServiceMessage();

                            } catch (JSONException e) {
                                Log.d("error socket ", ""+e.toString());
                                //e.printStackTrace();
                            }

                        }
                    });
                }
            });


            socket.on("mensajeprivado", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                //extract data from fired event
                                String id = data.getString("receptorNickname");
                                if(id.equals(IMEI)) {
                                    String message = data.getString("message");
                                    translateNotfOp(message);
                                    getServiceMessage();
                                }

                            } catch (JSONException e) {
                                Log.d("error socket ", ""+e.toString());
                                //e.printStackTrace();
                            }

                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            Log.d("error socket2 ", ""+e.toString());
            //e.printStackTrace();
        }

    }


    // Verificar si los favoritos guardados en la base SQLite existe en la lista de canales actual
    private void organizarFavoritos(){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoPlayerActivityBox.this, "db_favoritos",null, 1);
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
            for(int i = 0; i < liveBean.getData().size(); i++){
                if(name.equals(liveBean.getData().get(i).getName())){
                    encontro = true;
                    canalesFavoritos.add(liveBean.getData().get(i));
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
    private void setIdProgramaActual() {
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < liveBean.getData().get(channelIndex).getProgramas().size(); i++) {
            long horaActual = c.getTimeInMillis();
            long horaA = liveBean.getData().get(channelIndex).getProgramas().get(i).getCalendarInit().getTimeInMillis();
            long horaB = liveBean.getData().get(channelIndex).getProgramas().get(i).getCalendarFinish().getTimeInMillis();

            if (horaActual >= horaA && horaActual <= horaB) {
                idProgramCurrent = i;
                break;
            }
        }

    }

    // Inicializa todas las variables requeridas para la reproduccion y carga el canal actual
    private void initPlayer() {
        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("-vvv");
        libvlc = new LibVLC(VideoPlayerActivityBox.this, options);

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
                        if (event.getBuffering() >= 100.0f ) {
                            if(!controlError || numCurrent != channelIndex){
                                hideLoading();
                                Log.i(TAG, "onEvent: buffer success...");
                                handler.sendEmptyMessageDelayed(CODE_HIDE_BLACK, 500);
                                numCurrent = channelIndex;
                                getCodec();
                            }else {
                                playerInterface.seekTo(0);
                                controlError = false;
                            }
                            mediaPlayer.play();
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
                        //System.out.println("PausableChanged..............");
                        break;

                    case MediaPlayer.Event.EndReached:
                        //System.out.println("EndReached..............");
                        break;

                    case MediaPlayer.Event.MediaChanged:
                        //System.out.println("MediaChanged..............");
                        break;

                    case MediaPlayer.Event.Opening:
                        //System.out.println("Opening..............");
                        break;


                    case MediaPlayer.Event.Paused:
                        //System.out.println("Paused..............");
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
                        //System.out.println("SeekableChanged..............");
                        break;

                    case MediaPlayer.Event.LengthChanged:
                        //System.out.println("LengthChanged..............");
                        break;

                    case MediaPlayer.Event.Vout:
                        //System.out.println("Vout..............");
                        break;


                    case MediaPlayer.Event.ESAdded:
                        //System.out.println("ESAdded..............");
                        break;

                    case MediaPlayer.Event.ESDeleted:
                        //System.out.println("ESDeleted..............");
                        break;

                    case MediaPlayer.Event.ESSelected:
                        //System.out.println("ESSelected..............");
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

        media = new Media(libvlc, Uri.parse(liveBean.getData().get(channelIndex).getUrl()));
        mediaPlayer.setMedia(media);

    }

    // Iniciar actividad VideoPlayerActivity
    public static void openLive(Context context, LiveBean liveBean, MensajeBean mensajeBean, String IMEI, String direcPag) {
        VideoPlayerActivityBox.mensajeBean = mensajeBean;
        VideoPlayerActivityBox.liveBean = liveBean;
        VideoPlayerActivityBox.IMEI = IMEI;
        VideoPlayerActivityBox.direcPag = direcPag;
        context.startActivity(new Intent(context, VideoPlayerActivityBox.class));
    }

    // Iniciar actividad VideoPlayerActivity desde la vista de Categorias
    public static void openLiveB(Context context, LiveBean liveBean, MensajeBean mensajeBean,  String num, String IMEI) {
        VideoPlayerActivityBox.IMEI = IMEI;
        deMosaico = true;
        VideoPlayerActivityBox.mensajeBean = mensajeBean;
        VideoPlayerActivityBox.liveBean = liveBean;
        for (int i = 0; i < VideoPlayerActivityBox.liveBean.getData().size(); i++) {
            if (liveBean.getData().get(i).getNum().equals(num)) {
                VideoPlayerActivityBox.channelIndex = i;
                break;
            }
        }
        context.startActivity(new Intent(context, VideoPlayerActivityBox.class));
    }

    // Iniciar actividad VideoPlayerActivity desde la vista de Categorias pero sin errores como openLiveB.
    public static void openLiveC(Context context, LiveBean liveBean, MensajeBean mensajeBean, String IMEI, String num) {
        VideoPlayerActivityBox.mensajeBean = mensajeBean;
        VideoPlayerActivityBox.liveBean = liveBean;
        VideoPlayerActivityBox.IMEI = IMEI;
        //encontrar el index del canal elegido
        int indexOfChannel=0;
        for (int i = 0; i < VideoPlayerActivityBox.liveBean.getData().size(); i++) {
            if (liveBean.getData().get(i).getNum().equals(num)) {
                indexOfChannel=i;
                VideoPlayerActivityBox.channelIndex = indexOfChannel;
                break;
            }
        }
        //cargar el activity
        PreUtils.setInt(context, PROGRAM_KEY, indexOfChannel);
        context.startActivity(new Intent(context, VideoPlayerActivityBox.class));
    }

    // Reproduce canal actual
    private void play(int position) {
        reproduccion = (Reproduccion) new Reproduccion().execute();

        /*Uri parse = Uri.parse(liveBean.getData().get(position).getUrl());
        media = new Media(libvlc, parse);
        mediaPlayer.setMedia(media);
        ivlcVout.setVideoView(surfaceview);
        if (mSubtitlesSurface != null)
            ivlcVout.setSubtitlesView(mSubtitlesSurface);
        ivlcVout.attachViews();
        mediaPlayer.play();*/
    }

    // Escribiendo el numero del canal
    private void canalNum(String num) {
        if (numChange.length() <= 3) { //permite 3 digitos antes de agregar otro
            handler.removeMessages(CODE_CHANGE_BY_NUM);
            numChange += num;

            tvChannelNumberChange.setVisibility(View.VISIBLE);
            tvChannelNumberChange.setText(numChange);
            handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
        }
    }

    // Acumulado de numeros y comprobar claves
    private void claveExit(String letra) {
        wordKey += letra;

        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, delayBusNum);
    }

    // Inicializa la actividad AppsListActivity para ver la lista de aplicaciones instaladas en el dispositivo
    private void viewListApps() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){}

        surfaceview.setVisibility(View.INVISIBLE);
        mSubtitlesSurface.setVisibility(View.INVISIBLE);

        AppsListActivity.openLive(this, liveBean);
        finish();
    }

    // Mostrar u ocultar el consumo de Ram y CPU
    private void viewCpuRam() {
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

    private void viewRed(){
        if (llCpuRam.getVisibility() == View.INVISIBLE) {
            llCpuRam.setVisibility(View.VISIBLE);
            handler.postDelayed(d, 1000);
        } else {
            llCpuRam.setVisibility(View.INVISIBLE);
            handler.removeCallbacks(d);
        }

    }

    //Actualizar los datos de descarga
    private void getDownloadSpeed() {


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
    private void getChannelListActivity() {
        if (liveBean != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }catch (Exception e){}

            surfaceview.setVisibility(View.INVISIBLE);
            mSubtitlesSurface.setVisibility(View.INVISIBLE);

            socket.disconnect();
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
            handler.removeMessages(CODE_ACT_PLAN);

            //ChannelListActivityBox.channelIndex = channelIndex;
            ChannelListActivityBox.channelIndex = 0;
            PreUtils.setInt(VideoPlayerActivityBox.this, PROGRAM_KEY, channelIndex);
            ChannelListActivityBox.openLive(this, liveBean, IMEI, mensajeBean, direcPag);
            finish();
        }
    }

    private void openServiceActivity() {
        prepareForCloseVideoPlayerActivityBox();
        ServiceProgramActivity.openLive(VideoPlayerActivityBox.this);
        finish();
    }

    // Llenar Lista de Canales en interfaz
    private void adaptarListaCanales() {
        final ArrayList<LiveBean.DataBean> canales = new ArrayList<>();
        for (int i = 0; i < liveBean.getData().size(); i++) {
            if(!liveBean.getData().get(i).getId().equals(numCanalInformativo)){
                canales.add(liveBean.getData().get(i));
            }
        }

        ArrayAdapter<LiveBean.DataBean> cheeseAdapterA = new ArrayAdapter<LiveBean.DataBean>(this,
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
    private boolean existenFavs(){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoPlayerActivityBox.this, "db_favoritos",null, 1);
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

    //Pulsasiones
    private void onActionTouch(){
        // Boton para ver la lista de todos los canales en un panel izquierdo
        ivList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivList.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivList.setBackground(getDrawable(R.drawable.borde_volumen));

                        toggleList();
                        exitOptions();
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
                            channelIndex = liveBean.getData().size()-1;
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

                        if(consultarFavorito(liveBean.getData().get(channelIndex).getName())){
                            //Remover Fav por SQLite
                            eliminarFavorito(liveBean.getData().get(channelIndex).getName());
                            organizarFavoritos();

                            if(acFavoritos){
                                if(existenFavs()){
                                    channelIndex = 0;
                                    liveBean.setData(canalesFavoritos);
                                    setIdProgramaActual();
                                    showProgramInfo();
                                    changeChannel();
                                    ivFavorite.setImageResource(R.drawable.button_fav_b);
                                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                                }else{
                                    acFavoritos = false;

                                    liveBean.setData(canalesAux);

                                    ivFavorite.setImageResource(R.drawable.button_fav);
                                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));

                                }
                            }else{
                                ivFavorite.setImageResource(R.drawable.button_fav);
                                rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                            }
                        }else{
                            //Agregar Fav por SQLite
                            registrarFavorito(liveBean.getData().get(channelIndex).getName());
                            organizarFavoritos();

                            ivFavorite.setImageResource(R.drawable.button_fav_b);
                            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                        }

                        break;
                }
                return true;
            }

        });

        // Panel que controla los toques en pantalla para cambiar canal o mostrar la interfaz
        ivNav.setOnTouchListener(new OnSwipeTouchListener(VideoPlayerActivityBox.this) {
            public void onClick() {
                if(rlPanelNum.getVisibility() == View.VISIBLE) {
                    exitPanelNum();
                }else if(llList.getVisibility() == View.VISIBLE) {
                    exitList();
                }else if(llOptions.getVisibility() == View.INVISIBLE){
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    toggleInfoChannel();
                    togglePanelNum();
                    toggleOptions();
                    handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, 5000);
                }
            }

            public void onSwipeTop() {
                //
                if(rlPanelNum.getVisibility() == View.INVISIBLE && llList.getVisibility() == View.INVISIBLE){
                    change = true;
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    next();
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        toggleInfoChannel();
                        togglePlaylist();
                    }
                    changeChannelList();
                    setIdProgramaActual();
                    showProgramInfo();

                    change = false;
                    setIdProgramaActual();
                    showProgramInfo();
                    changeChannel();

                    if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                        ivFavorite.setImageResource(R.drawable.button_fav_b);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }
                }
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
                //
                if(rlPanelNum.getVisibility() == View.INVISIBLE && llList.getVisibility() == View.INVISIBLE){
                    change = true;
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    previous();
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        toggleInfoChannel();
                        togglePlaylist();
                    }
                    changeChannelList();
                    setIdProgramaActual();
                    showProgramInfo();

                    change = false;
                    setIdProgramaActual();
                    showProgramInfo();
                    changeChannel();


                    if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                        ivFavorite.setImageResource(R.drawable.button_fav_b);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }
                }
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
    private void registrarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoPlayerActivityBox.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_NOMBRE, name);

        Long idResultante = db.insert(Utilidades.TABLA_FAVORITO, null, values);
        db.close();

        System.out.println("Agregado aaa "+idResultante);
        Toast.makeText(getApplicationContext(), "Id Registro: "+idResultante, Toast.LENGTH_LONG);
    }

    // Eliminar de la base de datos SQLite un canal
    private void eliminarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoPlayerActivityBox.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        if(db != null){
            db.execSQL("DELETE FROM "+Utilidades.TABLA_FAVORITO+" WHERE "+Utilidades.CAMPO_NOMBRE+"" +
                    "='"+name+"'");
        }
        db.close();
    }

    // Consultar si el nombre de un canal se encuentra registrado en los favoritos
    private boolean consultarFavorito(String name){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(VideoPlayerActivityBox.this, "db_favoritos",null, 1);
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
    public void cerrarApp() {
        System.out.println("---PROGRAM_KEY "+channelIndex);

        PreUtils.setInt(VideoPlayerActivityBox.this, PROGRAM_KEY, channelIndex);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        /*if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }*/

        /*if (mediaPlayer != null) {
            mediaPlayer.release();
            ivlcVout.detachViews();
            libvlc.release();
        }*/

        llCpuRam.setVisibility(View.INVISIBLE);
        handler.removeCallbacks(r);
        handler.removeCallbacks(c);
        handler.removeCallbacks(d);

        tiempo_canal.cancel();
        socket.disconnect();
        finish();

        //System.exit(0);
        //android.os.Process.killProcess(android.os.Process.myPid());
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
                if (isOnlyChannelInfoActive()){
                    clearScreen();
                }
                else if(!isSomeHudActive()){
                    showChannelInfoAndHideLater();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:

                if(llList.getVisibility() == View.INVISIBLE){
                    change = true;
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    next();
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        toggleInfoChannel();
                        togglePlaylist();
                    }
                    changeChannelList();
                    showProgramInfo();
                    setIdProgramaActual();
                    changeChannel();


                    if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                        ivFavorite.setImageResource(R.drawable.button_fav_b);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }

                        change = false;
                        setIdProgramaActual();
                        showProgramInfo();
                        changeChannel();

                }
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(llList.getVisibility() == View.INVISIBLE){
                    change = true;
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    previous();
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        toggleInfoChannel();
                        togglePlaylist();
                    }
                    changeChannelList();
                    showProgramInfo();
                    setIdProgramaActual();
                    changeChannel();


                    if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                        ivFavorite.setImageResource(R.drawable.button_fav_b);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }

                        change = false;
                        setIdProgramaActual();
                        showProgramInfo();
                        changeChannel();

                }
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(llOptions.getVisibility() == View.VISIBLE){
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    posOpcion++;
                    navOpciones();
                    handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, 5000);
                }else{
                    onVolumen = true;
                    //Accion Barra de Volumen DOWN
                    handler.removeMessages(CODE_HIDE_VOLUMEN);
                    final AudioManager audioManagerB = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    rlVolumenA.setVisibility(View.VISIBLE);
                    sbVolumenA.setProgress(audioManagerB.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(llOptions.getVisibility() == View.VISIBLE){
                    handler.removeMessages(CODE_CLEAR_SCREEN);
                    posOpcion--;
                    navOpciones();
                    handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, 5000);
                }else{
                    onVolumen = true;
                    //Accion Barra de Volumen UP
                    handler.removeMessages(CODE_HIDE_VOLUMEN);
                    final AudioManager audioManagerA = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    rlVolumenA.setVisibility(View.VISIBLE);
                    sbVolumenA.setProgress(audioManagerA.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);
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
                onVolumen = true;
                //Accion Barra de Volumen UP
                handler.removeMessages(CODE_HIDE_VOLUMEN);
                final AudioManager audioManagerA = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                rlVolumenA.setVisibility(View.VISIBLE);
                sbVolumenA.setProgress(audioManagerA.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);

                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                onVolumen = true;
                //Accion Barra de Volumen DOWN
                handler.removeMessages(CODE_HIDE_VOLUMEN);
                final AudioManager audioManagerB = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                rlVolumenA.setVisibility(View.VISIBLE);
                sbVolumenA.setProgress(audioManagerB.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);

                break;

            case KeyEvent.KEYCODE_VOLUME_MUTE:
                if(ivMute.getVisibility() == View.INVISIBLE){
                    ivMute.setVisibility(View.VISIBLE);

                    rlVolumenA.setVisibility(View.INVISIBLE);
                }else{
                    ivMute.setVisibility(View.INVISIBLE);
                }
                break;

            case KeyEvent.KEYCODE_SETTINGS:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                System.out.println("++++++++++++++++++++++++SETTINGS");
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {


            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(llOptions.getVisibility() == View.INVISIBLE){
                    onVolumen = false;
                    handler.sendEmptyMessageDelayed(CODE_HIDE_VOLUMEN, 3000);
                }
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(llOptions.getVisibility() == View.INVISIBLE){
                    onVolumen = false;
                    handler.sendEmptyMessageDelayed(CODE_HIDE_VOLUMEN, 3000);
                }
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                onVolumen = false;
                handler.sendEmptyMessageDelayed(CODE_HIDE_VOLUMEN, 3000);
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                onVolumen = false;
                handler.sendEmptyMessageDelayed(CODE_HIDE_VOLUMEN, 3000);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    // Animaciones para navegar entre las opciones(Solo para TvBox)
    private void navOpciones(){
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
    private void selecOpcion(){

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
                if(consultarFavorito(liveBean.getData().get(channelIndex).getName())){
                    //Remover Fav por SQLite
                    eliminarFavorito(liveBean.getData().get(channelIndex).getName());
                    organizarFavoritos();

                    if(acFavoritos){
                        if(existenFavs()){
                            channelIndex = 0;
                            liveBean.setData(canalesFavoritos);
                            setIdProgramaActual();
                            showProgramInfo();
                            changeChannel();
                            ivFavorite.setImageResource(R.drawable.button_fav_b);
                            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                        }else{
                            acFavoritos = false;

                            liveBean.setData(canalesAux);

                            ivFavorite.setImageResource(R.drawable.button_fav);
                            rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));

                        }
                    }else{
                        ivFavorite.setImageResource(R.drawable.button_fav);
                        rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                    }
                }else{
                    //Agregar Fav por SQLite
                    registrarFavorito(liveBean.getData().get(channelIndex).getName());
                    organizarFavoritos();

                    ivFavorite.setImageResource(R.drawable.button_fav_b);
                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                }
                break;


             */
            case 2:
                ivInformation.setBackground(getDrawable(R.drawable.borde_volumen));
                exitOptions();
                channelIndex = liveBean.getData().size()-1;
                changeChannel();
                break;
        }
    }

    // Actualiza en la interfaz toda la informacion del canal actual
    private void showProgramInfo() {
        pbError.setText("");
        pbError.setVisibility(View.INVISIBLE);
        tvProgramA.setText("");
        tvProgramB.setText("CableEXITO");
        tvHorarioIni.setText("");
        tvHorarioFin.setText("");

        tvChannelName.setText(liveBean.getData().get(channelIndex).getName());
        tvProgramA.setText(liveBean.getData().get(channelIndex).getCalidad());
        tvChannelNumber.setText(liveBean.getData().get(channelIndex).getNum());
        tvChannelNumber.setTextColor(Color.rgb(241,96,96));
        handler.removeMessages(CODE_COLOR_NUM);
        handler.sendEmptyMessageDelayed(CODE_COLOR_NUM, 2000);

        if(!liveBean.getData().get(channelIndex).getName().equals("N/D")){
            tvChannelName.setText(liveBean.getData().get(channelIndex).getName());
            tvChannelNumber.setText(liveBean.getData().get(channelIndex).getNum());
            tvChannelNumber.setTextColor(Color.rgb(241,96,96));
            handler.removeMessages(CODE_COLOR_NUM);
            handler.sendEmptyMessageDelayed(CODE_COLOR_NUM, 2000);

            selecImg(tvChannelLogo, liveBean.getData().get(channelIndex).getLogo());
        }else{
            tvChannelName.setText("No disponible");
            tvChannelNumber.setText(liveBean.getData().get(channelIndex).getNum());
            tvChannelLogo.setImageResource(R.drawable.ic_mantenimiento);
        }

        if(idProgramCurrent < (liveBean.getData().get(channelIndex).getProgramas().size() - 1)){

            selecImg(tvClassification, liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getClasificacion());

            int h = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarInit().get(Calendar.HOUR_OF_DAY);
            String hora = Integer.toString(h);
            if(h == 0)
                hora = "00";
            int m = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarInit().get(Calendar.MINUTE);
            String minuto = Integer.toString(m);
            if(m == 0)
                minuto = "00";

            tvProgramA.setText(liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getNombreProgram());
            tvHorarioIni.setText("Inicio "+hora+":"+minuto);

            h = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().get(Calendar.HOUR_OF_DAY);
            hora = Integer.toString(h);
            if(h == 0)
                hora = "00";
            m = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().get(Calendar.MINUTE);
            minuto = Integer.toString(m);
            if(m == 0)
                minuto = "00";

            tvHorarioFin.setText("Fin "+hora+":"+minuto);

            if (idProgramCurrent < (liveBean.getData().get(channelIndex).getProgramas().size() - 1)) {
                /*h = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent + 1).getCalendarInit().get(Calendar.HOUR_OF_DAY);
                hora = Integer.toString(h);
                if(h == 0)
                    hora = "00";
                m = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent + 1).getCalendarInit().get(Calendar.MINUTE);
                minuto = Integer.toString(m);
                if(m == 0)
                    minuto = "00";*/

                tvProgramB.setText(liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent + 1).getNombreProgram());
            } else {
                tvProgramB.setText("");
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
            exitPanelNum();
        }else if(llList.getVisibility() == View.VISIBLE) {
            exitList();
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

    private void exitOptions() {

        if(llOptions.getVisibility() == View.VISIBLE && !enAnimacion){
            enAnimacion = true;
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
                    enAnimacion = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llOptions.startAnimation(exitAnimOptions);
        }
    }

    //Animacion de Panel de Numeros
    public void togglePanelNum() {
        if(rlPanelNum.getVisibility() == View.INVISIBLE){
            if (animInPanelNum == null) {
                animInPanelNum = new TranslateAnimation(rlPanelNum.getWidth(), 0f, 0f, 0f);
                animInPanelNum.setDuration(300);
            }
            animInPanelNum.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rlPanelNum.setVisibility(View.VISIBLE);
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
            rlPanelNum.startAnimation(animInPanelNum);
        }
    }

    private void exitPanelNum() {
        if(rlPanelNum.getVisibility() == View.VISIBLE && !enAnimacion){
            enAnimacion = true;
            if (exitAnimPanelNum == null) {
                exitAnimPanelNum = new TranslateAnimation(0f, rlPanelNum.getWidth(), 0f, 0f);
                exitAnimPanelNum.setDuration(1000);

            }
            exitAnimPanelNum.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlPanelNum.setVisibility(View.INVISIBLE);
                    enAnimacion = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlPanelNum.startAnimation(exitAnimPanelNum);
        }
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
            llList.startAnimation(animInList);
        }
    }

    private void exitList() {
        if(llList.getVisibility() == View.VISIBLE && !enAnimacion){
            enAnimacion = true;
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
                    enAnimacion = false;

                    //TvBox
                    togglePlaylist();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llList.startAnimation(exitAnimList);
        }
    }


    private void exitInfoChannel() {
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

    private void exitProgramList() {
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


    public void toggleInfoChannel() {
        if(rlDisplayDown.getVisibility() == View.INVISIBLE){
            if (animInBot == null) {
                animInBot = new TranslateAnimation(0f, 0f, rlDisplayDown.getHeight(), 0f);
                animInBot.setDuration(300);
            }

            animInBot.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rlDisplayDown.setVisibility(View.VISIBLE);
                    //tvChannelNumber.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlDisplayDown.startAnimation(animInBot);
        }
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
    private void showLoading() {
        if (tvBlack.getVisibility() == View.INVISIBLE) {
            tvBlack.setVisibility(View.VISIBLE);
            pbError.setText("");
            pbError.setVisibility(View.INVISIBLE);
        }
    }

    // Iniciar actividad ChannelListActivity
    private void hideLoading() {
        if (tvBlack.getVisibility() == View.VISIBLE) {
            pbError.setText("");
            pbError.setVisibility(View.INVISIBLE);
        }
    }

    // Ejecuta el play del canal actual
    private void changeChannel() {
        /*
        //ivlcVout.detachViews();

        if(liveBean.getData().get(channelIndex).getId().equals(numCanalInformativo)){
            pbError.setText("Seccion Informativo");
            getChannelListActivity();
        }else{
            pbError.setText("Problemas técnicos en el canal");
            play(channelIndex);
        }

        socket.emit("vivo_channel", IMEI,liveBean.getData().get(channelIndex).getName(),liveBean.getData().get(channelIndex).getNum(),liveBean.getData().get(channelIndex).getId());
        nuevoCanal = true;

         */
        //ivlcVout.detachViews();

        if(liveBean.getData().get(channelIndex).getId().equals(numCanalInformativo)){
            channelIndex=0;
        }

        pbError.setText("Problemas técnicos en el canal");
        play(channelIndex);


        socket.emit("vivo_channel", IMEI,liveBean.getData().get(channelIndex).getName(),liveBean.getData().get(channelIndex).getNum(),liveBean.getData().get(channelIndex).getId());
        nuevoCanal = true;
    }

    // Metodo para cambiar canal ppor numero
    private void cambiarPorNumero(int i) {
        for(int j = 0; j < liveBean.getData().size(); j++){
            if(i == Integer.parseInt(liveBean.getData().get(j).getNum())){
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                tvBlack.setVisibility(View.VISIBLE);
                channelIndex = j;
                changeChannel();


                if(consultarFavorito(liveBean.getData().get(channelIndex).getName())) {
                    ivFavorite.setImageResource(R.drawable.button_fav_b);
                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                }else{
                    ivFavorite.setImageResource(R.drawable.button_fav);
                    rlChannelName.setBackground(getDrawable(R.drawable.degradado_channel_name));
                }

                return;
            }
        }

        showNetworkInfo("No existe el canal " + i);
        handler.sendEmptyMessageDelayed(CODE_HIDE_ERROR, 2000);
    }

    // Canal siguiente
    private void next() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");}

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex++;

        if (channelIndex >= liveBean.getData().size()) {
            channelIndex = 0;
        }
    }

    // Canal Anterior
    private void previous() {
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
            channelIndex = liveBean.getData().size() - 1;
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

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //ivlcVout.detachViews();
        }
        cerrarApp();
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
        PreUtils.setInt(VideoPlayerActivityBox.this, PROGRAM_KEY, channelIndex);

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            ivlcVout.detachViews();
            libvlc.release();
        }

        //cerrarApp();
        finish();
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
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    // Hacer Ping, este metodo demora demaciado
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
    private long getMemorySize() {

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
    private static float getCpuUsage() {
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
    private void updateTime(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        setZeroClock();
    }

    private void setZeroClock(){
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

    private void initClock(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateTime();

                    curTime = hor + hour + min + minute;
                    tvSystemTime.setText(curTime);

                    Calendar c = Calendar.getInstance();
                    long horaActual = c.getTimeInMillis();
                    long horaB = liveBean.getData().get(channelIndex).getProgramas().get(idProgramCurrent).getCalendarFinish().getTimeInMillis();

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

    private class Reproduccion extends AsyncTask<Void, Integer, Media> {
        @Override
        protected Media doInBackground(Void... params) {
            Uri parse = Uri.parse(liveBean.getData().get(channelIndex).getUrl());
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
            Toast.makeText(VideoPlayerActivityBox.this, "",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl() {
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
    public int HUD_HIDE_TIME=5000;
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
    private void showNextChannel() {
        //pausar la reproduccion
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            System.out.println("ERROR PAUSE");}

        tvBlack.setVisibility(View.VISIBLE);
        channelIndex++;

        if (channelIndex >= liveBean.getData().size()) {
            channelIndex = 0;
        }
    }

    // Canal Anterior
    private void showPreviousChannel() {
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
            channelIndex = liveBean.getData().size() - 1;
        }
    }

    //Abrir el activity principal
    private void openServiceActivityAsTechnician(){
        prepareForCloseVideoPlayerActivityBox();
        ServiceProgramActivity.openLiveC(VideoPlayerActivityBox.this);
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

            socket.disconnect();
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
            handler.removeMessages(CODE_ACT_PLAN);

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
    }

    /**
     *Método que limpia toda la pantalla en un tiempo definido para que solo se visualice el video.
     *@param millis es la cantidad de tiempo en milisegundos que deben pasar antes de que se limpie toda la pantalla
     */
    public void clearScreen(int millis){
        handler.removeMessages(CODE_CLEAR_SCREEN);
        handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN, HUD_HIDE_TIME);
    }



    /**
     * Método que muestra la barra inferior con la información del canal
     */
    public void showChannelInfo(){
        if(rlDisplayDown.getVisibility() == View.INVISIBLE){
            if (animInBot == null) {
                animInBot = new TranslateAnimation(0f, 0f, rlDisplayDown.getHeight(), 0f);
                animInBot.setDuration(300);
            }

            animInBot.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rlDisplayDown.setVisibility(View.VISIBLE);
                    //tvChannelNumber.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlDisplayDown.startAnimation(animInBot);
        }
    }

    /**
     * Método que oculta la barra inferior con la información del canal
     */
    public void hideChannelInfo(){
        rlDisplayDown.setVisibility(View.INVISIBLE);
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
                        llList.getVisibility() == View.VISIBLE
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
    }


    /**
     *Método que muestra la barra inferior con la info del canal y se oculta despues de un tiempo prudente en milisegundos
     * @return void
     */
    public void showChannelInfoAndHideLater(){
        int tiempoPrudenteMilis=5000; //en milisegundos
        showChannelInfo();
        handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN,tiempoPrudenteMilis);
    }


    /**
     *Método que elimina los metodos con demora (delayed messages) que ocultan elementos hud
     * @return void
     */
    public void removeHudDelayedMessages(){
        handler.removeMessages(CODE_CLEAR_SCREEN);
    }
}
