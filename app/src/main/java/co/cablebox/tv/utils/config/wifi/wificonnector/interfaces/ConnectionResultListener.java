package co.cablebox.tv.utils.config.wifi.wificonnector.interfaces;

import android.net.wifi.SupplicantState;

public interface ConnectionResultListener {
    void successfulConnect(String SSID);
    void errorConnect(int codeReason);
    void onStateChange(SupplicantState supplicantState);
}