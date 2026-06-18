package test;

import Dao.*;
import Interface.ICompra;
import model.*;

public class TestCompra {
    static ICompra dao = new CompraDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST COMPRA ===");

        Cliente cli = new Cliente();
        cli.setDni("3333" + System.currentTimeMillis());
        cli.setNombre("Cliente Compra");
        int idCli = new ClienteDaoImpl().insertar(cli);

        Pelicula p = new Pelicula();
        p.setTitulo("Avatar Compra");
        p.setDuracion_minutos(162);
        new PeliculaDaoImpl().insertar(p);
        int idPel = new PeliculaDaoImpl().lista().stream()
            .filter(x -> x.getTitulo().equals("Avatar Compra"))
            .findFirst().get().getId_pelicula();

        Sala sala = new Sala();
        sala.setNombre("Sala Compra " + System.currentTimeMillis());
        sala.setTipo("2D");
        sala.setCapacidad_total(60);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().startsWith("Sala Compra"))
            .findFirst().get().getId_sala();

        Sala sl = new Sala();
        sl.setId_sala(idSala);
        Pelicula pel = new Pelicula();
        pel.setId_pelicula(idPel);

        Butaca but = new Butaca();
        but.setSala(sl);
        but.setFila("A");
        but.setNumero(1);
        but.setEstado("Disponible");
        new ButacaDaoImpl().insertar(but);
        int idBut = new ButacaDaoImpl().listarPorSala(idSala).get(0).getId_butaca();

        Funcion fun = new Funcion();
        fun.setPelicula(pel);
        fun.setSala(sl);
        fun.setHora_inicio("2026-08-01T15:00:00");
        fun.setEstado("Programada");
        new FuncionDaoImpl().insertar(fun);
        int idFun = new FuncionDaoImpl().lista().stream()
            .findFirst().get().getId_funcion();

        Cliente c = new Cliente();
        c.setId_cliente(idCli);
        Funcion f = new Funcion();
        f.setId_funcion(idFun);
        Butaca b = new Butaca();
        b.setId_butaca(idBut);

        Compra compra = new Compra();
        compra.setCliente(c);
        compra.setFuncion(f);
        compra.setButaca(b);
        compra.setPrecio(25.00);
        compra.setMetodo_pago("Yape");
        compra.setEstado("completada");
        compra.setCodigo_qr("QR-" + System.currentTimeMillis());
        int id = dao.insertar(compra);
        System.out.println("Insertar: " + (id > 0 ? "OK id=" + id : "FAIL"));

        Compra buscada = dao.searchById(id);
        System.out.println("SearchById: " + (buscada != null ? buscada.getMetodo_pago() : "FAIL"));

        System.out.println("Listar por cliente: " + dao.listarPorCliente(idCli).size() + " compras");
        System.out.println("Listar todo: " + dao.lista().size() + " compras");
    }
}
