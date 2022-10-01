package co.cablebox.tv.actualizacion;

public class DtoUpdating {
    Boolean actualizar;
    String nombre_apk;
    String url_base_apk;
    String version_tvbox;

    public DtoUpdating() {
    }

    public DtoUpdating(Boolean actualizar, String nombre_apk, String url_base_apk, String version_tvbox) {
        this.actualizar = actualizar;
        this.nombre_apk = nombre_apk;
        this.url_base_apk = url_base_apk;
        this.version_tvbox = version_tvbox;
    }

    public Boolean getActualizar() {
        return actualizar;
    }

    public void setActualizar(Boolean actualizar) {
        this.actualizar = actualizar;
    }

    public String getNombre_apk() {
        return nombre_apk;
    }

    public void setNombre_apk(String nombre_apk) {
        this.nombre_apk = nombre_apk;
    }

    public String getUrl_base_apk() {
        return url_base_apk;
    }

    public void setUrl_base_apk(String url_base_apk) {
        this.url_base_apk = url_base_apk;
    }

    public String getVersion_tvbox() {
        return version_tvbox;
    }

    public void setVersion_tvbox(String version_tvbox) {
        this.version_tvbox = version_tvbox;
    }

    @Override
    public String toString() {
        return "DtoUpdating{" +
                "actualizar=" + actualizar +
                ", nombre_apk='" + nombre_apk + '\'' +
                ", url_base_apk='" + url_base_apk + '\'' +
                ", version_tvbox='" + version_tvbox + '\'' +
                '}';
    }
}
