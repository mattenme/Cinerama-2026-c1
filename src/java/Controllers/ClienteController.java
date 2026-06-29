package Controllers;

import Interface.ICliente;
import Dao.ClienteDaoImpl;
import model.Cliente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import utils.BCrypt;
import utils.EmailUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ClienteController", urlPatterns = {"/ClienteController"})
public class ClienteController extends HttpServlet {

    private final ICliente clienteDao = new ClienteDaoImpl();
    private final Gson gson = new Gson();

    private String resolverRol(Cliente c) {
        String rol = c.getRol();
        if (rol == null || "cliente".equals(rol)) {
            if ("00000000".equals(c.getDni())) return "admin";
            return "cliente";
        }
        return rol;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String action = req.getParameter("action");
            if ("logout".equals(action)) {
                HttpSession session = req.getSession(false);
                if (session != null) session.invalidate();
                resp.getWriter().write("{\"success\":true}");
                return;
            }
            if ("login".equals(action)) {
                String dni = req.getParameter("dni");
                String contrasena = req.getParameter("contrasena");

                if (dni == null || contrasena == null || dni.isEmpty() || contrasena.isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"DNI y contrase\u00f1a requeridos\"}");
                    return;
                }

                Cliente c = clienteDao.autenticar(dni, contrasena);
                if (c != null) {
                    if (c.getVerificado() == 0 && !"00000000".equals(c.getDni())) {
                        resp.getWriter().write("{\"success\":false,\"needVerify\":true,\"id\":" + c.getId_cliente() + ",\"mensaje\":\"Debes verificar tu correo electr\u00f3nico\"}");
                        return;
                    }

                    String rol = resolverRol(c);
                    HttpSession session = req.getSession(true);
                    session.setAttribute("clienteId", c.getId_cliente());
                    session.setAttribute("rol", rol);

                    Map<String, Object> map = new HashMap<>();
                    map.put("success", true);
                    map.put("id_cliente", c.getId_cliente());
                    map.put("dni", c.getDni());
                    map.put("nombre", c.getNombre());
                    map.put("email", c.getEmail());
                    map.put("telefono", c.getTelefono());
                    map.put("rol", rol);
                    resp.getWriter().write(gson.toJson(map));
                } else {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Credenciales incorrectas\"}");
                }
                return;
            }

            String id = req.getParameter("id");
            String dni = req.getParameter("dni");
            if (id != null) {
                Cliente c = clienteDao.searchById(Integer.parseInt(id));
                if (c != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id_cliente", c.getId_cliente());
                    map.put("dni", c.getDni());
                    map.put("nombre", c.getNombre());
                    map.put("email", c.getEmail());
                    map.put("telefono", c.getTelefono());
                    map.put("rol", resolverRol(c));
                    resp.getWriter().write(gson.toJson(map));
                } else {
                    resp.getWriter().write("null");
                }
            } else if (dni != null) {
                Cliente c = clienteDao.searchByDni(dni);
                if (c != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id_cliente", c.getId_cliente());
                    map.put("dni", c.getDni());
                    map.put("nombre", c.getNombre());
                    map.put("email", c.getEmail());
                    map.put("telefono", c.getTelefono());
                    map.put("rol", resolverRol(c));
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
                String dni = req.getParameter("dni");
                String nombre = req.getParameter("nombre");
                String email = req.getParameter("email");
                String contrasena = req.getParameter("contrasena");

                if (dni == null || nombre == null || email == null || contrasena == null ||
                    dni.isEmpty() || nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"DNI, nombre, email y contrase\u00f1a requeridos\"}");
                    return;
                }

                // Verificar DNI duplicado
                if (clienteDao.searchByDni(dni) != null) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"El DNI ya est\u00e1 registrado\"}");
                    return;
                }

                boolean primerUsuario = !clienteDao.existeAdmin();
                String codigo = primerUsuario ? null : String.format("%06d", (int)(Math.random() * 1000000));
                Cliente c = new Cliente();
                c.setDni(dni);
                c.setNombre(nombre);
                c.setEmail(email);
                c.setTelefono(req.getParameter("telefono"));
                c.setContrasena(contrasena);
                c.setRol(primerUsuario ? "admin" : "cliente");
                c.setActivo(1);
                c.setVerificado(primerUsuario ? 1 : 0);
                c.setCodigoVerificacion(codigo);
                int id = clienteDao.insertar(c);

                if (id > 0) {
                    if (!primerUsuario) {
                        try {
                            EmailUtil.enviarCodigoVerificacion(email, codigo);
                        } catch (Exception ex) {
                            System.err.println("Error enviando codigo de verificación: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    resp.getWriter().write("{\"success\":true, \"id\":" + id + "}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Error al registrar usuario\"}");
                }
            } else if ("verificar".equals(action)) {
                String idStr = req.getParameter("id");
                String codigo = req.getParameter("codigo");
                if (idStr == null || codigo == null || codigo.isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"C\u00f3digo requerido\"}");
                    return;
                }
                int idCliente = Integer.parseInt(idStr);
                Cliente c = clienteDao.searchById(idCliente);
                if (c == null) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Cliente no encontrado\"}");
                    return;
                }
                if (codigo.equals(c.getCodigoVerificacion())) {
                    clienteDao.verificarCliente(idCliente);
                    resp.getWriter().write("{\"success\":true}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"C\u00f3digo incorrecto\"}");
                }
            } else if ("reenviarCodigo".equals(action)) {
                String idStr = req.getParameter("id");
                if (idStr == null) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID requerido\"}");
                    return;
                }
                int idCliente = Integer.parseInt(idStr);
                Cliente c = clienteDao.searchById(idCliente);
                if (c == null) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Cliente no encontrado\"}");
                    return;
                }
                String nuevoCodigo = String.format("%06d", (int)(Math.random() * 1000000));
                clienteDao.guardarCodigoVerificacion(idCliente, nuevoCodigo);
                EmailUtil.enviarCodigoVerificacion(c.getEmail(), nuevoCodigo);
                resp.getWriter().write("{\"success\":true}");
            } else if ("update".equals(action)) {
                Cliente c = clienteDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (c != null) {
                    if (req.getParameter("nombre") != null) c.setNombre(req.getParameter("nombre"));
                    if (req.getParameter("email") != null) c.setEmail(req.getParameter("email"));
                    if (req.getParameter("telefono") != null) c.setTelefono(req.getParameter("telefono"));
                    String pwActual = req.getParameter("contrasena_actual");
                    String pwNueva = req.getParameter("contrasena");
                    if (pwNueva != null && !pwNueva.isEmpty()) {
                        if (pwActual == null || pwActual.isEmpty()) {
                            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Ingresa tu contrase\u00f1a actual\"}");
                            return;
                        }
                        String hashGuardado = c.getContrasena();
                        if (hashGuardado == null || !BCrypt.checkpw(pwActual, hashGuardado)) {
                            resp.getWriter().write("{\"success\":false,\"mensaje\":\"Contrase\u00f1a actual incorrecta\"}");
                            return;
                        }
                        c.setContrasena(BCrypt.hashpw(pwNueva, BCrypt.gensalt()));
                    }
                    boolean ok = clienteDao.update(c);
                    resp.getWriter().write("{\"success\":" + ok + "}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Cliente no encontrado\"}");
                }
            } else if ("testEmail".equals(action)) {
                String testEmail = req.getParameter("email");
                if (testEmail == null || testEmail.isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"Email requerido\"}");
                    return;
                }
                String testCodigo = "123456";
                boolean ok = EmailUtil.enviarCodigoVerificacion(testEmail, testCodigo);
                resp.getWriter().write("{\"success\":" + ok + ",\"mensaje\":\"" + (ok ? "Correo enviado" : "Error al enviar correo - revisa logs del servidor") + "\"}");
            } else if ("toggleActivo".equals(action)) {
                boolean ok = clienteDao.toggleActivo(Integer.parseInt(req.getParameter("id")));
                resp.getWriter().write("{\"success\":" + ok + "}");
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
