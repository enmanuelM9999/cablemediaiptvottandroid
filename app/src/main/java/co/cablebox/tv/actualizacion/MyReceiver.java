package co.cablebox.tv.actualizacion;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;

import co.cablebox.tv.R;

public class MyReceiver extends BroadcastReceiver {

    DownloadManager myDownloadManager;
    long downloadedId;
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

            //obtener la ruta del apk descargado
            DownloadManager downloadManager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri apkUri = downloadManager.getUriForDownloadedFile(downloadedId);

            //abrir el apk descargado
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    myContext.startActivity(intent);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myContext.startActivity(intent);
                }else {
                    Toast.makeText(myContext, "File not found.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("MsjDescargar", "Se descargó sin problemas");
            handler.sendEmptyMessageDelayed(CODE_DOWNLOAD_SUCCESES, 3000);
        }
    }


    public void download (String ipmuxApksUrl, String fileName){
        //definir la url del archivo a descargar
        DownloadManager downloadmanager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(ipmuxApksUrl+"/"+fileName);

        //descargar archivo
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);//donde se guarda el archivo descargado
        downloadedId = downloadmanager.enqueue(request); //guardar el id de la descarga en una variable. Esto nos evita tener que borrar el apk del dispositivo si existe.

        //informar al usuario de la descarga con una barra de progreso
        final SeekBar mProgressBar = (SeekBar) myActivity.findViewById(R.id.sb_descarga);
        final TextView mPorcentaje = (TextView) myActivity.findViewById(R.id.tv_por_descarga);
        new Thread(new Runnable() {
            @Override public void run() {
                System.out.println("Entro");
                myDownloadManager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadedId);
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

                    //cuando finaliza la descarga, se invoca el método onReceive de esta clase
                }
            }
        }).start();
    }

    public void Descargar(String dir){
        eliminarPorExtension("/storage/emulated/0/apk/", "apk");

        //String url = "http://"+dir+"/file/CableBoxTv-Telefono.apk";
        String url = "http://"+dir+"/file/CableBoxTv-TvBox.apk";
        DownloadManager.Request myRequest;

        myDownloadManager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);

        myRequest = new DownloadManager.Request(Uri.parse(url));
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        String name = URLUtil.guessFileName(url, null, fileExtension);

        //Crear la carpeta
        File myFile = new File(Environment.getExternalStorageDirectory(), "apk");
        boolean isCreate = myFile.exists();
        if(!isCreate){
            myFile.mkdirs();
        }

        myRequest.setDestinationInExternalPublicDir("/apk", name);

        String h = myRequest.setDestinationInExternalPublicDir("/apk", name).toString();

        Log.e("Ruta_apk", h);
        Log.e("Descargar", "Ok");

        downloadedId = myDownloadManager.enqueue(myRequest);

        final SeekBar mProgressBar = (SeekBar) myActivity.findViewById(R.id.sb_descarga);
        final TextView mPorcentaje = (TextView) myActivity.findViewById(R.id.tv_por_descarga);
        new Thread(new Runnable() {
            @Override public void run() {
                System.out.println("Entro");
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadedId);
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

    public static void eliminarPorExtension(String path, String extension){
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


}
