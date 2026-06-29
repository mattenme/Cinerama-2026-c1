package Controllers;

import Interface.ICompra;
import Interface.ICliente;
import Interface.IAsiento;
import Dao.CompraDaoImpl;
import Dao.ClienteDaoImpl;
import Dao.AsientoDaoImpl;
import model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import utils.EmailUtil;
import utils.QRUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "CompraController", urlPatterns = {"/CompraController"})
public class CompraController extends HttpServlet {

    private final ICompra compraDao = new CompraDaoImpl();
    private final ICliente clienteDao = new ClienteDaoImpl();
    private final IAsiento asientoDao = new AsientoDaoImpl();
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
                for (String v : validos) { if (v.equalsIgnoreCase(metodo)) { valido = true; break; } }
                if (!valido) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"M\u00e9todo de pago inv\u00e1lido\"}");
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
                String idAsientoParam = req.getParameter("id_asiento");
                if (idAsientoParam == null || idAsientoParam.trim().isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No hay asientos\"}");
                    return;
                }
                String[] asientoIds = idAsientoParam.split(",");
                int numAsientos = 0;
                for (String bid : asientoIds) { if (!bid.trim().isEmpty()) numAsientos++; }
                if (numAsientos == 0) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No hay asientos\"}");
                    return;
                }
                String productos = req.getParameter("productos");
                String qr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                int lastId = 0;
                boolean firstSeat = true;
                String idClienteStr = req.getParameter("id_cliente");
                if (idClienteStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"Cliente requerido\"}"); return; }
                int idCliente = Integer.parseInt(idClienteStr);
                String idFuncionStr = req.getParameter("id_funcion");
                if (idFuncionStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"Funci\u00f3n requerida\"}"); return; }
                int idFuncion = Integer.parseInt(idFuncionStr);
                List<Compra> compras = new ArrayList<>();
                for (String bid : asientoIds) {
                    if (bid.trim().isEmpty()) continue;
                    Compra c = new Compra();
                    Cliente cli = new Cliente();
                    cli.setId_cliente(idCliente);
                    c.setCliente(cli);
                    Funcion f = new Funcion();
                    f.setId_funcion(idFuncion);
                    c.setFuncion(f);
                    Asiento a = new Asiento();
                    a.setId_asiento(Integer.parseInt(bid.trim()));
                    c.setAsiento(a);
                    c.setPrecio(firstSeat ? precio : 0);
                    c.setMetodo_pago(metodo);
                    c.setEstado(req.getParameter("estado"));
                    c.setCodigo_qr(qr);
                    c.setProductos(productos);
                    compras.add(c);
                    firstSeat = false;
                }
                if (compras.isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No hay asientos v\u00e1lidos\"}");
                    return;
                }
                lastId = compraDao.insertarLote(compras);

                if (lastId > 0) {
                    // Enviar email de confirmaci\u00f3n
                    try {
                        Cliente cli = clienteDao.searchById(idCliente);
                        if (cli != null && cli.getEmail() != null && !cli.getEmail().isEmpty()) {
                            Compra compra = compraDao.searchById(lastId);
                            if (compra != null) {
                                String asientosStr = "";
                                for (Compra c : compras) {
                                    if (!asientosStr.isEmpty()) asientosStr += ", ";
                                    Asiento bd = asientoDao.searchById(c.getAsiento().getId_asiento());
                                    if (bd != null) {
                                        asientosStr += (bd.getFila() != null ? bd.getFila() : "?") + bd.getNumero();
                                    } else {
                                        asientosStr += "#" + c.getAsiento().getId_asiento();
                                    }
                                }
                                String fecha = compra.getFuncion().getHora_inicio() != null
                                    ? compra.getFuncion().getHora_inicio() : "-";
                                String salaNombre = compra.getFuncion().getSala() != null
                                    ? compra.getFuncion().getSala().getNombre() : "-";
                                String peliculaTitulo = compra.getFuncion().getPelicula() != null
                                    ? compra.getFuncion().getPelicula().getTitulo() : "-";
                                byte[] qrBytes = QRUtil.generarQRBytes(qr);
                                EmailUtil.enviarConfirmacionCompra(
                                    cli.getEmail(), cli.getNombre(),
                                    peliculaTitulo, salaNombre, fecha,
                                    asientosStr, precio, qr, qrBytes
                                );
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error enviando email de confirmaci\u00f3n: " + e.getMessage());
                        e.printStackTrace();
                    }
                    resp.getWriter().write("{\"success\":true, \"id\":" + lastId + "}");
                } else {
                    resp.getWriter().write("{\"success\":false, \"id\":0, \"mensaje\":\"Error al procesar la compra. Los asientos pueden no estar disponibles o la sesi\u00f3n expir\u00f3.\"}");
                }
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                String idStr = req.getParameter("id");
                if (idStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}"); return; }
                boolean ok = compraDao.delete(Integer.parseInt(idStr));
                resp.getWriter().write("{\"success\":" + ok + "}");
            } else {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"Acci\u00f3n no v\u00e1lida\"}");
            }
        } catch (NumberFormatException e) {
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID o precio inv\u00e1lido\"}");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":" + gson.toJson(e.getMessage() != null ? e.getMessage() : "Error") + "}");
            return;
        }
    }
}
