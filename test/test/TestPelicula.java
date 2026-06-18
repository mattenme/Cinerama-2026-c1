package test;

import Dao.PeliculaDaoImpl;
import Interface.IPelicula;
import model.Pelicula;
import java.util.List;

public class TestPelicula {
    static IPelicula dao = new PeliculaDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST PELICULA ===");

        Pelicula p = new Pelicula();
        p.setTitulo("Inception " + System.currentTimeMillis());
        p.setDuracion_minutos(148);
        boolean ok = dao.insertar(p);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        List<Pelicula> lista = dao.lista();
        System.out.println("Listar: " + lista.size() + " registros");

        if (!lista.isEmpty()) {
            int id = lista.get(0).getId_pelicula();
            Pelicula buscada = dao.searchById(id);
            System.out.println("SearchById: " + (buscada != null ? buscada.getTitulo() : "FAIL"));

            buscada.setTitulo("Inception (Actualizado)");
            ok = dao.update(buscada);
            System.out.println("Update: " + (ok ? "OK" : "FAIL"));

            ok = dao.delete(id);
            System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
        }
    }
}
