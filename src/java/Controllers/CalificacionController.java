package Controllers;

import Interface.ICalificacion;
import Dao.CalificacionDaoImpl;
import model.Calificacion;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "CalificacionController", urlPatterns = {"/CalificacionController"})
public class CalificacionController extends HttpServlet {

    private final ICalificacion calDao = new CalificacionDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String idC = req.getParameter("id_cliente");
            String idP = req.getParameter("id_pelicula");
            if (idC != null && idP != null) {
                resp.getWriter().write(gson.toJson(calDao.searchById(Integer.parseInt(idC), Integer.parseInt(idP))));
            } else if (idP != null) {
                resp.getWriter().write(gson.toJson(calDao.listarPorPelicula(Integer.parseInt(idP))));
            } else if (idC != null) {
                resp.getWriter().write(gson.toJson(calDao.listarPorCliente(Integer.parseInt(idC))));
            } else {
                resp.getWriter().write(gson.toJson(calDao.lista()));
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
                Calificacion c = new Calificacion();
                c.setId_cliente(Integer.parseInt(req.getParameter("id_cliente")));
                c.setId_pelicula(Integer.parseInt(req.getParameter("id_pelicula")));
                c.setPuntuacion(Integer.parseInt(req.getParameter("puntuacion")));
                c.setComentario(req.getParameter("comentario"));
                ok = calDao.insertar(c);
            } else if ("delete".equals(action)) {
                ok = calDao.delete(
                    Integer.parseInt(req.getParameter("id_cliente")),
                    Integer.parseInt(req.getParameter("id_pelicula"))
                );
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
