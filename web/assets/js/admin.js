const CONTROLLERS = {
    pelicula: '/PeliculaController',
    sala: '/SalaController',
    funcion: '/FuncionController',
    butaca: '/ButacaController',
    calificacion: '/CalificacionController',
    incidencia: '/IncidenciaController',
    compra: '/CompraController',
    producto: '/ProductoController',
    promocion: '/PromocionController',
    cliente: '/ClienteController'
};

function checkResponse(r) {
    if (!r.ok) throw new Error('HTTP ' + r.status);
    return r;
}

function crearControlador(tipo, campos) {
    const base = API_URL + CONTROLLERS[tipo];
    const postOpts = (params) => ({
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    });
    return {
        listar: () => fetch(base).then(checkResponse).then(r => r.json()),
        buscar: (id) => fetch(base + '?id=' + id).then(checkResponse).then(r => r.json()),
        insertar: (datos) => {
            const params = new URLSearchParams({ action: 'insertar' });
            campos.forEach(c => { if (datos[c] !== undefined) params.append(c, datos[c]); });
            return fetch(base, postOpts(params)).then(checkResponse).then(r => r.json());
        },
        actualizar: (id, datos) => {
            const params = new URLSearchParams({ action: 'update', id: id });
            campos.forEach(c => { if (datos[c] !== undefined) params.append(c, datos[c]); });
            return fetch(base, postOpts(params)).then(checkResponse).then(r => r.json());
        },
        eliminar: (id) => {
            const params = new URLSearchParams({ action: 'delete', id: id });
            return fetch(base, postOpts(params)).then(checkResponse).then(r => r.json());
        }
    };
}

const Api = {
    pelicula: crearControlador('pelicula', ['titulo', 'duracion_minutos', 'genero', 'sinopsis', 'imagen_url']),
    producto: crearControlador('producto', ['nombre', 'descripcion', 'precio', 'imagen_url', 'categoria', 'activo']),
    sala: crearControlador('sala', ['nombre', 'tipo', 'capacidad_total']),
    funcion: crearControlador('funcion', ['id_pelicula', 'id_sala', 'hora_inicio', 'estado']),
    butaca: crearControlador('butaca', ['id_sala', 'fila', 'numero', 'estado']),
    calificacion: {
        listar: () => fetch(API_URL + '/CalificacionController').then(checkResponse).then(r => r.json()),
        listarPorPelicula: (idP) => fetch(API_URL + '/CalificacionController?id_pelicula=' + idP).then(checkResponse).then(r => r.json()),
        insertar: (datos) => {
            const params = new URLSearchParams({ action: 'insertar' });
            ['id_cliente', 'id_pelicula', 'puntuacion'].forEach(c => { if (datos[c] !== undefined) params.append(c, datos[c]); });
            return fetch(API_URL + '/CalificacionController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        },
        eliminar: (idC, idP) => {
            const params = new URLSearchParams({ action: 'delete', id_cliente: idC, id_pelicula: idP });
            return fetch(API_URL + '/CalificacionController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        }
    },
    promocion: crearControlador('promocion', ['codigo', 'descripcion', 'descuento', 'activo']),
    incidencia: crearControlador('incidencia', ['tipo', 'id_sala', 'id_funcion', 'id_cliente', 'estado']),
    compra: {
        listar: () => fetch(API_URL + '/CompraController').then(checkResponse).then(r => r.json()),
        listarPorCliente: (idC) => fetch(API_URL + '/CompraController?idCliente=' + idC).then(checkResponse).then(r => r.json()),
        buscar: (id) => fetch(API_URL + '/CompraController?id=' + id).then(checkResponse).then(r => r.json()),
        eliminar: (id) => {
            const params = new URLSearchParams({ action: 'delete', id: id });
            return fetch(API_URL + '/CompraController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        }
    },
    cliente: {
        listar: () => fetch(API_URL + '/ClienteController').then(checkResponse).then(r => r.json()),
        buscar: (id) => fetch(API_URL + '/ClienteController?id=' + id).then(checkResponse).then(r => r.json()),
        buscarPorDni: (dni) => fetch(API_URL + '/ClienteController?dni=' + dni).then(checkResponse).then(r => r.json()),
        eliminar: (id) => {
            const params = new URLSearchParams({ action: 'delete', id: id });
            return fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        }
    }
};

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function(el) {
        try { new bootstrap.Tooltip(el); } catch(e) {}
    });
});
