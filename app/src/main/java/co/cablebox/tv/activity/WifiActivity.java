package co.cablebox.tv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import co.cablebox.tv.R;
import co.cablebox.tv.utils.config.wifi.wificonnector.WifiConnector;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ConnectionResultListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.RemoveWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.ShowWifiListener;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiConnectorModel;
import co.cablebox.tv.utils.config.wifi.wificonnector.interfaces.WifiStateListener;

public class WifiActivity extends Activity implements WifiConnectorModel {

    // ui
    private Switch mSwitch;
    private TextView mWifiActiveTxtView;
    private RecyclerView rv;

    private WifiListRvAdapter adapter;
    private WifiConnector wifiConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        mSwitch = findViewById(R.id.wifiActivationSwitch);
        mWifiActiveTxtView = findViewById(R.id.wifiActivationTv);
        rv = findViewById(R.id.wifiRv);

        setLocationPermission();
        createWifiConnectorObject();
    }

    public static void openLive(Context context) {
        context.startActivity(new Intent(context, WifiActivity.class));
    }

    @Override
    public void onBackPressed() {
        ServiceProgramActivity.openLive(WifiActivity.this);
        finish();
    }

    @Override
    protected void onDestroy() {
        destroyWifiConnectorListeners();
        super.onDestroy();
    }

    @Override
    public void createWifiConnectorObject() {
        wifiConnector = new WifiConnector(this);
        wifiConnector.setLog(true);
        wifiConnector.registerWifiStateListener(new WifiStateListener() {
            @Override
            public void onStateChange(int wifiState) {

            }

            @Override
            public void onWifiEnabled() {
                WifiActivity.this.onWifiEnabled();
            }

            @Override
            public void onWifiEnabling() {

            }

            @Override
            public void onWifiDisabling() {

            }

            @Override
            public void onWifiDisabled() {
                WifiActivity.this.onWifiDisabled();
            }
        });

        if(wifiConnector.isWifiEnbled()){
            mSwitch.setChecked(true);
            onWifiEnabled();
        } else {
            mSwitch.setChecked(false);
            onWifiDisabled();
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiConnector.enableWifi();
                } else {
                    wifiConnector.disableWifi();
                }
            }
        });

        wifiAdapter();
    }

    private void wifiAdapter(){
        adapter = new WifiListRvAdapter(this.wifiConnector, new WifiListRvAdapter.WifiItemListener() {
            @Override
            public void onWifiItemClicked(ScanResult scanResult) {
                openConnectDialog(scanResult);
            }

            @Override
            public void onWifiItemLongClick(ScanResult scanResult) {
                disconnectFromAccessPoint(scanResult);
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
    }

    private void onWifiEnabled(){
        mWifiActiveTxtView.setText("Wi-Fi");
        if (permisionLocationOn()) {
            scanForWifiNetworks();
        } else {
            checkLocationTurnOn();
        }
    }

    private void onWifiDisabled(){
        mWifiActiveTxtView.setText("Wi-Fi");
        if(adapter != null)
            adapter.setScanResultList(new ArrayList<ScanResult>());
    }

    @Override
    public void scanForWifiNetworks() {
        wifiConnector.showWifiList(new ShowWifiListener() {
            @Override
            public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult) {
                adapter.setScanResultList(wifiScanResult);
            }

            @Override
            public void onNetworksFound(JSONArray wifiList) {

            }

            @Override
            public void errorSearchingNetworks(int errorCode) {
                Toast.makeText(WifiActivity.this, "Error al obtener lista de Wi-Fi, error: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openConnectDialog(ScanResult scanResult){
        ConnectToWifiDialog dialog = new ConnectToWifiDialog(WifiActivity.this, scanResult);
        dialog.setConnectButtonListener(new ConnectToWifiDialog.DialogListener() {
            @Override
            public void onConnectClicked(ScanResult scanResult, String password) {
                connectToWifiAccessPoint(scanResult, password);
            }
        });
        dialog.show();
    }

    @Override
    public void connectToWifiAccessPoint(final ScanResult scanResult, String password) {
        this.wifiConnector.setScanResult(scanResult, password);
        this.wifiConnector.setLog(true);
        this.wifiConnector.connectToWifi(new ConnectionResultListener() {
            @Override
            public void successfulConnect(String SSID) {
                Toast.makeText(WifiActivity.this, "Te has conectado a " + scanResult.SSID + "!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void errorConnect(int codeReason) {
                Toast.makeText(WifiActivity.this, "Error al conectarse al Wi-Fi: " + scanResult.SSID +"\nError: "+ codeReason,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStateChange(SupplicantState supplicantState) {

            }
        });
    }

    @Override
    public void disconnectFromAccessPoint(ScanResult scanResult) {
        this.wifiConnector.removeWifiNetwork(scanResult, new RemoveWifiListener() {
            @Override
            public void onWifiNetworkRemoved() {
                Toast.makeText(WifiActivity.this, "Has eliminado la red Wi-Fi acutal!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWifiNetworkRemoveError() {
                Toast.makeText(WifiActivity.this, "Error al eliminar la red!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void destroyWifiConnectorListeners() {
        wifiConnector.unregisterWifiStateListener();
    }

    // region permission
    private Boolean permisionLocationOn() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    private Boolean checkLocationTurnOn() {
        boolean onLocation = true;
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionGranted) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                onLocation = false;
                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog));
                dialog.setMessage("Please turn on your location");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        }
        return onLocation;
    }

    // endregion
}
