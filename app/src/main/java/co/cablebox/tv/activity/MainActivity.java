package co.cablebox.tv.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import co.cablebox.tv.AppState;
import co.cablebox.tv.socket.SmartphoneSocketConnection;
import co.cablebox.tv.socket.TvboxSocketConnection;
import co.cablebox.tv.user.SmartphoneUser;
import co.cablebox.tv.user.TvboxUser;
import co.cablebox.tv.user.User;


public class MainActivity extends AppCompatActivity {
    static final Integer PHONESTATS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    void startAppProto(){
        /*Before this, all app permissions must be granted (Location, Wifi, Cellphone data...)*/
        AppState.restartSocketConnection();

        /*Set app context*/
        AppState.setAppContext(this);

        /*Check device type*/
        //setDeviceTypeAndUserId();

        /*Choose if show Mobile GUI or TvBox GUI
        * Only Tvbox meanwhile
        * */
        openTvboxLoadingChannelsActivity();
    }



    void startApp(){
        try {
            /*...Before this, all app permissions must be granted (Location, Wifi, Cellphone data...)*/

            AppState.restartSocketConnection();

            /*Set app context*/
            AppState.setAppContext(this);

            /*Check device type*/
            Object[] deviceTypeAndSerialNumber = getDeviceTypeAndSerialNumber();
            int deviceType= (Integer) deviceTypeAndSerialNumber[0];
            String serialNumber= (String) deviceTypeAndSerialNumber[1];

            /*If is tvbox, open TvboxLoadingChannelsActivity
             * If is smartphone, open LoginActivity
             * */
            boolean isSmartphone= deviceType== User.DEVICE_SMARTPHONE;
            boolean isTvbox= deviceType== User.DEVICE_TVBOX;


            if (isTvbox){
                AppState.setSocketConnection(new TvboxSocketConnection());
                AppState.setUser(new TvboxUser(serialNumber));
                openTvboxLoadingChannelsActivity();
            }
            else if (isSmartphone){
                AppState.setSocketConnection(new SmartphoneSocketConnection());
                AppState.setUser(new SmartphoneUser());
                openLogin();
            }
            else{
                AppState.openErrorActivity("Error","No se reconoce el tipo de dispositivo");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openLogin(){
        Intent i= new Intent(this, LoginActivity.class);
        //i.putExtra("errorType",errorType); //pass props to the activity
        startActivity(i);
    }

    private void openTvboxLoadingChannelsActivity(){
        Intent i= new Intent(this, TvboxLoadingChannelsActivity.class);
        //i.putExtra("errorType",errorType); //pass props to the activity
        startActivity(i);
    }

    /**
     * @return the device type.
     */
    private Object[] getDeviceTypeAndSerialNumber(){
        /* If the device has an @imei, that means the device is a cellphone and can use a simcard.

         *  If the device does not have imei, that means the device is a tvbox, then
         *   extract the @serialNumber of it.
         * */
        int deviceType = User.DEVICE_SMARTPHONE;
        String serialNumber="unknown";
        try {
            //Get imei from device
            String imei= checkPermissionsAndGetImei(Manifest.permission.READ_PHONE_STATE, PHONESTATS);

            if(imei == null){ //is tvbox
                deviceType= User.DEVICE_TVBOX;
                serialNumber = getSerialNumber();
            }else{ // is smartphone
                deviceType= User.DEVICE_SMARTPHONE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return new Object[]{deviceType, serialNumber};
        }
    }

    private String checkPermissionsAndGetImei(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            return getImei();
        }
        return null;
    }

    private String getImei() {
        try {
            final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Hacemos la validación de métodos, ya que el método getDeviceId() ya no se admite para android Oreo en adelante, debemos usar el método getImei()
                return telephonyManager.getImei();
            }
            else {
                return telephonyManager.getDeviceId();
            }
        }catch (Exception e){
            System.out.println(e);
        }

        try {
            String deviceId;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
            return deviceId;
        }catch (Exception e){
            System.out.println(e);
        }
        return "0000000";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getSerialNumber() {
        String serialNumber;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            /*if (serialNumber.equals(""))
                serialNumber = null;
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;*/
            if (serialNumber.equals(""))
                serialNumber = Build.getSerial();
            if (serialNumber.equals(""))
                serialNumber = "---------------";
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

}