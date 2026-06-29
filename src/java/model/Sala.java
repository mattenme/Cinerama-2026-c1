package model;

public class Sala {

    private int id_sala;
    private String nombre;
    private String tipo;
    private int capacidad_total;
    private int activo;

    public Sala() {
    }

    public Sala(int id_sala, String nombre, String tipo, int capacidad_total, int activo) {
        this.id_sala = id_sala;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad_total = capacidad_total;
        this.activo = activo;
    }

    public int getId_sala() {
        return id_sala;
    }

    public void setId_sala(int id_sala) {
        this.id_sala = id_sala;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidad_total() {
        return capacidad_total;
    }

    public void setCapacidad_total(int capacidad_total) {
        this.capacidad_total = capacidad_total;
    }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
