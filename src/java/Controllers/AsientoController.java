package Controllers;

import Interface.IAsiento;
import Dao.AsientoDaoImpl;
import model.Asiento;
import model.Sala;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "AsientoController", urlPatterns = {"/AsientoController"})
public class AsientoController extends HttpServlet {

    private final IAsiento asientoDao = new AsientoDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String action = req.getParameter("action");
            String idSala = req.getParameter("idSala");
            if ("liberarReservadas".equals(action)) {
                if (idSala != null) {
                    int count = asientoDao.liberarReservadas(Integer.parseInt(idSala));
                    resp.getWriter().write("{\"success\":true,\"liberadas\":" + count + "}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"idSala requerido\"}");
                }
                return;
            }

            String id = req.getParameter("id");
            if (id != null) {
                resp.getWriter().write(gson.toJson(asientoDao.searchById(Integer.parseInt(id))));
            } else if (idSala != null) {
                resp.getWriter().write(gson.toJson(asientoDao.listarPorSala(Integer.parseInt(idSala))));
            } else {
                resp.getWriter().write(gson.toJson(asientoDao.lista()));
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
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                Asiento a = new Asiento();
                Sala s = new Sala();
                s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
                a.setSala(s);
                a.setFila(req.getParameter("fila"));
                a.setNumero(Integer.parseInt(req.getParameter("numero")));
                a.setEstado(req.getParameter("estado"));
                ok = asientoDao.insertar(a);
            } else if ("cambiarEstado".equals(action)) {
                ok = asientoDao.actualizarEstado(
                    Integer.parseInt(req.getParameter("id")),
                    req.getParameter("estado")
                );
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    ok = false;
                } else {
                    ok = asientoDao.delete(Integer.parseInt(req.getParameter("id")));
                }
            }
        } catch (NumberFormatException e) {
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID inv\u00e1lido\"}");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":" + gson.toJson(e.getMessage() != null ? e.getMessage() : "Error interno") + "}");
            return;
        }
        if (!resp.isCommitted()) {
            resp.getWriter().write("{\"success\":" + ok + ",\"mensaje\":" + gson.toJson(ok ? "Operaci\u00f3n exitosa" : "Error al realizar la operaci\u00f3n") + "}");
        }
    }
}
