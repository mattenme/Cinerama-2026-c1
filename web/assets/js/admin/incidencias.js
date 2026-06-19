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
            document.getElementById('formulario').classList.remove('d-none');
            if (data) {
                editandoId = data.id_incidencia;
                document.getElementById('form-titulo').textContent = 'Editar Incidencia #' + data.id_incidencia;
                document.getElementById('btn-guardar').textContent = 'Actualizar';
                document.getElementById('tipo').value = data.tipo || '';
                document.getElementById('descripcion').value = data.descripcion || '';
                document.getElementById('id_cliente').value = data.cliente ? data.cliente.id_cliente : '';
                document.getElementById('id_sala').value = data.sala ? data.sala.id_sala : '';
                document.getElementById('estado').value = data.estado || 'reportado';
            } else {
                editandoId = null;
                document.getElementById('form-titulo').textContent = 'Nueva Incidencia';
                document.getElementById('btn-guardar').textContent = 'Guardar';
                document.getElementById('tipo').value = '';
                document.getElementById('id_cliente').value = '';
                document.getElementById('id_sala').value = '';
                document.getElementById('estado').value = 'reportado';
            }
        }

        function ocultarFormulario() { document.getElementById('formulario').classList.add('d-none'); editandoId = null; }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-incidencias tbody');
            Api.incidencia.listar().then(lista => {
                tbody.innerHTML = lista.map(i => {
                    const badge = { reportado: 'bg-danger', en_proceso: 'bg-warning text-dark', resuelto: 'bg-success' };
                    const fecha = i.fecha_reporte ? new Date(i.fecha_reporte) : null;
                    const fechaStr = fecha ? fecha.toLocaleString('es-PE') : '-';
                    return `<tr>
                        <td>${i.id_incidencia}</td>
                        <td>${i.tipo || '-'}</td>
                        <td>${i.sala ? i.sala.nombre : '-'}</td>
                        <td>${i.cliente ? i.cliente.nombre || '#' + i.cliente.id_cliente : '-'}</td>
                        <td>${fechaStr}</td>
                        <td><span class="badge ${badge[i.estado] || 'bg-secondary'}">${i.estado || '-'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${i.id_incidencia})">Eliminar</button>
                        </td>
                    </tr>`;
                }).join('');
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

        window.guardar = function(e) {
            e.preventDefault();
            const datos = {
                tipo: document.getElementById('tipo').value,
                descripcion: document.getElementById('descripcion').value,
                id_cliente: document.getElementById('id_cliente').value || null,
                id_sala: document.getElementById('id_sala').value || null,
                estado: document.getElementById('estado').value
            };
            const promesa = editandoId
                ? Api.incidencia.actualizar(editandoId, datos)
                : Api.incidencia.insertar(datos);
            promesa.then(r => {
                if (r.success) { showSuccess(editandoId ? 'Incidencia actualizada' : 'Incidencia guardada'); ocultarFormulario(); cargarTabla(); }
                else showError(r.mensaje || 'Error');
            }).catch(() => showError('Error de conexi\u00F3n'));
        };

        window.eliminar = function(id) {
            showConfirm('\u00BFEliminar incidencia #' + id + '?', function() {
                Api.incidencia.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            });
        };
