package co.cablebox.tv.utils.config.wifi.wificonnector.interfaces;

import android.net.wifi.ScanResult;

public interface WifiConnectorModel {

    void createWifiConnectorObject();

    void scanForWifiNetworks();

    void connectToWifiAccessPoint(ScanResult scanResult, String password);

    void disconnectFromAccessPoint(ScanResult scanResult);

    void destroyWifiConnectorListeners();

}
