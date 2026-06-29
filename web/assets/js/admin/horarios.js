let editandoId = null;

        var filtroPelicula = null;
        var filtroSala = null;

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
            var params = new URLSearchParams(window.location.search);
            filtroPelicula = params.get('idPelicula');
            filtroSala = params.get('idSala');
            cargarTabla();
        });

        function aplicarFiltro(funciones) {
            if (filtroPelicula) {
                funciones = funciones.filter(function(f) { return f.pelicula && f.pelicula.id_pelicula == filtroPelicula; });
            }
            if (filtroSala) {
                funciones = funciones.filter(function(f) { return f.sala && f.sala.id_sala == filtroSala; });
            }
            return funciones;
        }

        function cargarSelects() {
            Api.pelicula.listar().then(peliculas => {
                const sel = document.getElementById('id_pelicula');
                sel.innerHTML = '<option value="">Seleccionar pel\u00EDcula</option>' +
                    peliculas.map(p => `<option value="${escapeHtml(p.id_pelicula)}">${escapeHtml(p.titulo)}</option>`).join('');
                if (!editandoId && filtroPelicula) sel.value = filtroPelicula;
            }).catch(function(e) { console.error(e); });
            Api.sala.listar().then(salas => {
                const sel = document.getElementById('id_sala');
                sel.innerHTML = '<option value="">Seleccionar sala</option>' +
                    salas.map(s => `<option value="${escapeHtml(s.id_sala)}">${escapeHtml(s.nombre)}</option>`).join('');
                if (!editandoId && filtroSala) sel.value = filtroSala;
            }).catch(function(e) { console.error(e); });
        }

        function mostrarFormulario(data) {
            editandoId = data ? data.id_funcion : null;
            var formHtml = '<form id="form-horario">' +
                '<div class="row g-3">' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Pel\u00edcula:</label><select class="form-select" id="id_pelicula" required><option value="">Cargando...</option></select></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Sala:</label><select class="form-select" id="id_sala" required><option value="">Cargando...</option></select></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Fecha y Hora:</label><input type="datetime-local" class="form-control" id="hora_inicio" required></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Estado:</label><select class="form-select" id="estado" required><option value="Programada">Programada</option><option value="En curso">En curso</option><option value="Finalizada">Finalizada</option><option value="Cancelada">Cancelada</option></select></div>' +
                '</div>' +
                '<div class="mt-3 d-flex gap-2"><button type="submit" class="btn btn-success">' + (editandoId ? 'Actualizar' : 'Guardar') + '</button><button type="button" class="btn btn-secondary" onclick="closeCrudModal()">Cancelar</button></div>' +
            '</form>';
            openCrudModal(editandoId ? 'Editar Horario #' + data.id_funcion : 'Nuevo Horario', formHtml, function() {
                guardarHorario();
            });
            cargarSelects();
            if (data) {
                document.getElementById('id_pelicula').value = data.pelicula ? data.pelicula.id_pelicula : '';
                document.getElementById('id_sala').value = data.sala ? data.sala.id_sala : '';
                document.getElementById('hora_inicio').value = data.hora_inicio ? data.hora_inicio.substring(0, 16) : '';
                document.getElementById('estado').value = data.estado || 'Programada';
            } else {
                if (filtroPelicula) document.getElementById('id_pelicula').value = filtroPelicula;
                if (filtroSala) document.getElementById('id_sala').value = filtroSala;
            }
        }

        function ocultarFormulario() {
            closeCrudModal();
        }

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
            const tbody = document.querySelector('#tabla-horarios tbody');
            Api.funcion.listar().then(todas => {
                var filtradas = aplicarFiltro(todas);
                totalItems = filtradas.length;
                var funciones = filtradas.slice(paginaActual * filasPorPagina, (paginaActual + 1) * filasPorPagina);
                var info = document.getElementById('filtro-info');
                if (filtroPelicula || filtroSala) {
                    var partes = [];
                    if (filtroPelicula) partes.push('Pel\u00EDcula #' + filtroPelicula);
                    if (filtroSala) partes.push('Sala #' + filtroSala);
                    info.innerHTML = 'Filtrando por: <strong>' + partes.join(', ') + '</strong> ? <a href="horarios.html" class="text-danger">Quitar filtro</a>';
                } else {
                    info.innerHTML = '';
                }
                tbody.innerHTML = funciones.map(f => {
                    const fecha = f.hora_inicio ? new Date(f.hora_inicio) : null;
                    const fechaStr = fecha ? fecha.toLocaleDateString('es-PE', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : '-';
                    var badgeMap = { 'Programada': 'bg-primary', 'En curso': 'bg-success', 'Finalizada': 'bg-secondary', 'Cancelada': 'bg-danger' };
                    const badge = badgeMap[f.estado] || 'bg-secondary';
                    return `<tr>
                        <td>${escapeHtml(f.id_funcion)}</td>
                        <td><strong>${escapeHtml(f.pelicula ? f.pelicula.titulo : '-')}</strong></td>
                        <td>${escapeHtml(f.sala ? f.sala.nombre : '-')}</td>
                        <td>${escapeHtml(fechaStr)}</td>
                        <td><span class="badge ${badge}">${escapeHtml(f.estado || '-')}</span></td>
                        <td><span class="badge ${f.activo == 1 ? 'bg-success' : 'bg-danger'}" style="cursor:pointer;" onclick="toggleActivoHorario(${escapeHtml(f.id_funcion)})">${f.activo == 1 ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarHorario(${escapeHtml(f.id_funcion)})">${iconSVG('edit')}</button>
                            <button class="btn btn-sm btn-outline-danger ms-1" onclick="eliminarHorario(${escapeHtml(f.id_funcion)})">${iconSVG('trash')}</button>
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

        window.guardarHorario = function() {
            const datos = {
                id_pelicula: document.getElementById('id_pelicula').value,
                id_sala: document.getElementById('id_sala').value,
                hora_inicio: document.getElementById('hora_inicio').value,
                estado: document.getElementById('estado').value
            };
            const promesa = editandoId
                ? Api.funcion.actualizar(editandoId, datos)
                : Api.funcion.insertar(datos);
            promesa.then(r => {
                if (r.success) {
                    showSuccess(editandoId ? 'Horario actualizado' : 'Horario guardado');
                    ocultarFormulario();
                    cargarTabla();
                } else showError(r.mensaje || 'Error');
            }).catch(() => showError('Error de conexi\u00F3n'));
        };

        window.toggleActivoHorario = function(id) {
            Api.funcion.toggleActivo(id).then(r => { if (r.success) cargarTabla(); }).catch(function() { showError('Error de conexi\u00F3n'); });
        };

        window.eliminarHorario = function(id) {
            showConfirm('\u00BFEliminar horario #' + id + '?', function() {
                Api.funcion.eliminar(id).then(r => {
                    if (r.success) cargarTabla();
                    else showError(r.mensaje || 'No se pudo eliminar el horario');
                }).catch(() => showError('Error de conexi\u00F3n'));
            });
        };

        window.editarHorario = function(id) {
            Api.funcion.buscar(id).then(f => {
                if (f) mostrarFormulario(f);
            }).catch(function(e) { console.error(e); });
        };
