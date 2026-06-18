package test;

import Dao.IncidenciaDaoImpl;
import Dao.SalaDaoImpl;
import Interface.IIncidencia;
import model.Incidencia;
import model.Sala;

public class TestIncidencia {
    static IIncidencia dao = new IncidenciaDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST INCIDENCIA ===");

        Sala sala = new Sala();
        sala.setNombre("Sala Incidencia");
        sala.setTipo("2D");
        sala.setCapacidad_total(40);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().equals("Sala Incidencia"))
            .findFirst().get().getId_sala();

        Sala s = new Sala();
        s.setId_sala(idSala);

        Incidencia i = new Incidencia();
        i.setTipo("Fallo tecnico");
        i.setSala(s);
        i.setEstado("Sin atender");
        boolean ok = dao.insertar(i);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        System.out.println("Listar todo: " + dao.lista().size() + " incidencias");

        var lista = dao.lista();
        if (!lista.isEmpty()) {
            int id = lista.get(0).getId_incidencia();
            Incidencia buscada = dao.searchById(id);
            System.out.println("SearchById: " + (buscada != null ? buscada.getTipo() : "FAIL"));

            buscada.setEstado("Atendida");
            ok = dao.update(buscada);
            System.out.println("Update: " + (ok ? "OK" : "FAIL"));

            ok = dao.delete(id);
            System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
        }
    }
}
