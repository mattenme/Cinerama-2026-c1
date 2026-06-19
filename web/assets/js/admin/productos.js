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
            document.getElementById('imagen').value = '';
            if (data) {
                editandoId = data.id_producto;
                document.getElementById('form-titulo').textContent = 'Editar Producto #' + data.id_producto;
                document.getElementById('btn-guardar').textContent = 'Actualizar';
                document.getElementById('nombre').value = data.nombre || '';
                document.getElementById('precio').value = data.precio || '';
                document.getElementById('categoria').value = data.categoria || 'Comida';
                document.getElementById('descripcion').value = data.descripcion || '';
                document.getElementById('imagen_url').value = data.imagen_url || '';
                document.getElementById('activo').checked = data.activo === true || data.activo === 'true';
            } else {
                editandoId = null;
                document.getElementById('form-titulo').textContent = 'Nuevo Producto';
                document.getElementById('btn-guardar').textContent = 'Guardar';
                document.getElementById('nombre').value = '';
                document.getElementById('precio').value = '';
                document.getElementById('categoria').value = 'Comida';
                document.getElementById('descripcion').value = '';
                document.getElementById('imagen_url').value = '';
                document.getElementById('activo').checked = true;
            }
        }

        function ocultarFormulario() { document.getElementById('formulario').classList.add('d-none'); editandoId = null; }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla tbody');
            Api.producto.listar().then(lista => {
                tbody.innerHTML = lista.map(p => {
                    const badgeCat = { Comida: 'bg-warning text-dark', Bebida: 'bg-info text-dark', Combo: 'bg-danger text-white' };
                    return `<tr>
                        <td>${p.id_producto}</td>
                        <td>${p.imagen_url ? '<img src="' + p.imagen_url + '" alt="img" style="width:40px;height:40px;object-fit:cover;border-radius:4px;" onerror="this.style.display=\'none\'">' : '<span class="text-muted">?</span>'}</td>
                        <td><strong>${p.nombre}</strong></td>
                        <td>${p.descripcion || '<span class="text-muted">?</span>'}</td>
                        <td><strong>S/ ${(p.precio || 0).toFixed(2)}</strong></td>
                        <td><span class="badge ${badgeCat[p.categoria] || 'bg-secondary'}">${p.categoria || '-'}</span></td>
                        <td>${p.activo ? '<span class="text-success fw-bold">S&iacute;</span>' : '<span class="text-danger">No</span>'}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editar(${p.id_producto})">Editar</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${p.id_producto})">Eliminar</button>
                        </td>
                    </tr>`;
                }).join('');
            }).catch(() => {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Error al cargar</td></tr>';
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
