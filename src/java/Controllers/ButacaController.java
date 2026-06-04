package Controllers;

import Interface.IButaca;
import Dao.ButacaDaoImpl;
import model.Butaca;
import model.Sala;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;

@WebServlet(name = "ButacaController", urlPatterns = {"/ButacaController"})
public class ButacaController extends HttpServlet {

    private final IButaca butacaDao = new ButacaDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        String idSala = req.getParameter("idSala");
        if (id != null) {
            resp.getWriter().write(gson.toJson(butacaDao.searchById(Integer.parseInt(id))));
        } else if (idSala != null) {
            resp.getWriter().write(gson.toJson(butacaDao.listarPorSala(Integer.parseInt(idSala))));
        } else {
            resp.getWriter().write(gson.toJson(butacaDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        boolean ok = false;

        if ("insertar".equals(action)) {
            Butaca b = new Butaca();
            Sala s = new Sala();
            s.setId_sala(Integer.parseInt(req.getParameter("id_sala")));
            b.setSala(s);
            b.setFila(req.getParameter("fila"));
            b.setNumero(Integer.parseInt(req.getParameter("numero")));
            b.setEstado(req.getParameter("estado"));
            ok = butacaDao.insertar(b);
        } else if ("cambiarEstado".equals(action)) {
            ok = butacaDao.actualizarEstado(
                Integer.parseInt(req.getParameter("id")),
                req.getParameter("estado")
            );
        } else if ("delete".equals(action)) {
            ok = butacaDao.delete(Integer.parseInt(req.getParameter("id")));
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
