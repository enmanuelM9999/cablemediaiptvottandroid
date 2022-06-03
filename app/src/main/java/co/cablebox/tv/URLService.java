package co.cablebox.tv;

import android.content.SharedPreferences;

public class URLService {
    private final String portNotation=":";

    private  String ipmuxProtocol = "http://";
    private  String ipmuxIP = "51.161.73.215";
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

    /**
     * Método que lee las variables socketProtocol, socketIP, socketPort, y construye una uri para que la app acceda al servidor de sockets y haga peticiones
     * */
    public String generateAndReturnSocketUri(){
        String portNotation= this.portNotation;
        if (socketPort.equals("")) portNotation="";
        return ""+socketProtocol+socketIP+portNotation+socketPort;
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
}
