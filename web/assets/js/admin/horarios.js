let editandoId = null;

        var filtroPelicula = null;
        var filtroSala = null;

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
                    peliculas.map(p => `<option value="${p.id_pelicula}">${p.titulo}</option>`).join('');
                if (!editandoId && filtroPelicula) sel.value = filtroPelicula;
            });
            Api.sala.listar().then(salas => {
                const sel = document.getElementById('id_sala');
                sel.innerHTML = '<option value="">Seleccionar sala</option>' +
                    salas.map(s => `<option value="${s.id_sala}">${s.nombre}</option>`).join('');
                if (!editandoId && filtroSala) sel.value = filtroSala;
            });
        }

        function mostrarFormulario(data) {
            document.getElementById('formulario-horario').classList.remove('d-none');
            cargarSelects();
            if (data) {
                editandoId = data.id_funcion;
                document.getElementById('form-titulo').textContent = 'Editar Horario #' + data.id_funcion;
                document.getElementById('btn-guardar').textContent = 'Actualizar';
                document.getElementById('id_pelicula').value = data.pelicula ? data.pelicula.id_pelicula : '';
                document.getElementById('id_sala').value = data.sala ? data.sala.id_sala : '';
                document.getElementById('hora_inicio').value = data.hora_inicio ? data.hora_inicio.substring(0, 16) : '';
                document.getElementById('estado').value = data.estado || 'activa';
            } else {
                editandoId = null;
                document.getElementById('form-titulo').textContent = 'Nuevo Horario';
                document.getElementById('btn-guardar').textContent = 'Guardar';
                document.getElementById('edit-id').value = '';
                document.getElementById('hora_inicio').value = '';
                document.getElementById('estado').value = 'Programada';
                if (filtroPelicula) document.getElementById('id_pelicula').value = filtroPelicula;
                if (filtroSala) document.getElementById('id_sala').value = filtroSala;
            }
        }

        function ocultarFormulario() {
            document.getElementById('formulario-horario').classList.add('d-none');
            editandoId = null;
        }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-horarios tbody');
            Api.funcion.listar().then(funciones => {
                funciones = aplicarFiltro(funciones);
                var total = funciones.length;
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
                        <td>${f.id_funcion}</td>
                        <td><strong>${f.pelicula ? f.pelicula.titulo : '-'}</strong></td>
                        <td>${f.sala ? f.sala.nombre : '-'}</td>
                        <td>${fechaStr}</td>
                        <td><span class="badge ${badge}">${f.estado || '-'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarHorario(${f.id_funcion})">Editar</button>
                            <button class="btn btn-sm btn-outline-danger ms-1" onclick="eliminarHorario(${f.id_funcion})">Eliminar</button>
                        </td>
                    </tr>`;
                }).join('');
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

        window.guardarHorario = function(event) {
            event.preventDefault();
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

        window.eliminarHorario = function(id) {
            showConfirm('\u00BFEliminar horario #' + id + '?', function() {
                Api.funcion.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            });
        };

        window.editarHorario = function(id) {
            Api.funcion.buscar(id).then(f => {
                if (f) mostrarFormulario(f);
            });
        };
