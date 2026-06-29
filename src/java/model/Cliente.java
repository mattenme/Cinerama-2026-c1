package model;

public class Cliente {

    private int id_cliente;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;
    private String rol;
    private int activo;
    private int verificado;
    private String codigoVerificacion;

    public Cliente() {}

    public Cliente(int id_cliente, String dni, String nombre) {
        this(id_cliente, dni, nombre, null, null, null, null, 1, 0, null);
    }

    public Cliente(int id_cliente, String dni, String nombre, String email, String telefono) {
        this(id_cliente, dni, nombre, email, telefono, null, null, 1, 0, null);
    }

    public Cliente(int id_cliente, String dni, String nombre, String email, String telefono,
                   String contrasena, String rol, int activo, int verificado, String codigoVerificacion) {
        this.id_cliente = id_cliente;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
        this.verificado = verificado;
        this.codigoVerificacion = codigoVerificacion;
    }

    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
    public int getVerificado() { return verificado; }
    public void setVerificado(int verificado) { this.verificado = verificado; }
    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }
}
