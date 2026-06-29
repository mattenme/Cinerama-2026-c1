let editandoId = null;

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

        function mostrarFormulario(data) {
            editandoId = data ? data.id_producto : null;
            var formHtml = '<form id="form-producto" enctype="multipart/form-data">' +
                '<div class="row g-3">' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Nombre:</label><input type="text" class="form-control" id="nombre" required></div>' +
                    '<div class="col-md-3"><label class="form-label fw-semibold">Precio (S/):</label><input type="number" step="0.01" class="form-control" id="precio" min="0" required></div>' +
                    '<div class="col-md-3"><label class="form-label fw-semibold">Categor\u00eda:</label><select class="form-select" id="categoria"><option value="Comida">Comida</option><option value="Bebida">Bebida</option><option value="Combo">Combo</option></select></div>' +
                    '<div class="col-12"><label class="form-label fw-semibold">Descripci\u00f3n:</label><textarea class="form-control" id="descripcion" rows="2"></textarea></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Imagen:</label><input type="file" class="form-control mb-2" id="imagen" accept="image/*"><input type="url" class="form-control" id="imagen_url" placeholder="O pega una URL (https://...)"></div>' +
                    '<div class="col-md-6 d-flex align-items-end pb-2"><div class="form-check"><input type="checkbox" class="form-check-input" id="activo" checked><label class="form-check-label fw-semibold" for="activo">Activo</label></div></div>' +
                '</div>' +
                '<div class="mt-3 d-flex gap-2"><button type="submit" class="btn btn-success">' + (editandoId ? 'Actualizar' : 'Guardar') + '</button><button type="button" class="btn btn-secondary" onclick="closeCrudModal()">Cancelar</button></div>' +
            '</form>';
            openCrudModal(editandoId ? 'Editar Producto #' + data.id_producto : 'Nuevo Producto', formHtml, function() {
                guardar();
            });
            if (data) {
                document.getElementById('nombre').value = data.nombre || '';
                document.getElementById('precio').value = data.precio || '';
                document.getElementById('categoria').value = data.categoria || 'Comida';
                document.getElementById('descripcion').value = data.descripcion || '';
                document.getElementById('imagen_url').value = data.imagen_url || '';
                document.getElementById('activo').checked = data.activo === true || data.activo === 'true';
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
            const tbody = document.querySelector('#tabla tbody');
            Api.producto.listar().then(todas => {
                totalItems = todas.length;
                var items = todas.slice(paginaActual * filasPorPagina, (paginaActual + 1) * filasPorPagina);
                tbody.innerHTML = items.map(p => {
                    const badgeCat = { Comida: 'bg-warning text-dark', Bebida: 'bg-info text-dark', Combo: 'bg-danger text-white' };
                    return `<tr>
                        <td>${p.id_producto}</td>
                        <td>${p.imagen_url ? '<img src="' + p.imagen_url + '" alt="img" class="admin-img-thumb" style="width:40px;height:40px;object-fit:cover;border-radius:4px;" onclick="abrirLightbox(\'' + p.imagen_url.replace(/'/g, "\\'") + '\')" onerror="this.style.display=\'none\'">' : '<span class="text-muted">?</span>'}</td>
                        <td><strong>${p.nombre}</strong></td>
                        <td>${p.descripcion || '<span class="text-muted">?</span>'}</td>
                        <td><strong>S/ ${(p.precio || 0).toFixed(2)}</strong></td>
                        <td><span class="badge ${badgeCat[p.categoria] || 'bg-secondary'}">${p.categoria || '-'}</span></td>
                        <td><span class="badge ${p.activo ? 'bg-success' : 'bg-danger'}" style="cursor:pointer;" onclick="toggleActivoProducto(${p.id_producto})">${p.activo ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editar(${p.id_producto})">${iconSVG('edit')}</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${p.id_producto})">${iconSVG('trash')}</button>
                        </td>
                    </tr>`;
                }).join('');
                actualizarPaginacion();
            }).catch(() => {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Error al cargar</td></tr>';
            });
        }

        window.toggleActivoProducto = function(id) {
            Api.producto.toggleActivo(id).then(r => { if (r.success) cargarTabla(); });
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
            const fd = new FormData();
            fd.append('action', editandoId ? 'update' : 'insertar');
            if (editandoId) fd.append('id', editandoId);
            fd.append('nombre', document.getElementById('nombre').value);
            fd.append('precio', document.getElementById('precio').value);
            fd.append('categoria', document.getElementById('categoria').value);
            fd.append('descripcion', document.getElementById('descripcion').value);
            fd.append('imagen_url', document.getElementById('imagen_url').value);
            fd.append('activo', document.getElementById('activo').checked ? '1' : '0');
            var fileInput = document.getElementById('imagen');
            if (fileInput.files.length > 0) fd.append('imagen', fileInput.files[0]);
            fetch(API_URL + '/ProductoController', { method: 'POST', body: fd })
                .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
                .then(r => {
                    if (r.success) { showSuccess(editandoId ? 'Producto actualizado' : 'Producto guardado'); ocultarFormulario(); cargarTabla(); }
                    else showError(r.mensaje || 'Error');
                }).catch(() => showError('Error de conexi\u00F3n'));
        };

        window.eliminar = function(id) {
            showConfirm('\u00BFEliminar producto #' + id + '?', function() {
                Api.producto.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            });
        };

        window.editar = function(id) {
            Api.producto.buscar(id).then(p => { if (p) mostrarFormulario(p); });
        };
