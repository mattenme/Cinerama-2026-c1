let editandoId = null;

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

        function mostrarFormulario(data) {
            editandoId = data ? data.id_promocion : null;
            var formHtml = '<form id="form-promo">' +
                '<div class="row g-3">' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">C\u00f3digo:</label><input type="text" class="form-control" id="codigo" placeholder="Ej: CINERAMA20" required style="text-transform:uppercase;"></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Descuento (%):</label><input type="number" class="form-control" id="descuento" min="1" max="100" required></div>' +
                    '<div class="col-md-8"><label class="form-label fw-semibold">Descripci\u00f3n:</label><input type="text" class="form-control" id="descripcion" placeholder="Ej: Descuento especial"></div>' +
                    '<div class="col-md-4"><label class="form-label fw-semibold">Activo:</label><select class="form-select" id="activo"><option value="1">S\u00ed</option><option value="0">No</option></select></div>' +
                '</div>' +
                '<div class="mt-3 d-flex gap-2"><button type="submit" class="btn btn-success">' + (editandoId ? 'Actualizar' : 'Guardar') + '</button><button type="button" class="btn btn-secondary" onclick="closeCrudModal()">Cancelar</button></div>' +
            '</form>';
            openCrudModal(editandoId ? 'Editar Promoci\u00f3n #' + data.id_promocion : 'Nueva Promoci\u00f3n', formHtml, function() {
                guardar();
            });
            if (data) {
                document.getElementById('codigo').value = data.codigo || '';
                document.getElementById('descripcion').value = data.descripcion || '';
                document.getElementById('descuento').value = data.descuento || '';
                document.getElementById('activo').value = data.activo;
            }
        }

        function ocultarFormulario() { closeCrudModal(); }

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
            const tbody = document.querySelector('#tabla-promociones tbody');
            Api.promocion.listar().then(todas => {
                totalItems = todas.length;
                var items = todas.slice(paginaActual * filasPorPagina, (paginaActual + 1) * filasPorPagina);
                tbody.innerHTML = items.map(p => {
                    const badge = p.activo == 1 ? 'bg-success' : 'bg-danger';
                    return `<tr>
                        <td>${escapeHtml(p.id_promocion)}</td>
                        <td><strong>${escapeHtml(p.codigo)}</strong></td>
                        <td>${escapeHtml(p.descripcion) || '<span class="text-muted">\u2014</span>'}</td>
                        <td><span class="badge bg-warning text-dark">${escapeHtml(p.descuento)}%</span></td>
                        <td><span class="badge ${p.activo == 1 ? 'bg-success' : 'bg-danger'}" style="cursor:pointer;" onclick="toggleActivoPromo(${escapeHtml(p.id_promocion)})">${p.activo == 1 ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editar(${escapeHtml(p.id_promocion)})">${iconSVG('edit')}</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${escapeHtml(p.id_promocion)})">${iconSVG('trash')}</button>
                        </td>
                    </tr>`;
                }).join('');
                actualizarPaginacion();
            }).catch(() => {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Error al cargar</td></tr>';
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

        window.guardar = function() {
            const datos = {
                codigo: document.getElementById('codigo').value.toUpperCase(),
                descripcion: document.getElementById('descripcion').value,
                descuento: document.getElementById('descuento').value,
                activo: document.getElementById('activo').value
            };
            const promesa = editandoId
                ? Api.promocion.actualizar(editandoId, datos)
                : Api.promocion.insertar(datos);
            promesa.then(r => {
                if (r.success) { showSuccess(editandoId ? 'Promoci\u00F3n actualizada' : 'Promoci\u00F3n guardada'); ocultarFormulario(); cargarTabla(); }
                else showError(r.mensaje || 'Error');
            }).catch(() => showError('Error de conexi\u00F3n'));
        };

        window.editar = function(id) {
            Api.promocion.buscar(id).then(p => { if (p) mostrarFormulario(p); }).catch(function(e) { console.error(e); });
        };

        window.toggleActivoPromo = function(id) {
            Api.promocion.toggleActivo(id).then(r => { if (r.success) cargarTabla(); }).catch(function() { showError('Error de conexi\u00F3n'); });
        };

        window.eliminar = function(id) {
            showConfirm('\u00BFEliminar promoci\u00F3n #' + id + '?', function() {
                Api.promocion.eliminar(id).then(r => { if (r.success) cargarTabla(); }).catch(function() { showError('Error de conexi\u00F3n'); });
            });
        };
