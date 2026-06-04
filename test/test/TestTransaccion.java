package test;

import Dao.*;
import Interface.ITransaccion;
import model.*;

public class TestTransaccion {
    static ITransaccion dao = new TransaccionDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST TRANSACCION ===");

        Cliente cli = new Cliente();
        cli.setDni("11111111");
        cli.setNombre("Cliente Trans");
        int idCli = new ClienteDaoImpl().insertar(cli);

        Pelicula p = new Pelicula();
        p.setTitulo("Avatar");
        p.setDuracion_minutos(162);
        new PeliculaDaoImpl().insertar(p);
        int idPel = new PeliculaDaoImpl().lista().stream()
            .filter(x -> x.getTitulo().equals("Avatar"))
            .findFirst().get().getId_pelicula();

        Sala sala = new Sala();
        sala.setNombre("Sala Trans");
        sala.setTipo("2D");
        sala.setCapacidad_total(60);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().equals("Sala Trans"))
            .findFirst().get().getId_sala();

        Pelicula pel = new Pelicula();
        pel.setId_pelicula(idPel);
        Sala sl = new Sala();
        sl.setId_sala(idSala);

        Funcion fun = new Funcion();
        fun.setPelicula(pel);
        fun.setSala(sl);
        fun.setHora_inicio("2026-06-02 18:00:00");
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
        t.setMonto_total(25.00);
        t.setMetodo_pago("Yape");
        t.setEstado("Completada");
        t.setCodigo_qr("QR-" + System.currentTimeMillis());
        int id = dao.insertar(t);
        System.out.println("Insertar: " + (id > 0 ? "OK id=" + id : "FAIL"));

        Transaccion buscada = dao.searchById(id);
        System.out.println("SearchById: " + (buscada != null ? buscada.getMetodo_pago() : "FAIL"));

        System.out.println("Listar por cliente: " + dao.listarPorCliente(idCli).size() + " transacciones");
        System.out.println("Listar todo: " + dao.lista().size() + " transacciones");
    }
}
