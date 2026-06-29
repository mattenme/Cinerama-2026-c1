document.addEventListener('DOMContentLoaded', function() {
    if (!localStorage.getItem('clienteId') || localStorage.getItem('clienteRol') !== 'admin') { window.location.href = '../login.html'; return; }
    fetch(API_URL + '/ClienteController?action=checkAdmin').then(function(r) { return r.json(); }).then(function(d) { if (!d.admin) { window.location.href = '../login.html'; } }).catch(function() { window.location.href = '../login.html'; });
    fetch('../includes/header.html')
        .then(r => r.text())
        .then(d => document.getElementById('header-placeholder').innerHTML = d)
        .catch(e => console.error(e));
    fetch('includes/sidebar.html')
        .then(r => r.text())
        .then(d => {
            document.getElementById('sidebar-placeholder').innerHTML = d;
            var nombre = localStorage.getItem('clienteNombre') || 'Admin';
            var avatarEl = document.getElementById('sidebar-user-avatar');
            var nameEl = document.getElementById('sidebar-user-name');
            if (nameEl) nameEl.textContent = nombre;
            if (avatarEl) avatarEl.textContent = nombre.charAt(0).toUpperCase();
            var page = window.location.pathname.split('/').pop().replace('.html', '');
            document.querySelectorAll('.nav-item[data-page="' + page + '"]').forEach(function(el) { el.classList.add('active'); });
        })
        .catch(e => console.error(e));
    cargarTabla();
});

var paginaActual = 0, filasPorPagina = 10, totalItems = 0;

function actualizarPaginacion() {
    var totalPaginas = Math.ceil(totalItems / filasPorPagina) || 1;
    var el;
    if (el = document.getElementById('pagination-info')) el.textContent = 'Total: ' + totalItems + ' registros';
    if (el = document.getElementById('pagination-page')) el.textContent = (paginaActual + 1) + ' / ' + totalPaginas;
    if (el = document.getElementById('btn-prev')) el.disabled = paginaActual <= 0;
    if (el = document.getElementById('btn-next')) el.disabled = paginaActual >= totalPaginas - 1;
}

window.paginar = function(dir) {
    var nueva = paginaActual + dir;
    if (nueva < 0) return;
    paginaActual = nueva;
    cargarTabla();
};

function cargarTabla() {
    const tbody = document.querySelector('#tabla tbody');
    Api.compra.listar().then(todas => {
        var grupos = {};
        todas.forEach(function(c) {
            var raw = c.codigo_qr || 'cmp_' + c.id_compra;
            var qr = raw.replace(/_\d+$/, '');
            if (!grupos[qr]) {
                grupos[qr] = {
                    id_compra: c.id_compra,
                    cliente: c.cliente,
                    funcion: c.funcion,
                    precio: 0,
                    metodo_pago: c.metodo_pago,
                    estado: c.estado,
                    fecha_compra: c.fecha_compra,
                    codigo_qr: raw,
                    productos: c.productos,
                    asientos: [],
                    filas: []
                };
            }
            var g = grupos[qr];
            g.precio += c.precio || 0;
            if (c.asiento) {
                g.asientos.push(c.asiento);
                g.filas.push(c.asiento.fila + c.asiento.numero);
            }
        });
        var keys = Object.keys(grupos);
        totalItems = keys.length;
        var pageKeys = keys.slice(paginaActual * filasPorPagina, (paginaActual + 1) * filasPorPagina);
        tbody.innerHTML = pageKeys.map(function(qr) {
            var g = grupos[qr];
            const fecha = g.fecha_compra ? new Date(g.fecha_compra) : null;
            const fechaStr = fecha ? fecha.toLocaleString('es-PE') : '-';
            const badgeEstado = { completada: 'bg-success', pendiente: 'bg-warning text-dark', cancelada: 'bg-danger' };
            const badgeMetodo = { 'Tarjeta Visa': 'bg-primary', 'Tarjeta Mastercard': 'bg-primary', Yape: 'bg-purple text-white', Plin: 'bg-info text-dark', Efectivo: 'bg-secondary' };
            var prodStr = '';
            if (g.productos) {
                var parts = g.productos.split(',');
                for (var i = 0; i < parts.length; i++) {
                    var p = parts[i].split(':');
                    if (p.length >= 4) {
                        prodStr += (i > 0 ? ', ' : '') + p[1] + ' x' + p[2];
                    }
                }
            }
            const asientosStr = g.filas.join(', ') || '-';
            return `<tr>
                <td>${escapeHtml(g.id_compra)}</td>
                <td>${escapeHtml(g.cliente ? g.cliente.nombre || '#' + g.cliente.id_cliente : '-')}</td>
                <td>${escapeHtml(g.funcion ? '#' + g.funcion.id_funcion : '-')}</td>
                <td>                ${escapeHtml(asientosStr)}</td>
                <td><strong>S/ ${escapeHtml(g.precio ? g.precio.toFixed(2) : '0.00')}</strong></td>
                <td><small>${escapeHtml(prodStr || '-')}</small></td>
                <td><span class="badge ${badgeMetodo[g.metodo_pago] || 'bg-secondary'}">${escapeHtml(g.metodo_pago || '-')}</span></td>
                <td><span class="badge ${badgeEstado[g.estado] || 'bg-secondary'}">${escapeHtml(g.estado || '-')}</span></td>
                <td>${escapeHtml(fechaStr)}</td>
                <td style="max-width:80px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${escapeHtml(g.codigo_qr || '')}">${escapeHtml(g.codigo_qr ? g.codigo_qr.substring(0, 8) + '...' : '-')}</td>
                <td><button class="btn btn-sm btn-outline-danger" onclick="eliminarGrupo('${escapeHtml(qr)}')">${iconSVG('trash')}</button></td>
            </tr>`;
        }).join('');
        actualizarPaginacion();
    }).catch(() => {
        tbody.innerHTML = '<tr><td colspan="11" class="text-center text-muted">Error al cargar</td></tr>';
    });
}

function filtrarTabla() {
    var input = document.getElementById('buscador');
    if (!input) return;
    var texto = input.value.toLowerCase();
    document.querySelectorAll('.table tbody tr').forEach(function(tr) {
        var coincide = false;
        tr.querySelectorAll('td').forEach(function(td) {
            if (td.textContent.toLowerCase().includes(texto)) coincide = true;
        });
        tr.style.display = coincide ? '' : 'none';
    });
}

window.eliminarGrupo = function(qr) {
    showConfirm('\u00BFEliminar todas las compras del grupo ' + qr + '?', function() {
        Api.compra.listar().then(function(lista) {
            var ids = [];
            lista.forEach(function(c) {
                var raw = c.codigo_qr || 'cmp_' + c.id_compra;
                var groupKey = raw.replace(/_\d+$/, '');
                if (groupKey === qr) ids.push(c.id_compra);
            });
            if (ids.length === 0) { cargarTabla(); return; }
            var pendientes = ids.length;
            ids.forEach(function(id) {
                Api.compra.eliminar(id).then(function() {
                    pendientes--;
                    if (pendientes === 0) cargarTabla();
                }).catch(function() { showError('Error de conexi\u00F3n'); });
            });
        }).catch(function(e) { console.error(e); });
    });
};
