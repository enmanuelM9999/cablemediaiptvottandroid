package co.cablebox.tv.user;

import android.content.Context;

import co.cablebox.tv.AppState;
import co.cablebox.tv.utils.StorageUtils;

public class SmartphoneUser implements User {
    /*Consts*/
    String DEFAULT_SMARTPHONE_USERNAME = "";
    String KEY_SMARTPHONE_USERNAME = "KEY_SMARTPHONE_USERNAME";

    String DEFAULT_SMARTPHONE_USER_PASSWORD = "";
    String KEY_SMARTPHONE_USER_PASSWORD = "KEY_SMARTPHONE_USER_PASSWORD";
    /*Vars*/
    public int deviceType;
    private String username;
    private String password;

    public SmartphoneUser() {
        deviceType = DEVICE_SMARTPHONE;

        /*
        * Load username and password saved in device
        * */
        Context context = AppState.getAppContext();
        String storedUserName = StorageUtils.getString(context, KEY_SMARTPHONE_USERNAME, DEFAULT_SMARTPHONE_USERNAME);
        String storedPassword = StorageUtils.getString(context, KEY_SMARTPHONE_USER_PASSWORD, DEFAULT_SMARTPHONE_USER_PASSWORD);
        this.username = storedUserName;
        this.password = storedPassword;
    }

    public String getUserId() {
        return username;
    }

    public void setUserId(String username) {
        this.username = username;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setUserCredentials(String[] userCredentials) {
        this.username = userCredentials[0];
        this.password = userCredentials[1];

        //Save user in device store
        saveUser(username,password);
    }

    @Override
    public boolean isLoggedIn() {
        boolean isLoggedIn = true;

        /*
        * If current username AND password are equals to default values, user is not logged in
        * */
        if (username.equals(DEFAULT_SMARTPHONE_USERNAME) && password.equals(DEFAULT_SMARTPHONE_USER_PASSWORD))
            isLoggedIn = false;
        return isLoggedIn;
    }

    @Override
    public void resetUserCredentials() {
        username=DEFAULT_SMARTPHONE_USERNAME;
        password=DEFAULT_SMARTPHONE_USER_PASSWORD;
        saveUser(username,password);
    }

    @Override
    public String[] getUserCredentials() {
        return new String[]{username, password};
    }

    private void saveUser(String username, String password){
        StorageUtils.setString(AppState.getAppContext(),KEY_SMARTPHONE_USERNAME,username);
        StorageUtils.setString(AppState.getAppContext(),KEY_SMARTPHONE_USER_PASSWORD,password);
    }

}
