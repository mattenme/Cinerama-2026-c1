package Controllers;

import Interface.IIncidencia;
import Dao.IncidenciaDaoImpl;
import model.Incidencia;
import model.Sala;
import model.Funcion;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "IncidenciaController", urlPatterns = {"/IncidenciaController"})
public class IncidenciaController extends HttpServlet {

    private final IIncidencia incDao = new IncidenciaDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        if (id != null) {
            resp.getWriter().write(gson.toJson(incDao.searchById(Integer.parseInt(id))));
        } else {
            resp.getWriter().write(gson.toJson(incDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        boolean ok = false;

        if ("insertar".equals(action)) {
            Incidencia i = new Incidencia();
            i.setTipo(req.getParameter("tipo"));
            if (req.getParameter("id_sala") != null) {
                Sala s = new Sala();
                s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
                i.setSala(s);
            }
            if (req.getParameter("id_funcion") != null) {
                Funcion f = new Funcion();
                f.setId_funcion(Integer.parseInt(req.getParameter("id_funcion")));
                i.setFuncion(f);
            }
            i.setReportado_por(req.getParameter("reportado_por"));
            i.setEstado(req.getParameter("estado"));
            ok = incDao.insertar(i);
        } else if ("update".equals(action)) {
            Incidencia i = incDao.searchById(Integer.parseInt(req.getParameter("id")));
            if (i != null) {
                if (req.getParameter("tipo") != null) i.setTipo(req.getParameter("tipo"));
                if (req.getParameter("id_sala") != null && !req.getParameter("id_sala").isEmpty()) {
                    Sala s = new Sala();
                    s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
                    i.setSala(s);
                }
                if (req.getParameter("id_funcion") != null && !req.getParameter("id_funcion").isEmpty()) {
                    Funcion f = new Funcion();
                    f.setId_funcion(Integer.parseInt(req.getParameter("id_funcion")));
                    i.setFuncion(f);
                }
                if (req.getParameter("reportado_por") != null) i.setReportado_por(req.getParameter("reportado_por"));
                if (req.getParameter("estado") != null) i.setEstado(req.getParameter("estado"));
                ok = incDao.update(i);
            }
        } else if ("delete".equals(action)) {
            ok = incDao.delete(Integer.parseInt(req.getParameter("id")));
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
