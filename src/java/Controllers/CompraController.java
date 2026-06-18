package Controllers;

import Interface.ICompra;
import Dao.CompraDaoImpl;
import model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "CompraController", urlPatterns = {"/CompraController"})
public class CompraController extends HttpServlet {

    private final ICompra compraDao = new CompraDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            String idCliente = req.getParameter("idCliente");
            if (id != null) {
                resp.getWriter().write(gson.toJson(compraDao.searchById(Integer.parseInt(id))));
            } else if (idCliente != null) {
                resp.getWriter().write(gson.toJson(compraDao.listarPorCliente(Integer.parseInt(idCliente))));
            } else {
                resp.getWriter().write(gson.toJson(compraDao.lista()));
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
        String action = req.getParameter("action");

        try {
        if ("insertar".equals(action)) {
            String metodo = req.getParameter("metodo_pago");
            String[] validos = {"Efectivo","Tarjeta Visa","Tarjeta Mastercard","Yape","Plin"};
            boolean valido = false;
            for (String v : validos) { if (v.equals(metodo)) { valido = true; break; } }
            if (!valido) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"Método de pago inválido\"}");
                return;
            }
            String precioStr = req.getParameter("precio");
            if (precioStr == null || precioStr.isEmpty()) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"Precio requerido\"}");
                return;
            }
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"Precio debe ser mayor a 0\"}");
                return;
            }
            String idButacaParam = req.getParameter("id_butaca");
            if (idButacaParam == null || idButacaParam.trim().isEmpty()) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"No hay butacas\"}");
                return;
            }
            String[] butacaIds = idButacaParam.split(",");
            int numButacas = 0;
            for (String bid : butacaIds) { if (!bid.trim().isEmpty()) numButacas++; }
            if (numButacas == 0) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"No hay butacas\"}");
                return;
            }
            double seatPortion = precio / numButacas;
            String productos = req.getParameter("productos");
            String qr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            int lastId = 0;
            boolean allOk = true;
            boolean first = true;
            String idClienteStr = req.getParameter("id_cliente");
            if (idClienteStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"Cliente requerido\"}"); return; }
            int idCliente = Integer.parseInt(idClienteStr);
            String idFuncionStr = req.getParameter("id_funcion");
            if (idFuncionStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"Funci\u00f3n requerida\"}"); return; }
            int idFuncion = Integer.parseInt(idFuncionStr);
            for (String bid : butacaIds) {
                if (bid.trim().isEmpty()) continue;
                Compra c = new Compra();
                Cliente cli = new Cliente();
                cli.setId_cliente(idCliente);
                c.setCliente(cli);
                Funcion f = new Funcion();
                f.setId_funcion(idFuncion);
                c.setFuncion(f);
                Butaca b = new Butaca();
                b.setId_butaca(Integer.parseInt(bid.trim()));
                c.setButaca(b);
                c.setPrecio(seatPortion);
                c.setMetodo_pago(metodo);
                c.setEstado(req.getParameter("estado"));
                c.setCodigo_qr(first ? qr : qr + "_" + bid.trim());
                c.setProductos(first ? productos : null);
                lastId = compraDao.insertar(c);
                if (lastId <= 0) allOk = false;
                first = false;
            }
            resp.getWriter().write("{\"success\":" + allOk + ", \"id\":" + lastId + "}");
        } else if ("delete".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}"); return; }
            boolean ok = compraDao.delete(Integer.parseInt(idStr));
            resp.getWriter().write("{\"success\":" + ok + "}");
        } else {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Acci\u00f3n no v\u00e1lida\"}");
        }
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID o precio inv\u00e1lido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"" + (e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error") + "\"}");
        }
    }
}
