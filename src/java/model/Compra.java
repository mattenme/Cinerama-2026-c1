package model;

import java.sql.Timestamp;

public class Compra {

    private int id_compra;
    private Cliente cliente;
    private Funcion funcion;
    private Butaca butaca;
    private double precio;
    private String metodo_pago;
    private String estado;
    private String codigo_qr;
    private Timestamp fecha_compra;
    private String productos;

    public Compra() {
    }

    public Compra(int id_compra, Cliente cliente, Funcion funcion, Butaca butaca,
            double precio, String metodo_pago, String estado,
            String codigo_qr, Timestamp fecha_compra) {
        this.id_compra = id_compra;
        this.cliente = cliente;
        this.funcion = funcion;
        this.butaca = butaca;
        this.precio = precio;
        this.metodo_pago = metodo_pago;
        this.estado = estado;
        this.codigo_qr = codigo_qr;
        this.fecha_compra = fecha_compra;
    }

    public int getId_compra() { return id_compra; }
    public void setId_compra(int id_compra) { this.id_compra = id_compra; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }

    public Butaca getButaca() { return butaca; }
    public void setButaca(Butaca butaca) { this.butaca = butaca; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getMetodo_pago() { return metodo_pago; }
    public void setMetodo_pago(String metodo_pago) { this.metodo_pago = metodo_pago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigo_qr() { return codigo_qr; }
    public void setCodigo_qr(String codigo_qr) { this.codigo_qr = codigo_qr; }

    public Timestamp getFecha_compra() { return fecha_compra; }
    public void setFecha_compra(Timestamp fecha_compra) { this.fecha_compra = fecha_compra; }

    public String getProductos() { return productos; }
    public void setProductos(String productos) { this.productos = productos; }
}
