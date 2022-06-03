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
        if (socketConn==null) {
            Toast.makeText(AppState.getAppContext(), "getSocketConn +null",Toast.LENGTH_SHORT).show();
            socketConn= new SocketConn();
        }
        else {
            Toast.makeText(AppState.getAppContext(), "getSocketConn +no null",Toast.LENGTH_SHORT).show();
        }
        return socketConn;
    }

    public static URLService getUrlService() {
        if (urlService==null) urlService= new URLService();
        return urlService;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        AppState.appContext = appContext;
    }
}
