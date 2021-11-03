package co.cablebox.tv.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import butterknife.Bind;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.R;
import co.cablebox.tv.bean.LiveBean;
import co.cablebox.tv.bean.MensajeBean;
import co.cablebox.tv.bean.Programa;
import co.cablebox.tv.utils.ConexionSQLiteHelper;
import co.cablebox.tv.utils.IResult;
import co.cablebox.tv.utils.MCrypt;
import co.cablebox.tv.utils.NetWorkUtils;
import co.cablebox.tv.utils.Utilidades;
import co.cablebox.tv.utils.VolleyService;

public class ChannelListActivityBox extends Activity {
    private static final String TAG = ChannelListActivityBox.class.getName();

    private static String direcPag = "51.161.73.204";
    private String numRepro = "1"; // No del canal que se esta reproduciendo

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


    //Listas de Canales y id del canal actual
        public static MensajeBean mensajeBean;
        private static LiveBean liveBean;
        public static int channelIndex = 0;

    private List<List<LiveBean.DataBean>> canalesCategorias; // Lista de canales por categorias
    private List<String> categorias; // Lista de Categorias
    private List<Programa> programasFiltrados; // Lista de programas del canal actual

    // Posiciones de seleccion de canal, categoria y programa
        private static int posCanal = 0;
        private static int posCategoria = 0;
        private static int posPrgram = 0;

    // Variables para el correcto funcionamiento del reproductor con VLC
        private SurfaceHolder surfaceHolder;
        private LibVLC libvlc = null;
        private MediaPlayer mediaPlayer = null;
        private IVLCVout ivlcVout;
        private Media media;

    /* Comprobantes para saber si la seleccion se encuentra en la lista de canales o lista  de  programas,
     * esto se hace para mantener un control de movimientos en la vista*/
        private boolean selectListChannel = false;
        private boolean selectInfoProgram = false;

    // Variables para bloquear la el tactil y barras de notificacion y navegacion
        WindowManager wmanager;
        LinearLayout llayout;

