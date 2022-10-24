package co.cablebox.tv.activity.settings;

import android.graphics.drawable.Drawable;

import co.cablebox.tv.R;
import co.cablebox.tv.activity.helpers.SettingsGridViewItem;

public class TvboxSettingsActivity extends SettingsActivity{
    @Override
    public void loadMoreConfigurations(){
        Drawable icon;
        String text;
        String actionType= SettingsGridViewItem.ACTION_TYPE_START_CONFIGURATION;
        String action;
        String bgColor;
        String bgColorAlpha;

        //Use Tvbox with subscriptions style
        icon = getResources().getDrawable(R.drawable.login);
        text="Suscripción";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_TO_SUBSCRIPTIONS;
        bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));
    }
}
