package test;

import Dao.CalificacionDaoImpl;
import Dao.ClienteDaoImpl;
import Dao.PeliculaDaoImpl;
import Interface.ICalificacion;
import model.Calificacion;
import model.Cliente;
import model.Pelicula;

public class TestCalificacion {
    static ICalificacion dao = new CalificacionDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST CALIFICACION ===");

        Cliente cli = new Cliente();
        cli.setDni("8765" + System.currentTimeMillis());
        cli.setNombre("Test Calif");
        int idCli = new ClienteDaoImpl().insertar(cli);

        Pelicula pel = new Pelicula();
        pel.setTitulo("Matrix");
        pel.setDuracion_minutos(136);
        new PeliculaDaoImpl().insertar(pel);
        int idPel = new PeliculaDaoImpl().lista().stream()
            .filter(p -> p.getTitulo().equals("Matrix"))
            .findFirst().get().getId_pelicula();

        Calificacion cal = new Calificacion();
        cal.setId_cliente(idCli);
        cal.setId_pelicula(idPel);
        cal.setPuntuacion(4);
        boolean ok = dao.insertar(cal);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        Calificacion buscada = dao.searchById(idCli, idPel);
        System.out.println("SearchById: " + (buscada != null ? "puntuacion=" + buscada.getPuntuacion() : "FAIL"));

        System.out.println("Listar por pelicula: " + dao.listarPorPelicula(idPel).size() + " registros");
        System.out.println("Listar por cliente: " + dao.listarPorCliente(idCli).size() + " registros");
        System.out.println("Listar todo: " + dao.lista().size() + " registros");

        ok = dao.delete(idCli, idPel);
        System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
    }
}
