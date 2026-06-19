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

        function cargarTabla() {
            const tbody = document.querySelector('#tabla tbody');
            Api.cliente.listar().then(lista => {
                tbody.innerHTML = lista.map(c => {
                    return `<tr>
                        <td>${c.id_cliente}</td>
                        <td>${c.dni || '-'}</td>
                        <td><strong>${c.nombre || '-'}</strong></td>
                        <td>${c.email || '<span class="text-muted">?</span>'}</td>
                        <td>${c.telefono || '<span class="text-muted">?</span>'}</td>
                        <td><button class="btn btn-sm btn-outline-danger" onclick="eliminar(${c.id_cliente})">Eliminar</button></td>
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

        window.eliminar = function(id) {
            if (confirm('\u00BFEliminar cliente #' + id + '?')) {
                Api.cliente.eliminar(id).then(r => { if (r.success) cargarTabla(); });
            }
        };
