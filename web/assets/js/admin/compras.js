document.addEventListener('DOMContentLoaded', function() {
    if (!localStorage.getItem('clienteId') || localStorage.getItem('clienteAdmin') !== 'true') { window.location.href = '../login.html'; return; }
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

function cargarTabla() {
    const tbody = document.querySelector('#tabla tbody');
    Api.compra.listar().then(lista => {
        var grupos = {};
        lista.forEach(function(c) {
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
                    butacas: [],
                    filas: []
                };
            }
            var g = grupos[qr];
            g.precio += c.precio || 0;
            if (c.butaca) {
                g.butacas.push(c.butaca);
                g.filas.push(c.butaca.fila + c.butaca.numero);
            }
        });
        tbody.innerHTML = Object.keys(grupos).map(function(qr) {
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
            const butacasStr = g.filas.join(', ') || '-';
            return `<tr>
                <td>${g.id_compra}</td>
                <td>${g.cliente ? g.cliente.nombre || '#' + g.cliente.id_cliente : '-'}</td>
                <td>${g.funcion ? '#' + g.funcion.id_funcion : '-'}</td>
                <td>${butacasStr}</td>
                <td><strong>S/ ${g.precio ? g.precio.toFixed(2) : '0.00'}</strong></td>
                <td><small>${prodStr || '-'}</small></td>
                <td><span class="badge ${badgeMetodo[g.metodo_pago] || 'bg-secondary'}">${g.metodo_pago || '-'}</span></td>
                <td><span class="badge ${badgeEstado[g.estado] || 'bg-secondary'}">${g.estado || '-'}</span></td>
                <td>${fechaStr}</td>
                <td style="max-width:80px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${g.codigo_qr || ''}">${g.codigo_qr ? g.codigo_qr.substring(0, 8) + '...' : '-'}</td>
                <td><button class="btn btn-sm btn-outline-danger" onclick="eliminarGrupo('${qr}')">Eliminar</button></td>
            </tr>`;
        }).join('');
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
                });
            });
        });
    });
};
