package co.cablebox.tv;

public class User {
    /*Const*/
    public static final int DEVICE_SMARTPHONE =0;
    public static final int DEVICE_TVBOX=1;

    /*Vars*/
    public int deviceType;
    public  boolean isLoggedIn=false; //The user log in once, the second time the app login automatically
    public  String userId ="";


    public User() {
        deviceType=DEVICE_SMARTPHONE;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean loginMobileDevice(String user, String pass){
        return loginWithUserAndPass(user,pass);
    }

    public boolean loginTvboxDevice(String deviceId){
        return loginWithDeviceId(deviceId);
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    private boolean loginWithUserAndPass(String user, String pass){
        boolean loggedIn=false;
        isLoggedIn=loggedIn;
        return loggedIn;
    }

    private boolean loginWithDeviceId(String deviceId){
        boolean loggedIn=false;
        isLoggedIn=loggedIn;
        return loggedIn;
    }
}
