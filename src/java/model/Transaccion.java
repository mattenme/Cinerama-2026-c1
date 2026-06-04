package model;

import java.sql.Timestamp;

public class Transaccion {

    private int id_transaccion;
    private Cliente cliente;
    private Funcion funcion;
    private double monto_total;
    private String metodo_pago;
    private String estado;
    private String codigo_qr;
    private Timestamp fecha_transaccion;

    public Transaccion() {
    }

    public Transaccion(int id_transaccion, Cliente cliente, Funcion funcion,
            double monto_total, String metodo_pago, String estado,
            String codigo_qr, Timestamp fecha_transaccion) {
        this.id_transaccion = id_transaccion;
        this.cliente = cliente;
        this.funcion = funcion;
        this.monto_total = monto_total;
        this.metodo_pago = metodo_pago;
        this.estado = estado;
        this.codigo_qr = codigo_qr;
        this.fecha_transaccion = fecha_transaccion;
    }

    public int getId_transaccion() {
        return id_transaccion;
    }

    public void setId_transaccion(int id_transaccion) {
        this.id_transaccion = id_transaccion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public double getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(double monto_total) {
        this.monto_total = monto_total;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo_qr() {
        return codigo_qr;
    }

    public void setCodigo_qr(String codigo_qr) {
        this.codigo_qr = codigo_qr;
    }

    public Timestamp getFecha_transaccion() {
        return fecha_transaccion;
    }

    public void setFecha_transaccion(Timestamp fecha_transaccion) {
        this.fecha_transaccion = fecha_transaccion;
    }
}
