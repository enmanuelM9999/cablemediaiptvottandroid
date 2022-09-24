package co.cablebox.tv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.factory.SmartphoneAppFactory;
import co.cablebox.tv.factory.TvboxAppFactory;
import co.cablebox.tv.factory.SubscriptionsAppFactory;
import co.cablebox.tv.user.User;
import co.cablebox.tv.utils.StorageUtils;


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

    void startApp(){
        try {
            /*...Before this, all app permissions must be granted (Location, Wifi, Cellphone data...)*/

            AppState.restartSocketConnection();

            /*Set app context*/
            AppState.setAppContext(this);

            /*Check device type*/
            String deviceType= getDeviceType();

            /*Check local storage for saved app style*/
            deviceType=StorageUtils.getString(AppState.getAppContext(),"DEVICE_TYPE",deviceType);

            /*
             *Configure the app style depending on the device type
             * */
            boolean isSmartphone= deviceType.equals(User.USER_DEVICE_SMARTPHONE);
            boolean isTvbox= deviceType.equals(User.USER_DEVICE_TVBOX);
            boolean isTvboxSubscriptions= deviceType.equals(User.USER_DEVICE_TVBOX_SUBSCRIPTIONS);


            if (isTvbox){
                AppState.setAppFactory(new TvboxAppFactory());
            }
            else if (isSmartphone){
                AppState.setAppFactory(new SmartphoneAppFactory());
            }
            else if (isTvboxSubscriptions){
                AppState.setAppFactory(new SubscriptionsAppFactory());
            }
            else{
                ActivityLauncher.launchErrorActivity("Error","No se reconoce el tipo de dispositivo");
            }

//            AppState.setAppFactory(new SmartphoneAppFactory());


            openLogin();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openLogin(){
        ActivityLauncher.launchLoginActivity();
    }

    /**
     * @return the device type.
     */
    private String getDeviceType(){
        /* If the device has an @imei, that means the device is a cellphone and can use a simcard.

         *  If the device does not have imei, that means the device is a tvbox, then
         *   extract the @serialNumber of it.
         * */
        String deviceType = User.USER_DEVICE_SMARTPHONE;
        String serialNumber="unknown";
        try {
            //Get imei from device
            String imei= checkPermissionsAndGetImei(Manifest.permission.READ_PHONE_STATE, PHONESTATS);

            if(imei == null){ //is tvbox
                deviceType= User.USER_DEVICE_TVBOX;
            }else{ // is smartphone
                deviceType= User.USER_DEVICE_SMARTPHONE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return deviceType;
            //return User.DEVICE_TVBOX; //---------------temp
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
}