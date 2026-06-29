package model;

public class Asiento {

    private int id_asiento;
    private Sala sala;
    private String fila;
    private int numero;
    private String estado;

    public Asiento() {
    }

    public Asiento(int id_asiento, Sala sala, String fila, int numero, String estado) {
        this.id_asiento = id_asiento;
        this.sala = sala;
        this.fila = fila;
        this.numero = numero;
        this.estado = estado;
    }

    public int getId_asiento() {
        return id_asiento;
    }

    public void setId_asiento(int id_asiento) {
        this.id_asiento = id_asiento;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
