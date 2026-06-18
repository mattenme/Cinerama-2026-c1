package model;

public class Cliente {

    private int id_cliente;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente() {
    }

    public Cliente(int id_cliente, String dni, String nombre) {
        this(id_cliente, dni, nombre, null, null);
    }

    public Cliente(int id_cliente, String dni, String nombre,
            String email, String telefono) {
        this.id_cliente = id_cliente;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
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
}
