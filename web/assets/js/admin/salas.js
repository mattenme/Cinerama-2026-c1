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
            var params = new URLSearchParams(window.location.search);
            var editar = params.get('editar');
            if (editar) {
                setTimeout(function() {
                    Api.sala.buscar(editar).then(function(s) { if (s) mostrarFormulario(s); });
                }, 500);
            }
        });

        function mostrarFormulario(data) {
            editandoId = data ? data.id_sala : null;
            var formHtml = '<form id="form-sala">' +
                '<div class="row g-3">' +
                    '<div class="col-12"><label class="form-label fw-semibold">Nombre de Sala:</label><input type="text" class="form-control" id="nombre" required></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Tipo de Sala:</label><select class="form-select" id="tipo" required><option value="">Seleccionar tipo</option><option value="2d">2D</option><option value="3d">3D</option><option value="imax">IMAX</option><option value="vip">VIP</option></select></div>' +
                    '<div class="col-md-6"><label class="form-label fw-semibold">Capacidad:</label><input type="number" class="form-control" id="capacidad_total" min="20" max="500" required></div>' +
                '</div>' +
                '<div class="mt-3 d-flex gap-2"><button type="submit" class="btn btn-success">' + (editandoId ? 'Actualizar' : 'Guardar') + '</button><button type="button" class="btn btn-secondary" onclick="closeCrudModal()">Cancelar</button></div>' +
            '</form>';
            openCrudModal(editandoId ? 'Editar Sala #' + data.id_sala : 'Nueva Sala', formHtml, function() {
                guardarSala();
            });
            if (data) {
                document.getElementById('nombre').value = data.nombre || '';
                document.getElementById('tipo').value = data.tipo || '';
                document.getElementById('capacidad_total').value = data.capacidad_total || '';
            }
        }

        function ocultarFormulario() {
            closeCrudModal();
        }

        function cargarTabla() {
            const tbody = document.querySelector('#tabla-salas tbody');
            Api.sala.listar().then(salas => {
                tbody.innerHTML = salas.map(s => {
                    const badgeTipo = { '2d': 'secondary', '3d': 'info', imax: 'dark', vip: 'warning' };
                    const badge = badgeTipo[s.tipo] || 'secondary';
                    return `<tr>
                        <td>${s.id_sala}</td>
                        <td><strong>${s.nombre}</strong></td>
                        <td>${s.capacidad_total}</td>
                        <td><span class="badge bg-${badge}">${(s.tipo || '-').toUpperCase()}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarSala(${s.id_sala})">Editar</button>
                            <a href="horarios.html?idSala=${s.id_sala}" class="btn btn-sm btn-outline-info">Horarios</a>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminarSala(${s.id_sala})">Eliminar</button>
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

        function generarButacasPorCapacidad(idSala, capacidad) {
            var seatsPerRow = 10;
            var totalRows = Math.ceil(capacidad / seatsPerRow);
            var pendientes = 0;
            var created = 0;
            var errores = 0;
            for (var r = 0; r < totalRows; r++) {
                var rowLetter = String.fromCharCode(65 + r);
                var seatsInThisRow = Math.min(seatsPerRow, capacidad - r * seatsPerRow);
                for (var n = 1; n <= seatsInThisRow; n++) {
                    (function(fila, num) {
                        pendientes++;
                        var datos = { id_sala: idSala, fila: fila, numero: num, estado: 'Disponible' };
                        Api.butaca.insertar(datos).then(function(resp) {
                            if (resp.success) created++;
                            else errores++;
                            pendientes--;
                            if (pendientes === 0 && created > 0) {
                                showSuccess('Sala guardada y se crearon ' + created + ' asientos.' + (errores > 0 ? ' (' + errores + ' errores)' : ''));
                            }
                        }).catch(function() {
                            errores++;
                            pendientes--;
                            if (pendientes === 0 && created > 0) {
                                showError('Sala guardada pero hubo ' + errores + ' errores al crear asientos.');
                            }
                        });
                    })(rowLetter, n);
                }
            }
            setTimeout(function() {
                if (pendientes > 0 && created > 0) {
                    showWarning('Sala guardada. Creando asientos... (' + created + ' creados, ' + pendientes + ' pendientes)');
                }
            }, 15000);
        }

        window.guardarSala = function() {
            var datos = {
                nombre: document.getElementById('nombre').value,
                tipo: document.getElementById('tipo').value,
                capacidad_total: document.getElementById('capacidad_total').value
            };
            var capacidad = parseInt(datos.capacidad_total);
            var promesa = editandoId
                ? Api.sala.actualizar(editandoId, datos)
                : Api.sala.insertar(datos);
            promesa.then(function(r) {
                if (!r.success) { showError(r.mensaje || 'Error'); return; }

                var alumbre = function(idSala) {
                    if (!editandoId) {
                        generarButacasPorCapacidad(idSala, capacidad);
                    } else {
                        fetch(API_URL + '/ButacaController?idSala=' + idSala)
                            .then(function(resp) { if (!resp.ok) throw new Error('HTTP ' + resp.status); return resp.json(); })
                            .then(function(existentes) {
                                if (existentes.length < capacidad) {
                                    generarButacasPorCapacidad(idSala, capacidad - existentes.length);
                                }
                            })
                            .catch(function() {});
                    }
                };

                if (editandoId) {
                    alumbre(editandoId);
                    showSuccess('Sala actualizada');
                } else {
                    Api.sala.listar().then(function(salas) {
                        var nueva = null;
                        for (var i = 0; i < salas.length; i++) {
                            if (salas[i].nombre === datos.nombre && salas[i].capacidad_total == capacidad) {
                                nueva = salas[i];
                                break;
                            }
                        }
                        if (nueva) alumbre(nueva.id_sala);
                    });
                    showSuccess('Sala guardada');
                }
                ocultarFormulario();
                cargarTabla();
            }).catch(function() { showError('Error de conexi\u00F3n'); });
        };

        window.eliminarSala = function(id) {
            showConfirm('\u00BFEliminar sala #' + id + '?', function() {
                Api.sala.eliminar(id).then(function(r) { if (r.success) cargarTabla(); });
            });
        };

        window.editarSala = function(id) {
            Api.sala.buscar(id).then(s => {
                if (s) mostrarFormulario(s);
            });
        };
