package Controllers;

import Interface.IProducto;
import Dao.ProductoDaoImpl;
import model.Producto;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

@WebServlet(name = "ProductoController", urlPatterns = {"/ProductoController"})
@MultipartConfig(maxFileSize = 52428800, fileSizeThreshold = 2097152, maxRequestSize = 52428800)
public class ProductoController extends HttpServlet {

    private final IProducto prodDao = new ProductoDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            String activos = req.getParameter("activos");
            if (id != null) {
                resp.getWriter().write(gson.toJson(prodDao.searchById(Integer.parseInt(id))));
            } else if ("true".equals(activos)) {
                resp.getWriter().write(gson.toJson(prodDao.listarActivos()));
            } else {
                resp.getWriter().write(gson.toJson(prodDao.lista()));
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
                Producto p = new Producto();
                p.setNombre(req.getParameter("nombre"));
                p.setDescripcion(req.getParameter("descripcion"));
                p.setPrecio(Double.parseDouble(req.getParameter("precio")));
                p.setCategoria(req.getParameter("categoria"));
                p.setActivo("1".equals(req.getParameter("activo")) || "true".equals(req.getParameter("activo")) ? 1 : 0);
                String img = subirImagen(req, req.getContextPath());
                p.setImagen_url(img != null ? img : req.getParameter("imagen_url"));
                int id = prodDao.insertar(p);
                ok = id > 0;
            } else if ("update".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                Producto p = prodDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (p != null) {
                    p.setNombre(req.getParameter("nombre"));
                    p.setDescripcion(req.getParameter("descripcion"));
                    p.setPrecio(Double.parseDouble(req.getParameter("precio")));
                    p.setCategoria(req.getParameter("categoria"));
                    p.setActivo("1".equals(req.getParameter("activo")) || "true".equals(req.getParameter("activo")) ? 1 : 0);
                    String img = subirImagen(req, req.getContextPath());
                    if (img != null) {
                        borrarImagenAnterior(p.getImagen_url(), req);
                        p.setImagen_url(img);
                    } else if (req.getParameter("imagen_url") != null && !req.getParameter("imagen_url").isEmpty()) {
                        p.setImagen_url(req.getParameter("imagen_url"));
                    }
                    ok = prodDao.update(p);
                }
            } else if ("toggleActivo".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                ok = prodDao.toggleActivo(Integer.parseInt(req.getParameter("id")));
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                Producto p = prodDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (p != null) {
                    ok = prodDao.delete(p.getId_producto());
                    if (ok) borrarImagenAnterior(p.getImagen_url(), req);
                }
            }
        } catch (NumberFormatException e) {
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID o precio inv\u00e1lido\"}");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":" + gson.toJson(e.getMessage() != null ? e.getMessage() : "Error") + "}");
            return;
        }
        if (!resp.isCommitted()) {
            resp.getWriter().write("{\"success\":" + ok + ",\"mensaje\":" + gson.toJson(ok ? "Operaci\u00f3n exitosa" : "Error al realizar la operaci\u00f3n") + "}");
        }
    }

    private String rootPath() {
        String r = getServletContext().getRealPath("/");
        return r.endsWith(File.separator) ? r : r + File.separator;
    }

    private String subirImagen(HttpServletRequest req, String ctxPath) throws ServletException, IOException {
        String ct = req.getContentType();
        if (ct == null || !ct.toLowerCase().startsWith("multipart/")) return null;
        Part filePart = req.getPart("imagen");
        if (filePart == null || filePart.getSize() == 0) return null;
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String dir = rootPath() + "assets" + File.separator + "img" + File.separator + "comida";
        new File(dir).mkdirs();
        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        String ext = fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() : "jpg";
        String format = ext.equals("jpeg") ? "jpg" : (ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("wbmp") ? ext : "jpg");
        File outFile = new File(dir + File.separator + uniqueName);
        BufferedImage original = ImageIO.read(filePart.getInputStream());
        if (original != null) {
            int maxW = 800, maxH = 800;
            int w = original.getWidth(), h = original.getHeight();
            if (w > maxW || h > maxH) {
                double ratio = Math.min((double) maxW / w, (double) maxH / h);
                int nw = (int) (w * ratio), nh = (int) (h * ratio);
                BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(original, 0, 0, nw, nh, null);
                g.dispose();
                ImageIO.write(resized, format, outFile);
            } else {
                ImageIO.write(original, format, outFile);
            }
        } else {
            return null;
        }
        return ctxPath + "/assets/img/comida/" + uniqueName;
    }

    private void borrarImagenAnterior(String ruta, HttpServletRequest req) {
        if (ruta == null || ruta.startsWith("http")) return;
        try {
            String ctx = req.getContextPath();
            String relative = ruta;
            if (relative.startsWith(ctx + "/")) relative = relative.substring(ctx.length() + 1);
            else if (relative.startsWith("/")) relative = relative.substring(1);
            String pathFile = rootPath() + relative.replace("/", File.separator);
            new File(pathFile).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
