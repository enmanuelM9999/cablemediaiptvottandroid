package co.cablebox.tv;

import android.content.Context;
import android.content.Intent;

import co.cablebox.tv.activity.MainActivity;
import co.cablebox.tv.activity.ServiceProgramActivity;
import co.cablebox.tv.activity.VideoPlayerActivityBox;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.user.User;

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
        try {
            Context context= AppState.getAppContext();
            boolean isSmartphoneMode=AppState.getUser().getDeviceType()== User.DEVICE_SMARTPHONE;

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
    public static void launchErrorActivity(String errorType, String errorMsg){
        Context context= AppState.getAppContext();
        Class<?> errorActivity= AppState.getAppFactory().getErrorActivity();

        Intent i= new Intent(context, errorActivity);
        i.putExtra("errorType",errorType); //pass props to the activity
        i.putExtra("errorMsg",errorMsg); //pass props to the activity
        context.startActivity(i);
    }

    public static void launchServiceProgramActivityAsTechnician(){
        launcheServiceProgramActivity(true);
    }

    public static void launchServiceProgramActivityAsNormalUser(){
        launcheServiceProgramActivity(false);
    }

    private static void launcheServiceProgramActivity(boolean needsImportantSettings){
        AppState.restartSocketConnection();

        Context context= AppState.getAppContext();
        Class<?> activityClass= ServiceProgramActivity.class;

        Intent i= new Intent(context, activityClass);
        ServiceProgramActivity.needsImportantSettings = needsImportantSettings;
        context.startActivity(i);
    }

    public static void launchMainActivity(){
        Context context= AppState.getAppContext();
        Intent i= new Intent(context, MainActivity.class);
        //i.putExtra("errorType",errorType); //pass props to the activity
        context.startActivity(i);
    }
}
