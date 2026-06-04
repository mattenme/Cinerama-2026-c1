package Controllers;

import Interface.ISala;
import Interface.IButaca;
import Dao.SalaDaoImpl;
import Dao.ButacaDaoImpl;
import model.Sala;
import model.Butaca;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "SalaController", urlPatterns = {"/SalaController"})
public class SalaController extends HttpServlet {

    private final ISala salaDao = new SalaDaoImpl();
    private final IButaca butacaDao = new ButacaDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        if (id != null) {
            resp.getWriter().write(gson.toJson(salaDao.searchById(Integer.parseInt(id))));
        } else {
            resp.getWriter().write(gson.toJson(salaDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        boolean ok = false;

        if ("insertar".equals(action)) {
            Sala s = new Sala();
            s.setNombre(req.getParameter("nombre"));
            s.setTipo(req.getParameter("tipo"));
            s.setCapacidad_total(Integer.parseInt(req.getParameter("capacidad_total")));
            int salaId = salaDao.insertar(s);
            if (salaId > 0) {
                s.setId_sala(salaId);
                String[] filas = {"A","B","C","D","E","F","G","H","I","J"};
                int numeros = 10;
                for (String fila : filas) {
                    for (int n = 1; n <= numeros; n++) {
                        Butaca b = new Butaca();
                        b.setSala(s);
                        b.setFila(fila);
                        b.setNumero(n);
                        b.setEstado("Disponible");
                        butacaDao.insertar(b);
                    }
                }
                ok = true;
            }
        } else if ("update".equals(action)) {
            Sala s = salaDao.searchById(Integer.parseInt(req.getParameter("id")));
            if (s != null) {
                s.setNombre(req.getParameter("nombre"));
                s.setTipo(req.getParameter("tipo"));
                s.setCapacidad_total(Integer.parseInt(req.getParameter("capacidad_total")));
                ok = salaDao.update(s);
            }
        } else if ("delete".equals(action)) {
            ok = salaDao.delete(Integer.parseInt(req.getParameter("id")));
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
