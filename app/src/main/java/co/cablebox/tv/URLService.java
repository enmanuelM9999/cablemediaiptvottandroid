package co.cablebox.tv;

import android.content.SharedPreferences;

import co.cablebox.tv.utils.PreUtils;

public class URLService {
    private final String portNotation=":";

    private  String ipmuxProtocol = "http://";
    private  String ipmuxIP = "51.161.73.216";
    private  String ipmuxPort = "5508";
    private  String ipmuxApiPath = "/api/RestController.php";

    private  String socketProtocol = ipmuxProtocol;
    private  String socketIP = ipmuxIP;
    private  String socketPort = "4010";

    public URLService(){

    }

    public URLService(String ipmuxIP, String ipmuxPort) {
        this.ipmuxIP = ipmuxIP;
        this.ipmuxPort = ipmuxPort;
    }

    /**
     * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort, ipmuxApiPath y construye una uri para que la app acceda al servidor ipmux y haga peticiones
     * */
    public String generateAndReturnIpmuxUri(){
        String portNotation= this.portNotation;
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxProtocol+ipmuxIP+portNotation+ipmuxPort+ipmuxApiPath;
    }


    public String generateAndReturnDownloadApkUri(){
        return "http://51.161.73.217:5508/file/subidas";
    }

    /**
     * Método que lee las variables socketProtocol, socketIP, socketPort, y construye una uri para que la app acceda al servidor de sockets y haga peticiones
     * */
    public String generateAndReturnSocketUri(){
        String portNotation= this.portNotation;
        if (getSocketPort().equals("")) portNotation="";
        return ""+getSocketProtocol()+getSocketIP()+portNotation+getSocketPort();
    }

    /**
     * Método que lee las variables socketProtocol, socketIP, socketPort, y construye una uri para que la app acceda al servidor de sockets y haga peticiones
     * */
    public String generateAndReturnSocketUriWithoutProtocol(){
        String portNotation= this.portNotation;
        if (getSocketPort().equals("")) portNotation="";
        return ""+getSocketIP()+portNotation+getSocketPort();
    }

    public String getIpmuxIP() {
        return ipmuxIP;
    }

    public void setIpmuxIP(String ipmuxIP) {
        this.ipmuxIP = ipmuxIP;
    }

    public String getIpmuxPort() {
        return ipmuxPort;
    }

    public void setIpmuxPort(String ipmuxPort) {
        this.ipmuxPort = ipmuxPort;
    }

    public String getSocketIP() {
        String ip= PreUtils.getString(AppState.getAppContext(),"SOCKET_IP",this.socketIP);
        return ip;
    }

    public void setSocketIP(String socketIP) {
        PreUtils.setString(AppState.getAppContext(),"SOCKET_IP",socketIP);
        this.socketIP = socketIP;
    }

    public String getSocketPort() {
        String port= PreUtils.getString(AppState.getAppContext(),"SOCKET_PORT",this.socketPort);
        return port;
    }

    public void setSocketPort(String socketPort) {
        PreUtils.setString(AppState.getAppContext(),"SOCKET_PORT",socketPort);
        this.socketPort = socketPort;
    }

    public String getSocketProtocol() {
        return socketProtocol;
    }

    public void setSocketProtocol(String socketProtocol) {
        this.socketProtocol = socketProtocol;
    }
}
