package model;

public class Pelicula {

    private int id_pelicula;
    private String titulo;
    private int duracion_minutos;
    private String genero;
    private String sinopsis;
    private String imagen_url;
    private String trailer_url;

    public Pelicula() {
    }

    public Pelicula(int id_pelicula, String titulo, int duracion_minutos) {
        this(id_pelicula, titulo, duracion_minutos, null, null, null, null);
    }

    public Pelicula(int id_pelicula, String titulo, int duracion_minutos,
            String genero, String sinopsis, String imagen_url, String trailer_url) {
        this.id_pelicula = id_pelicula;
        this.titulo = titulo;
        this.duracion_minutos = duracion_minutos;
        this.genero = genero;
        this.sinopsis = sinopsis;
        this.imagen_url = imagen_url;
        this.trailer_url = trailer_url;
    }

    public int getId_pelicula() {
        return id_pelicula;
    }

    public void setId_pelicula(int id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDuracion_minutos() {
        return duracion_minutos;
    }

    public void setDuracion_minutos(int duracion_minutos) {
        this.duracion_minutos = duracion_minutos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }
}
