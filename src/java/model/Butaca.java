package model;

public class Butaca {

    private int id_butaca;
    private Sala sala;
    private String fila;
    private int numero;
    private String estado;

    public Butaca() {
    }

    public Butaca(int id_butaca, Sala sala, String fila, int numero, String estado) {
        this.id_butaca = id_butaca;
        this.sala = sala;
        this.fila = fila;
        this.numero = numero;
        this.estado = estado;
    }

    public int getId_butaca() {
        return id_butaca;
    }

    public void setId_butaca(int id_butaca) {
        this.id_butaca = id_butaca;
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
