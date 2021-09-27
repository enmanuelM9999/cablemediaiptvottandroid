package co.cablebox.tv.utils.config.wifi.wificonnector.interfaces;

public interface WifiStateListener{

    void onStateChange(int wifiState);

    void onWifiEnabled();

    void onWifiEnabling();

    void onWifiDisabling();

    void onWifiDisabled();

}