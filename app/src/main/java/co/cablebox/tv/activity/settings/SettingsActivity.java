package co.cablebox.tv.activity.settings;

import static co.cablebox.tv.utils.ToolBox.convertDpToPx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.BuildConfig;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.ConnectToWifiDialog;
import co.cablebox.tv.activity.WifiListRvAdapter;
import co.cablebox.tv.activity.helpers.SettingsGridViewItem;
import co.cablebox.tv.activity.videoplayer.VideoplayerActivity;
import co.cablebox.tv.actualizacion.MyReceiver;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.bean.MensajeBean;
import co.cablebox.tv.factory.SubscriptionsAppFactory;
import co.cablebox.tv.user.User;
import co.cablebox.tv.utils.IResult;
import co.cablebox.tv.utils.MCrypt;
import co.cablebox.tv.utils.StorageUtils;
import co.cablebox.tv.utils.StreamUtils;
import co.cablebox.tv.utils.VolleyService;
import co.cablebox.tv.utils.config.wifi.wificonnector.WifiConnector;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ConnectionResultListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.RemoveWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ShowWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiConnectorModel;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiStateListener;

public abstract class SettingsActivity extends Activity implements WifiConnectorModel {

    MediaPlayer mp;

    @BindView(R.id.cablebox_title)
    TextView tvCableboxTitle;


    @BindView(R.id.btn_ok)
    Button btnOK;
    @BindView(R.id.et_ip)
    EditText etIP;
    @BindView(R.id.ll_ip_nueva)
    LinearLayout llIpNueva;


    //Panel de Numeros
    @BindView(R.id.ll_type_num)
    LinearLayout llTypeNum;
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
    @BindView(R.id.ll_screen_admin_suspension)
    LinearLayout llScreenAdminSuspension;



    private TranslateAnimation animInListWifi;
    private TranslateAnimation exitAnimListWifi;
    private TranslateAnimation animInBtnConf;
    private TranslateAnimation exitAnimBtnConf;

    //Sirve para administrar el acceso a los items del gridview
    private PackageManager packageManager;
    public List<SettingsGridViewItem> gridViewItems;
    private GridView gridView;
    ArrayAdapter<SettingsGridViewItem> gridViewAdapter;


    private Switch mSwitch;
    private TextView mWifiActiveTxtView;
    private RecyclerView rv;

    private WifiListRvAdapter wifiAdapter;
    private WifiConnector wifiConnector;
    private boolean onWifi = false;

    private static final String TAG = SettingsActivity.class.getName();

    // Resultados obtenidos del JSON enviado del servidor
    private Gson gson;
    private Channels channels;
    private MensajeBean mensajeBean;
    /**
     * Define el estado del dispositivo.
     * Ejm ('Activo','Inactivo','Suspension_administrativa','Suspension_mora','Suspension_tecnica','Finalizado','Suspension_demo')
     */
    private String deviceState="";


    // Variables para bloquear la el tactil y barras de notificacion y navegacion
        WindowManager wmanager;
        LinearLayout llayout;

    // Variable guardar Ip
        private final static String IP_KEY = "ipLista";


    // Acciones que se ejecutar si el JSON leido es correcto o no
        private final static int CODE_NETWORK_ERROR = 0;
        private final static int CODE_NETWORK_SUCCESS = 1;
        private final static int CODE_SALIR_APP = 3;
        private final static int CODE_ACT = 4;
        private final static int CODE_ACT_PLAN = 5;
        private final static int CODE_TRY_PLAYER = 6;

        // Variable que define si la activity se cargará en modo Técnico para hacer ajustes. False significa que no es un Técnico y la activity cargará normal para un usuario
        public static boolean isTechnician=false;

        // Variable que define si la activity cargará ajustes cruciales que pueden causar fallos en la app.
        public static boolean needsImportantSettings =false;

    private static int what = 0;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_NETWORK_SUCCESS:
                    break;

                case CODE_NETWORK_ERROR:
                    //Link de la lista de canales erroneo
                    //Toast.makeText(ServiceProgramActivity.this, "Error de Red!", Toast.LENGTH_LONG).show();
                    break;

