package test;

import Dao.ClienteDaoImpl;
import Interface.ICliente;
import model.Cliente;
import java.util.List;

public class TestCliente {
    static ICliente dao = new ClienteDaoImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST CLIENTE ===");

        Cliente c = new Cliente();
        c.setDni("12345678");
        c.setNombre("Juan Perez");
        c.setCancelaciones_acumuladas(0);
        c.setEs_frecuente(false);
        int id = dao.insertar(c);
        System.out.println("Insertar: " + (id > 0 ? "OK id=" + id : "FAIL"));

        List<Cliente> lista = dao.lista();
        System.out.println("Listar: " + lista.size() + " registros");

        Cliente buscado = dao.searchById(id);
        System.out.println("SearchById: " + (buscado != null ? buscado.getNombre() : "FAIL"));

        Cliente porDni = dao.searchByDni("12345678");
        System.out.println("SearchByDni: " + (porDni != null ? porDni.getNombre() : "FAIL"));

        buscado.setEs_frecuente(true);
        boolean ok = dao.update(buscado);
        System.out.println("Update: " + (ok ? "OK" : "FAIL"));

        ok = dao.delete(id);
        System.out.println("Delete: " + (ok ? "OK" : "FAIL"));
    }
}
