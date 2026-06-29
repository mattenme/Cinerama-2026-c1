const CONTROLLERS = {
    pelicula: '/PeliculaController',
    sala: '/SalaController',
    funcion: '/FuncionController',
    asiento: '/AsientoController',
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
        listar: (start, limit) => {
            var url = base;
            if (start !== undefined && limit !== undefined) url += '?start=' + start + '&limit=' + limit;
            return fetch(url).then(checkResponse).then(r => r.json());
        },
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
        },
        toggleActivo: (id) => {
            const params = new URLSearchParams({ action: 'toggleActivo', id: id });
            return fetch(base, postOpts(params)).then(checkResponse).then(r => r.json());
        }
    };
}

const Api = {
    pelicula: Object.assign(crearControlador('pelicula', ['titulo', 'duracion_minutos', 'genero', 'sinopsis', 'imagen_url']), {
        toggleDestacado: (id) => {
            const params = new URLSearchParams({ action: 'toggleDestacado', id: id });
            return fetch(API_URL + '/PeliculaController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        }
    }),
    producto: crearControlador('producto', ['nombre', 'descripcion', 'precio', 'imagen_url', 'categoria', 'activo']),
    sala: crearControlador('sala', ['nombre', 'tipo', 'capacidad_total']),
    funcion: crearControlador('funcion', ['id_pelicula', 'id_sala', 'hora_inicio', 'estado']),
    asiento: crearControlador('asiento', ['id_sala', 'fila', 'numero', 'estado']),
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
        },
        toggleActivo: (id) => {
            const params = new URLSearchParams({ action: 'toggleActivo', id: id });
            return fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params }).then(checkResponse).then(r => r.json());
        }
    }
};

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function(el) {
        try { new bootstrap.Tooltip(el); } catch(e) {}
    });
    var m = document.getElementById('modal-crud');
    if (!m) {
        m = document.createElement('div');
        m.id = 'modal-crud';
        m.className = 'modal fade';
        m.tabIndex = -1;
        m.setAttribute('aria-hidden', 'true');
        m.innerHTML = '<div class="modal-dialog modal-dialog-centered modal-lg"><div class="modal-content bg-dark text-white"><div class="modal-header border-secondary"><h5 class="modal-title" id="modal-crud-titulo"></h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div><div class="modal-body" id="modal-crud-body"></div></div></div>';
        document.body.appendChild(m);
    }
});

function openCrudModal(titulo, formHtml, onSave) {
    document.getElementById('modal-crud-titulo').textContent = titulo;
    document.getElementById('modal-crud-body').innerHTML = formHtml;
    _crudEditandoId = null;
    var modal = new bootstrap.Modal(document.getElementById('modal-crud'));
    modal.show();
    var form = document.querySelector('#modal-crud-body form');
    if (form) {
        form.onsubmit = function(e) {
            e.preventDefault();
            onSave();
        };
    }
}

function closeCrudModal() {
    var modal = bootstrap.Modal.getInstance(document.getElementById('modal-crud'));
    if (modal) modal.hide();
}

window.iconSVG = function(name) {
    var icons = {
        edit: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10z"/></svg>',
        trash: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/><path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1z"/></svg>',
        clock: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg>',
        plus: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/></svg>',
        check: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/></svg>',
        x: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/></svg>'
    };
    return icons[name] || '';
};

function abrirLightbox(src) {
    var el = document.getElementById('imageLightbox');
    if (!el) return;
    el.querySelector('.modal-body img').src = src;
    new bootstrap.Modal(el).show();
}
