package co.cablebox.tv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import butterknife.Bind;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.cablebox.tv.BuildConfig;
import co.cablebox.tv.R;
import co.cablebox.tv.actualizacion.MyReceiver;
import co.cablebox.tv.bean.LiveBean;
import co.cablebox.tv.bean.MensajeBean;
import co.cablebox.tv.socket.Notificaciones;
import co.cablebox.tv.utils.IResult;
import co.cablebox.tv.utils.MCrypt;
import co.cablebox.tv.utils.NetWorkUtils;
import co.cablebox.tv.utils.PreUtils;
import co.cablebox.tv.utils.StreamUtils;
import co.cablebox.tv.utils.VolleyService;
import co.cablebox.tv.utils.config.wifi.wificonnector.WifiConnector;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ConnectionResultListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.RemoveWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ShowWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiConnectorModel;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiStateListener;

import static co.cablebox.tv.activity.VideoPlayerActivityBox.IMEI;

public class ServiceProgramActivity extends Activity implements WifiConnectorModel {

    @BindView(R.id.btn_iniciar)
    Button btnIniciar;
    @BindView(R.id.btn_wifi)
    Button btnWifi;
    @BindView(R.id.btn_actua)
    Button btnActua;
    @BindView(R.id.btn_apps)
    Button btnApps;
    @BindView(R.id.btn_cambiar_ip)
    Button btnCambiarIp;
    @BindView(R.id.btn_fabrica)
    Button btnFabrica;
    @BindView(R.id.btn_switch)
    Button btnSwitch;
    @BindView(R.id.rl_btn_switch)
    RelativeLayout Rl_BtnSwitch;
    @BindView(R.id.tv_url)
    TextView tpUrl;
    @BindView(R.id.ll_config_btn)
    LinearLayout llConfigBo; //panel inferior de botones cambiar ip, switch, modo multicast...

    @BindView(R.id.btn_ok)
    Button btnOK;
    @BindView(R.id.et_ip)
    EditText etIP;
    @BindView(R.id.ll_ip_nueva)
    LinearLayout llIpNueva;

