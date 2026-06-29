document.addEventListener('DOMContentLoaded', function() {
            if (!localStorage.getItem('clienteId') || localStorage.getItem('clienteRol') !== 'admin') { window.location.href = '../login.html'; return; }
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
            Api.cliente.listar().then(todas => {
                totalItems = todas.length;
                var items = todas.slice(paginaActual * filasPorPagina, (paginaActual + 1) * filasPorPagina);
                tbody.innerHTML = items.map(c => {
                    return `<tr>
                        <td>${c.id_cliente}</td>
                        <td>${c.dni || '-'}</td>
                        <td><strong>${c.nombre || '-'}</strong></td>
                        <td>${c.email || '<span class="text-muted">?</span>'}</td>
                        <td>${c.telefono || '<span class="text-muted">?</span>'}</td>
                        <td><span class="badge ${c.activo == 1 ? 'bg-success' : 'bg-danger'}" style="cursor:pointer;" onclick="toggleActivo(${c.id_cliente})">${c.activo == 1 ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${c.id_cliente})">${iconSVG('trash')}</button>
                        </td>
                    </tr>`;
                }).join('');
                actualizarPaginacion();
            }).catch(() => {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Error al cargar</td></tr>';
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

        window.toggleActivo = function(id) {
            Api.cliente.toggleActivo(id).then(r => { if (r.success) cargarTabla(); });
        };

        window.eliminar = function(id) {
            showConfirm('\u00BFEliminar cliente #' + id + '?', function() {
                Api.cliente.eliminar(id).then(r => { if (r.success) cargarTabla(); else showError(r.mensaje || 'Error al eliminar'); });
            });
        };
