package co.cablebox.tv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import butterknife.Bind;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.BuildConfig;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.helpers.ServiceProgramGridViewItem;
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

public class ServiceProgramActivity extends Activity implements WifiConnectorModel {

    MediaPlayer mp;

    @BindView(R.id.cablebox_title)
    TextView tvCableboxTitle;

    @BindView(R.id.ll_open_tech_mode)
    LinearLayout llOpenTechMode;

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

    //Pantallas de error como "no hay conexión" y "cargando canales"
    @BindView(R.id.ll_loading_channels)
    LinearLayout llLoadingChannels;
    @BindView(R.id.ll_screen_generic_failure)
    LinearLayout llScreenGenericFailure;
    @BindView(R.id.ll_screen_nonet)
    LinearLayout llScreenNonet;
    @BindView(R.id.ll_screen_nochannels)
    LinearLayout llScreenNochannels;
    @BindView(R.id.ll_screen_technical_suspension)
    LinearLayout llScreenTechnicalSuspension;
    @BindView(R.id.ll_screen_suspended_for_payment)
    LinearLayout llScreenSuspendedForPayment;
    @BindView(R.id.ll_screen_demo_expired)
    LinearLayout llScreenDemoExpired;

    /*
    * Controla si la app se está ejecutando en un teléfono o en una tablet
    * */
    private boolean isSmartphoneMode=false;

    private TranslateAnimation animInListWifi;
    private TranslateAnimation exitAnimListWifi;
    private TranslateAnimation animInBtnConf;
    private TranslateAnimation exitAnimBtnConf;

    //Sirve para administrar el acceso a los items del gridview
    private PackageManager packageManager;
    private List<ServiceProgramGridViewItem> gridViewItems;
    private GridView gridView;
    ArrayAdapter<ServiceProgramGridViewItem> gridViewAdapter;


    private Switch mSwitch;
    private TextView mWifiActiveTxtView;
    private RecyclerView rv;

    private WifiListRvAdapter wifiAdapter;
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
        private static String ipmuxProtocol = "http://";
        private static String ipmuxIP = "51.161.73.214";
        private static String ipmuxPort = "5509";
        private static String ipmuxApiPath = "/api/RestController.php";
    // Leer y obtener informacion de los canales a traves de un JSON
        private static String BASE_URI = "http://"+ ipmuxIP +":5509/api/RestController.php";
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
        private final static int CODE_TRY_PLAYER = 6;

        // Las "failure screens" son las pantallas "revisa tu conexión a internet" y "no se puede acceder a los canales"
        private final static int CODE_CAN_SHOW_FAILURE_SCREENS = 7;

        private boolean viewApp = false;

        // Variable que define si la activity se cargará en modo Técnico para hacer ajustes. False significa que no es un Técnico y la activity cargará normal para un usuario
        private static boolean isTechnician=false;

    private static int what = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_TRY_PLAYER:
                    try {
                        openChannels();

                        if (canShowFailureScreens){
                            String userStatus= getUserStatus();
                            switch (userStatus){
                                case "payment":
                                    showSuspendedForPaymentScreen();
                                    break;
                                case "demo_expired":
                                    showDemoExpiredScreen();
                                    break;
                                case "tech_suspension":
                                    showTechnicalSuspensionScreen();
                                    break;
                                default:
                                    showGenericFailureScreen();
                                    break;
                            }
                            if(!isNetDisponible()){
                                showNonetScreen();
                            }

                        }
                    }catch(Exception e){
                    }
                    handler.sendEmptyMessageDelayed(CODE_TRY_PLAYER,5000);
                    break;
                case CODE_NETWORK_SUCCESS:
                    setLiveData();
                    break;

                case CODE_NETWORK_ERROR:
                    //Link de la lista de canales erroneo
                    //Toast.makeText(ServiceProgramActivity.this, "Error de Red!", Toast.LENGTH_LONG).show();
                    break;

                case CODE_SALIR_APP:
                    handler.removeMessages(CODE_ACT_PLAN);
                    if (wordKey.equals(Q15QSFD)) {
                        turnOnTechnicianMode();
                    }
                    wordKey = "";
                    break;

