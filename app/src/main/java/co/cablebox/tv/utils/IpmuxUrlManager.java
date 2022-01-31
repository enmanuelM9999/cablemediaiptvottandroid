package co.cablebox.tv.utils;

import android.content.SharedPreferences;

public class IpmuxUrlManager {
    // Variable guardar Ip
    private final static String IP_KEY = "ipLista";

    public static String ipmuxProtocol = "http://";
    public static String ipmuxIP = "51.161.73.214";
    public static String ipmuxPort = "5509";
    private static String ipmuxApiPath = "/api/RestController.php";
    private static String ipmuxApksPath = "/file";


    /**
     * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort, ipmuxApiPath y construye una uri para que la app acceda al servidor ipmux y haga peticiones
     * */
    public static String generateAndReturnIpmuxApiUrl(){
        String portNotation = ":";
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxProtocol+ipmuxIP+portNotation+ipmuxPort+ipmuxApiPath;
    }

    /**
     * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort,  y construye una url que es el host de la app
     * */
    public static String generateAndReturnIpmuxApksUrl(){
        String portNotation = ":";
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxProtocol+ipmuxIP+portNotation+ipmuxPort+ipmuxApksPath;
    }

    /**
     * Método que lee las variables ipmuxProtocol, ipmuxIP, ipmuxPort,  y construye una url que es el host de la app
     * */
    public static String generateAndReturnIpmuxUrl(){
        String portNotation = ":";
        if (ipmuxPort.equals("")) portNotation="";
        return ""+ipmuxIP+portNotation+ipmuxPort;
    }

    /**
     * Método que lee una cadena de texto y devuelve un arreglo de 2 posiciones. La posición 0 corresponde a la IP y la posición 1 al puerto
     * @param ipText es una cadena de texto por ejm "51.111.111.1:3298", "ipmux.cablebox.com"
     * @return ip y puerto. Devuelve un arreglo de 2 posiciones. La posición 0 corresponde a la IP y la posición 1 al puerto
     */
    public static String [] getIpAndPortByText(String ipText){
        //valores por defecto por si no es una url válida
        String ipAndPort []={ipmuxIP,ipmuxPort};

        boolean stringHaveOnlyOnePortNotation =  countCharacter(ipText,':') == 1;
        boolean stringDontHavePortNotation =  countCharacter(ipText,':') == 0;
        if(stringHaveOnlyOnePortNotation){ //el usuario escribió la ip+puerto
            try {
                ipAndPort=ipText.trim().split(":");
            } catch (Exception e){}
        }
        else if(stringDontHavePortNotation){ //el usuario escribió el dominio sin el puerto
            ipAndPort[0]=ipText;
            ipAndPort[1]="";
        }

        return ipAndPort;
    }

    /**
     * calcular el número de veces que se repite un carácter en un String
     * @param cadena
     * @param character
     * @return
     */
    public static int countCharacter(String cadena, char character) {
        int posicion, contador = 0;
        //se busca la primera vez que aparece
        posicion = cadena.indexOf(character);
        while (posicion != -1) { //mientras se encuentre el caracter
            contador++;           //se cuenta
            //se sigue buscando a partir de la posición siguiente a la encontrada
            posicion = cadena.indexOf(character, posicion + 1);
        }
        return contador;
    }

    /*
    public static void setIpAndPortByDevice(){
        SharedPreferences sharpref = getPreferences(getBaseContext().MODE_PRIVATE);
        ipmuxIP = sharpref.getString("IP", ipmuxIP);// ipmux
        ipmuxPort = sharpref.getString("PORT", ipmuxPort);// ipmux
        BASE_URI = generateAndReturnIpmuxApiUrl();
        System.out.println("IP: "+BASE_URI);
    }

     */

}
