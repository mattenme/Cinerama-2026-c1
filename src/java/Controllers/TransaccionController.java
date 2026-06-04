package Controllers;

import Interface.ITransaccion;
import Dao.TransaccionDaoImpl;
import model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "TransaccionController", urlPatterns = {"/TransaccionController"})
public class TransaccionController extends HttpServlet {

    private final ITransaccion transDao = new TransaccionDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        String idCliente = req.getParameter("idCliente");
        if (id != null) {
            resp.getWriter().write(gson.toJson(transDao.searchById(Integer.parseInt(id))));
        } else if (idCliente != null) {
            resp.getWriter().write(gson.toJson(transDao.listarPorCliente(Integer.parseInt(idCliente))));
        } else {
            resp.getWriter().write(gson.toJson(transDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        if ("insertar".equals(action)) {
            Transaccion t = new Transaccion();
            Cliente c = new Cliente();
            c.setId_cliente(Integer.parseInt(req.getParameter("id_cliente")));
            t.setCliente(c);
            Funcion f = new Funcion();
            f.setId_funcion(Integer.parseInt(req.getParameter("id_funcion")));
            t.setFuncion(f);
            t.setMonto_total(Double.parseDouble(req.getParameter("monto_total")));
            t.setMetodo_pago(req.getParameter("metodo_pago"));
            t.setEstado(req.getParameter("estado"));
            t.setCodigo_qr(UUID.randomUUID().toString());
            int id = transDao.insertar(t);
            resp.getWriter().write("{\"success\":" + (id > 0) + ", \"id\":" + id + "}");
        } else if ("delete".equals(action)) {
            boolean ok = transDao.delete(Integer.parseInt(req.getParameter("id")));
            resp.getWriter().write("{\"success\":" + ok + "}");
        }
    }
}
