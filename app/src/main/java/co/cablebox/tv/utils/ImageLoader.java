package co.cablebox.tv.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.ToastManager;

public class ImageLoader {
    /**
     *
     * @param logoName
     * @param canvas
     */
    public static void loadChannelLogo(String logoName, ImageView canvas){
        //Build url with logo "http://server.com/archivos/logo/cartoons.png"
        String remoteFolder=AppState.getUrlService().generateAndReturnChannelLogoFolder();
        logoName=remoteFolder+"/"+logoName;

        Drawable defaultChannelLogo=AppState.getAppContext().getDrawable(R.drawable.plantilla);
        Glide.with(AppState.getAppContext())
                .load(logoName)
                .error(defaultChannelLogo)
                .into(canvas);
    }
}
