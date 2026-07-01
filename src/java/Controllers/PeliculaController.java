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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                return;
            }

            String startStr = req.getParameter("start");
            String limitStr = req.getParameter("limit");
            if (startStr != null && limitStr != null) {
                int start = Integer.parseInt(startStr);
                int limit = Integer.parseInt(limitStr);
                var map = new java.util.HashMap<String, Object>();
                map.put("data", peliculaDao.listaPaginada(start, limit));
                map.put("total", peliculaDao.contar());
                resp.getWriter().write(gson.toJson(map));
                return;
            }

            resp.getWriter().write(gson.toJson(peliculaDao.lista()));
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
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                Pelicula p = new Pelicula();
                p.setTitulo(req.getParameter("titulo"));
                p.setDuracion_minutos(Integer.parseInt(req.getParameter("duracion_minutos")));
                p.setGenero(req.getParameter("genero"));
                p.setSinopsis(req.getParameter("sinopsis"));
                String img = subirImagen(req, req.getContextPath());
                p.setImagen_url(img != null ? img : req.getParameter("imagen_url"));
                String dest = req.getParameter("destacado");
                p.setDestacado(dest != null ? Integer.parseInt(dest) : 0);
                p.setActivo(1);
                ok = peliculaDao.insertar(p);
            } else if ("update".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
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
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                Pelicula p = peliculaDao.searchById(Integer.parseInt(req.getParameter("id")));
                if (p != null) {
                    ok = peliculaDao.delete(p.getId_pelicula());
                    if (ok) borrarImagenAnterior(p.getImagen_url(), req);
                }
            } else if ("toggleDestacado".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                ok = peliculaDao.toggleDestacado(Integer.parseInt(req.getParameter("id")));
            } else if ("toggleActivo".equals(action)) {
                if (!utils.AuthUtil.esAdmin(req)) {
                    resp.getWriter().write("{\"success\":false,\"mensaje\":\"No autorizado\"}");
                    return;
                }
                ok = peliculaDao.toggleActivo(Integer.parseInt(req.getParameter("id")));
            }
            if (!resp.isCommitted()) {
                resp.getWriter().write("{\"success\":" + ok + ",\"mensaje\":\"" + (ok ? "Operaci\u00f3n exitosa" : "Error al realizar la operaci\u00f3n") + "\"}");
            }
        } catch (NumberFormatException e) {
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":\"ID o duraci\u00f3n inv\u00e1lido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            if (!resp.isCommitted()) resp.getWriter().write("{\"success\":false,\"mensaje\":" + gson.toJson(e.getMessage() != null ? e.getMessage() : "Error") + "}");
        }
    }

    private String rootPath() {
        String r = getServletContext().getRealPath("/");
        return r.endsWith(File.separator) ? r : r + File.separator;
    }

    private String sourcePath() {
        // 1. System property (explicit override)
        String sp = System.getProperty("cinerama.source.webapp");
        if (sp != null && !sp.isEmpty() && new File(sp).isDirectory()) {
            sp = sp.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            if (!sp.endsWith(File.separator)) sp += File.separator;
            return sp;
        }
        // 2. Auto-detect from build/web/../../web/ (NetBeans default structure)
        try {
            File candidate = new File(rootPath() + ".." + File.separator + ".." + File.separator + "web");
            String abs = candidate.getCanonicalPath();
            if (candidate.isDirectory() && new File(abs + File.separator + "WEB-INF").exists()) {
                if (!abs.endsWith(File.separator)) abs += File.separator;
                return abs;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private void copyToSourceDir(String relative) {
        String sp = sourcePath();
        if (sp == null) return;
        try {
            Path src = new File(rootPath() + relative.replace("/", File.separator)).toPath();
            Path dest = new File(sp + relative.replace("/", File.separator)).toPath();
            dest.getParent().toFile().mkdirs();
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("No se pudo copiar a source dir: " + e.getMessage());
        }
    }

    private void deleteFromSourceDir(String relative) {
        String sp = sourcePath();
        if (sp == null) return;
        new File(sp + relative.replace("/", File.separator)).delete();
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
        String ext = fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() : "jpg";
        String format = ext.equals("jpeg") ? "jpg" : (ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("wbmp") ? ext : "jpg");
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
            copyToSourceDir("assets/img/peliculas/" + uniqueName);
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
            deleteFromSourceDir(relative.replace("\\", "/"));
        } catch (Exception e) {
        }
    }
}
