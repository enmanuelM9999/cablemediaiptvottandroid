package co.cablebox.tv;

import android.content.Context;
import android.widget.Toast;

public class AppState {

    public static Context appContext;

    public static User user=null;

    public static SocketConn socketConn=null;

    public static URLService urlService=null;



    public static User getUser() {
        if (user==null) user= new User();
        return user;
    }

    public static SocketConn getSocketConn() {
        if (socketConn==null) socketConn = new SocketConn();

        return socketConn;
    }

    public static URLService getUrlService() {
        if (urlService==null) urlService= new URLService();
        return urlService;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void restartSocketConn(){
        if (socketConn!=null) socketConn.disconnect();
        socketConn=null;
    }

    public static void setAppContext(Context appContext) {
        AppState.appContext = appContext;
    }
}
