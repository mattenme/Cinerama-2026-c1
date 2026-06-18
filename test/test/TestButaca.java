package test;

import Dao.ButacaDaoImpl;
import Dao.SalaDaoImpl;
import Interface.IButaca;
import model.Butaca;
import model.Sala;

public class TestButaca {
    static IButaca dao = new ButacaDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST BUTACA ===");

        String salaName = "Sala Butaca Test " + System.currentTimeMillis();
        Sala sala = new Sala();
        sala.setNombre(salaName);
        sala.setTipo("3D");
        sala.setCapacidad_total(50);
        new SalaDaoImpl().insertar(sala);
        int idSala = new SalaDaoImpl().lista().stream()
            .filter(s -> s.getNombre().equals(salaName))
            .findFirst().get().getId_sala();

        Sala s = new Sala();
        s.setId_sala(idSala);

        Butaca b = new Butaca();
        b.setSala(s);
        b.setFila("A");
        b.setNumero(1);
        b.setEstado("Disponible");
        boolean ok = dao.insertar(b);
        System.out.println("Insertar: " + (ok ? "OK" : "FAIL"));

        System.out.println("Listar por sala: " + dao.listarPorSala(idSala).size() + " butacas");
        System.out.println("Listar todo: " + dao.lista().size() + " butacas");

        var lista = dao.listarPorSala(idSala);
        if (!lista.isEmpty()) {
            int id = lista.get(0).getId_butaca();
            Butaca buscada = dao.searchById(id);
            System.out.println("SearchById: " + (buscada != null ? buscada.getFila() + buscada.getNumero() : "FAIL"));

            ok = dao.actualizarEstado(id, "Vendida");
            System.out.println("Cambiar estado: " + (ok ? "OK" : "FAIL"));

            b.setId_butaca(id);
            b.setEstado("Disponible");
            ok = dao.update(b);
            System.out.println("Update: " + (ok ? "OK" : "FAIL"));

            ok = dao.delete(id);
            System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
        }
    }
}
