package model;

public class Promocion {
    private int idPromocion;
    private String codigo;
    private String descripcion;
    private int descuento;
    private int activo;

    public Promocion() {}

    public Promocion(int idPromocion, String codigo, String descripcion, int descuento, int activo) {
        this.idPromocion = idPromocion;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.activo = activo;
    }

    public int getIdPromocion() { return idPromocion; }
    public void setIdPromocion(int idPromocion) { this.idPromocion = idPromocion; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDescuento() { return descuento; }
    public void setDescuento(int descuento) { this.descuento = descuento; }
    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
