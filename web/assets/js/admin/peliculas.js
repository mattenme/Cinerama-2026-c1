let editandoId = null;

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

        function mostrarFormulario(data) {
            document.getElementById('formulario-pelicula').classList.remove('d-none');
                document.getElementById('imagen').value = '';
            if (data) {
                editandoId = data.id_pelicula;
                document.getElementById('form-titulo').textContent = 'Editar Pel\u00EDcula #' + data.id_pelicula;
                document.getElementById('btn-guardar').textContent = 'Actualizar';
                document.getElementById('titulo').value = data.titulo || '';
                document.getElementById('duracion_minutos').value = data.duracion_minutos || '';
                document.getElementById('genero').value = data.genero || '';
                document.getElementById('sinopsis').value = data.sinopsis || '';
                document.getElementById('imagen_url').value = data.imagen_url || '';
                document.getElementById('destacado').checked = data.destacado == 1;
            } else {
                editandoId = null;
                document.getElementById('form-titulo').textContent = 'Nueva Pel\u00EDcula';
                document.getElementById('btn-guardar').textContent = 'Guardar';
                document.getElementById('titulo').value = '';
                document.getElementById('duracion_minutos').value = '';
                document.getElementById('genero').value = '';
                document.getElementById('sinopsis').value = '';
                document.getElementById('imagen_url').value = '';
                document.getElementById('destacado').checked = false;
            }
        }

        function ocultarFormulario() {
            document.getElementById('formulario-pelicula').classList.add('d-none');
            editandoId = null;
        }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-peliculas tbody');
            Api.pelicula.listar().then(peliculas => {
                tbody.innerHTML = peliculas.map(p => `
                    <tr>
                        <td>${p.id_pelicula}</td>
                        <td>${p.imagen_url ? '<img src="' + p.imagen_url + '" alt="img" style="width:40px;height:60px;object-fit:cover;border-radius:4px;">' : '<span class="text-muted">?</span>'}</td>
                        <td><strong>${p.titulo}</strong></td>
                        <td>${p.duracion_minutos || '-'} min</td>
                        <td>${p.genero || '<span class="text-muted">?</span>'}</td>
                        <td>${p.destacado == 1 ? '<span class="badge bg-warning text-dark">S\u00ED</span>' : '<span class="text-muted">?</span>'}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarPelicula(${p.id_pelicula})">Editar</button>
                            <a href="horarios.html?idPelicula=${p.id_pelicula}" class="btn btn-sm btn-outline-info">Horarios</a>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminarPelicula(${p.id_pelicula})">Eliminar</button>
                        </td>
                    </tr>
                `).join('');
            }).catch(err => {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Error al cargar</td></tr>';
                console.error(err);
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

        window.guardarPelicula = function(event) {
            event.preventDefault();
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
                    } else showError('Error: ' + (r.error || 'desconocido'));
                }).catch(err => showError('Error de conexi\u00F3n: ' + err));
        };

        window.eliminarPelicula = function(id) {
            showConfirm('\u00BFEliminar pel\u00EDcula #' + id + '?', function() {
                Api.pelicula.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            });
        };

        window.editarPelicula = function(id) {
            Api.pelicula.buscar(id).then(p => {
                if (p) mostrarFormulario(p);
            });
        };
