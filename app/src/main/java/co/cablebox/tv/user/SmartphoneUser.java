package co.cablebox.tv.user;

import co.cablebox.tv.user.User;

public class SmartphoneUser implements User {
    /*Vars*/
    public int deviceType;
    private String username;
    private String password;
    public  boolean isLoggedIn=false; //The smartphoneUser log in once, the second time the app login automatically
    public  String userId;

    public SmartphoneUser() {
        deviceType=DEVICE_SMARTPHONE;
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

    public void setUserCredentials(String[] userCredentials){
        this.username=userCredentials[0];
        this.password=userCredentials[1];
    }

    @Override
    public String[] getUserCredentials() {
        return new String[] {username,password};
    }


}
