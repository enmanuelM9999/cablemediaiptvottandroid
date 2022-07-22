package co.cablebox.tv.user;

public interface User {
    int DEVICE_SMARTPHONE =0;
    int DEVICE_TVBOX=1;
    int DEVICE_TVBOX_SUBSCRIPTIONS=2;

    String USER_DEVICE_SMARTPHONE ="DEVICE_SMARTPHONE";
    String USER_DEVICE_TVBOX="DEVICE_TVBOX";
    String USER_DEVICE_TVBOX_SUBSCRIPTIONS="DEVICE_TVBOX_SUBSCRIPTIONS";

    String getUserId();

    void setUserId(String userId);

    int getDeviceType();

    String[] getUserCredentials();

    void setUserCredentials(String[] userCredentials);

    boolean isLoggedIn();

    void resetUserCredentials();

}

