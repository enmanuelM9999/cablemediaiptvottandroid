package co.cablebox.tv.socket;

public class Notificaciones {

    private String nickname;
    private String message ;
    private Boolean visto;

    public Notificaciones(){

    }
    public Notificaciones(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
        this.visto = false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getVisto() {
        return visto;
    }

    public void setVisto(Boolean visto) {
        this.visto = visto;
    }
}