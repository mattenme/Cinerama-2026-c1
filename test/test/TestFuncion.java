package test;

import Dao.FuncionDaoImpl;
import Dao.PeliculaDaoImpl;
import Dao.SalaDaoImpl;
import Interface.IFuncion;
import model.Funcion;
import model.Pelicula;
import model.Sala;

public class TestFuncion {
    static IFuncion dao = new FuncionDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST FUNCION ===");

        Pelicula p = new Pelicula();
        p.setTitulo("Interestelar Test");
        p.setDuracion_minutos(169);
        new PeliculaDaoImpl().insertar(p);
        int idPel = new PeliculaDaoImpl().lista().stream()
            .filter(x -> x.getTitulo().equals("Interestelar Test"))
            .findFirst().get().getId_pelicula();

        Sala sala = new Sala();
        sala.setNombre("Sala Funcion Test");
        sala.setTipo("IMAX");
        sala.setCapacidad_total(80);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().equals("Sala Funcion Test"))
            .findFirst().get().getId_sala();

        Pelicula pel = new Pelicula();
        pel.setId_pelicula(idPel);
        Sala sl = new Sala();
        sl.setId_sala(idSala);

        Funcion f = new Funcion();
        f.setPelicula(pel);
        f.setSala(sl);
        f.setHora_inicio("2026-07-01T20:00:00");
        f.setEstado("Programada");
        boolean ok = dao.insertar(f);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        System.out.println("Listar por pelicula: " + dao.listarPorPelicula(idPel).size() + " funciones");
        System.out.println("Listar todo: " + dao.lista().size() + " funciones");

        var lista = dao.listarPorPelicula(idPel);
        if (!lista.isEmpty()) {
            int id = lista.get(0).getId_funcion();
            Funcion buscada = dao.searchById(id);
            System.out.println("SearchById: " + (buscada != null ? buscada.getHora_inicio() : "FAIL"));

            buscada.setEstado("Cancelada");
            ok = dao.update(buscada);
            System.out.println("Update: " + (ok ? "OK" : "FAIL"));

            ok = dao.delete(id);
            System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
        }
    }
}
