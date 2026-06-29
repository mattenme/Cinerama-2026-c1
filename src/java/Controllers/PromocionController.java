package Controllers;

import Interface.IPromocion;
import Dao.PromocionDaoImpl;
import model.Promocion;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "PromocionController", urlPatterns = {"/PromocionController"})
public class PromocionController extends HttpServlet {

    private final IPromocion promoDao = new PromocionDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String codigo = req.getParameter("codigo");
            if (codigo != null && !codigo.isEmpty()) {
                Promocion p = promoDao.buscarPorCodigo(codigo.toUpperCase());
                Map<String, Object> json = new HashMap<>();
                if (p != null) {
                    json.put("success", true);
                    json.put("codigo", p.getCodigo());
                    json.put("descuento", p.getDescuento());
                    json.put("descripcion", p.getDescripcion());
                } else {
                    json.put("success", false);
                    json.put("error", "C\u00f3digo inv\u00e1lido o vencido");
                }
                resp.getWriter().write(gson.toJson(json));
                return;
            }
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    Promocion p = promoDao.buscarPorId(Integer.parseInt(idStr));
                    if (p != null) {
                        resp.getWriter().write(gson.toJson(p));
                    } else {
                        resp.getWriter().write("{\"success\":false,\"error\":\"No encontrado\"}");
                    }
                } catch (NumberFormatException e) {
                    resp.getWriter().write("{\"success\":false,\"error\":\"ID inv\u00e1lido\"}");
                }
                return;
            }
            resp.getWriter().write(gson.toJson(promoDao.listar()));
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"error\":\"Error interno\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String action = req.getParameter("action");
            if (action == null) {
                resp.getWriter().write("{\"success\":false,\"mensaje\":\"Acci\u00f3n no especificada\"}");
                return;
            }
            switch (action) {
                case "insertar": {
                    String codigo = req.getParameter("codigo");
                    if (codigo == null || codigo.isEmpty()) {
                        resp.getWriter().write("{\"success\":false,\"mensaje\":\"C\u00f3digo requerido\"}");
                        return;
                    }
                    Promocion p = new Promocion();
                    p.setCodigo(codigo.toUpperCase());
                    p.setDescripcion(req.getParameter("descripcion"));
                    String desc = req.getParameter("descuento");
                    p.setDescuento(desc != null ? Integer.parseInt(desc) : 0);
                    p.setActivo(1);
                    promoDao.insertar(p);
                    resp.getWriter().write("{\"success\":true,\"mensaje\":\"Promoci\u00f3n guardada\"}");
                    break;
                }
                case "update": {
                    String idStr = req.getParameter("id");
                    if (idStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}"); return; }
                    int id = Integer.parseInt(idStr);
                    Promocion p = promoDao.buscarPorId(id);
                    if (p == null) {
                        resp.getWriter().write("{\"success\":false,\"mensaje\":\"No encontrado\"}");
                        return;
                    }
                    String codigo = req.getParameter("codigo");
                    if (codigo != null) p.setCodigo(codigo.toUpperCase());
                    if (req.getParameter("descripcion") != null) p.setDescripcion(req.getParameter("descripcion"));
                    String d = req.getParameter("descuento");
                    if (d != null) p.setDescuento(Integer.parseInt(d));
                    String act = req.getParameter("activo");
                    if (act != null) p.setActivo(Integer.parseInt(act));
                    promoDao.actualizar(p);
                    resp.getWriter().write("{\"success\":true,\"mensaje\":\"Promoci\u00f3n actualizada\"}");
                    break;
                }
                case "toggleActivo": {
                    String idStr2 = req.getParameter("id");
                    if (idStr2 == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}"); return; }
                    promoDao.toggleActivo(Integer.parseInt(idStr2));
                    resp.getWriter().write("{\"success\":true,\"mensaje\":\"Estado cambiado\"}");
                    break;
                }
                case "delete": {
                    if (!utils.AuthUtil.esAdmin(req)) {
                        resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                        return;
                    }
                    String idStr = req.getParameter("id");
                    if (idStr == null) { resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}"); return; }
                    promoDao.eliminar(Integer.parseInt(idStr));
                    resp.getWriter().write("{\"success\":true,\"mensaje\":\"Eliminada\"}");
                    break;
                }
                default:
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Acci\u00f3n no v\u00e1lida\"}");
            }
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID o descuento inv\u00e1lido\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
