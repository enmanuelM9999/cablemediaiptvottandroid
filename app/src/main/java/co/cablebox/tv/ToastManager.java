package co.cablebox.tv;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastManager {
    public static void toast(String text) {
        showToast(text);
        showToast(text);
    }

    private static void showToast(String text){
        Context context= AppState.getAppContext();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
