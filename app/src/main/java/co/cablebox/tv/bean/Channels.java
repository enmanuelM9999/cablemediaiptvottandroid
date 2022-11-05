package co.cablebox.tv.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channels implements Serializable {

    private List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    // Datos de cada canal
    public static class Channel implements Serializable{
        private String id;
        private String num;
        private String name;
        private String url;
        private String url_multicast;
        private String categoria;
        private String logo;
        private String calidad;
        private List<Programa> programas;

        public Channel() {
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
            return logo;
        }

        public String getLogo_DEPRECATED() {
            String logo2 = logo
                    .replace("&", "_")
                    .replace("+", "_")
                    .replace("-", "_")
                    .replace("!", "_")
                    .replace("ñ", "_")
                    .replace("*", "_")
                    .replaceAll("\b[0-9]", "_") ; // starts with number

            System.out.println("------------------------------------------------------------------------------------log "+logo2);
            //remove extension
            if(logo2.contains(".")){
                String[] str= logo2.split("\\.");
                logo2= str[0];
            }
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

        @Override
        public String toString() {
            return "Channel{" +
                    "id='" + id + '\'' +
                    ", num='" + num + '\'' +
                    ", name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", url_multicast='" + url_multicast + '\'' +
                    ", categoria='" + categoria + '\'' +
                    ", logo='" + logo + '\'' +
                    ", calidad='" + calidad + '\'' +
                    ", programas=" + programas +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Channels{" +
                "data=" + channels +
                '}';
    }
}
