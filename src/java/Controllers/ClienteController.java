package Controllers;

import Interface.ICliente;
import Dao.ClienteDaoImpl;
import model.Cliente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@WebServlet(name = "ClienteController", urlPatterns = {"/ClienteController"})
@MultipartConfig(maxFileSize = 5242880, fileSizeThreshold = 2097152)
public class ClienteController extends HttpServlet {

    private final ICliente clienteDao = new ClienteDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        String dni = req.getParameter("dni");
        if (id != null) {
            resp.getWriter().write(gson.toJson(clienteDao.searchById(Integer.parseInt(id))));
        } else if (dni != null) {
            resp.getWriter().write(gson.toJson(clienteDao.searchByDni(dni)));
        } else {
            resp.getWriter().write(gson.toJson(clienteDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        if ("insertar".equals(action)) {
            Cliente c = new Cliente();
            c.setDni(req.getParameter("dni"));
            c.setNombre(req.getParameter("nombre"));
            c.setEmail(req.getParameter("email"));
            c.setTelefono(req.getParameter("telefono"));
            c.setAvatar_url(subirAvatar(req));
            c.setCancelaciones_acumuladas(0);
            c.setEs_frecuente(false);
            int id = clienteDao.insertar(c);
            resp.getWriter().write("{\"success\":true, \"id\":" + id + "}");
        } else if ("update".equals(action)) {
            Cliente c = clienteDao.searchById(Integer.parseInt(req.getParameter("id")));
            if (c != null) {
                if (req.getParameter("nombre") != null) c.setNombre(req.getParameter("nombre"));
                if (req.getParameter("email") != null) c.setEmail(req.getParameter("email"));
                if (req.getParameter("telefono") != null) c.setTelefono(req.getParameter("telefono"));
                String avatar = subirAvatar(req);
                if (avatar != null) c.setAvatar_url(avatar);
                boolean ok = clienteDao.update(c);
                resp.getWriter().write("{\"success\":" + ok + "}");
            } else {
                resp.getWriter().write("{\"success\":false}");
            }
        } else if ("delete".equals(action)) {
            boolean ok = clienteDao.delete(Integer.parseInt(req.getParameter("id")));
            resp.getWriter().write("{\"success\":" + ok + "}");
        }
    }

    private String subirAvatar(HttpServletRequest req) throws ServletException, IOException {
        String ct = req.getContentType();
        if (ct == null || !ct.toLowerCase().startsWith("multipart/")) return null;
        Part filePart = req.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) return null;
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uploadDir = getServletContext().getRealPath("/") + "uploads" + File.separator + "avatars";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        filePart.write(uploadDir + File.separator + uniqueName);
        return "uploads/avatars/" + uniqueName;
    }
}
