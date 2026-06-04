package test;

import Dao.SalaDaoImpl;
import Interface.ISala;
import model.Sala;
import java.util.List;

public class TestSala {
    static ISala dao = new SalaDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST SALA ===");

        Sala s = new Sala();
        s.setNombre("Sala 1");
        s.setTipo("2D");
        s.setCapacidad_total(100);
        boolean ok = dao.insertar(s) != -1;
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        List<Sala> lista = dao.lista();
        System.out.println("Listar: " + lista.size() + " registros");

        if (!lista.isEmpty()) {
            int id = lista.get(0).getId_sala();
            Sala buscada = dao.searchById(id);
            System.out.println("SearchById: " + (buscada != null ? buscada.getNombre() : "FAIL"));

            buscada.setCapacidad_total(120);
            ok = dao.update(buscada);
            System.out.println("Update: " + (ok ? "OK" : "FAIL"));

            ok = dao.delete(id);
            System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
        }
    }
}
