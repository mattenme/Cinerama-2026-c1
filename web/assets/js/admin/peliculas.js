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
            var isEdit = data != null;
            editandoId = isEdit ? data.id_pelicula : null;
            var formHtml = '<form id="form-pelicula" enctype="multipart/form-data">' +
                '<div class="row g-3">' +
                    '<div class="col-12"><label class="form-label fw-semibold">T\u00edtulo:</label><input type="text" class="form-control" id="titulo" required></div>' +
                    '<div class="col-6"><label class="form-label fw-semibold">Duraci\u00f3n (minutos):</label><input type="number" class="form-control" id="duracion_minutos" min="30" max="300" required></div>' +
                    '<div class="col-6"><label class="form-label fw-semibold">G\u00e9nero:</label><input type="text" class="form-control" id="genero" placeholder="Ej: Acci\u00f3n, Drama"></div>' +
                    '<div class="col-12"><label class="form-label fw-semibold">Sinopsis:</label><textarea class="form-control" id="sinopsis" rows="3" placeholder="Breve descripci\u00f3n de la pel\u00edcula"></textarea></div>' +
                    '<div class="col-6"><label class="form-label fw-semibold">Imagen:</label><input type="file" class="form-control mb-2" id="imagen" accept="image/*"><input type="url" class="form-control" id="imagen_url" placeholder="O pega una URL (https://...)"></div>' +
                    '<div class="col-6 d-flex align-items-end"><div class="form-check"><input type="checkbox" class="form-check-input" id="destacado" value="1"><label class="form-check-label fw-semibold" for="destacado">Mostrar en p\u00e1gina principal</label></div></div>' +
                '</div>' +
                '<div class="mt-3 d-flex gap-2"><button type="submit" class="btn btn-success">' + (isEdit ? 'Actualizar' : 'Guardar') + '</button><button type="button" class="btn btn-secondary" onclick="closeCrudModal()">Cancelar</button></div>' +
            '</form>';
            openCrudModal(isEdit ? 'Editar Pel\u00edcula #' + data.id_pelicula : 'Nueva Pel\u00edcula', formHtml, function() {
                guardarPelicula();
            });
            if (isEdit) {
                document.getElementById('titulo').value = data.titulo || '';
                document.getElementById('duracion_minutos').value = data.duracion_minutos || '';
                document.getElementById('genero').value = data.genero || '';
                document.getElementById('sinopsis').value = data.sinopsis || '';
                document.getElementById('imagen_url').value = data.imagen_url || '';
                document.getElementById('destacado').checked = data.destacado == 1;
            }
        }

        function ocultarFormulario() {
            closeCrudModal();
        }

        var paginaActual = 0;
        var filasPorPagina = 10;
        var totalItems = 0;

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-peliculas tbody');
            Api.pelicula.listar(paginaActual * filasPorPagina, filasPorPagina).then(resp => {
                var peliculas = resp.data || resp;
                totalItems = resp.total || peliculas.length;
                tbody.innerHTML = peliculas.map(p => `
                    <tr>
                        <td>${p.id_pelicula}</td>
                        <td>${p.imagen_url ? '<img src="' + p.imagen_url + '" alt="img" class="admin-img-thumb" style="width:40px;height:60px;object-fit:cover;border-radius:4px;" onclick="abrirLightbox(\'' + p.imagen_url.replace(/'/g, "\\'") + '\')">' : '<span class="text-muted">?</span>'}</td>
                        <td><strong>${p.titulo}</strong></td>
                        <td>${p.duracion_minutos || '-'} min</td>
                        <td>${p.genero || '<span class="text-muted">?</span>'}</td>
                        <td><span class="badge ${p.destacado == 1 ? 'bg-warning text-dark' : 'bg-secondary'}" style="cursor:pointer;" onclick="toggleDestacadoPelicula(${p.id_pelicula})">${p.destacado == 1 ? 'S\u00ED' : 'No'}</span></td>
                        <td><span class="badge ${p.activo == 1 ? 'bg-success' : 'bg-danger'}" style="cursor:pointer;" onclick="toggleActivoPelicula(${p.id_pelicula})">${p.activo == 1 ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarPelicula(${p.id_pelicula})">${iconSVG('edit')}</button>
                            <a href="horarios.html?idPelicula=${p.id_pelicula}" class="btn btn-sm btn-outline-info">${iconSVG('clock')}</a>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminarPelicula(${p.id_pelicula})">${iconSVG('trash')}</button>
                        </td>
                    </tr>
                `).join('');
                actualizarPaginacion();
            }).catch(err => {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Error al cargar</td></tr>';
                console.error(err);
            });
        }

        function actualizarPaginacion() {
            var totalPaginas = Math.ceil(totalItems / filasPorPagina) || 1;
            document.getElementById('pagination-info').textContent = 'Total: ' + totalItems + ' registros';
            document.getElementById('pagination-page').textContent = (paginaActual + 1) + ' / ' + totalPaginas;
            document.getElementById('btn-prev').disabled = paginaActual <= 0;
            document.getElementById('btn-next').disabled = paginaActual >= totalPaginas - 1;
        }

        window.paginar = function(dir) {
            var nueva = paginaActual + dir;
            if (nueva < 0) return;
            paginaActual = nueva;
            cargarTabla();
        };

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

        window.guardarPelicula = function() {
            const fd = new FormData();
            fd.append('action', editandoId ? 'update' : 'insertar');
            if (editandoId) fd.append('id', editandoId);
            fd.append('titulo', document.getElementById('titulo').value);
            fd.append('duracion_minutos', document.getElementById('duracion_minutos').value);
            fd.append('genero', document.getElementById('genero').value);
            fd.append('sinopsis', document.getElementById('sinopsis').value);
            fd.append('imagen_url', document.getElementById('imagen_url').value);
            fd.append('destacado', document.getElementById('destacado').checked ? '1' : '0');

            const fileInput = document.getElementById('imagen');
            if (fileInput.files.length > 0) fd.append('imagen', fileInput.files[0]);
            fetch(API_URL + '/PeliculaController', { method: 'POST', body: fd })
                .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
                .then(r => {
                    if (r.success) {
                        showSuccess(editandoId ? 'Pel\u00EDcula actualizada' : 'Pel\u00EDcula guardada');
                        ocultarFormulario();
                        cargarTabla();
                    } else showError(r.mensaje || 'Error desconocido');
                }).catch(err => showError('Error de conexi\u00F3n: ' + err));
        };

        window.toggleDestacadoPelicula = function(id) {
            Api.pelicula.toggleDestacado(id).then(r => { if (r.success) cargarTabla(); });
        };

        window.toggleActivoPelicula = function(id, actual) {
            Api.pelicula.toggleActivo(id).then(r => { if (r.success) cargarTabla(); });
        };

        window.eliminarPelicula = function(id) {
            showConfirm('\u00BFEliminar pel\u00EDcula #' + id + '?', function() {
                Api.pelicula.eliminar(id).then(function(r) {
                    if (r.success) cargarTabla();
                    else showError(r.mensaje || 'Error al eliminar');
                });
            });
        };

        window.editarPelicula = function(id) {
            Api.pelicula.buscar(id).then(p => {
                if (p) mostrarFormulario(p);
            });
        };
