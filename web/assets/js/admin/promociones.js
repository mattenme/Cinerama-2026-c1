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
                editandoId = data.id_promocion;
                document.getElementById('form-titulo').textContent = 'Editar Promoci\u00F3n #' + data.id_promocion;
                document.getElementById('btn-guardar').textContent = 'Actualizar';
                document.getElementById('codigo').value = data.codigo || '';
                document.getElementById('descripcion').value = data.descripcion || '';
                document.getElementById('descuento').value = data.descuento || '';
                document.getElementById('activo').value = data.activo;
            } else {
                editandoId = null;
                document.getElementById('form-titulo').textContent = 'Nueva Promoci\u00F3n';
                document.getElementById('btn-guardar').textContent = 'Guardar';
                document.getElementById('codigo').value = '';
                document.getElementById('descripcion').value = '';
                document.getElementById('descuento').value = '';
                document.getElementById('activo').value = '1';
            }
        }

        function ocultarFormulario() { document.getElementById('formulario').classList.add('d-none'); editandoId = null; }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-promociones tbody');
            Api.promocion.listar().then(lista => {
                tbody.innerHTML = lista.map(p => {
                    const badge = p.activo == 1 ? 'bg-success' : 'bg-danger';
                    return `<tr>
                        <td>${p.id_promocion}</td>
                        <td><strong>${p.codigo}</strong></td>
                        <td>${p.descripcion || '<span class="text-muted">\u2014</span>'}</td>
                        <td><span class="badge bg-warning text-dark">${p.descuento}%</span></td>
                        <td><span class="badge ${badge}">${p.activo == 1 ? 'S\u00ED' : 'No'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editar(${p.id_promocion})">Editar</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${p.id_promocion})">Eliminar</button>
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

        window.guardar = function(e) {
            e.preventDefault();
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
            Api.promocion.buscar(id).then(p => { if (p) mostrarFormulario(p); });
        };

        window.eliminar = function(id) {
            showConfirm('\u00BFEliminar promoci\u00F3n #' + id + '?', function() {
                Api.promocion.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            });
        };
