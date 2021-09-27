package co.cablebox.tv.bean;

import java.util.ArrayList;
import java.util.List;

public class ProgramBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private Object type;
        private String canal;
        private List<Programa> programas;

        public DataBean(String canal) {
            this.canal = canal;
            programas = new ArrayList<>();
        }

        public Object getType() {
            return type;
        }

        public List<Programa> getProgramas() {
            return programas;
        }

        public void setProgramas(List<Programa> programas) {
            this.programas = programas;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public String getCanal() {
            return canal;
        }

        public void setCanal(String canal) {
            this.canal = canal;
        }
    }
}
