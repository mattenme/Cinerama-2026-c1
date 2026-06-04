package model;

public class BoletoDetalle {

    private int id_boleto;
    private Transaccion transaccion;
    private Butaca butaca;
    private double precio_aplicado;
    private boolean qr_usado;

    public BoletoDetalle() {
    }

    public BoletoDetalle(int id_boleto, Transaccion transaccion, Butaca butaca,
            double precio_aplicado, boolean qr_usado) {
        this.id_boleto = id_boleto;
        this.transaccion = transaccion;
        this.butaca = butaca;
        this.precio_aplicado = precio_aplicado;
        this.qr_usado = qr_usado;
    }

    public int getId_boleto() {
        return id_boleto;
    }

    public void setId_boleto(int id_boleto) {
        this.id_boleto = id_boleto;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public Butaca getButaca() {
        return butaca;
    }

    public void setButaca(Butaca butaca) {
        this.butaca = butaca;
    }

    public double getPrecio_aplicado() {
        return precio_aplicado;
    }

    public void setPrecio_aplicado(double precio_aplicado) {
        this.precio_aplicado = precio_aplicado;
    }

    public boolean isQr_usado() {
        return qr_usado;
    }

    public void setQr_usado(boolean qr_usado) {
        this.qr_usado = qr_usado;
    }
}
