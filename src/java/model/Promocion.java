package model;

public class Promocion {
    private int id_promocion;
    private String codigo;
    private String descripcion;
    private int descuento;
    private int activo;

    public Promocion() {}

    public Promocion(int id_promocion, String codigo, String descripcion, int descuento, int activo) {
        this.id_promocion = id_promocion;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.activo = activo;
    }

    public int getId_promocion() { return id_promocion; }
    public void setId_promocion(int id_promocion) { this.id_promocion = id_promocion; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDescuento() { return descuento; }
    public void setDescuento(int descuento) { this.descuento = descuento; }
    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
