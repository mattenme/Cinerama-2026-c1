package model;

import java.sql.Timestamp;

public class Incidencia {
    private int id_incidencia;
    private String tipo;
    private String descripcion;
    private Sala sala;
    private Funcion funcion;
    private Cliente cliente;
    private Timestamp fecha_reporte;
    private String estado;

    public Incidencia() {}

    public Incidencia(int id_incidencia, String tipo, String descripcion, Sala sala, Funcion funcion,
                      Cliente cliente, Timestamp fecha_reporte, String estado) {
        this.id_incidencia = id_incidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.sala = sala;
        this.funcion = funcion;
        this.cliente = cliente;
        this.fecha_reporte = fecha_reporte;
        this.estado = estado;
    }

    public int getId_incidencia() { return id_incidencia; }
    public void setId_incidencia(int id_incidencia) { this.id_incidencia = id_incidencia; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }
    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Timestamp getFecha_reporte() { return fecha_reporte; }
    public void setFecha_reporte(Timestamp fecha_reporte) { this.fecha_reporte = fecha_reporte; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
