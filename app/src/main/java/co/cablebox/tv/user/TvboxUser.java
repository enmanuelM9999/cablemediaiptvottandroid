package co.cablebox.tv.user;

public class TvboxUser implements User{
    /*Vars*/
    public int deviceType;
    private String serialNumber;

    public TvboxUser() {
        deviceType=DEVICE_TVBOX;
    }

    public TvboxUser(String serialNumber) {
        deviceType=DEVICE_TVBOX;
        this.serialNumber=serialNumber;
    }

    public String getUserId() {
        return serialNumber;
    }

    public void setUserId(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getDeviceType() {
        return deviceType;
    }

    @Override
    public String[] getUserCredentials() {
        return new String[] {serialNumber};
    }

    @Override
    public void setUserCredentials(String[] userCredentials) {
        this.serialNumber= userCredentials[0];
    }

}
