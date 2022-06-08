package co.cablebox.tv;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import co.cablebox.tv.activity.VideoPlayerActivityBox;
import co.cablebox.tv.activity.error.ErrorActivity;
import co.cablebox.tv.activity.error.TvboxErrorActivity;
import co.cablebox.tv.activity.login.LoginActivity;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.factory.AppFactory;
import co.cablebox.tv.factory.SmartphoneAppFactory;
import co.cablebox.tv.socket.SocketConnection;
import co.cablebox.tv.socket.TvboxSocketConnection;
import co.cablebox.tv.user.TvboxUser;
import co.cablebox.tv.user.User;

public class AppState {

    /*Vars*/
    private static AppFactory appFactory=new SmartphoneAppFactory();
    private static Context appContext = null;
    private static User user = null;
    private static SocketConnection socketConnection = null;
    private static URLService urlService = null;

    /*Getters*/
    public static AppFactory getAppFactory() {
        return appFactory;
    }
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
    public static void setAppFactory(AppFactory appFactory){
        AppState.appFactory =appFactory;
        setSocketConnection(getAppFactory().getSocketConnection());
        setUser(getAppFactory().getUser());
    }

    /*Transactional*/
    public static void restartSocketConnection() {
        if (socketConnection != null) {
            socketConnection.disconnect();
            socketConnection = null;
        }
    }
}
