package model;

public class Cliente {

    private int id_cliente;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private String avatar_url;
    private int cancelaciones_acumuladas;
    private boolean es_frecuente;

    public Cliente() {
    }

    public Cliente(int id_cliente, String dni, String nombre,
            int cancelaciones_acumuladas, boolean es_frecuente) {
        this(id_cliente, dni, nombre, null, null, null, cancelaciones_acumuladas, es_frecuente);
    }

    public Cliente(int id_cliente, String dni, String nombre,
            String email, String telefono, String avatar_url,
            int cancelaciones_acumuladas, boolean es_frecuente) {
        this.id_cliente = id_cliente;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.avatar_url = avatar_url;
        this.cancelaciones_acumuladas = cancelaciones_acumuladas;
        this.es_frecuente = es_frecuente;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getCancelaciones_acumuladas() {
        return cancelaciones_acumuladas;
    }

    public void setCancelaciones_acumuladas(int cancelaciones_acumuladas) {
        this.cancelaciones_acumuladas = cancelaciones_acumuladas;
    }

    public boolean isEs_frecuente() {
        return es_frecuente;
    }

    public void setEs_frecuente(boolean es_frecuente) {
        this.es_frecuente = es_frecuente;
    }
}
