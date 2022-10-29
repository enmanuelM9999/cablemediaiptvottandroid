package co.cablebox.tv.activity.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.URLService;

public class TvboxLoginActivity extends LoginActivity {

    @BindView(R.id.tvSerialNumber)
    TextView tvSerialNumber;

    @BindView(R.id.tvServerInfo)
    TextView tvServerInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void start(){
        /*Set GUI*/
        setContentView(R.layout.activity_tvbox_loading_channels);
        ButterKnife.bind(this);

        checkNetwork();

        onStartActivity();

        /*Active full screen*/
        setFullScreenMode();

        /*Set serialNumber in User credentials*/
        String serialNumber= getSerialNumber();
        //String serialNumber="enm12345";//---------------temp
        AppState.getUser().setUserCredentials(new String[]{serialNumber});

        /*Set serial number and server info in screen*/
        showSerialNumberAndServerInfo();

        /*Connect socket*/
        connectSocket();
    }

    private void showSerialNumberAndServerInfo(){
        showSerialNumber();
//        showServerInfo();
    }
    private void showServerInfo(){
        /*Get server info*/
        String serverInfo= AppState.getUrlService().generateAndReturnSocketUriWithoutProtocol();

        /*Set server info*/
        tvServerInfo.setText(serverInfo);
    }
    private void showSerialNumber(){
        /*Get serial number info*/
        String serialNumber= AppState.getUser().getUserId();

        /*Set serial number*/
        tvSerialNumber.setText(serialNumber);
    }

    private void connectSocket(){
        AppState.getSocketConnection().socketEmitConnect();
    }

    public void setFullScreenMode() {
        String TAG = TvboxLoginActivity.class.getName();
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static String getSerialNumber() {
//        String serialNumber;
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//
//            serialNumber = (String) get.invoke(c, "gsm.sn1");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "ril.serialnumber");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "ro.serialno");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "sys.serialnumber");
//            if (serialNumber.equals(""))
//                serialNumber = Build.SERIAL;
//
//            // If none of the methods above worked
//            /*if (serialNumber.equals(""))
//                serialNumber = null;
//            if (serialNumber.equals(Build.UNKNOWN))
//                serialNumber = null;*/
//            if (serialNumber.equals(""))
//                serialNumber = Build.getSerial();
//            if (serialNumber.equals(""))
//                serialNumber = "---------------";
//        } catch (Exception e) {
//            e.printStackTrace();
//            serialNumber = null;
//        }
//
//        return serialNumber;
//    }

    public String getSerialNumber(){
        return getMacAddr();
    }

    static String DEFAULT_MAC_ADDRESS="UNKNOWN";

    public static String getMacAddr() {
        try {
            String mac= getEthernetMacAddress();
            if(mac.equals(DEFAULT_MAC_ADDRESS)){
                mac=getWlanMacAddress();
            }
            return mac;

//            return getEthernetMacAddress();
        } catch (Exception ex) {
        }
        return DEFAULT_MAC_ADDRESS;
    }

    public static String getEthernetMacAddress() {
        String macAddress = DEFAULT_MAC_ADDRESS;
        try {
            List<NetworkInterface> allNetworkInterfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface nif : allNetworkInterfaces) {
                if (!nif.getName().equalsIgnoreCase("eth0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return macAddress;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                macAddress = res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macAddress;
    }

    public static String getWlanMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return DEFAULT_MAC_ADDRESS;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return DEFAULT_MAC_ADDRESS;
    }
}