    // Variables de Interfaz
        @BindView(R.id.rl_back_cat)
        RelativeLayout rlCategorias;
        @BindView(R.id.tv_cat_name)
        TextView tvCategorias;
        @BindView(R.id.arrow_left)
        ImageView arrowLeft;
        @BindView(R.id.arrow_right)
        ImageView arrowRight;
        @BindView(R.id.iv_return)
        ImageView ivReturn;
        @BindView(R.id.lv_canales)
        ListView lvCanales;
        @BindView(R.id.channel_surface)
        SurfaceView surfaceView;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.pb_error)
        TextView pbError;
        @BindView(R.id.tv_black)
        TextView tvBlack;
        @BindView(R.id.lv_program_channel)
        GridView lvProgramChannel;
        @BindView(R.id.ll_info_programa)
        LinearLayout llInfoPrograma;
        @BindView(R.id.tv_descripcion)
        TextView tvDescrip;
        @BindView(R.id.tv_nom)
        TextView tvNombreDescrip;
        @BindView(R.id.tv_net_state)
        TextView tvNetState;
        @BindView(R.id.tv_channel_number)
        TextView tvChannelNumber;
        @BindView(R.id.rl_tv_number)
        RelativeLayout rlChannelNumber;
        @BindView(R.id.iv_mute)
        ImageView ivMute;
        @BindView(R.id.iv_control_info)
        ImageView ivControlInfo;

        //View CPU RAM
        @BindView(R.id.ll_cpu_ram)
        LinearLayout llCpuRam;
        @BindView(R.id.tv_cpu)
        TextView tvCpu;
        @BindView(R.id.tv_ram)
        TextView tvRam;

        @BindView(R.id.rl_volumenA)
        RelativeLayout rlVolumenA;
        @BindView(R.id.sb_volumenA)
        SeekBar sbVolumenA;
        /*@BindView(R.id.tv_volumen)
        TextView tvVolumen;*/

        @BindView(R.id.ll_actualizando)
        LinearLayout llActualizando;

        @BindView(R.id.tv_dia)
        TextView tvDia;


        @BindView(R.id.fondo_not)
        RelativeLayout fondoNot;
        @BindView(R.id.mensaje_not)
        TextView mensajeNot;

        private int posNot = 0;
        private TranslateAnimation animNotif;


    private NetworkReceiver networkReceiver;

    // Claves
        private static final String Q15QSFD = "55555"; // Lista de Programas instalados en el dispositivo (En el celular no funciona)
        private static final String Q12FAFD = "99999"; // Visualisar Consumo de CPU y RAM
        private String palCalve = "";
    private int delayBusNum = 3000;

    //Eventos
        private final static int CODE_SALIR_MODULO = 1;
        private final static int CODE_HIDE_BLACK = 2;
        private final static int CODE_CHANGE_BY_NUM = 3;
        private final static int CODE_HIDE_ERROR = 4;
        private final static int CODE_HIDE_PRESSED = 5;
        private final static int CODE_HIDE_VOLUMEN = 6;
        private final static int CODE_ACT_PLAN = 7;
        private final static int CODE_NOTF_VIEW = 8;

    // Variables de control de tiempo actual
        private int horaAc=0, minutoAc =0, segundoAc = 0, anioAc = 0, mesAc = 0, diaAc = 0, idProgramaActual = 0;
        private Thread iniReloj = null;
        private Runnable re;
        private String sec, min, hor;
        private boolean auxTiempo = true;

    // Variables de control y administracion de eventos
        private String numCambio = "";
        private boolean writingNum = false;
        private boolean onVolumen = false;
        private boolean controlError = false;
        private int numCurrent = 0;

        private Reproduccion reproduccion = null;
        private static boolean failNet = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_SALIR_MODULO:
                    if (palCalve.equals(Q15QSFD)) {
                        viewListApps();
                    } else if (palCalve.equals(Q12FAFD)) {
                        viewCpuRam();
                    }
                    delayBusNum = 3000;
                    palCalve = "";
                    break;

                case CODE_HIDE_BLACK:
                    tvBlack.setVisibility(View.INVISIBLE);
                    break;

                case CODE_CHANGE_BY_NUM:
                    if (numCambio != "") {
                        cambiarPorNumero(Integer.parseInt(numCambio));
                    }
                    numCambio = "";
                    rlChannelNumber.setVisibility(View.INVISIBLE);
                    writingNum = false;
                    delayBusNum = 3000;
                    break;

                case CODE_HIDE_ERROR:
                    hideNetworkInfo();
                    break;

                case CODE_HIDE_PRESSED:
                    arrowLeft.setBackground(null);
                    arrowRight.setBackground(null);
                    ivReturn.setBackground(null);
                    break;

                case CODE_HIDE_VOLUMEN:
                    if (!onVolumen){
                        rlVolumenA.setVisibility(View.INVISIBLE);
                    }
                    break;

                case CODE_ACT_PLAN:
                    ServiceProgramActivity.openLiveB(ChannelListActivityBox.this);
                    finish();
                    break;

                case CODE_NOTF_VIEW:
                    mensajeNot.setVisibility(View.INVISIBLE);
                    fondoNot.setVisibility(View.INVISIBLE);
                    setServiceMessage(mensajeBean.getData().get(0).getId_dispositivo(), mensajeBean.getData().get(0).getId_mensaje());
                    break;
            }
        }
    };

    //Socket Notificaciones
    private Socket socket;
    public static String Nickname;

    public static String IMEI = "";

    //Canal Informativo
    private String numCanalInformativo = "2";
    private String cateCanalInformativo = "informativo";

    //Timer Canal
    Timer tiempo_canal = new Timer();
    boolean nuevoCanal = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        ButterKnife.bind(this);

        /*if (!Settings.canDrawOverlays(getApplicationContext())) {
            finishAndRemoveTask();
            startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION"));
        }
        bloquearBarras();*/

        organizarCanales();
        inicio();
        initData();
        initPlayer();

        re = new RefresfClock();
        iniReloj = new Thread(re);
        iniReloj.start();

        actualizarListaCanales();
        actualizarListaProgramas();

        arrowLeft.setBackground(null);
        arrowRight.setBackground(null);
        ivReturn.setBackground(null);

        lvCanales.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //Canales
        lvCanales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //Para TvBox
                    posCanal = position;
                    numRepro = canalesCategorias.get(posCategoria).get(posCanal).getNum();
                    openChannel();

                    //Para Celular
                    /*posCanal = position;
                    numRepro = canalesCategorias.get(posCategoria).get(posCanal).getNum();
                    lvCanales.setSelection(posCanal);
                    pbError.setText("");
                    pbError.setVisibility(View.INVISIBLE);
                    changeChannel();
                    actualizarListaProgramas();*/
            }
        });

        //Activar para TvBox
        lvCanales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posCanal = i;
                numRepro = canalesCategorias.get(posCategoria).get(posCanal).getNum();
                showNewChannel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Programas
        lvProgramChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                tvDescrip.setText(programasFiltrados.get(position).getDescripcion());
                tvNombreDescrip.setText(programasFiltrados.get(position).getNombreProgram());
                llInfoPrograma.setVisibility(View.VISIBLE);
                selectInfoProgram = true;
                selectListChannel = false;
                posPrgram = position;

                //Para Celular
                ivControlInfo.setVisibility(View.VISIBLE);
            }
        });

        lvProgramChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectListChannel = false;
                selectInfoProgram = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Opciones para Celular
        arrowLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        arrowLeft.setBackground(getDrawable(R.drawable.bordes_izq_act));

                        posCategoria--;
                        if(posCategoria < 0)
                            posCategoria = categorias.size() - 1;
                        tvCategorias.setText(categorias.get(posCategoria));
                        if(tvCategorias.getText().equals("FAVORITOS")){
                            rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                        }else{
                            rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
                        }
                        actualizarListaCanales();
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowLeft.setBackground(null);
                        break;
                }
                return true;
            }

        });

        arrowRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        arrowRight.setBackground(getDrawable(R.drawable.bordes_der_act));

                        posCategoria++;
                        if(posCategoria >= categorias.size())
                            posCategoria = 0;
                        tvCategorias.setText(categorias.get(posCategoria));
                        if(tvCategorias.getText().equals("FAVORITOS")){
                            rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                        }else{
                            rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
                        }
                        actualizarListaCanales();
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowRight.setBackground(null);
                        break;
                }
                return true;
            }

        });

        ivControlInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                llInfoPrograma.setVisibility(View.INVISIBLE);
                selectInfoProgram = false;
                selectListChannel = true;

                lvProgramChannel.setSelection(posPrgram);

                ivControlInfo.setVisibility(View.INVISIBLE);
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChannel();
            }
        });

    }

    // Organiza la lista de canales por categorias y las categorias
    private void organizarCanales() {
        ArrayList<LiveBean.DataBean> canalesLiveBean = new ArrayList<>();
        for (int i = 0; i < liveBean.getData().size(); i++) {
            if(!liveBean.getData().get(i).getNum().equals(numCanalInformativo)){
                canalesLiveBean.add(liveBean.getData().get(i));
            }
        }

        categorias = new ArrayList<>();
        canalesCategorias = new ArrayList<>();

        for (int i = 0; i < canalesLiveBean.size(); i++) {
            boolean existe = false;
            for (int j = 0; j < categorias.size(); j++) {
                if (canalesLiveBean.get(i).getCategoria().equals(categorias.get(j))) {
                    existe = true;
                }
            }
            if (!existe)
                categorias.add(canalesLiveBean.get(i).getCategoria());
        }

        for (int j = 0; j < categorias.size(); j++) {
            List<LiveBean.DataBean> canales = new ArrayList<>();
            for (int i = 0; i < canalesLiveBean.size(); i++) {
                if (categorias.get(j).equals(canalesLiveBean.get(i).getCategoria())) {
                    canales.add(canalesLiveBean.get(i));
                }
            }
            canalesCategorias.add(canales);
        }

        categorias.add("TODOS");
        canalesCategorias.add(canalesLiveBean);

        categorias.add("FAVORITOS");
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(ChannelListActivityBox.this, "db_favoritos",null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor c = db.query(
                Utilidades.TABLA_FAVORITO,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        ArrayList<LiveBean.DataBean> canales = new ArrayList<>();
        System.out.println("Numero de Favoritos "+c.getCount());
        if(c.getCount() < 1){
            canalesCategorias.add(canales);
        }else{
            while(c.moveToNext()){
                String name = c.getString(c.getColumnIndex(Utilidades.CAMPO_NOMBRE));
                System.out.println("Favorito "+name);
                // Acciones...
                for(int i = 0; i < liveBean.getData().size(); i++){
                    if(name.equals(liveBean.getData().get(i).getName())){
                        canales.add(liveBean.getData().get(i));
                    }
                }
            }
            canalesCategorias.add(canales);
        }

    }

    // Actualiza en interfaz la lista de canales cada que se cambia la categoria
    private void actualizarListaCanales() {

        ArrayAdapter<LiveBean.DataBean> cheeseAdapterA = new ArrayAdapter<LiveBean.DataBean>(this,
                R.layout.lv_channel_item,
                canalesCategorias.get(posCategoria)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.lv_channel_item, null);
                }

                if(canalesCategorias.get(posCategoria).get(position).getNum().equals("0"))
                    return convertView;

                if(!canalesCategorias.get(posCategoria).get(position).getName().equals("N/D")){
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.tv_program_logo);
                    selecImg(imageView, canalesCategorias.get(posCategoria).get(position).getLogo());

                    TextView textView = (TextView) convertView.findViewById(R.id.tv_program_name);
                    textView.setText(canalesCategorias.get(posCategoria).get(position).getNum() + "    " +
                            canalesCategorias.get(posCategoria).get(position).getName());
                }else{
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.tv_program_logo);
                    imageView.setImageResource(R.drawable.ic_mantenimiento);

                    TextView textView = (TextView) convertView.findViewById(R.id.tv_program_name);
                    textView.setText(canalesCategorias.get(posCategoria).get(position).getNum() + "    No disponible");
                }
                return convertView;
            }
        };

        lvCanales.setAdapter(cheeseAdapterA);

    }

    // Actualiza la lista de programas cuando se selecciona un canal
    private void actualizarListaProgramas() {
        boolean encontradoInicio = false;
        programasFiltrados = new ArrayList<>();
        Calendar programaTope = null;

        //Filtrar programas
        for(int i = 0; i < canalesCategorias.get(posCategoria).get(posCanal).getProgramas().size(); i++){
            if(!encontradoInicio){

                Calendar currentTime = Calendar.getInstance();

                long horaActual = currentTime.getTimeInMillis();
                long horaA = liveBean.getData().get(channelIndex).getProgramas().get(i).getCalendarInit().getTimeInMillis();
                long horaB = liveBean.getData().get(channelIndex).getProgramas().get(i).getCalendarFinish().getTimeInMillis();

                if(horaActual >= horaA && horaActual <= horaB){
                    System.out.println("Agrego Programa");
                    programasFiltrados.add(canalesCategorias.get(posCategoria).get(posCanal).getProgramas().get(i));
                    idProgramaActual = i;
                    encontradoInicio = true;

                    int diasMes = currentTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int diaSiguiente = currentTime.get(Calendar.DAY_OF_MONTH) + 2;
                    if(diaSiguiente > diasMes){
                        int dif = diaSiguiente - diasMes;
                        int mes = currentTime.get(Calendar.MONTH) + 1;
                        if(mes > 12){
                            mes = 1;
                            int anio = currentTime.get(Calendar.YEAR) + 1;
                            currentTime.set(anio, mes, dif, 0, 0, 0);
                        }else{
                            currentTime.set(currentTime.get(Calendar.YEAR), mes, dif, 0, 0, 0);
                        }
                    }else{
                        currentTime.set(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), diaSiguiente, 0, 0, 0);
                    }
                    programaTope = currentTime;
                }

            }else if(encontradoInicio){
                long horaMax = programaTope.getTimeInMillis();
                long horaA = canalesCategorias.get(posCategoria).get(posCanal).getProgramas().get(i).getCalendarInit().getTimeInMillis();

                if(horaA <= horaMax)
                    programasFiltrados.add(canalesCategorias.get(posCategoria).get(posCanal).getProgramas().get(i));
            }
        }

        ArrayAdapter<Programa> cheeseAdapterA = new ArrayAdapter<Programa>(this,
                R.layout.lv_program_item,
                programasFiltrados) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.lv_program_item, null);
                }
                int h = programasFiltrados.get(position).getCalendarInit().get(Calendar.HOUR_OF_DAY);
                String hora = Integer.toString(h);
                if(h == 0)
                    hora = "00";
                int m = programasFiltrados.get(position).getCalendarInit().get(Calendar.MINUTE);
                String minuto = Integer.toString(m);
                if(m == 0)
                    minuto = "00";

                TextView textView = (TextView) convertView.findViewById(R.id.tv_program_info);
                textView.setText(hora+":"+minuto+"  "+programasFiltrados.get(position).getNombreProgram());

                ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_program_ca);
                selecImg(imageView, programasFiltrados.get(position).getClasificacion());

                return convertView;
            }
        };

        System.out.println("Programas "+programasFiltrados.size());
        if(programasFiltrados.size() <= 1){
            lvProgramChannel.setFocusable(false);
            lvCanales.setFocusable(true);
            lvCanales.setSelection(posCanal);
            selectListChannel = true;
            selectInfoProgram = false;
        }else {
            lvProgramChannel.setFocusable(true);
            lvProgramChannel.setSelection(0);
            lvCanales.setFocusable(false);
            selectListChannel = false;
            selectInfoProgram = true;
        }
        lvProgramChannel.setAdapter(cheeseAdapterA);
        auxTiempo = true;
    }

    // Cambia el canal y empieza la reproduccion del seleccionado
    private void changeChannel() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){}

        tvBlack.setVisibility(View.VISIBLE);
        tvNumber.setText(canalesCategorias.get(posCategoria).get(posCanal).getNum());

        //Celular
        //onLongClickChannel = false;

        //ivlcVout.detachViews();

        socket.emit("vivo_channel", IMEI,canalesCategorias.get(posCategoria).get(posCanal).getName(),canalesCategorias.get(posCategoria).get(posCanal).getNum(),canalesCategorias.get(posCategoria).get(posCanal).getId());
        nuevoCanal = true;
        play();
    }

    // Play a traves de Hilos
    private void play() {
        reproduccion = (Reproduccion) new Reproduccion().execute();

        /*Uri parse = Uri.parse(canalesCategorias.get(posCategoria).get(posCanal).getUrl());
        media = new Media(libvlc, parse);
        mediaPlayer.setMedia(media);
        ivlcVout.setVideoView(surfaceView);
        ivlcVout.attachViews();
        mediaPlayer.play();*/
    }

    // Buscar imagen en la drawable y actualizar imagen en la interfaz
    private void selecImg(ImageView imageView, String nombre){
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

    /* Iniciar Variables necesarias para la actividad */
    private void initData(){
        //Socket
        gson = new Gson();
        socketNoti();

        boolean encontro = false;
        for(int i = 0; i < canalesCategorias.size(); i++){
            for(int j = 0; j < canalesCategorias.get(i).size(); j++){
                if(liveBean.getData().get(channelIndex).getNum().equals(canalesCategorias.get(i).get(j).getNum())){
                    posCategoria = i;
                    posCanal = j;
                    numRepro = canalesCategorias.get(posCategoria).get(posCanal).getNum();
                    encontro = true;
                    break;
                }
            }
            if(encontro)
                break;
        }
        tvCategorias.setText(categorias.get(posCategoria));
        if(tvCategorias.getText().equals("FAVORITOS")){
            rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
        }else{
            rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
        }

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

        socket.emit("vivo_channel", IMEI,canalesCategorias.get(posCategoria).get(posCanal).getName(),canalesCategorias.get(posCategoria).get(posCanal).getNum(),canalesCategorias.get(posCategoria).get(posCanal).getId());

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
                    socket.emit("time_channel", IMEI,canalesCategorias.get(posCategoria).get(posCanal).getName());
            }
        },0,1000);*/
    }

    // Inicializa todas las variables requeridas para la reproduccion y carga el canal actual
    private void initPlayer() {
        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("-vvv");
        libvlc = new LibVLC(ChannelListActivityBox.this, options);
        surfaceHolder = surfaceView.getHolder();
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
                        if (event.getBuffering() >= 100.0f) {
                            if(!controlError || numCurrent != channelIndex){
                                pbError.setText("");
                                pbError.setVisibility(View.INVISIBLE);
                                Log.i(TAG, "onEvent: buffer success...");
                                    handler.sendEmptyMessageDelayed(CODE_HIDE_BLACK, 500);
                                numCurrent = channelIndex;
                            }else {
                                playerInterface.seekTo(0);
                                controlError = false;
                            }
                            mediaPlayer.play();
                        }
                        break;

                    case MediaPlayer.Event.Playing:
                        Log.i(TAG, "onEvent: playing...");
                        controlError = true;
                        break;

                    case MediaPlayer.Event.EncounteredError:
                        Log.i(TAG, "onEvent: error...");
                        //mediaPlayer.stop();
                        play();
                        //Toast.makeText(VideoPlayerActivity.this, "Canal Inactivo!", Toast.LENGTH_LONG).show();
                        tvBlack.setVisibility(View.VISIBLE);
                        if (!failNet){
                            pbError.setText("Problemas tecnicos en el canal");
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

                        play();

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

        media = new Media(libvlc, Uri.parse(canalesCategorias.get(posCategoria).get(posCanal).getUrl()));
        mediaPlayer.setMedia(media);

        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(surfaceView);
        ivlcVout.attachViews();
        mediaPlayer.play();
        ivlcVout.addCallback(new IVLCVout.Callback() {
            @Override
            public void onSurfacesCreated(IVLCVout vlcVout) {
                int sw = surfaceView.getWidth();
                int sh = surfaceView.getHeight();

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
    }

    // Pone una pantalla negra sobre el SurfaceView para ocultar el canal anterior  mientras carga el actual
    private void showLoading() {
        if (tvBlack.getVisibility() == View.INVISIBLE) {
            tvNumber.setText(canalesCategorias.get(posCategoria).get(posCanal).getNum());
            tvBlack.setVisibility(View.VISIBLE);
            pbError.setText("");
            pbError.setVisibility(View.INVISIBLE);
        }
    }

    // Iniciar actividad ChannelListActivity
    public static void openLive(Context context, LiveBean liveBean, String IMEI, MensajeBean mensajeBean, String direcPag) {
        ChannelListActivityBox.mensajeBean = mensajeBean;
        ChannelListActivityBox.IMEI = IMEI;
        ChannelListActivityBox.liveBean = liveBean;;
        ChannelListActivityBox.direcPag = direcPag;
        context.startActivity(new Intent(context, ChannelListActivityBox.class));
    }

    // Inicia la actividad VideoPlayerActivity con el canal elegido
    private void openChannel() {
        if (liveBean != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }catch (Exception e){}

            surfaceView.setVisibility(View.INVISIBLE);

            VideoPlayerActivityBox.openLiveC(this, liveBean, mensajeBean, IMEI,posCanal);
            finish();
        }
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


            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String message = data.getString("message");

                                System.out.println("Entra Mensaje "+message);
                                gson = new Gson();
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
                                    System.out.println("Entra Mensaje "+message);

                                    //mensajeBean.getData().add(new MensajeBean.DataBean(message));
                                    gson = new Gson();
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

        } catch (URISyntaxException e) {
            Log.d("error socket2 ", ""+e.toString());
            //e.printStackTrace();
        }

    }

    // Metodos para reconocer cuando se oprime una tecla desde controles compatibles con la TvBox
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                cerrarApp();
                break;

            case KeyEvent.KEYCODE_BACK:
                System.out.println("ATRAS!!!!");
                break;

            case KeyEvent.KEYCODE_MENU:
                showNewChannel();
                break;

            case KeyEvent.KEYCODE_J:
                changeChannel();
                actualizarListaProgramas();
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(selectInfoProgram){
                    llInfoPrograma.setVisibility(View.INVISIBLE);
                    selectInfoProgram = false;
                    selectListChannel = true;

                    if(posPrgram > programasFiltrados.size())
                        lvProgramChannel.setSelection(posPrgram);

                }else if(!selectListChannel){
                    lvProgramChannel.setFocusable(false);
                    lvCanales.setFocusable(true);
                    lvCanales.setSelection(posCanal);
                    selectListChannel = true;
                    selectInfoProgram = false;

                    ivReturn.setBackground(getDrawable(R.drawable.bordes_izq_act));
                }else{
                    arrowLeft.setBackground(getDrawable(R.drawable.bordes_izq_act));

                    posCategoria--;
                    if(posCategoria < 0)
                        posCategoria = categorias.size() - 1;
                    tvCategorias.setText(categorias.get(posCategoria));
                    if(tvCategorias.getText().equals("FAVORITOS")){
                        rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
                    }
                    actualizarListaCanales();
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(selectListChannel){
                    arrowRight.setBackground(getDrawable(R.drawable.bordes_der_act));

                    posCategoria++;
                    if(posCategoria >= categorias.size())
                        posCategoria = 0;
                    tvCategorias.setText(categorias.get(posCategoria));
                    if(tvCategorias.getText().equals("FAVORITOS")){
                        rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
                    }else{
                        rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
                    }
                    actualizarListaCanales();
                }
                break;

            case KeyEvent.KEYCODE_0:
                writingNum = true;
                canalNum("0");
                break;

            case KeyEvent.KEYCODE_1:
                writingNum = true;
                canalNum("1");
                break;

            case KeyEvent.KEYCODE_2:
                writingNum = true;
                canalNum("2");
                break;

            case KeyEvent.KEYCODE_3:
                writingNum = true;
                canalNum("3");
                break;

            case KeyEvent.KEYCODE_4:
                writingNum = true;
                canalNum("4");
                break;

            case KeyEvent.KEYCODE_5:
                writingNum = true;
                canalNum("5");
                claveExit("5");
                break;

            case KeyEvent.KEYCODE_6:
                writingNum = true;
                canalNum("6");
                break;

            case KeyEvent.KEYCODE_7:
                writingNum = true;
                canalNum("7");
                break;

            case KeyEvent.KEYCODE_8:
                writingNum = true;
                canalNum("8");
                break;

            case KeyEvent.KEYCODE_9:
                writingNum = true;
                canalNum("9");
                claveExit("9");
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
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(selectListChannel){
                    arrowLeft.setBackground(null);
                }else{
                    ivReturn.setBackground(null);
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(selectListChannel){
                    arrowRight.setBackground(null);
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

    // Al oprimir el boton BACK
    @Override
    public void onBackPressed() {
        if(selectInfoProgram){
            llInfoPrograma.setVisibility(View.INVISIBLE);
            selectInfoProgram = false;
            selectListChannel = true;

            lvProgramChannel.setSelection(posPrgram);

        }else if(!selectListChannel){
            selectInfoProgram = false;
            ivReturn.setBackground(getDrawable(R.drawable.bordes_izq_act));
            handler.sendEmptyMessageDelayed(CODE_HIDE_PRESSED, 200);

            lvProgramChannel.setFocusable(false);
            lvCanales.setFocusable(true);
            lvCanales.setSelection(posCanal);
            selectListChannel = true;
        }else{
            VideoPlayerActivityBox.openLive(this, liveBean, mensajeBean, IMEI, direcPag);
            finish();
        }
    }

    // Escribiendo el numero del canal
    private void canalNum(String num) {
        if (numCambio.length() <= 2) {
            numCambio += num;

            rlChannelNumber.setVisibility(View.VISIBLE);
            tvChannelNumber.setText(numCambio);
            handler.sendEmptyMessageDelayed(CODE_HIDE_BLACK, 300);
            handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
        }
    }

    // Metodo para cambiar canal por numero
    private void cambiarPorNumero(int i) {
        System.out.println("num A "+i);
        if (i > liveBean.getData().size() || i <= 0) {
            showNetworkInfo("No existe el canal " + i);
            handler.sendEmptyMessageDelayed(CODE_HIDE_ERROR, 2000);
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            //}
            tvBlack.setVisibility(View.VISIBLE);
            for(int j = 0; j < liveBean.getData().size(); j++){
                int num = Integer.parseInt(liveBean.getData().get(j).getNum());
                System.out.println("num "+i+" = "+num);
                if (i == num){
                    channelIndex = j;
                    break;
                }
            }

            boolean encontro = false;
            for(int ki = 0; ki < canalesCategorias.size(); ki++){
                for(int j = 0; j < canalesCategorias.get(ki).size(); j++){
                    if(liveBean.getData().get(channelIndex).getNum().equals(canalesCategorias.get(ki).get(j).getNum())){
                        posCategoria = ki;
                        posCanal = j;
                        numRepro = canalesCategorias.get(posCategoria).get(posCanal).getNum();
                        encontro = true;
                        break;
                    }
                }
                if(encontro)
                    break;
            }
            tvCategorias.setText(categorias.get(posCategoria));
            if(tvCategorias.getText().equals("FAVORITOS")){
                rlCategorias.setBackground(getDrawable(R.drawable.degradado_channel_name_favorite));
            }else{
                rlCategorias.setBackground(getDrawable(R.drawable.bordes_suave_top));
            }

            actualizarListaCanales();
            actualizarListaProgramas();

            lvProgramChannel.setFocusable(false);
            lvCanales.setFocusable(true);
            lvCanales.setSelection(posCanal);
            selectListChannel = true;
            selectInfoProgram = false;

            changeChannel();
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        if (networkReceiver == null) {
            registerNetReceiver();
        }

        //mediaPlayer.play();

        playerInterface.seekTo(0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //ivlcVout.detachViews();
        }

        cerrarApp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ivlcVout.setVideoView(surfaceView);
        //ivlcVout.attachViews(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            ivlcVout.detachViews();
            libvlc.release();
        }
        socket.disconnect();
        finish();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

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

    // Metodo para cerrar la aplicacion
    public void cerrarApp() {
        tiempo_canal.cancel();
        socket.disconnect();
        finish();

        //System.exit(0);
        //android.os.Process.killProcess(android.os.Process.myPid());
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

    private void claveExit(String letra){
        palCalve += letra;

        handler.sendEmptyMessageDelayed(CODE_SALIR_MODULO, delayBusNum);
    }

    private void viewListApps(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        Intent intent = new Intent(getApplicationContext(), AppsListActivity.class);
        startActivity(intent);
    }

    private void viewCpuRam(){
        if(llCpuRam.getVisibility() == View.INVISIBLE){
            //Mostrar CPU y RAM
            llCpuRam.setVisibility(View.VISIBLE);
            handler.postDelayed(r, 1000);
            handler.postDelayed(c, 1000);
        }else{
            //Detener
            llCpuRam.setVisibility(View.INVISIBLE);
            handler.removeCallbacks(r);
            handler.removeCallbacks(c);
        }
    }

    //Control de tiempos
    private void updateTime(){
        Calendar c = Calendar.getInstance();
        anioAc = c.get(Calendar.YEAR);
        mesAc = c.get(Calendar.MONTH);
        diaAc = c.get(Calendar.DAY_OF_MONTH);
        horaAc = c.get(Calendar.HOUR_OF_DAY);
        minutoAc = c.get(Calendar.MINUTE);
        segundoAc = c.get(Calendar.SECOND);
        setZeroClock();
    }

    private void setZeroClock(){
        if(horaAc >= 0 && horaAc <= 9){
            hor = "0";
        }else{
            hor = "";
        }

        if(minutoAc >= 0 && minutoAc <= 9){
            min = ":0";
        }else{
            min = ":";
        }

        if(segundoAc >= 0 && segundoAc <= 9){
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

                    Calendar currentTime = Calendar.getInstance();

                    long horaActual = currentTime.getTimeInMillis();
                    long horaB = canalesCategorias.get(posCategoria).get(posCanal).getProgramas().get(idProgramaActual).getCalendarFinish().getTimeInMillis();

                    if (horaActual >= horaB && auxTiempo) {
                        actualizarListaProgramas();
                        auxTiempo = false;

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
            try {
                Uri parse = Uri.parse(canalesCategorias.get(posCategoria).get(posCanal).getUrl());
                media = new Media(libvlc, parse);
                mediaPlayer.setMedia(media);
                mediaPlayer.play();
            }catch (Exception e){ }
            return media;
        }
        @Override
        protected void onPostExecute(Media media) {

        }

        @Override
        protected void onCancelled() {
            Toast.makeText(ChannelListActivityBox.this, "",
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

    public void activarBarras() {
        this.wmanager.removeViewImmediate(this.llayout);
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

    //Animaciones
    //Animacion de Notificacion
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

    private void openServiceActivity() {
        if (liveBean != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }catch (Exception e){}

            surfaceView.setVisibility(View.INVISIBLE);

            socket.disconnect();

            handler.removeMessages(CODE_SALIR_MODULO);
            handler.removeMessages(CODE_HIDE_BLACK);
            handler.removeMessages(CODE_CHANGE_BY_NUM);
            handler.removeMessages(CODE_HIDE_ERROR);
            handler.removeMessages(CODE_HIDE_PRESSED);
            handler.removeMessages(CODE_HIDE_VOLUMEN);
            handler.removeMessages(CODE_ACT_PLAN);
            handler.removeMessages(CODE_NOTF_VIEW);

            ServiceProgramActivity.openLive(ChannelListActivityBox.this);
            finish();
        }
    }

    //enmanuel

    public void showNewChannel(){
        changeChannel();
        actualizarListaProgramas();
        pbError.setText("");
        pbError.setVisibility(View.INVISIBLE);
    }

}
