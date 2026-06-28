package Controllers;

import Interface.IPelicula;
import Dao.PeliculaDaoImpl;
import model.Pelicula;
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

@WebServlet(name = "PeliculaController", urlPatterns = {"/PeliculaController"})
@MultipartConfig(maxFileSize = 52428800, fileSizeThreshold = 2097152, maxRequestSize = 52428800)
public class PeliculaController extends HttpServlet {

    private final IPelicula peliculaDao = new PeliculaDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            if (id != null) {
                Pelicula p = peliculaDao.searchById(Integer.parseInt(id));
                resp.getWriter().write(gson.toJson(p));
            } else {
                resp.getWriter().write(gson.toJson(peliculaDao.lista()));
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
            boolean ok = false;

            if ("insertar".equals(action)) {
                Pelicula p = new Pelicula();
                p.setTitulo(req.getParameter("titulo"));
                p.setDuracion_minutos(Integer.parseInt(req.getParameter("duracion_minutos")));
                p.setGenero(req.getParameter("genero"));
                p.setSinopsis(req.getParameter("sinopsis"));
                String img = subirImagen(req, req.getContextPath());
                p.setImagen_url(img != null ? img : req.getParameter("imagen_url"));
                String dest = req.getParameter("destacado");
                p.setDestacado(dest != null ? Integer.parseInt(dest) : 0);
                ok = peliculaDao.insertar(p);
            } else if ("update".equals(action)) {
                Pelicula p = peliculaDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (p != null) {
                    p.setTitulo(req.getParameter("titulo"));
                    p.setDuracion_minutos(Integer.parseInt(req.getParameter("duracion_minutos")));
                    p.setGenero(req.getParameter("genero"));
                    p.setSinopsis(req.getParameter("sinopsis"));
                    String img = subirImagen(req, req.getContextPath());
                    if (img != null) {
                        borrarImagenAnterior(p.getImagen_url(), req);
                        p.setImagen_url(img);
                    }
                    String dest = req.getParameter("destacado");
                    p.setDestacado(dest != null ? Integer.parseInt(dest) : 0);
                    ok = peliculaDao.update(p);
                }
            } else if ("delete".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"error\":\"No autorizado\"}");
                    return;
                }
                Pelicula p = peliculaDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (p != null) {
                    ok = peliculaDao.delete(p.getId_pelicula());
                    if (ok) borrarImagenAnterior(p.getImagen_url(), req);
                }
            }
            resp.getWriter().write("{\"success\":" + ok + "}");
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"error\":\"ID o duraci\u00f3n inv\u00e1lido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
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
        String dir = rootPath() + "assets" + File.separator + "img" + File.separator + "peliculas";
        new File(dir).mkdirs();
        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        String ext = fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.')).toLowerCase() : ".jpg";
        String format = ext.equals(".png") ? "png" : "jpg";
        File outFile = new File(dir + File.separator + uniqueName);
        BufferedImage original = ImageIO.read(filePart.getInputStream());
        if (original != null) {
            int maxW = 1200, maxH = 1200;
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
        return ctxPath + "/assets/img/peliculas/" + uniqueName;
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
        }
    }
}
