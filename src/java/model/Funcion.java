package model;

public class Funcion {

    private int id_funcion;
    private Pelicula pelicula;
    private Sala sala;
    private String hora_inicio;
    private String estado;

    public Funcion() {
    }

    public Funcion(int id_funcion, Pelicula pelicula, Sala sala,
            String hora_inicio, String estado) {
        this.id_funcion = id_funcion;
        this.pelicula = pelicula;
        this.sala = sala;
        this.hora_inicio = hora_inicio;
        this.estado = estado;
    }

    public int getId_funcion() {
        return id_funcion;
    }

    public void setId_funcion(int id_funcion) {
        this.id_funcion = id_funcion;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
