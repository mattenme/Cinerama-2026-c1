package Controllers;

import Interface.ISala;
import Dao.SalaDaoImpl;
import model.Sala;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "SalaController", urlPatterns = {"/SalaController"})
public class SalaController extends HttpServlet {

    private final ISala salaDao = new SalaDaoImpl();
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
                int salaId = salaDao.insertarConButacas(s, 10);
                if (salaId > 0) {
                    s.setId_sala(salaId);
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
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
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
