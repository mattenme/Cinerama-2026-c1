package Controllers;

import Interface.ICliente;
import Dao.ClienteDaoImpl;
import model.Cliente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ClienteController", urlPatterns = {"/ClienteController"})
public class ClienteController extends HttpServlet {

    private final ICliente clienteDao = new ClienteDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            String dni = req.getParameter("dni");
            if (id != null) {
                resp.getWriter().write(gson.toJson(clienteDao.searchById(Integer.parseInt(id))));
            } else if (dni != null) {
                Cliente c = clienteDao.searchByDni(dni);
                if (c != null) {
                    boolean esAdmin = "00000000".equals(c.getDni());
                    HttpSession session = req.getSession(true);
                    session.setAttribute("clienteId", c.getId_cliente());
                    session.setAttribute("admin", esAdmin);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id_cliente", c.getId_cliente());
                    map.put("dni", c.getDni());
                    map.put("nombre", c.getNombre());
                    map.put("email", c.getEmail());
                    map.put("telefono", c.getTelefono());
                    map.put("esAdmin", esAdmin);
                    resp.getWriter().write(gson.toJson(map));
                } else {
                    resp.getWriter().write("null");
                }
            } else {
                resp.getWriter().write(gson.toJson(clienteDao.lista()));
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
        try {
            String action = req.getParameter("action");
            if ("insertar".equals(action)) {
                Cliente c = new Cliente();
                c.setDni(req.getParameter("dni"));
                c.setNombre(req.getParameter("nombre"));
                c.setEmail(req.getParameter("email"));
                c.setTelefono(req.getParameter("telefono"));
                int id = clienteDao.insertar(c);
                resp.getWriter().write("{\"success\":true, \"id\":" + id + "}");
            } else if ("update".equals(action)) {
                Cliente c = clienteDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (c != null) {
                    if (req.getParameter("nombre") != null) c.setNombre(req.getParameter("nombre"));
                    if (req.getParameter("email") != null) c.setEmail(req.getParameter("email"));
                    if (req.getParameter("telefono") != null) c.setTelefono(req.getParameter("telefono"));
                    boolean ok = clienteDao.update(c);
                    resp.getWriter().write("{\"success\":" + ok + "}");
                } else {
                    resp.getWriter().write("{\"success\":false}");
                }
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                boolean ok = clienteDao.delete(Integer.parseInt(req.getParameter("id")));
                resp.getWriter().write("{\"success\":" + ok + "}");
            }
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID inv\u00e1lido\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Error interno\"}");
            e.printStackTrace();
        }
    }
}
