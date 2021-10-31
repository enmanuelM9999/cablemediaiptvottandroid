package co.cablebox.tv.actualizacion;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.nkzawa.socketio.client.Url;

import java.io.File;
import java.io.FileFilter;

import butterknife.ButterKnife;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.ServiceProgramActivity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MyReceiver extends BroadcastReceiver {

    DownloadManager myDownloadManager;
    long tamano;
    IntentFilter myIntentFilter;

    private Context myContext;
    private Activity myActivity;

    private final static int CODE_DOWNLOAD_SUCCESES = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_DOWNLOAD_SUCCESES:
                    final TextView tvInfo = (TextView) myActivity.findViewById(R.id.tv_mns_info);
                    tvInfo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public MyReceiver(Activity myActivity) {
        this.myContext = myActivity;
        this.myActivity = myActivity;

        myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(tamano);

            Cursor cursor = myDownloadManager.query(query);

            if(cursor.moveToFirst()){
                int columIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                if(DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columIndex)){
                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    File file = new File(uriString);

                    System.out.println(file);

                    Intent pantallaInstall = new Intent(Intent.ACTION_VIEW);
                    pantallaInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pantallaInstall.setDataAndType(Uri.parse(uriString), "application/vnd.android.package-archive");
                    myActivity.startActivity(pantallaInstall);

                    Log.e("MsjDescargar", "Se descargo sin problemas");
                    handler.sendEmptyMessageDelayed(CODE_DOWNLOAD_SUCCESES, 3000);
                }
            }
        }
    }

    public void Descargar(String dir){
        eliminarPorExtension("/storage/emulated/0/apk/", "apk");

        //String url = "http://"+dir+":5509/file/CableBoxTv-Telefono.apk";
        String url = "http://"+dir+":5509/file/CableBoxTv-TvBox.apk";
        DownloadManager.Request myRequest;

        myDownloadManager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);

        myRequest = new DownloadManager.Request(Uri.parse(url));
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        String name = URLUtil.guessFileName(url, null, fileExtension);

        //Crear la carpeta
        File myFile = new File(Environment.getExternalStorageDirectory(), "apk");
        boolean isCreate = myFile.exists();

        if(!isCreate){
            isCreate = myFile.mkdirs();
        }

        myRequest.setDestinationInExternalPublicDir("/apk", name);

        String h = myRequest.setDestinationInExternalPublicDir("/apk", name).toString();

        Log.e("Ruta_apk", h);
        Log.e("Descargar", "Ok");

        tamano = myDownloadManager.enqueue(myRequest);

        final SeekBar mProgressBar = (SeekBar) myActivity.findViewById(R.id.sb_descarga);
        final TextView mPorcentaje = (TextView) myActivity.findViewById(R.id.tv_por_descarga);
        new Thread(new Runnable() {
            @Override public void run() {
                System.out.println("Entro");
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(tamano);
                    Cursor cursor = myDownloadManager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                    myActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            mProgressBar.setProgress((int) dl_progress);
                            mPorcentaje.setText(dl_progress+"%");
                        }
                    });

                    Log.d("Progreso", statusMessage(cursor)+" - "+dl_progress);
                    if(dl_progress == 100){
                        //estadoBotones(true);
                    }
                    cursor.close();
                }
            }
        }).start();
    }


    private String statusMessage(Cursor c) {
        String msg = "???";
        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;
            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;
            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;
            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;
            default:
                msg = "Download is nowhere in sight";
                break;
        } return (msg);
    }

    public static void eliminarPorExtension(String path, final String extension){
        File[] archivos = new File(path).listFiles(new FileFilter() {
            public boolean accept(File archivo) {
                if (archivo.isFile())
                return archivo.getName().endsWith('.' + extension);
                return false;
            }
        });
        try {
            for (File archivo : archivos)
                archivo.delete();
        }catch (Exception e){
            System.out.println(e);
        }
    }


    public void Registrar(MyReceiver myReceiver){
        myContext.registerReceiver(myReceiver, myIntentFilter);
    }

    public void borrarRegistro(MyReceiver myReceiver){
        myContext.unregisterReceiver(myReceiver);
    }

    public void estadoBotones(boolean estado){
        final Button btnWifi = (Button) myActivity.findViewById(R.id.btn_wifi);
        final Button btnApps = (Button) myActivity.findViewById(R.id.btn_apps);
        final Button btnActua = (Button) myActivity.findViewById(R.id.btn_actua);
        final Button btnCambiarIp = (Button) myActivity.findViewById(R.id.btn_cambiar_ip);
        final Button btnIniciar = (Button) myActivity.findViewById(R.id.btn_iniciar);
        final Button btnFabrica = (Button) myActivity.findViewById(R.id.btn_fabrica);
        final Button btnOK = (Button) myActivity.findViewById(R.id.btn_ok);


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
        btnOK.setClickable(estado);
        btnOK.setFocusable(estado);
    }
}
