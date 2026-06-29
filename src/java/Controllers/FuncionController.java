package Controllers;

import Interface.IFuncion;
import Dao.FuncionDaoImpl;
import model.Funcion;
import model.Pelicula;
import model.Sala;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "FuncionController", urlPatterns = {"/FuncionController"})
public class FuncionController extends HttpServlet {

    private final IFuncion funcionDao = new FuncionDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            String idPelicula = req.getParameter("idPelicula");
            if (id != null) {
                resp.getWriter().write(gson.toJson(funcionDao.searchById(Integer.parseInt(id))));
            } else if (idPelicula != null) {
                resp.getWriter().write(gson.toJson(funcionDao.listarPorPelicula(Integer.parseInt(idPelicula))));
            } else {
                resp.getWriter().write(gson.toJson(funcionDao.lista()));
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
        String error = "";
        try {
            String action = req.getParameter("action");
            if ("insertar".equals(action)) {
                Funcion f = new Funcion();
                Pelicula p = new Pelicula();
                p.setId_pelicula(Integer.parseInt(req.getParameter("id_pelicula")));
                f.setPelicula(p);
                Sala s = new Sala();
                s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
                f.setSala(s);
                f.setHora_inicio(req.getParameter("hora_inicio"));
                f.setEstado(req.getParameter("estado"));
                f.setActivo(1);
                ok = funcionDao.insertar(f);
            } else if ("update".equals(action)) {
                Funcion f = funcionDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (f != null) {
                    Pelicula p = new Pelicula();
                    p.setId_pelicula(Integer.parseInt(req.getParameter("id_pelicula")));
                    f.setPelicula(p);
                    Sala s = new Sala();
                    s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
                    f.setSala(s);
                    f.setHora_inicio(req.getParameter("hora_inicio"));
                    f.setEstado(req.getParameter("estado"));
                    ok = funcionDao.update(f);
                }
            } else if ("toggleActivo".equals(action)) {
                ok = funcionDao.toggleActivo(Integer.parseInt(req.getParameter("id")));
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                ok = funcionDao.delete(Integer.parseInt(req.getParameter("id")));
            }
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID inv\u00e1lido\"}");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            error = gson.toJson(e.getMessage() != null ? e.getMessage() : "Error desconocido");
        }
        if (!resp.isCommitted()) {
            resp.getWriter().write("{\"success\":" + ok + ",\"mensaje\":" + error + "}");
        }
    }
}