                case CODE_ACT:
                    llActualizando.setVisibility(View.INVISIBLE);
                    isUpdatingApp = false;
                    setButtonsState(true);

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
                case CODE_CAN_SHOW_FAILURE_SCREENS:
                    canShowFailureScreens=true;
                    break;
            }
        }
    };

    private static final String Q15QSFD = "12345";
    private static final String SH0W1M31 = "88888";
    private String wordKey = "";
    private int delayBusNum = 3000;

    //Actualizar APK
    MyReceiver myReceiver;
    private static String versionLocal;
    private static String versionServer;
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
        public boolean isUpdatingApp = true;

    private boolean conectado = false;

    //Socket Notificaciones
        private Socket socket;
        public static String Nickname;

        public static String IMEI = "";

    //notifications control
    public boolean canShowFailureScreens = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void start(){
        setContentView(R.layout.activity_service_list);
        ButterKnife.bind(this);

        setFontOnTitle(); //Fix para que el título tenga una fuente personalizada

        SharedPreferences sharpref = getPreferences(getBaseContext().MODE_PRIVATE);
        ipmuxIP = sharpref.getString("IP", ipmuxIP);// ipmux
        ipmuxPort = sharpref.getString("PORT", ipmuxPort);// ipmux
        BASE_URI = generateAndReturnIpmuxApiUrl();
        System.out.println("IP: "+BASE_URI);

        //Descargar Apk
        initDescarga();

        mSwitch = findViewById(R.id.wifiActivationSwitch);
        mWifiActiveTxtView = findViewById(R.id.wifiActivationTv);
        rv = findViewById(R.id.wifiRv);

        //Para Instalar APK
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        versionLocal = BuildConfig.VERSION_NAME;
        tvVersion.setText("v"+versionLocal+" - CableMEDIA® 2022");
        setLocationPermission();
        createWifiConnectorObject();

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
            isSmartphoneMode = true;
            tvImei.setText("IMEI: "+imei);
            tvImei.setVisibility(View.VISIBLE);
            //llConfigBo.setVisibility(View.VISIBLE);
            url_type = "unicast";
        }

        List<Notificaciones> list = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Notificaciones n = new Notificaciones("","Algo "+i);
            list.add(n);
        }

        //declarar las acciones de peticiones a la api
        initVolleyCallback();

        //buscar la version de la app del server
        buscarVersion();

        //llActualizando.setVisibility(View.VISIBLE);
        handler.removeMessages(CODE_ACT);
        handler.sendEmptyMessageDelayed(CODE_ACT, 0);
        if(rlMensajeWifi.getVisibility() == View.VISIBLE){
            onWifi = true;
            showWifiPanel();
        }

        //socketNoti();
        funciones();

        llLoadingChannels.setVisibility(View.INVISIBLE);
        hideAllFailureScreens();
        loadItemsToGridView();
        if(!isTechnician){ //usuario normal
            guaranteeOpenChannelsWithBusyWaiting();

        }
        else if(isTechnician){ //usuario técnico
            hideWifiPanel();
        }
    }
    private void inicio() {
        //generar token
        generateToken();

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
        mVolleyServiceTK.postDataVolley("POSTCALL", generateAndReturnIpmuxApiUrl(), sendObj);
    }

    //Notifaciones por Socket
    private void socketNoti(){
        //connect you socket client to the server
        try {
            Nickname = IMEI;
            System.out.println("NickSocket "+Nickname);
            socket = IO.socket("http://"+ ipmuxIP +":4010/");
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
        generateToken();
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
        mVolleyServiceVersion.postDataVolley("POSTCALL", generateAndReturnIpmuxApiUrl(), sendObjV);

    }

    private void openChannels(){
        inicio();
        if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
            handler.removeMessages(CODE_ACT_PLAN);
            String localUrl = getServerFromFile(LOCAL_URL);
            if (!TextUtils.isEmpty(localUrl)) {
                BASE_URI = localUrl;
            }
            initData();
        }
    }

    private void funciones(){

        llOpenTechMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnTechnicianMode();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

                    String myarray []=  getIpAndPortByText(etIP.getText().toString());
                    ipmuxIP=myarray[0];
                    ipmuxPort=myarray[1];

                    SharedPreferences sharepref = getPreferences(getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharepref.edit();
                    editor.putString("IP", ipmuxIP);
                    editor.putString("PORT", ipmuxPort);
                    editor.commit();
                    BASE_URI = generateAndReturnIpmuxApiUrl();

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
                    //socketNoti();
                    initData();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etIP.getWindowToken(), 0);
                }
            }
        });


    }

    private void initDescarga(){
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
                    tk=res;
                    System.out.println("Respuesta "+tk);
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
                        System.out.println("++++++++++++++++++++++++JSON by server"+s);
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
                    handler.sendEmptyMessageDelayed(what,0);
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
                        versionServer = verA[1].substring(0, verA[1].length()-4);
                        versionLocal = BuildConfig.VERSION_NAME;
                        System.out.println("Version server "+versionServer+" - Version local "+ versionLocal);

                        handler.removeMessages(CODE_ACT);
                        handler.sendEmptyMessageDelayed(CODE_ACT, 0);

                        if(!versionLocal.equals(versionLocal)){
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                hideWifiPanel();
                closeApp();
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

            case KeyEvent.KEYCODE_6:
                break;

            case KeyEvent.KEYCODE_8:
                claveExit("8");
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
                    isSmartphoneMode=true;
                    VideoPlayerActivityBox.openLive(this, liveBean, mensajeBean, imei, ipmuxIP,ipmuxPort, isSmartphoneMode);

                }else{
                    imei = getSerialNumber();
                    isSmartphoneMode=false;
                    VideoPlayerActivityBox.openLive(this, liveBean, mensajeBean, imei, ipmuxIP,ipmuxPort, isSmartphoneMode);

                }

                finish();

            }catch (Exception e){
                //Lista de canales vacia
                System.out.println("Error de direccion: "+e);
                /*
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

                 */
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
    public void closeApp() {
        myReceiver.borrarRegistro(myReceiver);
        destroyWifiConnectorListeners();
        turnOffTechnicianMode();
        canShowFailureScreens=false;
        removeAllHandlerMessages();
        hideAllFailureScreens();
        isUpdatingApp =false;
        //finish();
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
                    JSONObject sendObj = null;
                    try {
                        String code = MCrypt.bytesToHex(mc.encrypt(aux.toString()));
                        Log.d("loca>",tk);
                        sendObj = new JSONObject("{'q':'channels','tk':'"+tk+"','url_type':'"+url_type+"'}");
                        System.out.println(sendObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mVolleyServiceCH.postDataVolley("POSTCALL", generateAndReturnIpmuxApiUrl(), sendObj);

                } catch (Exception e) {
                    System.out.println("Error "+e);
                } finally {
                }
            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        if(llIpNueva.getVisibility() == View.VISIBLE) {
            llIpNueva.setVisibility(View.INVISIBLE);
        } else if(llRedes.getVisibility() == View.VISIBLE){
            hideWifiPanel();
        }
    }

    // Comprobar si la conexion esta disponible
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    // Hacer Ping, este metodo demora demasiado
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
        System.out.println("onPause service");
        closeApp();


    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume service");
        start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUpdatingApp=false;

        finish();
    }


    /**
     * Sirve para hacer clickeables los botones de la interfaz
     * @param estado
     */
    public void setButtonsState(boolean estado){
        System.out.println(estado);
        btnOK.setClickable(estado);
        btnOK.setFocusable(estado);
    }


    //Animacion de Lista de Wifi
    public void showWifiPanel() {
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

    private void hideWifiPanel() {
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
        wifiAdapter = new WifiListRvAdapter(this.wifiConnector, new WifiListRvAdapter.WifiItemListener() {
            @Override
            public void onWifiItemClicked(ScanResult scanResult) {
                openConnectDialog(scanResult);
            }

            @Override
            public void onWifiItemLongClick(ScanResult scanResult) {
                disconnectFromAccessPoint(scanResult);
            }
        });
        rv.setAdapter(wifiAdapter);
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
        if(wifiAdapter != null)
            wifiAdapter.setScanResultList(new ArrayList<ScanResult>());
    }

    @Override
    public void scanForWifiNetworks() {
        wifiConnector.showWifiList(new ShowWifiListener() {
            @Override
            public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult) {
                wifiAdapter.setScanResultList(wifiScanResult);
            }

            @Override
            public void onNetworksFound(JSONArray wifiList) {

            }

            @Override
            public void errorSearchingNetworks(int errorCode) {
                //Toast.makeText(ServiceProgramActivity.this, "Error al obtener lista de Wi-Fi, error: " + errorCode, Toast.LENGTH_SHORT).show();
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

    /** "Garantizar la apertura de canales con espera activa"
     * Método que usa la espera activa (busy waiting) para garantizar que se abra el reproductor en cuanto sea posible acceder a los canales.
     * Se pregunta cada tiempo en segs si es posible acceder a los canales (por si el cliente recién paga o por si el internet vuelve).
     */
    public void guaranteeOpenChannelsWithBusyWaiting(){

        if (isSmartphoneMode)
            llOpenTechMode.setVisibility(View.VISIBLE);

        llLoadingChannels.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(CODE_CAN_SHOW_FAILURE_SCREENS,10000);
        handler.sendEmptyMessageDelayed(CODE_TRY_PLAYER,2000);
    }

    public void removeAllHandlerMessages(){
        handler.removeMessages(CODE_ACT_PLAN);
        handler.removeMessages(CODE_TRY_PLAYER);
        handler.removeMessages(CODE_ACT);
        handler.removeMessages(CODE_NETWORK_SUCCESS);
        handler.removeMessages(CODE_NETWORK_ERROR);
        handler.removeMessages(CODE_SALIR_APP);
        handler.removeMessages(CODE_CAN_SHOW_FAILURE_SCREENS);
    }

    public void hideAllFailureScreens(){
        handler.removeMessages(CODE_CAN_SHOW_FAILURE_SCREENS);

        llScreenNochannels.setVisibility(View.INVISIBLE);
        llScreenNonet.setVisibility(View.INVISIBLE);
        llScreenTechnicalSuspension.setVisibility(View.INVISIBLE);
        llScreenSuspendedForPayment.setVisibility(View.INVISIBLE);
        llScreenDemoExpired.setVisibility(View.INVISIBLE);
        llScreenGenericFailure.setVisibility(View.INVISIBLE);
    }

    public void showGenericFailureScreen(){
        hideAllFailureScreens();
        llScreenGenericFailure.setVisibility(View.VISIBLE);
    }

    public void showNonetScreen(){
        hideAllFailureScreens();
        llScreenNonet.setVisibility(View.VISIBLE);
    }

    public void showNochannelsScreen(){
        hideAllFailureScreens();
        llScreenNonet.setVisibility(View.VISIBLE);
    }

    public void showTechnicalSuspensionScreen(){
        hideAllFailureScreens();
        llScreenTechnicalSuspension.setVisibility(View.VISIBLE);
    }

    public void showSuspendedForPaymentScreen(){
        hideAllFailureScreens();
        llScreenSuspendedForPayment.setVisibility(View.VISIBLE);
    }

    public void showDemoExpiredScreen(){
        hideAllFailureScreens();
        llScreenDemoExpired.setVisibility(View.VISIBLE);
    }

    private String getUserStatus(){
        String userStatus="inactivo";
        return userStatus;
    }

    /**
     * Permite abrir la app en modo técnico, en el cual se puede configurar la caja y abrir aplicaciones instaladas como PlutoTV, y configurar
     * parámetros como el wifi y la ip del servidor.
     */
    public void turnOnTechnicianMode(){
        //ocultar panel de cargando canales
        llLoadingChannels.setVisibility(View.INVISIBLE);
        //ocultar pantallas de error "no canales" y "sin conexión"
        hideAllFailureScreens();
        //ocultar el boton de abrir ajustes del modo smartphone
        llOpenTechMode.setVisibility(View.INVISIBLE);
        //evitar que se sigan mostrando las pantallas de error "no canales" y "sin conexión"
        canShowFailureScreens=false;
        //dejar de intentar reproducir canales
        handler.removeMessages(CODE_TRY_PLAYER);
        //esconder el panel de wifi que se expandió
        hideWifiPanel();
        onWifi=false;
        //funciones de botones
        funciones();
        //tech mode
        isTechnician=true;
    }

    public void turnOffTechnicianMode(){
        //tech mode
        isTechnician=false;
    }


    /**
    * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort, ipmuxApiPath y construye una uri para que la app acceda al servidor ipmux y haga peticiones
    * */
    public String generateAndReturnIpmuxApiUrl(){
        String portNotation = ":";
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxProtocol+ipmuxIP+portNotation+ipmuxPort+ipmuxApiPath;
    }

    /**
     * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort,  y construye una url que es el host de la app
     * */
    public String generateAndReturnIpmuxApksUrl(){
        String portNotation = ":";
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxProtocol+ipmuxIP+portNotation+ipmuxPort+"/file";
    }

    /**
     * Método que lee una cadena de texto y devuelve un arreglo de 2 posiciones. La posición 0 corresponde a la IP y la posición 1 al puerto
     * @param ipText es una cadena de texto por ejm "51.111.111.1:3298", "ipmux.cablebox.com"
     * @return ip y puerto
     */
    public String [] getIpAndPortByText(String ipText){
        //valores por defecto por si no es una url válida
        String ipAndPort []={ipmuxIP,ipmuxPort};

        boolean stringHaveOnlyOnePortNotation =  countCharacter(ipText,':') == 1;
        boolean stringDontHavePortNotation =  countCharacter(ipText,':') == 0;
        if(stringHaveOnlyOnePortNotation){ //el usuario escribió la ip+puerto
            try {
                ipAndPort=ipText.trim().split(":");
            } catch (Exception e){}
        }
        else if(stringDontHavePortNotation){ //el usuario escribió el dominio sin el puerto
            ipAndPort[0]=ipText;
            ipAndPort[1]="";
        }

        return ipAndPort;
    }

    /**
     * calcular el número de veces que se repite un carácter en un String
     * @param cadena
     * @param character
     * @return
     */
    public static int countCharacter(String cadena, char character) {
        int posicion, contador = 0;
        //se busca la primera vez que aparece
        posicion = cadena.indexOf(character);
        while (posicion != -1) { //mientras se encuentre el caracter
            contador++;           //se cuenta
            //se sigue buscando a partir de la posición siguiente a la encontrada
            posicion = cadena.indexOf(character, posicion + 1);
        }
        return contador;
    }

    private void loadItemsToGridView(){
        packageManager =getPackageManager();
        gridViewItems= new ArrayList<>();
        gridView = findViewById(R.id.grid_view);

        loadConfigurationsToArrayList();
        loadAppsToArrayList();

        loadAllArrayListsToGridView();
        loadGridViewListeners();

        mp= MediaPlayer.create(ServiceProgramActivity.this,R.raw.ui_effect);
    }

    private void loadConfigurationsToArrayList(){
        Drawable icon;
        String text;
        String actionType= ServiceProgramGridViewItem.ACTION_TYPE_START_CONFIGURATION;
        String action;
        String bgColor;
        String bgColorAlpha;

        //verCanales
        icon = getResources().getDrawable(R.drawable.house);
        text="Ver canales";
        action=ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_CHANNELS;
        bgColor=ServiceProgramGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha=ServiceProgramGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new ServiceProgramGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //net
        icon = getResources().getDrawable(R.drawable.wifi);
        text="Red";
        action=ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_RED;
        gridViewItems.add(new ServiceProgramGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //actualizar
        icon = getResources().getDrawable(R.drawable.download);
        text="Actualizar";
        action=ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_UPDATE;
        gridViewItems.add(new ServiceProgramGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //cambiarIp
        icon = getResources().getDrawable(R.drawable.icon_ip);
        text="Cambiar IP";
        action=ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_CHANGE_IP;
        gridViewItems.add(new ServiceProgramGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));


    }

    private void loadAppsToArrayList(){

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);
        for (ResolveInfo ri: availableActivities){

            if
            (   ri.activityInfo.packageName.equals("com.android.tv.settings") ||
                ri.activityInfo.packageName.equals("tv.pluto.android") ||
                ri.activityInfo.packageName.equals("com.anydesk.anydeskandroid") ||
                ri.activityInfo.packageName.equals("org.videolan.vlc")
            )
            {
                Drawable icon= ri.loadIcon(packageManager);
                String text= ri.loadLabel(packageManager).toString();
                String actionType= ServiceProgramGridViewItem.ACTION_TYPE_START_APP;
                String action =ri.activityInfo.packageName;
                String bgColor=ServiceProgramGridViewItem.DEFAULT_BG_COLOR;
                String bgColorAlpha=ServiceProgramGridViewItem.DEFAULT_BG_COLOR;


                //Cambiar el color de fondo segun la app
                if (ri.activityInfo.packageName.equals("com.android.tv.settings")){
                    icon=getResources().getDrawable(R.drawable.settings);
                }

                if (ri.activityInfo.packageName.equals("tv.pluto.android")){
                    icon=getResources().getDrawable(R.drawable.pluto_tv_white);
                   }
                if (ri.activityInfo.packageName.equals("com.anydesk.anydeskandroid")) {
                    icon=getResources().getDrawable(R.drawable.anydesk_white);
                }
                if (ri.activityInfo.packageName.equals("org.videolan.vlc")){
                    icon=getResources().getDrawable(R.drawable.vlc_white);
                }


                ServiceProgramGridViewItem item= new ServiceProgramGridViewItem(icon,text,actionType,action, bgColor,bgColorAlpha);
                gridViewItems.add(item);
            }
        }
    }

    private void loadAllArrayListsToGridView(){

        gridViewAdapter = new ArrayAdapter<ServiceProgramGridViewItem>(ServiceProgramActivity.this, R.layout.item, gridViewItems){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.row_item, null);
                    convertView.setClipToOutline(true);
                }

                View theItem = convertView.findViewById(R.id.grid_view_item);
                theItem.setBackgroundColor(Color.parseColor(gridViewItems.get(position).getBgColorAlpha()));

                ImageView appIcon = (ImageView) convertView.findViewById(R.id.image_app);
                appIcon.setImageDrawable(gridViewItems.get(position).getIcon());

                TextView appName = (TextView) convertView.findViewById(R.id.name_app);
                appName.setText(gridViewItems.get(position).getText());



                return convertView;
            }
        };
        gridView.setAdapter(gridViewAdapter);
    }


    /**
     * Cargar accines que se ejecutan al darle clic a un item del grid view
     */
    private  void loadGridViewListeners(){

        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setDefaultBackgroudInGridItems();

                float scalingFactor = 1.0f; // scale down to half the size
                //view.setScaleX(scalingFactor);
                //view.setScaleY(scalingFactor);
                view.startAnimation(getGridItemAnimation());
                //playUiSound();
                //view.setBackgroundColor(Color.parseColor(gridViewItems.get(position).getBgColor()));
                //view.setTranslationY(-15.0f);
                //view.setTranslationZ(3.0f);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                gridViewAdapter.notifyDataSetChanged(); //fix para que el gridView se pueda clickear con los dedos y mouse, no solo con el control remoto

                //********** Item clicked

                try {
                    if (isUpdatingApp) throw new Exception("La aplicación se está actualizando, no es permitido acceder a los demás ajustes");

                    //obtenemos el item clickeado
                    ServiceProgramGridViewItem theItem= gridViewItems.get(pos);
                    //obtenemos el tipo de accion del item
                    String theActionType = theItem.getActionType();

                    //si el tipo de acción es igual a iniciar configuración...
                    if (theActionType.equals(ServiceProgramGridViewItem.ACTION_TYPE_START_CONFIGURATION)){

                        switch (theItem.getAction()){
                            case ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_CHANNELS:
                                guaranteeOpenChannelsWithBusyWaiting();
                                break;
                            case ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_RED:
                                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
                                    handler.removeMessages(CODE_ACT_PLAN);
                                    if(llRedes.getVisibility() == View.INVISIBLE) {
                                        onWifi = true;
                                        showWifiPanel();
                                        rv.requestFocus();
                                    }else if(llRedes.getVisibility() == View.VISIBLE){
                                        onWifi = false;
                                        hideWifiPanel();
                                    }
                                }
                                break;
                            case ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_UPDATE:
                                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
                                    handler.removeMessages(CODE_ACT_PLAN);
                                    llDescarga.setVisibility(View.VISIBLE);
                                    isUpdatingApp=true;
                                    //myReceiver.Descargar(ipmuxIP+":"+ipmuxPort);

                                    String fileName="CableBoxTv-TvBox.apk";
                                    if (isSmartphoneMode)
                                        fileName="CableBoxTv-Smartphone.apk";
                                    myReceiver.download(generateAndReturnIpmuxApksUrl(),fileName);

                                }
                                else{
                                    Toast.makeText(ServiceProgramActivity.this,"No requiere actualización",Toast.LENGTH_LONG);
                                }
                                break;
                            case ServiceProgramGridViewItem.ACTION_START_CONFIGURATION_CHANGE_IP:
                                showChangeIpDialog();
                                break;
                        }

                    }

                    //si el tipo de acción es igual a iniciar app...
                    else if(theActionType.equals(ServiceProgramGridViewItem.ACTION_TYPE_START_APP)){
                        Intent i = packageManager.getLaunchIntentForPackage(theItem.getAction());
                        startActivity(i);
                    }
                } catch(Exception e){System.out.println(e);}




            }
        });
    }

    //
    public TranslateAnimation getGridItemAnimation() {
            TranslateAnimation anim= new TranslateAnimation(0f, 0.0f, 0f, -11.0f);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setFillAfter(true);
            anim.setDuration(500);
            return anim;
    }

    private void setDefaultBackgroudInGridItems(){
        for(int i=0; i<gridViewItems.size();i++){
            View theView= gridView.getChildAt(i);
            //theView.setBackgroundColor(Color.parseColor(gridViewItems.get(i).getBgColorAlpha()));
            theView.clearAnimation();
            float scalingFactor = 1.0f; // scale down to half the size
            theView.setScaleX(scalingFactor);
            theView.setScaleY(scalingFactor);

            //theView.setTranslationY(0.0f);
            //theView.setTranslationZ(0.0f);
        }
    }

    private void setFontOnTitle(){
        Typeface segoe;
        String fontPath="fonts/segoe_ui_bold.ttf";
        segoe= Typeface.createFromAsset(getAssets(),fontPath);
        tvCableboxTitle.setTypeface(segoe);
    }

    private void showChangeIpDialog(){
        if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
            handler.removeMessages(CODE_ACT_PLAN);

            EditText inputNewIp;
            AlertDialog.Builder builder= new AlertDialog.Builder(ServiceProgramActivity.this);
            builder.setTitle("Cambiar IP");
            //builder.setMessage(""); //Mensaje además del titulo
            inputNewIp= new EditText(ServiceProgramActivity.this);

            //Pintar la ip configurada en el EditText
            SharedPreferences sharpref = getPreferences(getBaseContext().MODE_PRIVATE);
            inputNewIp.setText(sharpref.getString("IP", ipmuxIP)+":"+sharpref.getString("PORT", ipmuxPort));
            inputNewIp.setMaxLines(1);
            inputNewIp.setPadding(20,10,20,10);
            builder.setView(inputNewIp);

            //Set positive button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String txt= inputNewIp.getText().toString();

                    if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

                        String myarray []=  getIpAndPortByText(inputNewIp.getText().toString());
                        ipmuxIP=myarray[0];
                        ipmuxPort=myarray[1];

                        SharedPreferences sharepref = getPreferences(getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharepref.edit();
                        editor.putString("IP", ipmuxIP);
                        editor.putString("PORT", ipmuxPort);
                        editor.commit();
                        BASE_URI = generateAndReturnIpmuxApiUrl();

                        PreUtils.setString(ServiceProgramActivity.this, IP_KEY, BASE_URI);

                        String localUrl = getServerFromFile(LOCAL_URL);
                        if (!TextUtils.isEmpty(localUrl)) {
                            BASE_URI = localUrl;
                        }

                        llIpNueva.setVisibility(View.INVISIBLE);
                        Toast.makeText(ServiceProgramActivity.this, "La Ip ha cambiado", Toast.LENGTH_SHORT).show();


                        inicio();
                        //socketNoti();
                        initData();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputNewIp.getWindowToken(), 0);
                    }

                }
            });

            //Set negative button
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String txt= inputNewIp.getText().toString();
                    dialog.dismiss();
                }
            });


            //Create Dialog
            AlertDialog ad= builder.create();
            ad.show();

            if (!isSmartphoneMode)
            ad.getWindow().setLayout(300, 180); //Controlling width and height.


        }
    }

    private void playUiSound(){
        /*
        int sonido;
        SoundPool sp= new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sonido=sp.load(this,R.raw.ui_effect,1);
        sp.play(sonido, 1,1,1,0,0);
         */

        //AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // For example to set the volume of played media to maximum.
        //audioManager.setStreamVolume (AudioManager.STREAM_MUSIC,  50,0);
        mp.start();
        //Toast.makeText(ServiceProgramActivity.this,"sound", Toast.LENGTH_SHORT).show();
    }

    private boolean appNeedUpdate(){
        //buscarVersion();
        return !"3.11".equals("3.11");
    }

    private void generateToken(){
        mVolleyServiceTK = new VolleyService(mResultCallbackTK,this);
    }





}