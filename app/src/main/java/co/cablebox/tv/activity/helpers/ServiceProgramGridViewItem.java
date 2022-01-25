package co.cablebox.tv.activity.helpers;

import android.graphics.drawable.Drawable;

public class ServiceProgramGridViewItem {
    /**
     * Las siguientes constantes controlan que cada item del grid pueda hacer 1 de 2 cosas al darle clic:
     * 1. Debe abrir una aplicación instalada en android "ACTION_TYPE_START_APP"
     * 2. Debe abrir una configuración de la app cablebox "ACTION_TYPE_START_CONFIGURATION"
     */
    public static final String ACTION_TYPE_START_APP ="startApp";
    public static final String ACTION_TYPE_START_CONFIGURATION ="startConfiguration";


    /**
     * Son todos los posible valores de @action cuando @actionType es igual a "ACTION_TYPE_START_CONFIGURATION"
     *
     */
    public static final String ACTION_START_CONFIGURATION_CHANNELS ="startChannels";
    public static final String ACTION_START_CONFIGURATION_RED ="startRed";
    public static final String ACTION_START_CONFIGURATION_UPDATE ="startUpdate";
    public static final String ACTION_START_CONFIGURATION_CHANGE_IP ="startChangeIp";

    //public static final String DEFAULT_BG_COLOR ="#D92B2D2E";
    public static final String DEFAULT_BG_COLOR ="#D909162A";



    private Drawable icon;
    private String text;
    private String actionType;
    private String action;
    private String bgColor;
    private String bgColorAlpha;

    /**
     * Constructor
     * @param icon Icono de cada item del gridview
     * @param text Es el texto que se muestra con el icono
     * @param actionType Es una de las siguientes constantes "ServiceProgramGridViewItem.ACTION_TYPE_START_APP" o "ServiceProgramGridViewItem.ACTION_TYPE_START_CONFIGURATION"
     * @param action es la propia acción al darle clic a un elemento. Ejm: "openChannels", "org.videolan.lan", "openWifiConfigs", "openChangeIp"
     */
    public ServiceProgramGridViewItem(Drawable icon, String text, String actionType, String action, String bgColor, String bgColorAlpha) {
        this.icon = icon;
        this.text = text;
        this.actionType = actionType;
        this.action = action;
        this.bgColor = bgColor;
        this.bgColorAlpha = bgColorAlpha;
    }


    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getBgColorAlpha() {
        return bgColorAlpha;
    }

    public void setBgColorAlpha(String bgColorAlpha) {
        this.bgColorAlpha = bgColorAlpha;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
