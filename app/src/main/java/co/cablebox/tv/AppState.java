package co.cablebox.tv;

import android.content.Context;
import android.content.Intent;

import co.cablebox.tv.activity.ErrorActivity;
import co.cablebox.tv.activity.VideoPlayerActivityBox;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.socket.SocketConnection;
import co.cablebox.tv.user.User;

public class AppState {

    /*Vars*/
    private static Context appContext = null;
    private static User user = null;
    private static SocketConnection socketConnection = null;
    private static URLService urlService = null;

    /*Getters*/
    public static User getUser() {
        return user;
    }
    public static SocketConnection getSocketConnection() {
        return socketConnection;
    }
    public static URLService getUrlService() {
        if (urlService == null) urlService = new URLService();
        return urlService;
    }
    public static Context getAppContext() {
        return appContext;
    }

    /*Setters*/
    public static void setAppContext(Context appContext) {
        AppState.appContext = appContext;
    }
    public static void setUser(User user) {
        AppState.user = user;
    }
    public static void setSocketConnection(SocketConnection socketConnection) {
        AppState.socketConnection = socketConnection;
    }

    /*Transactional*/
    public static void restartSocketConnection() {
        if (socketConnection != null) socketConnection.disconnect();
        socketConnection = null;
    }

    public static void openVideoPlayer(Channels channels){
        try {
            Context context= getAppContext();
            boolean isSmartphoneMode=getUser().getDeviceType()== User.DEVICE_SMARTPHONE;

            Intent i= new Intent(context, VideoPlayerActivityBox.class);
            i.putExtra("channels", channels); //pass props to the activity
            i.putExtra("isSmartphoneMode",isSmartphoneMode); //pass props to the activity
            context.startActivity(i);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Notify an error. Then, show the Error Activity
     */
    public static void openErrorActivity(String errorType,String errorMsg){
        Context context= getAppContext();
        Intent i= new Intent(context, ErrorActivity.class);
        i.putExtra("errorType",errorType); //pass props to the activity
        i.putExtra("errorMsg",errorMsg); //pass props to the activity
        context.startActivity(i);
    }
}
