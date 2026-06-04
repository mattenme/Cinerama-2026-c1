package Controllers;

import Interface.IPelicula;
import Dao.PeliculaDaoImpl;
import model.Pelicula;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PeliculaController", urlPatterns = {"/PeliculaController"})
public class PeliculaController extends HttpServlet {

    private final IPelicula peliculaDao = new PeliculaDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        if (id != null) {
            Pelicula p = peliculaDao.searchById(Integer.parseInt(id));
            resp.getWriter().write(gson.toJson(p));
        } else {
            resp.getWriter().write(gson.toJson(peliculaDao.lista()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        boolean ok = false;

        if ("insertar".equals(action)) {
            Pelicula p = new Pelicula();
            p.setTitulo(req.getParameter("titulo"));
            p.setDuracion_minutos(Integer.parseInt(req.getParameter("duracion_minutos")));
            p.setGenero(req.getParameter("genero"));
            p.setSinopsis(req.getParameter("sinopsis"));
            p.setImagen_url(req.getParameter("imagen_url"));
            p.setTrailer_url(req.getParameter("trailer_url"));
            ok = peliculaDao.insertar(p);
        } else if ("update".equals(action)) {
            Pelicula p = peliculaDao.searchById(Integer.parseInt(req.getParameter("id")));
            if (p != null) {
                p.setTitulo(req.getParameter("titulo"));
                p.setDuracion_minutos(Integer.parseInt(req.getParameter("duracion_minutos")));
                p.setGenero(req.getParameter("genero"));
                p.setSinopsis(req.getParameter("sinopsis"));
                p.setImagen_url(req.getParameter("imagen_url"));
                p.setTrailer_url(req.getParameter("trailer_url"));
                ok = peliculaDao.update(p);
            }
        } else if ("delete".equals(action)) {
            ok = peliculaDao.delete(Integer.parseInt(req.getParameter("id")));
        }
        resp.getWriter().write("{\"success\":" + ok + "}");
    }
}