                case CODE_SALIR_APP:
                    llTypeNum.setVisibility(View.INVISIBLE); //ocultar boton que muestra el panel de números
                    rlPanelNum.setVisibility(View.INVISIBLE); //ocultar panel de números
                    handler.removeMessages(CODE_ACT_PLAN);
                    if (wordKey.equals(KEY_OPEN_APP_TECHNICIAN_MODE)) {
                        turnOnTechnicianMode();
                    } else if(wordKey.equals(KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE)){
                        SettingsActivity.needsImportantSettings=true;
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

                    }
                    break;

            }
        }
    };



    private static final String KEY_OPEN_APP_TECHNICIAN_MODE = "12345";
    private static final String SH0W1M31 = "88888";
    private static final String KEY_OPEN_APP_ADVANCED_TECHNICIAN_MODE = "54321";
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

    //SocketConnection Notificaciones
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

        SettingsActivity.isTechnician=true;
        /*Recover props*/
        SettingsActivity.needsImportantSettings = getIntent().getBooleanExtra("needsImportantSettings",false);
        System.out.println("-----------needsImportantSettings"+needsImportantSettings);

        //Necesary for start apk downloading
        initDescarga();

        /*Wifi settings*/
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


        String idMsg="ID: "+AppState.getUser().getUserId();
        tvImei.setText(idMsg);
        tvImei.setVisibility(View.VISIBLE);

        //declarar las acciones de peticiones a la api
        //initVolleyCallback();



        //llActualizando.setVisibility(View.VISIBLE);
        handler.removeMessages(CODE_ACT);
        handler.sendEmptyMessageDelayed(CODE_ACT, 0);

        //socketNoti();
        funciones();

        //panel numérico para smartphones
        numberTypingOnActionTouch();

        llLoadingChannels.setVisibility(View.INVISIBLE);
        hideAllFailureScreens();


            turnOnTechnicianMode();

    }


    private void funciones(){
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

                    String myarray []=  getIpAndPortByText(etIP.getText().toString());
                    String ipmuxIP=myarray[0];
                    String ipmuxPort=myarray[1];

                    SharedPreferences sharepref = getPreferences(getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharepref.edit();
                    editor.putString("IP", ipmuxIP);
                    editor.putString("PORT", ipmuxPort);
                    editor.commit();

                    llIpNueva.setVisibility(View.INVISIBLE);
                    Toast.makeText(SettingsActivity.this, "La Ip ha cambiado", Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etIP.getWindowToken(), 0);
                }
            }
        });


    }

    /**
     * Acciones para el evento touch para todos los elementos que conforman el entorno del panel numérico
     */
    private void numberTypingOnActionTouch(){
        //boton que hace visible el panel numérico
        llTypeNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        llTypeNum.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        if (rlPanelNum.getVisibility()==View.VISIBLE){
                            rlPanelNum.setVisibility(View.INVISIBLE);
                        }
                        else{
                            rlPanelNum.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        llTypeNum.setBackground(null);
                        break;
                }
                return true;
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
    }

    /**
     *Método para decir a la app que se presionó un número
     * @return void
     */
    public void pressNumber(String number){
        claveExit(number);
    }


    private void initDescarga(){
        myReceiver = new MyReceiver(SettingsActivity.this);
        myReceiver.Registrar(myReceiver);
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



    // Metodo para cerrar la aplicacion
    public void closeApp() {
        //myReceiver.borrarRegistro(myReceiver);
        destroyWifiConnectorListeners();
        turnOffTechnicianMode();
        SettingsActivity.needsImportantSettings =false;
        canShowFailureScreens=false;
        removeAllHandlerMessages();
        hideAllFailureScreens();
        isUpdatingApp =false;
        unregisterReceiver();
        finish();
    }

    private void unregisterReceiver() {
        myReceiver.borrarRegistro(myReceiver);
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
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

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
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void openLiveB(Context context) {
        SettingsActivity.camPlan = true;
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    /**
     * Inicia la Actividad ServiceProgramActivity como un técnico/technician
     * @param context
     */
    public static void openLiveC(Context context) {
        SettingsActivity.isTechnician = true;
        context.startActivity(new Intent(context, SettingsActivity.class));
    }


    // Acumulado de numeros y comprobar claves
    private void claveExit(String letra) {
        wordKey += letra;

        handler.removeMessages(CODE_ACT_PLAN);
        handler.removeMessages(CODE_SALIR_APP);
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
                    onWifi=true;
                    rv.requestFocus();
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
                    onWifi=false;
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
                SettingsActivity.this.onWifiEnabled();
            }

            @Override
            public void onWifiEnabling() {

            }

            @Override
            public void onWifiDisabling() {

            }

            @Override
            public void onWifiDisabled() {
                SettingsActivity.this.onWifiDisabled();
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
        ConnectToWifiDialog dialog = new ConnectToWifiDialog(SettingsActivity.this, scanResult);
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
                Toast.makeText(SettingsActivity.this, "Te has conectado a " + scanResult.SSID + "!!", Toast.LENGTH_SHORT).show();
                rlMensajeWifi.setVisibility(View.INVISIBLE);
                conectado = true;
                setLocationPermission();
                createWifiConnectorObject();
            }

            @Override
            public void errorConnect(int codeReason) {
                Toast.makeText(SettingsActivity.this, "Error al conectarse al Wi-Fi: " + scanResult.SSID +"\nError: "+ codeReason,
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
                Toast.makeText(SettingsActivity.this, "Has eliminado la red Wi-Fi acutal!", Toast.LENGTH_SHORT).show();
                rlMensajeWifi.setVisibility(View.VISIBLE);
                conectado = true;
                setLocationPermission();
                createWifiConnectorObject();
            }

            @Override
            public void onWifiNetworkRemoveError() {
                Toast.makeText(SettingsActivity.this, "Error al eliminar la red!", Toast.LENGTH_SHORT).show();
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


    public void removeAllHandlerMessages(){
        handler.removeMessages(CODE_ACT_PLAN);
        handler.removeMessages(CODE_TRY_PLAYER);
        handler.removeMessages(CODE_ACT);
        handler.removeMessages(CODE_NETWORK_SUCCESS);
        handler.removeMessages(CODE_NETWORK_ERROR);
        handler.removeMessages(CODE_SALIR_APP);
    }

    public void hideAllFailureScreens(){
        llScreenNochannels.setVisibility(View.INVISIBLE);
        llScreenNonet.setVisibility(View.INVISIBLE);
        llScreenTechnicalSuspension.setVisibility(View.INVISIBLE);
        llScreenSuspendedForPayment.setVisibility(View.INVISIBLE);
        llScreenDemoExpired.setVisibility(View.INVISIBLE);
        llScreenGenericFailure.setVisibility(View.INVISIBLE);
        llScreenAdminSuspension.setVisibility(View.INVISIBLE);
    }

    public void showGenericFailureScreen(){
        hideAllFailureScreens();
        llScreenGenericFailure.setVisibility(View.VISIBLE);
    }

    public void showNonetScreen(){
        hideAllFailureScreens();
        llScreenNonet.setVisibility(View.VISIBLE);
    }
    public void showAdminScreen(){
        hideAllFailureScreens();
        llScreenAdminSuspension.setVisibility(View.VISIBLE);
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

    private String getDeviceState(){
        return deviceState;
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
        //evitar que se sigan mostrando las pantallas de error "no canales" y "sin conexión"
        canShowFailureScreens=false;
        //dejar de intentar reproducir canales
        handler.removeMessages(CODE_TRY_PLAYER);
        //funciones de botones
        funciones();
        //habilitar el gridview
        loadItemsToGridView();
        //tech mode
        isTechnician=true;
    }

    public void turnOffTechnicianMode(){
        //informacion del usuario
        setUserInfo();
        //deshabilitar los toques del gridview y del menu de wifi
        try {
            gridView.setAdapter(null);
            hideWifiPanel();
        }catch(Exception e){}
        //tech mode
        isTechnician=false;
    }





    /**
     * Método que lee una cadena de texto y devuelve un arreglo de 2 posiciones. La posición 0 corresponde a la IP y la posición 1 al puerto
     * @param ipText es una cadena de texto por ejm "51.111.111.1:3298", "ipmux.cablebox.com"
     * @return ip y puerto
     */
    public String [] getIpAndPortByText(String ipText){
        //valores por defecto por si no es una url válida
        String defaultIp= AppState.getUrlService().getSocketIP();
        String defaultPort= AppState.getUrlService().getSocketPort();
        String ipAndPort []={defaultIp,defaultPort};

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

    /**
     * Método principal que carga el gridview
     */
    private void loadItemsToGridView(){
        packageManager =getPackageManager();
        gridViewItems= new ArrayList<>();
        gridView = findViewById(R.id.grid_view);

        /*Define all items for grid view*/
        loadConfigurationsToArrayList();
        loadSettingsToArrayList();
        loadAppsToArrayList();

        /*Set items*/
        loadAllArrayListsToGridView();
        loadGridViewListeners();
        if (!SettingsActivity.needsImportantSettings){
            gridView.setNumColumns(3);
            gridView.setHorizontalSpacing(convertDpToPx(40,this));
            gridView.setPadding(convertDpToPx(50,this),convertDpToPx(0,this),convertDpToPx(50,this),convertDpToPx(0,this));
        }

    }

    /**
     * Ver loadItemsToGridView()
     */
    public void loadConfigurationsToArrayList(){
        Drawable icon;
        String text;
        String actionType= SettingsGridViewItem.ACTION_TYPE_START_CONFIGURATION;
        String action;
        String bgColor;
        String bgColorAlpha;

        //verCanales
        icon = getResources().getDrawable(R.drawable.watch_tv_3d);
        text="Ver canales";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANNELS;
        bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //net
        icon = getResources().getDrawable(R.drawable.wifi);
        text="Red";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_RED;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //actualizar
        icon = getResources().getDrawable(R.drawable.download);
        text="Actualizar";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_UPDATE;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //fix para saber si la activity necesita mostrar ajustes importantes o no. Los ajustes importantes o delicados, son los que pueden causar un mal funcionamiento de la app si no se saben usar
        if (SettingsActivity.needsImportantSettings){
            //cambiarIp
            icon = getResources().getDrawable(R.drawable.icon_ip_3d);
            text="Cambiar IP";
            action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_IP;
            gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));
        }

        loadMoreConfigurations();


    }

    public void loadMoreConfigurations(){

    }

    /**
     * Ver loadItemsToGridView()
     */
    private void loadSettingsToArrayList(){

        if (SettingsActivity.needsImportantSettings){
            Drawable icon= getResources().getDrawable(R.drawable.settings);
            String text= "Ajustes";
            String actionType= SettingsGridViewItem.ACTION_TYPE_START_SETTINGS;
            String action =Settings.ACTION_SETTINGS;
            String bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
            String bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;

            SettingsGridViewItem item= new SettingsGridViewItem(icon,text,actionType,action, bgColor,bgColorAlpha);
            gridViewItems.add(item);
        }
    }

    /**
     * Ver loadItemsToGridView()
     */
    private void loadAppsToArrayList(){

        if (SettingsActivity.needsImportantSettings){
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);
            for (ResolveInfo ri: availableActivities){

                if
                (
                        ri.activityInfo.packageName.equals("tv.pluto.android") ||
                        ri.activityInfo.packageName.equals("com.anydesk.anydeskandroid") ||
                        ri.activityInfo.packageName.equals("org.videolan.vlc")
                )
                {
                    Drawable icon= ri.loadIcon(packageManager);
                    String text= ri.loadLabel(packageManager).toString();
                    String actionType= SettingsGridViewItem.ACTION_TYPE_START_APP;
                    String action =ri.activityInfo.packageName;
                    String bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
                    String bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;


                    if (ri.activityInfo.packageName.equals("tv.pluto.android")){
                        icon=getResources().getDrawable(R.drawable.pluto_tv_white);
                    }
                    if (ri.activityInfo.packageName.equals("com.anydesk.anydeskandroid")) {
                        icon=getResources().getDrawable(R.drawable.anydesk_white);
                    }
                    if (ri.activityInfo.packageName.equals("org.videolan.vlc")){
                        icon=getResources().getDrawable(R.drawable.vlc_white);
                    }


                    SettingsGridViewItem item= new SettingsGridViewItem(icon,text,actionType,action, bgColor,bgColorAlpha);
                    gridViewItems.add(item);
                }
            }
        }
    }

    /**
     *  Ver loadItemsToGridView()
     */
    private void loadAllArrayListsToGridView(){

        gridViewAdapter = new ArrayAdapter<SettingsGridViewItem>(SettingsActivity.this, R.layout.item, gridViewItems){
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
     * Cargar accines que se ejecutan al darle clic a un item del grid view.
     * Ver loadItemsToGridView()
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
                    SettingsGridViewItem theItem= gridViewItems.get(pos);
                    //obtenemos el tipo de accion del item
                    String theActionType = theItem.getActionType();

                    //si el tipo de acción es igual a iniciar configuración...
                    if (theActionType.equals(SettingsGridViewItem.ACTION_TYPE_START_CONFIGURATION)){

                        switch (theItem.getAction()){
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANNELS:
                                ActivityLauncher.launchMainActivity();
                                /*
                                turnOffTechnicianMode();
                                guaranteeOpenChannelsWithBusyWaiting();*/
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_RED:
                                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
                                    handler.removeMessages(CODE_ACT_PLAN);
                                    if(llRedes.getVisibility() == View.INVISIBLE) {
                                        showWifiPanel();
                                    }else if(llRedes.getVisibility() == View.VISIBLE){
                                        hideWifiPanel();
                                    }
                                }
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_UPDATE:
                                if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){
                                    /*
                                    handler.removeMessages(CODE_ACT_PLAN);
                                    llDescarga.setVisibility(View.VISIBLE);
                                    isUpdatingApp=true;
                                    //myReceiver.Descargar(ipmuxIP+":"+ipmuxPort);

                                    String fileName=AppState.getUrlService().getApkName();
                                    myReceiver.download(AppState.getUrlService().generateAndReturnApkDownloadUri(),fileName);
                                    * */
                                    String fileName=AppState.getUrlService().getApkName();
                                    String host=AppState.getUrlService().generateAndReturnApkDownloadUri();
                                    ActivityLauncher.launchUpdatingActivity(host,fileName);
                                }
                                else{
                                    Toast.makeText(SettingsActivity.this,"No requiere actualización",Toast.LENGTH_LONG);
                                }
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_IP:
                                showChangeIpDialog();
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_TO_SUBSCRIPTIONS:
                                StorageUtils.setString(AppState.getAppContext(),"DEVICE_TYPE",User.USER_DEVICE_TVBOX_SUBSCRIPTIONS);
                                ActivityLauncher.launchMainActivity();
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_TO_IPTV:
                                StorageUtils.setString(AppState.getAppContext(),"DEVICE_TYPE",User.USER_DEVICE_TVBOX);
                                ActivityLauncher.launchMainActivity();
                                break;
                            case SettingsGridViewItem.ACTION_START_CONFIGURATION_LOGOUT:
                                ActivityLauncher.logout();
                                break;
                        }

                    }

                    //si el tipo de acción es igual a iniciar app...
                    else if(theActionType.equals(SettingsGridViewItem.ACTION_TYPE_START_APP)){
                        Intent i = packageManager.getLaunchIntentForPackage(theItem.getAction());
                        startActivity(i);
                    }

                    //si el tipo de acción es igual a abrir ajustes del S.O. ...
                    else if(theActionType.equals(SettingsGridViewItem.ACTION_TYPE_START_SETTINGS)){
                        startActivity(new Intent(theItem.getAction()));
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
            AlertDialog.Builder builder= new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Cambiar IP");
            //builder.setMessage(""); //Mensaje además del titulo
            inputNewIp= new EditText(SettingsActivity.this);

            //Pintar la ip configurada en el EditText
            inputNewIp.setText(AppState.getUrlService().generateAndReturnSocketUriWithoutProtocol());
            inputNewIp.setMaxLines(1);
            inputNewIp.setPadding(20,10,20,10);
            builder.setView(inputNewIp);

            //Set positive button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String txt= inputNewIp.getText().toString();

                    if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

                        String myarray []=  getIpAndPortByText(txt);
                        String ipmuxIP=myarray[0];
                        String ipmuxPort=myarray[1];

                        AppState.getUrlService().setSocketIP(ipmuxIP);
                        AppState.getUrlService().setSocketPort(ipmuxPort);

                        llIpNueva.setVisibility(View.INVISIBLE);
                        Toast.makeText(SettingsActivity.this, "La Ip ha cambiado", Toast.LENGTH_SHORT).show();
                        //inicio();
                        //socketNoti();
                        //initData();

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
        //mp.start();
        //Toast.makeText(ServiceProgramActivity.this,"sound", Toast.LENGTH_SHORT).show();
    }

    private boolean appNeedUpdate(){
        //buscarVersion();
        return !"3.11".equals("3.11");
    }

    private void generateToken(){
        mVolleyServiceTK = new VolleyService(mResultCallbackTK,this);
    }

    private void setUserInfo(){
        //tvInfoImei.setText(imeiMsg);
        //tvInfoServer.setText("Server: "+AppState.getUrlService().generateAndReturnSocketUriWithoutProtocol());
    }

    public void showLogoutDialog(){

        AlertDialog.Builder builder= new AlertDialog.Builder(AppState.getAppContext());
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

        //dont add in smartphone
        ad.getWindow().setLayout(300, 180); //Controlling width and height.
    }





}