package model;

import java.sql.Timestamp;

public class Calificacion {

    private int id_cliente;
    private int id_pelicula;
    private int puntuacion;
    private Timestamp fecha_calificacion;

    public Calificacion() {
    }

    public Calificacion(int id_cliente, int id_pelicula, int puntuacion, Timestamp fecha_calificacion) {
        this.id_cliente = id_cliente;
        this.id_pelicula = id_pelicula;
        this.puntuacion = puntuacion;
        this.fecha_calificacion = fecha_calificacion;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_pelicula() {
        return id_pelicula;
    }

    public void setId_pelicula(int id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Timestamp getFecha_calificacion() {
        return fecha_calificacion;
    }

    public void setFecha_calificacion(Timestamp fecha_calificacion) {
        this.fecha_calificacion = fecha_calificacion;
    }
}
