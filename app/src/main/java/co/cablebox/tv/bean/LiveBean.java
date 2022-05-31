package co.cablebox.tv.bean;

import java.util.ArrayList;
import java.util.List;

public class LiveBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    // Datos de cada canal
    public static class DataBean {
        private String id;
        private String num;
        private String name;
        private String url;
        private String url_multicast;
        private String categoria;
        private String logo;
        private String calidad;
        private List<Programa> programas;

        public DataBean() {
            programas = new ArrayList<>();
        }

        public String getId() { return id; }

        public void setId(String id) {
            this.id = id;
        }


        public String getNum() { return num; }

        public void setNum(String num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public String getCalidad() {
            return calidad;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public String getUrlMulticast() {
            return url_multicast;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLogo() {
            String logo2 = logo
                    .replace("&", "_")
                    .replace("+", "_")
                    .replace("-", "_");

            return logo2;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public void setCategoria(String categoria){ this.categoria = categoria; }

        public String getCategoria(){ return categoria; }

        public List<Programa> getProgramas() {
            return programas;
        }

        public void setProgramas(List<Programa> programas) {
            this.programas = programas;
        }
    }
}
