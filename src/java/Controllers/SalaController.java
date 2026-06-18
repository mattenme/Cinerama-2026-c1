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
        try {
            String id = req.getParameter("id");
            if (id != null) {
                resp.getWriter().write(gson.toJson(salaDao.searchById(Integer.parseInt(id))));
            } else {
                resp.getWriter().write(gson.toJson(salaDao.lista()));
            }
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID inv\u00e1lido\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Error interno\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        boolean ok = false;
        try {
            String action = req.getParameter("action");
            if ("insertar".equals(action)) {
                String nombre = req.getParameter("nombre");
                if (salaDao.existeNombre(nombre)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Ya existe una sala con ese nombre\"}");
                    return;
                }
                Sala s = new Sala();
                s.setNombre(nombre);
                s.setTipo(req.getParameter("tipo"));
                s.setCapacidad_total(Integer.parseInt(req.getParameter("capacidad_total")));
                int salaId = salaDao.insertar(s);
                if (salaId > 0) {
                    s.setId_sala(salaId);
                    int cap = s.getCapacidad_total();
                    int cols = 10;
                    int rows = (int) Math.ceil((double) cap / cols);
                    int asientosCreados = 0;
                    for (int r = 0; r < rows && asientosCreados < cap; r++) {
                        char filaChar = (char) ('A' + r);
                        for (int n = 1; n <= cols && asientosCreados < cap; n++) {
                            Butaca b = new Butaca();
                            b.setSala(s);
                            b.setFila(String.valueOf(filaChar));
                            b.setNumero(n);
                            b.setEstado("Disponible");
                            butacaDao.insertar(b);
                            asientosCreados++;
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
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID inv\u00e1lido\"}");
            return;
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Error interno\"}");
            e.printStackTrace();
            return;
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
