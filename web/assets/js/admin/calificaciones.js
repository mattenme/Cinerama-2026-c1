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
            const tbody = document.querySelector('#tabla-calificaciones tbody');
            Promise.all([
                Api.calificacion.listar(),
                Api.pelicula.listar(),
                Api.cliente.listar()
            ]).then(([lista, peliculas, clientes]) => {
                const peliMap = {};
                peliculas.forEach(p => peliMap[p.id_pelicula] = p.titulo);
                const cliMap = {};
                clientes.forEach(c => cliMap[c.id_cliente] = c.nombre);
                tbody.innerHTML = lista.map(c => {
                    const fecha = c.fecha_calificacion ? new Date(c.fecha_calificacion) : null;
                    const fechaStr = fecha ? fecha.toLocaleString('es-PE') : '-';
                    var estrellasHtml = '';
                    for (var i = 0; i < 5; i++) {
                        estrellasHtml += i < c.puntuacion ? '<i class="bi bi-star-fill text-warning"></i>' : '<i class="bi bi-star text-warning"></i>';
                    }
                    return `<tr>
                        <td><strong>${cliMap[c.id_cliente] || '#' + c.id_cliente}</strong></td>
                        <td>${peliMap[c.id_pelicula] || '#' + c.id_pelicula}</td>
                        <td><span class="text-nowrap">${estrellasHtml}</span> <span class="text-muted">(${c.puntuacion}/5)</span></td>
                        <td>${fechaStr}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminar(${c.id_cliente},${c.id_pelicula})">Eliminar</button>
                        </td>
                    </tr>`;
                }).join('');
            }).catch(() => {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Error al cargar</td></tr>';
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

        window.eliminar = function(idC, idP) {
            if (confirm('\u00BFEliminar calificaci\u00F3n del cliente ' + idC + ' para pel\u00EDcula ' + idP + '?')) {
                Api.calificacion.eliminar(idC, idP).then(r => { if (r.success) cargarTabla(); });
            }
        };
