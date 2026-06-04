package model;

import java.sql.Timestamp;

public class Incidencia {
    private int id_incidencia;
    private String tipo;
    private Sala sala;
    private Funcion funcion;
    private Timestamp fecha_reporte;
    private String reportado_por;
    private String estado;

    public Incidencia() {}

    public Incidencia(int id_incidencia, String tipo, Sala sala, Funcion funcion,
                      Timestamp fecha_reporte, String reportado_por, String estado) {
        this.id_incidencia = id_incidencia;
        this.tipo = tipo;
        this.sala = sala;
        this.funcion = funcion;
        this.fecha_reporte = fecha_reporte;
        this.reportado_por = reportado_por;
        this.estado = estado;
    }

    public int getId_incidencia() { return id_incidencia; }
    public void setId_incidencia(int id_incidencia) { this.id_incidencia = id_incidencia; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }

    public Timestamp getFecha_reporte() { return fecha_reporte; }
    public void setFecha_reporte(Timestamp fecha_reporte) { this.fecha_reporte = fecha_reporte; }

    public String getReportado_por() { return reportado_por; }
    public void setReportado_por(String reportado_por) { this.reportado_por = reportado_por; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
