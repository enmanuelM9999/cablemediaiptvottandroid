package co.cablebox.tv.user;

public interface User {
    int DEVICE_SMARTPHONE =0;
    int DEVICE_TVBOX=1;

    String getUserId();

    void setUserId(String userId);

    int getDeviceType();

    String[] getUserCredentials();

    void setUserCredentials(String[] userCredentials);

    boolean isLoggedIn();

    void resetUserCredentials();

}

