package co.cablebox.tv.bean;

import java.util.ArrayList;
import java.util.List;

public class MensajeBean {

    private ArrayList<DataBean> data = new ArrayList<>();

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int id;
        private int id_dispositivo;
        private int id_mensaje;
        private boolean visto;
        private String texto;

        public DataBean(String txt){
            id = 0;
            id_dispositivo = 0;
            id_mensaje = 0;
            visto = false;
            texto = txt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId_dispositivo() {
            return id_dispositivo;
        }

        public void setId_dispositivo(int id_dispositivo) {
            this.id_dispositivo = id_dispositivo;
        }

        public int getId_mensaje() {
            return id_mensaje;
        }

        public void setId_mensaje(int id_mensaje) {
            this.id_mensaje = id_mensaje;
        }

        public boolean isVisto() {
            return visto;
        }

        public void setVisto(boolean visto) {
            this.visto = visto;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }
    }
}
