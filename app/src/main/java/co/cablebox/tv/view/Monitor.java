package co.cablebox.tv.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.cablebox.tv.activity.PermissionActivity;

public class Monitor extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, PermissionActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}