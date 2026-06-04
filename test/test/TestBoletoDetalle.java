package test;

import Dao.*;
import Interface.IBoletoDetalle;
import model.*;

public class TestBoletoDetalle {
    static IBoletoDetalle dao = new BoletoDetalleDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST BOLETO DETALLE ===");

        Cliente cli = new Cliente();
        cli.setDni("22222222");
        cli.setNombre("Cliente Boleto");
        int idCli = new ClienteDaoImpl().insertar(cli);

        Pelicula p = new Pelicula();
        p.setTitulo("El Padrino");
        p.setDuracion_minutos(175);
        new PeliculaDaoImpl().insertar(p);
        int idPel = new PeliculaDaoImpl().lista().stream()
            .filter(x -> x.getTitulo().equals("El Padrino"))
            .findFirst().get().getId_pelicula();

        Sala sala = new Sala();
        sala.setNombre("Sala Boleto");
        sala.setTipo("2D");
        sala.setCapacidad_total(30);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().equals("Sala Boleto"))
            .findFirst().get().getId_sala();

        Sala sl = new Sala();
        sl.setId_sala(idSala);
        Pelicula pel = new Pelicula();
        pel.setId_pelicula(idPel);

        Butaca but = new Butaca();
        but.setSala(sl);
        but.setFila("B");
        but.setNumero(5);
        but.setEstado("Disponible");
        new ButacaDaoImpl().insertar(but);
        int idBut = new ButacaDaoImpl().listarPorSala(idSala).get(0).getId_butaca();

        Funcion fun = new Funcion();
        fun.setPelicula(pel);
        fun.setSala(sl);
        fun.setHora_inicio("2026-06-03 15:00:00");
        fun.setEstado("Programada");
        new FuncionDaoImpl().insertar(fun);
        int idFun = new FuncionDaoImpl().lista().stream()
            .findFirst().get().getId_funcion();

        Cliente c = new Cliente();
        c.setId_cliente(idCli);
        Funcion f = new Funcion();
        f.setId_funcion(idFun);

        Transaccion t = new Transaccion();
        t.setCliente(c);
        t.setFuncion(f);
        t.setMonto_total(15.00);
        t.setMetodo_pago("Efectivo");
        t.setEstado("Completada");
        t.setCodigo_qr("QR-BOLETO");
        int idTrans = new TransaccionDaoImpl().insertar(t);

        Transaccion tr = new Transaccion();
        tr.setId_transaccion(idTrans);
        Butaca bu = new Butaca();
        bu.setId_butaca(idBut);

        BoletoDetalle bd = new BoletoDetalle();
        bd.setTransaccion(tr);
        bd.setButaca(bu);
        bd.setPrecio_aplicado(15.00);
        boolean ok = dao.insertar(bd);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        var lista = dao.listarPorTransaccion(idTrans);
        System.out.println("Listar por transaccion: " + lista.size() + " boletos");

        if (!lista.isEmpty()) {
            int idB = lista.get(0).getId_boleto();
            ok = dao.marcarQrUsado(idB);
            System.out.println("Marcar QR usado: " + (ok ? "OK" : "FAIL"));
        }
    }
}
