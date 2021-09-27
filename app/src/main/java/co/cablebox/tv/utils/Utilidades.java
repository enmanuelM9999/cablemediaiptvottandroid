package co.cablebox.tv.utils;

// Base de datos favorito para SQLite
public class Utilidades {

    public static final String TABLA_FAVORITO = "favorito";
    public static final String CAMPO_NOMBRE = "nombre";

    public static String CREAR_TABLA_FAVORITO = "CREATE TABLE "+TABLA_FAVORITO+"("+CAMPO_NOMBRE+" TEXT)";

}
