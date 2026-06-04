package Controllers;

import Interface.IBoletoDetalle;
import Dao.BoletoDetalleDaoImpl;
import model.BoletoDetalle;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "BoletoDetalleController", urlPatterns = {"/BoletoDetalleController"})
public class BoletoDetalleController extends HttpServlet {

    private final IBoletoDetalle boletoDao = new BoletoDetalleDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idTrans = req.getParameter("idTransaccion");
        if (idTrans != null) {
            List<BoletoDetalle> lista = boletoDao.listarPorTransaccion(Integer.parseInt(idTrans));
            resp.getWriter().write(gson.toJson(lista));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        boolean ok = false;

        if ("marcarUsado".equals(action)) {
            ok = boletoDao.marcarQrUsado(Integer.parseInt(req.getParameter("id")));
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
