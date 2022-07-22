package co.cablebox.tv.activity.settings;

import android.graphics.drawable.Drawable;

import co.cablebox.tv.R;
import co.cablebox.tv.activity.helpers.SettingsGridViewItem;

public class SubscriptionsSettingsActivity extends SettingsActivity{

    @Override
    public void loadMoreConfigurations(){
        Drawable icon;
        String text;
        String actionType= SettingsGridViewItem.ACTION_TYPE_START_CONFIGURATION;
        String action;
        String bgColor;
        String bgColorAlpha;


        //logout
        icon = getResources().getDrawable(R.drawable.settings);
        text="Cerrar sesión";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_LOGOUT;
        bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //Use Tvbox with iptv style
        icon = getResources().getDrawable(R.drawable.settings);
        text="Usar IPTV";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_TO_IPTV;
        bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

    }
}
