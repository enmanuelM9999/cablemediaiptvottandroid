package co.cablebox.tv.actualizacion;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
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

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileFilter;

import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.BuildConfig;
import co.cablebox.tv.R;
import co.cablebox.tv.ToastManager;

public class MyReceiver extends BroadcastReceiver {

    DownloadManager myDownloadManager;
    public static long downloadedId;
    IntentFilter myIntentFilter;
    static String fileNameToDownload=AppState.getUrlService().getApkName();

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
            System.out.println("---------path download id: "+apkUri);

            String fileName= fileNameToDownload;
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

//            //install with root
//            ToastManager.toast("Downloadedapk!");
//            installAPKWithRoot(file.getAbsolutePath());

            //Or intall without root
//            apkUri= Uri.parse("file://"+file.getAbsolutePath());
//            System.out.println("---------path absolute: "+apkUri);
//            installAPKWithUserPermissions(apkUri);
            installAPK(file.getAbsolutePath());


            Log.e("MsjDescargar", "Se descargó sin problemas");
            handler.sendEmptyMessageDelayed(CODE_DOWNLOAD_SUCCESES, 3000);
        }
    }

    public static void installAPKWithRootBK(String filename){
        File file = new File(filename);
        if(file.exists()){
            try {
                String command;
                command = "adb install -r " + filename;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                ToastManager.toast(e.getMessage());
            }
        }
    }

    public static void installAPKWithRoot(String filename){
        boolean appWasInstalled= RootApkInstaller.install(filename);
        ToastManager.toast("Was installed "+appWasInstalled);
    }

    public static void installAPKWithUserPermissions(Uri apkUri){
        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//                intent.setData(apkUri);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                AppState.getAppContext().startActivity(intent);
//            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                AppState.getAppContext().startActivity(intent);
//            }else {
//                Toast.makeText( AppState.getAppContext(), "File not found.", Toast.LENGTH_LONG).show();
//            }


//            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            intent.setData(apkUri);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            AppState.getAppContext().startActivity(intent);


            installAPK("");



        }catch (Exception e){ e.printStackTrace();}
    }
    static void installAPK(String PATH){
//        String PATH = " file://"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileNameToDownload;
        System.out.println("---------install APK: "+PATH);

        File file = new File(PATH);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(AppState.getAppContext(), new File(PATH)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                AppState.getAppContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error in opening the file!");
                Toast.makeText(AppState.getAppContext(),"Error instalando el paquete",Toast.LENGTH_LONG).show();
                ActivityLauncher.launchMainActivity();
            }
        }else{
            Toast.makeText(AppState.getAppContext(),"El paquete de instalación no existe",Toast.LENGTH_LONG).show();
            ActivityLauncher.launchMainActivity();
        }
    }
    static  Uri uriFromFile(Context context, File file) {
        DownloadManager downloadManager = (DownloadManager) AppState.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uriForNougatOrAbove = downloadManager.getUriForDownloadedFile(downloadedId);
//        Uri uriForNougatOrAbove=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        Uri uriForBelowNougat=Uri.fromFile(file);
        System.out.println("-----uriForNougatOrAbove "+uriForNougatOrAbove);
        System.out.println("-----uriForBelowNougat "+uriForBelowNougat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return uriForNougatOrAbove;
        } else {
            return uriForBelowNougat;
        }
    }


    public void onReceiveBK(Context context, Intent intent) {

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



    public void downloaBK (String ipmuxApksUrl, String fileName){
        System.out.println("-----------------------------------------downloading"+ipmuxApksUrl+"/"+fileName);

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

    public DownloadManager download (String ipmuxApksUrl, String fileName){
        fileNameToDownload=fileName;
        System.out.println("-----------------------------------------downloading"+ipmuxApksUrl+"/"+fileName);

        /*Delete file before download*/
//        apkUri= Uri.parse("file://"+file.getAbsolutePath());
//        System.out.println("---------path absolute: "+apkUri);
//        installAPKWithUserPermissions(apkUri);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);
        File file2= new File("file://"+file.getAbsolutePath());

        boolean exists= file.exists();
        System.out.println("-----------exists " +exists+ file.getAbsolutePath());

        boolean exists2= file2.exists();
        System.out.println("-----------exists2 " +exists2 + file2.getAbsolutePath());

        boolean deleted = file.delete();
        System.out.println("-----------was deleted " +deleted);

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
        request.addRequestHeader("Accept", "application/vnd.android.package-archive");
        downloadedId = downloadmanager.enqueue(request); //guardar el id de la descarga en una variable. Esto nos evita tener que borrar el apk del dispositivo si existe.

        //informar al usuario de la descarga con una barra de progreso
        final SeekBar mProgressBar = (SeekBar) myActivity.findViewById(R.id.sb_descarga);
        final TextView mPorcentaje = (TextView) myActivity.findViewById(R.id.tv_por_descarga);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
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

                        //cuando finaliza la descarga, se invoca el método onReceive(...) de esta clase
                    }
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }
        }).start();

        return downloadmanager;
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
