package co.cablebox.tv;

import android.content.Context;
import android.content.Intent;

import co.cablebox.tv.activity.MainActivity;
import co.cablebox.tv.activity.VideoPlayerActivityBox;
import co.cablebox.tv.activity.settings.SettingsActivity;
import co.cablebox.tv.activity.updating.UpdatingActivity;
import co.cablebox.tv.activity.videoplayer.VideoplayerActivity;
import co.cablebox.tv.bean.Channels;

public class ActivityLauncher {

    public static void launchLoginActivity(){
        try {
            Context context= AppState.getAppContext();
            Class<?> loginActivity = AppState.getAppFactory().getLoginActivity();

            Intent i= new Intent(context, loginActivity);
            context.startActivity(i);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void launchVideoPlayer(Channels channels){
        launchVideoPlayer( channels, false);
    }

    public static void launchVideoPlayer(Channels channels,boolean checkIfMustUpdate){
        try {
            Context context= AppState.getAppContext();
            Class<?> playerActivity= AppState.getAppFactory().getVideoPlayerActivity();

            Intent i= new Intent(context, playerActivity);
            i.putExtra("channels", channels); //pass props to the activity
            i.putExtra("checkIfMustUpdate",checkIfMustUpdate);
            context.startActivity(i);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Notify an error. Then, show the Error Activity
     */
    public static void launchErrorActivity(String errorType, String errorMsg){
        VideoplayerActivity.canCloseSocketConnectionPauseVideoPlayer=false;

        Context context= AppState.getAppContext();
        Class<?> errorActivity= AppState.getAppFactory().getErrorActivity();

        Intent i= new Intent(context, errorActivity);
        i.putExtra("errorType",errorType); //pass props to the activity
        i.putExtra("errorMsg",errorMsg); //pass props to the activity
        context.startActivity(i);
    }

    public static void launchSettingsActivityAsTechnician(){
        launchSettingsActivity(true);
    }

    public static void launchSettingsActivityAsNormalUser(){
        launchSettingsActivity(false);
    }

    private static void launchSettingsActivity(boolean needsImportantSettings){
        VideoplayerActivity.canCloseSocketConnectionPauseVideoPlayer=true;

        Context context= AppState.getAppContext();
        Class<?> activityClass= AppState.getAppFactory().getSettingsActivity();

        Intent i= new Intent(context, activityClass);
        SettingsActivity.needsImportantSettings=needsImportantSettings;
        i.putExtra("needsImportantSettings",needsImportantSettings); //pass props to the activity
        System.out.println("------------putting "+needsImportantSettings);
        context.startActivity(i);
    }

    public static void launchMainActivity(){
        Context context= AppState.getAppContext();
        Intent i= new Intent(context, MainActivity.class);
        //i.putExtra("errorType",errorType); //pass props to the activity
        context.startActivity(i);
    }

    public static void launchUpdatingActivity(String host, String fileName){
        Context context= AppState.getAppContext();
        Intent i= new Intent(context, UpdatingActivity.class);
        i.putExtra("host",host); //pass props to the activity
        i.putExtra("fileName",fileName); //pass props to the activity
        context.startActivity(i);
    }

    public static void logout(){
        AppState.getUser().resetUserCredentials();
        launchMainActivity();
    }
}