    @BindView(R.id.ll_redes)
    LinearLayout llRedes;
    @BindView(R.id.rl_mensaje_wifi)
    RelativeLayout rlMensajeWifi;

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.ll_descarga)
    LinearLayout llDescarga;
    @BindView(R.id.ll_actualizando)
    LinearLayout llActualizando;

    @BindView(R.id.tv_imei)
    TextView tvImei;

    private TranslateAnimation animInListWifi;
    private TranslateAnimation exitAnimListWifi;
    private TranslateAnimation animInBtnConf;
    private TranslateAnimation exitAnimBtnConf;

    private Switch mSwitch;
    private TextView mWifiActiveTxtView;
    private RecyclerView rv;

    private WifiListRvAdapter adapter;
    private WifiConnector wifiConnector;
    private boolean onWifi = false;

    private static final String TAG = ServiceProgramActivity.class.getName();
    // Resultados obtenidos del JSON leido
    private Gson gson;
    private LiveBean liveBean;
    private MensajeBean mensajeBean;

    //Codigo IMEI, Identificador del dispositivo
        private String imei;
        private boolean isCel = false;
        static final Integer PHONESTATS = 0x1;

    // Variables para conexion a servidor por medio de Volley
        /*public VolleyS volleyS;
        private RequestQueue mQueue;*/

    // Variables para bloquear la el tactil y barras de notificacion y navegacion
        WindowManager wmanager;
        LinearLayout llayout;

    // Variable guardar Ip
        private final static String IP_KEY = "ipLista";

        //SharedPreferences sharIp = getSharedPreferences("ArchivoIP", getApplicationContext().MODE_PRIVATE);
        private static String direcPag = "ipmux.cablebox.co";
    // Leer y obtener informacion de los canales a traves de un JSON
        private static String BASE_URI = "http://"+direcPag+":5509/api/RestController.php";
        private static String BASE_URI_AUX = "http://51.161.73.204:5509/api/RestController.php";

        private static final String LIVE_URI = "/api/live";
        private static final String LIVE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyApp/LiveInfo";
        private static final String LOCAL_URL = Environment.getExternalStorageDirectory() + File.separator + "MyApp" + File.separator + "AppServer";
        private static final String LOCAL_LIST_FILE = "liveList.xml";

        private static final String MESSAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyApp/MessageInfo";
        private static final String LOCAL_MESSAGES_FILE = "messageList.xml";

    // Acciones que se ejecutar si el JSON leido es correcto o no
        private final static int CODE_NETWORK_ERROR = 0;
        private final static int CODE_NETWORK_SUCCESS = 1;
        private final static int CODE_SALIR_APP = 3;
        private final static int CODE_ACT = 4;
        private final static int CODE_ACT_PLAN = 5;

        private boolean viewApp = false;

        // Variable que define si la activity se cargará en modo Técnico para hacer ajustes. False siginifica que no es un Técnico y la activity cargará normal para un usuario
        private static boolean isTechnician=false;

    private static int what = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_NETWORK_SUCCESS:
                    setLiveData();
                    break;

                case CODE_NETWORK_ERROR:
                    //Link de la lista de canales erroneo
                    Toast.makeText(ServiceProgramActivity.this, "Error de Red!", Toast.LENGTH_LONG).show();
                    break;

                case CODE_SALIR_APP:
                    handler.removeMessages(CODE_ACT_PLAN);
                    if (wordKey.equals(Q15QSFD)) {
                        if(llConfigBo.getVisibility() == View.INVISIBLE){
                            togglePanelConf();
                            delayBusNum = 0;
                        }else if(llConfigBo.getVisibility() == View.VISIBLE){
                            exitPanelConf();
                            delayBusNum = 3000;
                        }
                    }else if(wordKey.equals(SH0W1M31)){
                        if(llConfigBo.getVisibility() == View.INVISIBLE){
                            if(isCel)
                                tvImei.setText("IMEI: "+imei);
                            else
                                tvImei.setText("MAC: "+imei);
                            tvImei.setVisibility(View.VISIBLE);
                            delayBusNum = 0;
                        }else if(llConfigBo.getVisibility() == View.VISIBLE){
                            tvImei.setVisibility(View.INVISIBLE);
                            delayBusNum = 3000;
                        }
                    }
                    wordKey = "";
                    break;

                case CODE_ACT:
                    llActualizando.setVisibility(View.INVISIBLE);
                    actualizando = false;
                    estadoBotones(true);

                    handler.removeMessages(CODE_ACT_PLAN);
                    if(!onWifi){
                        handler.sendEmptyMessageDelayed(CODE_ACT_PLAN, 10000);
                    }
                    break;

                case CODE_ACT_PLAN:
                    if(!onWifi) {
                        System.out.println("Entra CODE_ACT_PLAN");
                        camPlan = false;
                        inicarVid();
                    }
                    break;
            }
        }
    };

    private static final String Q15QSFD = "55555";
    private static final String SH0W1M31 = "88888";
    private String wordKey = "";
    private int delayBusNum = 3000;

    //Actualizar APK
    MyReceiver myReceiver;
    private String versionActual;
    private String versionNueva;
    private String urlApk;

    MCrypt mc = new MCrypt();
    IResult mResultCallbackTK = null;
    VolleyService mVolleyServiceTK;

    IResult mResultCallbackCH = null;
    VolleyService mVolleyServiceCH;
    IResult mResultCallbackMS = null;
    VolleyService mVolleyServiceMS;
    IResult mResultCallbackVersion = null;
    VolleyService mVolleyServiceVersion;

    private static String tk = "";
    private static String url_type = "unicast"; //unicast or multicast

    //Actualizacion del plan
        public static boolean camPlan = false;
        public boolean actualizando = true;

    private boolean conectado = false;

    //Socket Notificaciones
        private Socket socket;
        public static String Nickname;

        public static String IMEI = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        ButterKnife.bind(this);

        SharedPreferences sharpref = getPreferences(getBaseContext().MODE_PRIVATE);
        direcPag = sharpref.getString("IP", direcPag);// ipmux
        BASE_URI = "http://"+direcPag+":5509/api/RestController.php";

        /*String ip = PreUtils.getString(ServiceProgramActivity.this, IP_KEY, "http://"+direcPag+":5509/api/RestController.php");
        BASE_URI = ip;*/
        System.out.println("IP: "+BASE_URI);

        estadoBotones(false);

        //Descargar Apk
        InitDescarga();

        mSwitch = findViewById(R.id.wifiActivationSwitch);
        mWifiActiveTxtView = findViewById(R.id.wifiActivationTv);
        rv = findViewById(R.id.wifiRv);

        //Para Instalar APK
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        tvVersion.setText("IPTv-BOX Copyrigth CableMEDIA® 2019 Version ANDROID-7.1-"+BuildConfig.VERSION_NAME);
        setLocationPermission();
        createWifiConnectorObject();

        //EjemploSocket.openLive(this);

        consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        if(imei == null){
            isCel = false;
            imei = getSerialNumber();
            tvImei.setText("MAC: "+imei);
            tvImei.setVisibility(View.VISIBLE);
            /*if (!Settings.canDrawOverlays(getApplicationContext())) {
                finishAndRemoveTask();
                startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION"));
            }
            bloquearBarras();*/
        }else{
            isCel = true;
            tvImei.setText("IMEI: "+imei);
            tvImei.setVisibility(View.VISIBLE);
            //llConfigBo.setVisibility(View.VISIBLE);
            url_type = "unicast";
            Rl_BtnSwitch.setVisibility(View.GONE);
        }

        List<Notificaciones> list = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Notificaciones n = new Notificaciones("","Algo "+i);
            list.add(n);
        }

        llActualizando.setVisibility(View.VISIBLE);
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                handler.removeMessages(CODE_ACT);
                handler.sendEmptyMessageDelayed(CODE_ACT, 0);
                if(rlMensajeWifi.getVisibility() == View.VISIBLE){
                    onWifi = true;
                    toggleWifi();
                }

            }
        }.start();

        inicio();
        socketNoti();

        funciones();

        if(!isTechnician){
            setLiveData();
        }
    }

    private void inicio() {
        initVolleyCallback();
        mVolleyServiceTK = new VolleyService(mResultCallbackTK,this);
        Date date= new Date();
        long time = date.getTime();
        StringBuilder aux = new StringBuilder();
        aux.append(imei);
        aux.append("___");
        aux.append(time);
        //mVolleyService.getDataVolley("GETCALL","http://cmxpon.cablebox.co:5505/api/RestController.php?q=client&tk=cf4da3d85afbe06c32828ac371a7c036f31f0e55ac4a4515e630baf56096a085");
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

    //Notifaciones por Socket
    private void socketNoti(){
        //connect you socket client to the server
        try {
            Nickname = IMEI;
            System.out.println("NickSocket "+Nickname);
            socket = IO.socket("http://"+direcPag+":4010/");
            socket.connect();
            socket.emit("join", Nickname);

            socket.on("updateapp", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                //Iniciar Update
                                ServiceProgramActivity.openLive(ServiceProgramActivity.this);
                                finish();
                            } catch (Exception e) {
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

    private void buscarVersion(){
        mVolleyServiceVersion = new VolleyService(mResultCallbackVersion,this);
        JSONObject sendObjV = null;
        try {
            sendObjV = new JSONObject("{'q':'version','tk':'"+tk+"'}");
        } catch (JSONException e) {
            System.out.println("Error VS 1 "+e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error VS 2 "+e);
            e.printStackTrace();
        }
        mVolleyServiceVersion.postDataVolley("POSTCALL", "http://"+direcPag+":5509/api/RestController.php", sendObjV);

    }

    private void funciones(){
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                handler.removeMessages(CODE_ACT_PLAN);
                String localUrl = getServerFromFile(LOCAL_URL);
                if (!TextUtils.isEmpty(localUrl)) {
                    BASE_URI = localUrl;
                }
                //mQueue = Volley.newRequestQueue(this);
                //volleyS = new VolleyS(mQueue);
                initData();
            }
            }
        });

        btnWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                handler.removeMessages(CODE_ACT_PLAN);
                if(llRedes.getVisibility() == View.INVISIBLE) {
                    onWifi = true;
                    toggleWifi();
                }else if(llRedes.getVisibility() == View.VISIBLE){
                    onWifi = false;
                    exitWifi();
                }
            }
            }
        });

        btnActua.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                estadoBotones(false);
                handler.removeMessages(CODE_ACT_PLAN);
                llDescarga.setVisibility(View.VISIBLE);
                myReceiver.Descargar(direcPag);
            }
            }
        });

        btnCambiarIp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
            handler.removeMessages(CODE_ACT_PLAN);
            SharedPreferences sharpref = getPreferences(getBaseContext().MODE_PRIVATE);
            etIP.setText(sharpref.getString("IP", direcPag));

            llIpNueva.setVisibility(View.VISIBLE);
        }
            }
        });

        btnApps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                    viewApp = true;
                    handler.removeMessages(CODE_ACT_PLAN);
                    handler.removeMessages(CODE_NETWORK_SUCCESS);
                    viewListApps();
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                    direcPag = etIP.getText().toString();
                    SharedPreferences sharepref = getPreferences(getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharepref.edit();
                    editor.putString("IP", direcPag);
                    editor.commit();
                    BASE_URI = "http://"+direcPag+"/api/RestController.php";

                    PreUtils.setString(ServiceProgramActivity.this, IP_KEY, BASE_URI);

                    String localUrl = getServerFromFile(LOCAL_URL);
                    if (!TextUtils.isEmpty(localUrl)) {
                        BASE_URI = localUrl;
                    }

                    llIpNueva.setVisibility(View.INVISIBLE);
                    Toast.makeText(ServiceProgramActivity.this, "La Ip ha cambiado", Toast.LENGTH_SHORT).show();

                    //mQueue = Volley.newRequestQueue(this);
                    //volleyS = new VolleyS(mQueue);
                    inicio();
                    socketNoti();
                    initData();
                }
            }
        });

        btnFabrica.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(llDescarga.getVisibility() == View.INVISIBLE && !actualizando){
                    direcPag = "51.161.73.204";
                    url_type = "multicast";
                    tpUrl.setText("Unicast");
                    Toast.makeText(ServiceProgramActivity.this, "Reproduciendo direcciones Multicast", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharepref = getPreferences(getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharepref.edit();
                    editor.putString("IP", "51.161.73.204");
                    editor.commit();
                    BASE_URI = BASE_URI_AUX;

                    PreUtils.setString(ServiceProgramActivity.this, IP_KEY, BASE_URI);

                    String localUrl = getServerFromFile(LOCAL_URL);
                    if (!TextUtils.isEmpty(localUrl)) {
                        BASE_URI = localUrl;
                    }

                    Toast.makeText(ServiceProgramActivity.this, "La Ip se ha reiniciado, oprima Inicio Normal", Toast.LENGTH_SHORT).show();

                    //mQueue = Volley.newRequestQueue(this);
                    //volleyS = new VolleyS(mQueue);
                    inicio();
                    socketNoti();
                    initData();
                }

            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(url_type.equals("multicast")){
                    url_type = "unicast";
                    tpUrl.setText("Multicast");
                    Toast.makeText(ServiceProgramActivity.this, "Reproduciendo direcciones Unicast", Toast.LENGTH_SHORT).show();
                }
                else{
                    url_type = "multicast";
                    tpUrl.setText("Unicast");
                    Toast.makeText(ServiceProgramActivity.this, "Reproduciendo direcciones Multicast", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitDescarga(){
        myReceiver = new MyReceiver(ServiceProgramActivity.this);
        myReceiver.Registrar(myReceiver);
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
                    System.out.println("Respuesta "+tk);
                    buscarVersion();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                System.out.println("Error--- Token");
            }
        };


        mResultCallbackCH = new IResult() {
            @Override
            public void notifySuccess(String requestType, final JSONObject response) {
                Log.d(TAG, "Volley requester CH " + requestType);
                Log.d(TAG, "Volley JSON post CH " + response);

                try {
                    String s = response.toString();

                    if (!TextUtils.isEmpty(s)) {
                        System.out.println("ASGASGAS "+s);
                        liveBean = gson.fromJson(s, LiveBean.class);

                        File dirFile = new File(LIVE_DIR);

                        if (dirFile.exists()) {
                            dirFile.delete();
                        }
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }

                        File file = new File(LIVE_DIR, LOCAL_LIST_FILE);
                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(file, false);
                            stream.write(s.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        what = CODE_NETWORK_SUCCESS;
                    }

                    if (!TextUtils.isEmpty(s)) {
                    }
                    String res = response.getString("data");
                    //Log.d("resultado: ",""+res);
                    tk=res;
                } catch (Exception e) {
                    Log.d("error1","srgergerergegrr");
                    e.printStackTrace();
                    what = CODE_NETWORK_ERROR;
                    if (liveBean != null) {
                        what = CODE_NETWORK_SUCCESS;
                    }
                } finally {
                    handler.sendEmptyMessage(what);
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                System.out.println("Error--- Canales");
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
                    tk=res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester MS " + requestType);
                Log.d(TAG, "Volley JSON post MS " + "That didn't work!");
                System.out.println("Error--- Mensajes");
            }
        };

        mResultCallbackVersion = new IResult() {
            @Override
            public void notifySuccess(String requestType, final JSONObject response) {
                Log.d(TAG, "Volley requester Version " + requestType);
                Log.d(TAG, "Volley JSON post Version " + response);

                try {
                    String s=response.toString();
                    if (!TextUtils.isEmpty(s)) {
                        String[] verA = s.split("version\":\"");
                        String versionActual = verA[1].substring(0, verA[1].length()-4);
                        String versionThis = BuildConfig.VERSION_NAME;
                        System.out.println("Version A "+versionThis+" - Version B "+versionActual);

                        handler.removeMessages(CODE_ACT);
                        handler.sendEmptyMessageDelayed(CODE_ACT, 0);

                        if(!versionThis.equals(versionActual)){
                            System.out.println("requiere actualizacion");
                            handler.sendEmptyMessageDelayed(CODE_ACT_PLAN, 10000);
                            /*handler.removeMessages(CODE_ACT_PLAN);
                            llActualizando.setVisibility(View.INVISIBLE);
                            estadoBotones(false);
                            llDescarga.setVisibility(View.VISIBLE);
                            myReceiver.Descargar();*/
                        }else{
                            handler.sendEmptyMessageDelayed(CODE_ACT_PLAN, 10000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester Version " + requestType);
                Log.d(TAG, "Volley JSON post Version " + "That didn't work!");
                System.out.println("Error--- Version");
            }
        };
    }


    public String getVersionName(Context ctx){
        try {
            return ctx.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Crear Archivo local para guardar el JSON
    private String getServerFromFile(String fileName) {
        File file = new File(fileName);
        String str = "";
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                str = StreamUtils.stream2String(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }


    // Permisos para obtener IMEI
    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ServiceProgramActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ServiceProgramActivity.this, permission)) {

                ActivityCompat.requestPermissions(ServiceProgramActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ServiceProgramActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = obtenerIMEI();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getSerialNumber() {
        String serialNumber;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            /*if (serialNumber.equals(""))
                serialNumber = null;
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;*/
            if (serialNumber.equals(""))
                serialNumber = Build.getSerial();
            if (serialNumber.equals(""))
                serialNumber = "---------------";
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }


    // Obtener IMEI
    private String obtenerIMEI() {
        try {
            final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Hacemos la validación de métodos, ya que el método getDeviceId() ya no se admite para android Oreo en adelante, debemos usar el método getImei()
                return telephonyManager.getImei();
            }
            else {
                return telephonyManager.getDeviceId();
            }
        }catch (Exception e){
            System.out.println(e);
        }

        try {
            String deviceId;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
            return deviceId;
        }catch (Exception e){
            System.out.println(e);
        }
        return "0000000";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        handler.removeMessages(CODE_ACT_PLAN);
        if(llDescarga.getVisibility() == View.INVISIBLE && !onWifi)
            handler.sendEmptyMessageDelayed(CODE_ACT_PLAN, 10000);
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                exitWifi();
                cerrarApp();
                break;

            case KeyEvent.KEYCODE_1:
                claveExit("1");
                break;

            case KeyEvent.KEYCODE_2:
                claveExit("2");
                break;

            case KeyEvent.KEYCODE_3:
                claveExit("3");
                break;

            case KeyEvent.KEYCODE_4:
                claveExit("4");
                break;

            case KeyEvent.KEYCODE_5:
                claveExit("5");
                break;

            case KeyEvent.KEYCODE_8:
                claveExit("8");
                break;

            case KeyEvent.FLAG_EDITOR_ACTION:
                System.out.println("Oprimio Enter");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Iniciar la Actividad VideoPlayerActivity
    private void setLiveData() {
        if (liveBean != null || !viewApp) {
            try{
                for (int i = 0; i < liveBean.getData().size(); i++){
                    for(int j = 0; j < liveBean.getData().get(i).getProgramas().size(); j++){
                        String date = liveBean.getData().get(i).getProgramas().get(j).getTimeDateInit();
                        Calendar calendar = Calendar.getInstance();
                        String datereip = date.replace("/Date(", "").replace(")/", "");
                        Long timeInMillis = Long.valueOf(datereip);
                        calendar.setTimeInMillis(timeInMillis);
                        liveBean.getData().get(i).getProgramas().get(j).setCalendarInit(calendar);

                        date = liveBean.getData().get(i).getProgramas().get(j).getTimeDateFinish();
                        calendar = Calendar.getInstance();
                        datereip = date.replace("/Date(", "").replace(")/", "");
                        timeInMillis = Long.valueOf(datereip);
                        calendar.setTimeInMillis(timeInMillis);
                        liveBean.getData().get(i).getProgramas().get(j).setCalendarFinish(calendar);

                    }
                }

                //Si el IMEI no es Null significa que la aplicacion se esta ejecutando en un Celular, si no la App esta en una TvBox
                consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);

                //VideoPlayerActivity.openLive(this, liveBean, mensajeBean, imei, direcPag);
                if (imei != null) {
                    VideoPlayerActivity.openLive(this, liveBean, mensajeBean, imei, direcPag);
                }else{
                    imei = getSerialNumber();
                    VideoPlayerActivityBox.openLive(this, liveBean, mensajeBean, imei, direcPag);
                }

                finish();

            }catch (Exception e){
                //Lista de canales vacia
                System.out.println("Error de direccion: "+e);

                try {

                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cargando canales. Por favor espere...") //Dispositivo no registrado
                            .setMessage("")
                            .setNegativeButton(null, null)// sin listener
                            .setPositiveButton(R.string.reintentar, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Reintentar
                                    Intent intent=new Intent();
                                    intent.setClass(ServiceProgramActivity.this, ServiceProgramActivity.this.getClass());
                                    ServiceProgramActivity.this.startActivity(intent);
                                    ServiceProgramActivity.this.finish();

                                    ServiceProgramActivity.openLive(ServiceProgramActivity.this);
                                    finish();
                                }
                            })
                            .show();
                }catch (Exception ex){ }
            }
        }
    }

    // Inicializa Variables y redirecciona a la Actividad VideoPlayerActivity
    private void initData() {

        gson = new Gson();
        liveBean = new LiveBean();
        mensajeBean = new MensajeBean();

        String str = getServiceListFromFile(LIVE_DIR);
        //System.out.println("STR: "+str);
        if (!TextUtils.isEmpty(str)) {
            liveBean = gson.fromJson(str, LiveBean.class);
        }

        if (!NetWorkUtils.getNetState(this)) {
            handler.sendEmptyMessage(CODE_NETWORK_ERROR);
            return;
        }

        getServiceListFromServer();
    }

    // Obtener lista de canales desde Json local
    private String getServiceListFromFile(String directory) {
        File file = new File(directory, LOCAL_LIST_FILE);
        String str = "";
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                str = StreamUtils.stream2String(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    // Metodo para cerrar la aplicacion
    public void cerrarApp() {
        isTechnician=false;
        openLive(this);
        finish();

        /*System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());*/
    }

    // Obtener lista de canales desde Json
    private void getServiceListFromServer() {
        mVolleyServiceCH = new VolleyService(mResultCallbackCH,this);
        new Thread() {
            @Override
            public void run() {
                try {

                    Date date= new Date();
                    long time = date.getTime();
                    StringBuilder aux = new StringBuilder();
                    aux.append(imei);
                    aux.append("___");
                    aux.append(time);
                    //mVolleyService.getDataVolley("GETCALL","http://cmxpon.cablebox.co:5505/api/RestController.php?q=client&tk=cf4da3d85afbe06c32828ac371a7c036f31f0e55ac4a4515e630baf56096a085");
                    JSONObject sendObj = null;
                    try {
                        System.out.println("-------------holi");
                        String code = MCrypt.bytesToHex(mc.encrypt(aux.toString()));
                        Log.d("loca>",tk);
                        sendObj = new JSONObject("{'q':'channels','tk':'"+tk+"','url_type':'"+url_type+"'}");
                        System.out.println(sendObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mVolleyServiceCH.postDataVolley("POSTCALL", "http://"+direcPag+":5509/api/RestController.php", sendObj);

                    /*if(camPlan) {
                        camPlan = false;
                        System.out.println("CAMBIANDO PLAN");
                        inicarVid();
                    }*/


                    /*
                    HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URI).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        String s = StreamUtils.stream2String(is);
                        if (!TextUtils.isEmpty(s)) {
                            liveBean = gson.fromJson(s, LiveBean.class);

                            File dirFile = new File(LIVE_DIR);
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                            }

                            File file = new File(LIVE_DIR, LOCAL_LIST_FILE);
                            FileOutputStream stream = new FileOutputStream(file);
                            stream.write(s.getBytes());
                            what = CODE_NETWORK_SUCCESS;
                        }
                    }*/
                } catch (Exception e) {
                    System.out.println("Error "+e);
                } finally {
                }
            }
        }.start();


        /*mVolleyServiceMS = new VolleyService(mResultCallbackMS,this);
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
        mVolleyServiceMS.postDataVolley("POSTCALL", "http://cmxpon.cablebox.co:5506/api/RestController.php", sendObj);*/


       /*mVolleyServiceMS = new VolleyService(mResultCallbackMS,this);
        new Thread() {
            @Override
            public void run() {
                try {
                    //Mensajes
                    System.out.println("Buscando Mensajes");
                    Date date= new Date();
                    long time = date.getTime();
                    StringBuilder aux = new StringBuilder();
                    aux.append(imei);
                    aux.append("___");
                    aux.append(time);

                    JSONObject sendObj = null;
                    try {
                        String code = MCrypt.bytesToHex(mc.encrypt(aux.toString()));
                        sendObj = new JSONObject("{'q':'messages','tk':'"+tk+"'}");
                    } catch (JSONException e) {
                        System.out.println("Error MS 1 "+e);
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.out.println("Error MS 2 "+e);
                        e.printStackTrace();
                    }
                    mVolleyServiceMS.postDataVolley("POSTCALL", "http://cmxpon.cablebox.co:5506/api/RestController.php", sendObj);
                } catch (Exception e) {
                    System.out.println("Error MSMS "+e);
                } finally {
                }
            }
        }.start();*/

    }

    @Override
    public void onBackPressed() {
        if(llIpNueva.getVisibility() == View.VISIBLE) {
            llIpNueva.setVisibility(View.INVISIBLE);
        } else if(llRedes.getVisibility() == View.VISIBLE){
            exitWifi();
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
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    // Inicia la Actividad ServiceProgramActivity
    public static void openLive(Context context) {
        context.startActivity(new Intent(context, ServiceProgramActivity.class));
    }

    public static void openLiveB(Context context) {
        ServiceProgramActivity.camPlan = true;
        context.startActivity(new Intent(context, ServiceProgramActivity.class));
    }

    //Inicia la Actividad ServiceProgramActivity como un técnico/technician
    public static void openLiveC(Context context) {
        ServiceProgramActivity.isTechnician = true;
        context.startActivity(new Intent(context, ServiceProgramActivity.class));
    }

    private void inicarVid(){
        if(llDescarga.getVisibility() == View.INVISIBLE){
            String localUrl = getServerFromFile(LOCAL_URL);
            if (!TextUtils.isEmpty(localUrl)) {
                BASE_URI = localUrl;
            }
            initData();
        }
    }

    // Acumulado de numeros y comprobar claves
    private void claveExit(String letra) {
        wordKey += letra;

        handler.removeMessages(CODE_ACT_PLAN);
        handler.sendEmptyMessageDelayed(CODE_SALIR_APP, delayBusNum);
    }

    // Inicializa la actividad AppsListActivity para ver la lista de aplicaciones instaladas en el dispositivo
    private void viewListApps() {
        AppsListActivity.openLive(this, liveBean);
        finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        myReceiver.borrarRegistro(myReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        myReceiver.Registrar(myReceiver);
    }

    @Override
    protected void onDestroy() {
        destroyWifiConnectorListeners();
        super.onDestroy();
    }


    public void estadoBotones(boolean estado){
        System.out.println(estado);
        btnWifi.setClickable(estado);
        btnWifi.setFocusable(estado);
        btnApps.setClickable(estado);
        btnApps.setFocusable(estado);
        btnActua.setClickable(estado);
        btnActua.setFocusable(estado);
        btnCambiarIp.setClickable(estado);
        btnCambiarIp.setFocusable(estado);
        btnIniciar.setClickable(estado);
        btnIniciar.setFocusable(estado);
        btnFabrica.setClickable(estado);
        btnFabrica.setFocusable(estado);
        btnSwitch.setClickable(estado);
        btnSwitch.setFocusable(estado);
        btnOK.setClickable(estado);
        btnOK.setFocusable(estado);
    }

    //Animacion de Lista de Wifi
    public void toggleWifi() {
        if(llRedes.getVisibility() == View.INVISIBLE){
            //llProgramList.setVisibility(View.INVISIBLE);
            if (animInListWifi == null) {
                animInListWifi = new TranslateAnimation(-llRedes.getWidth(), 0f, 0f, 0f);
                animInListWifi.setDuration(300);
            }
            animInListWifi.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    llRedes.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llRedes.startAnimation(animInListWifi);
        }
    }

    private void exitWifi() {
        if(llRedes.getVisibility() == View.VISIBLE){
            if (exitAnimListWifi == null) {
                exitAnimListWifi = new TranslateAnimation(0f, -llRedes.getWidth(), 0f, 0f);
                exitAnimListWifi.setDuration(1000);

            }
            exitAnimListWifi.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    llRedes.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llRedes.startAnimation(exitAnimListWifi);
        }
    }

    //Animacion de botones de configuracion
    public void togglePanelConf() {
        if(llConfigBo.getVisibility() == View.INVISIBLE){
            if (animInBtnConf == null) {
                animInBtnConf = new TranslateAnimation(llConfigBo.getWidth(), 0f, 0f, 0f);
                animInBtnConf.setDuration(300);
            }
            animInBtnConf.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    llConfigBo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llConfigBo.startAnimation(animInBtnConf);
        }
    }

    private void exitPanelConf() {
        if(llConfigBo.getVisibility() == View.VISIBLE){
            if (exitAnimBtnConf == null) {
                exitAnimBtnConf = new TranslateAnimation(0f, llConfigBo.getWidth(), 0f, 0f);
                exitAnimBtnConf.setDuration(1000);

            }
            exitAnimBtnConf.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    llConfigBo.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llConfigBo.startAnimation(exitAnimBtnConf);
        }
    }

    //Wifi
    @Override
    public void createWifiConnectorObject() {
        wifiConnector = new WifiConnector(this);
        wifiConnector.setLog(true);
        wifiConnector.registerWifiStateListener(new WifiStateListener() {
            @Override
            public void onStateChange(int wifiState) {

            }

            @Override
            public void onWifiEnabled() {
                ServiceProgramActivity.this.onWifiEnabled();
            }

            @Override
            public void onWifiEnabling() {

            }

            @Override
            public void onWifiDisabling() {

            }

            @Override
            public void onWifiDisabled() {
                ServiceProgramActivity.this.onWifiDisabled();
            }
        });

        if(wifiConnector.isWifiEnbled()){
            mSwitch.setChecked(true);
            onWifiEnabled();
        } else {
            mSwitch.setChecked(false);
            onWifiDisabled();
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiConnector.enableWifi();
                } else {
                    wifiConnector.disableWifi();
                }
            }
        });

        wifiAdapter();

        if(!conectado){
            if(isNetDisponible()){
                //Conectado a internet
                rlMensajeWifi.setVisibility(View.INVISIBLE);
            }else{
                //Sin Conexion a internet
                rlMensajeWifi.setVisibility(View.VISIBLE);
            }
        }
    }

    private void wifiAdapter(){
        adapter = new WifiListRvAdapter(this.wifiConnector, new WifiListRvAdapter.WifiItemListener() {
            @Override
            public void onWifiItemClicked(ScanResult scanResult) {
                openConnectDialog(scanResult);
            }

            @Override
            public void onWifiItemLongClick(ScanResult scanResult) {
                disconnectFromAccessPoint(scanResult);
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
    }

    private void onWifiEnabled(){
        mWifiActiveTxtView.setText("Wi-Fi");
        if (permisionLocationOn()) {
            scanForWifiNetworks();
        } else {
            checkLocationTurnOn();
        }
    }

    private void onWifiDisabled(){
        mWifiActiveTxtView.setText("Wi-Fi");
        if(adapter != null)
            adapter.setScanResultList(new ArrayList<ScanResult>());
    }

    @Override
    public void scanForWifiNetworks() {
        wifiConnector.showWifiList(new ShowWifiListener() {
            @Override
            public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult) {
                adapter.setScanResultList(wifiScanResult);
            }

            @Override
            public void onNetworksFound(JSONArray wifiList) {

            }

            @Override
            public void errorSearchingNetworks(int errorCode) {
                Toast.makeText(ServiceProgramActivity.this, "Error al obtener lista de Wi-Fi, error: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openConnectDialog(ScanResult scanResult){
        handler.removeMessages(CODE_ACT_PLAN);
        onWifi = true;
        ConnectToWifiDialog dialog = new ConnectToWifiDialog(ServiceProgramActivity.this, scanResult);
        dialog.setConnectButtonListener(new ConnectToWifiDialog.DialogListener() {
            @Override
            public void onConnectClicked(ScanResult scanResult, String password) {
                connectToWifiAccessPoint(scanResult, password);
            }
        });
        dialog.show();
    }

    @Override
    public void connectToWifiAccessPoint(final ScanResult scanResult, String password) {
        this.wifiConnector.setScanResult(scanResult, password);
        this.wifiConnector.setLog(true);
        this.wifiConnector.connectToWifi(new ConnectionResultListener() {
            @Override
            public void successfulConnect(String SSID) {
                Toast.makeText(ServiceProgramActivity.this, "Te has conectado a " + scanResult.SSID + "!!", Toast.LENGTH_SHORT).show();
                rlMensajeWifi.setVisibility(View.INVISIBLE);
                conectado = true;
                setLocationPermission();
                createWifiConnectorObject();
            }

            @Override
            public void errorConnect(int codeReason) {
                Toast.makeText(ServiceProgramActivity.this, "Error al conectarse al Wi-Fi: " + scanResult.SSID +"\nError: "+ codeReason,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStateChange(SupplicantState supplicantState) {

            }
        });
    }

    @Override
    public void disconnectFromAccessPoint(ScanResult scanResult) {
        this.wifiConnector.removeWifiNetwork(scanResult, new RemoveWifiListener() {
            @Override
            public void onWifiNetworkRemoved() {
                Toast.makeText(ServiceProgramActivity.this, "Has eliminado la red Wi-Fi acutal!", Toast.LENGTH_SHORT).show();
                rlMensajeWifi.setVisibility(View.VISIBLE);
                conectado = true;
                setLocationPermission();
                createWifiConnectorObject();
            }

            @Override
            public void onWifiNetworkRemoveError() {
                Toast.makeText(ServiceProgramActivity.this, "Error al eliminar la red!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void destroyWifiConnectorListeners() {
        wifiConnector.unregisterWifiStateListener();
    }

    // region permission
    private Boolean permisionLocationOn() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    private Boolean checkLocationTurnOn() {
        boolean onLocation = true;
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionGranted) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                onLocation = false;
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog));
                dialog.setMessage("Please turn on your location");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        }
        return onLocation;
    }


}