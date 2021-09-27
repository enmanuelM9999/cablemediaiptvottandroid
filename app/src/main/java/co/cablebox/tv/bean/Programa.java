package co.cablebox.tv.bean;

import java.util.Calendar;

// Programa
public class Programa {

    private String nombreProgram;
    private String clasificacion;
    private String descripcion;
    private String timeDateInit;
    private String timeDateFinish;
    private Calendar calendarInit;
    private Calendar calendarFinish;

    public Programa(String nombreProgram, String clasificacion, String timeDateInit, String timeDateFinish) {
        this.nombreProgram = nombreProgram;
        this.clasificacion = clasificacion;
        this.timeDateInit = timeDateInit;
        this.timeDateFinish = timeDateFinish;
    }

    public String getNombreProgram() {
        return nombreProgram;
    }

    public void setNombreProgram(String nombreProgram) {
        this.nombreProgram = nombreProgram;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTimeDateInit() {
        return timeDateInit;
    }

    public void setTimeDateInit(String timeDateInit) {
        this.timeDateInit = timeDateInit;
    }

    public String getTimeDateFinish() {
        return timeDateFinish;
    }

    public void setTimeDateFinish(String timeDateFinish) {
        this.timeDateFinish = timeDateFinish;
    }

    public Calendar getCalendarInit() {
        return calendarInit;
    }

    public void setCalendarInit(Calendar calendarInit) {
        this.calendarInit = calendarInit;
    }

    public Calendar getCalendarFinish() {
        return calendarFinish;
    }

    public void setCalendarFinish(Calendar calendarFinish) {
        this.calendarFinish = calendarFinish;
    }
}
