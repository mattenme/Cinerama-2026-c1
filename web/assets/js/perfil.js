function escapeHtmlAttr(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/'/g, '&#39;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

document.addEventListener('DOMContentLoaded', function() {
    var clienteId = localStorage.getItem('clienteId');
    if (!clienteId) return;

    var container = document.getElementById('perfil-container');

    fetch(API_URL + '/ClienteController?id=' + clienteId)
        .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(function(cliente) {
            localStorage.setItem('clienteNombre', cliente.nombre);

            container.innerHTML =
                '<div class="perfil-header mb-4">' +
                    '<div class="row align-items-center">' +
                        '<div class="col-auto">' +
                            '<img src="assets/img/usuario.ico" class="avatar-img">' +
                        '</div>' +
                        '<div class="col">' +
                            '<h1 class="fw-bold mb-1">' + cliente.nombre + '</h1>' +
                            '<p class="mb-0 opacity-75">DNI: ' + cliente.dni + '</p>' +
                            (cliente.email ? '<p class="mb-0 opacity-75">' + cliente.email + '</p>' : '') +
                            (cliente.telefono ? '<p class="mb-0 opacity-75">' + cliente.telefono + '</p>' : '') +
                        '</div>' +
                        '<div class="col-auto d-flex gap-2">' +
                            '<button class="btn btn-outline-warning d-none" id="btn-reportar-incidencia" onclick="reportarIncidencia()">Reportar Incidencia</button>' +
                            '<button class="btn btn-outline-light" onclick="mostrarEditar(\'' + escapeHtmlAttr(cliente.nombre) + '\',\'' + escapeHtmlAttr(cliente.email||'') + '\',\'' + escapeHtmlAttr(cliente.telefono||'') + '\')">Editar Perfil</button>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div id="editar-form" class="card p-4 mb-4 bg-light border-0 d-none">' +
                    '<h3 class="mb-3">Editar Perfil</h3>' +
                    '<form onsubmit="guardarPerfil(event)">' +
                        '<div class="row g-3">' +
                            '<div class="col-12"><label class="form-label fw-semibold">Nombre:</label><input type="text" class="form-control" id="edit-nombre" required></div>' +
                            '<div class="col-6"><label class="form-label fw-semibold">Email:</label><input type="email" class="form-control" id="edit-email"></div>' +
                            '<div class="col-6"><label class="form-label fw-semibold">Tel\u00E9fono:</label><input type="tel" class="form-control" id="edit-telefono"></div>' +
                            '<div class="col-12"><hr><h6 class="fw-bold">Cambiar Contrase\u00F1a</h6></div>' +
                            '<div class="col-12"><label class="form-label fw-semibold">Contrase\u00F1a Actual:</label><input type="password" class="form-control" id="edit-contrasena-actual"></div>' +
                            '<div class="col-6"><label class="form-label fw-semibold">Nueva Contrase\u00F1a:</label><input type="password" class="form-control" id="edit-contrasena-nueva"></div>' +
                            '<div class="col-6"><label class="form-label fw-semibold">Confirmar:</label><input type="password" class="form-control" id="edit-contrasena-confirmar"></div>' +
                        '</div>' +
                        '<div class="mt-3 d-flex gap-2">' +
                            '<button type="submit" class="btn btn-success">Guardar</button>' +
                            '<button type="button" class="btn btn-secondary" onclick="cancelarEditar()">Cancelar</button>' +
                        '</div>' +
                    '</form>' +
                '</div>' +
                '<div class="row g-4">' +
                    '<div class="col-12">' +
                        cargarHistorial(clienteId) +
                    '</div>' +
                '</div>';
        })
        .catch(function() {
            container.innerHTML = '<div class="text-center py-5 text-danger">Error al cargar perfil</div>';
        });

    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function(el) {
        try { new bootstrap.Tooltip(el); } catch(e) {}
    });
});

function mostrarEditar(nombre, email, telefono) {
    document.getElementById('editar-form').classList.remove('d-none');
    document.getElementById('edit-nombre').value = nombre;
    document.getElementById('edit-email').value = email;
    document.getElementById('edit-telefono').value = telefono;
}

function cancelarEditar() {
    document.getElementById('editar-form').classList.add('d-none');
}

function guardarPerfil(event) {
    event.preventDefault();
    var btn = event.target.querySelector('button[type="submit"]');
    if (btn && btn.disabled) return;
    var id = localStorage.getItem('clienteId');
    var params = new URLSearchParams();
    params.append('action', 'update');
    params.append('id', id);
    params.append('nombre', document.getElementById('edit-nombre').value);
    params.append('email', document.getElementById('edit-email').value);
    params.append('telefono', document.getElementById('edit-telefono').value);

    var pwActual = document.getElementById('edit-contrasena-actual').value;
    var pwNueva = document.getElementById('edit-contrasena-nueva').value;
    var pwConfirmar = document.getElementById('edit-contrasena-confirmar').value;

    function limpiarPw() {
        document.getElementById('edit-contrasena-actual').value = '';
        document.getElementById('edit-contrasena-nueva').value = '';
        document.getElementById('edit-contrasena-confirmar').value = '';
    }

    if (pwNueva || pwConfirmar || pwActual) {
        if (!pwActual) { limpiarPw(); showError('Ingresa tu contrase\u00F1a actual para cambiar la contrase\u00F1a'); return; }
        if (!pwNueva || !pwConfirmar) { limpiarPw(); showError('Completa todos los campos de contrase\u00F1a'); return; }
        if (pwNueva !== pwConfirmar) { limpiarPw(); showError('Las contrase\u00F1as nuevas no coinciden'); return; }
        if (pwNueva.length < 4) { limpiarPw(); showError('La contrase\u00F1a debe tener al menos 4 caracteres'); return; }
        params.append('contrasena_actual', pwActual);
        params.append('contrasena', pwNueva);
    }
    setLoading(btn, true);
    fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
        .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(function(res) {
            if (res.success) {
                localStorage.setItem('clienteNombre', document.getElementById('edit-nombre').value);
                cancelarEditar();
                showSuccess('Perfil actualizado correctamente');
                setTimeout(function() { location.reload(); }, 1000);
            } else {
            setLoading(btn, false);
            limpiarPw();
            showError(res.mensaje || 'Error al guardar');
                }
        })
        .catch(function() { setLoading(btn, false); showError('Error de conexi\u00F3n'); });
}

function cargarHistorial(clienteId) {
    var html = '<div class="card historial-card shadow-sm mb-4"><div class="card-header bg-dark text-white">Compras y Calificaciones</div><div class="table-responsive"><table class="table table-striped table-hover align-middle mb-0"><thead class="table-dark"><tr><th>Pel\u00EDcula</th><th>Sala</th><th>Asientos</th><th>Fecha</th><th>Monto</th><th>M\u00E9todo</th><th>Estado</th><th>Calificar</th></tr></thead><tbody id="compras-body"><tr><td colspan="8" class="text-center text-muted">Cargando...</td></tr></tbody></table></div></div>';

    Promise.all([
        fetch(API_URL + '/CompraController?idCliente=' + clienteId).then(function(r) { if (!r.ok) throw new Error(); return r.json(); }),
        fetch(API_URL + '/CalificacionController?id_cliente=' + clienteId).then(function(r) { if (!r.ok) throw new Error(); return r.json(); })
    ])
    .then(function(results) {
        var trans = results[0];
        var calificaciones = results[1] || [];
        var tbody = document.getElementById('compras-body');
        if (trans.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Sin compras</td></tr>';
            return;
        }
        var btn = document.getElementById('btn-reportar-incidencia');
        if (btn) btn.classList.remove('d-none');
        _salasVisitadas = [];
        var addedSalas = {};
        trans.forEach(function(c) {
            if (c.funcion && c.funcion.sala) {
                var s = c.funcion.sala;
                if (!addedSalas[s.id_sala]) {
                    addedSalas[s.id_sala] = true;
                    _salasVisitadas.push({ id_sala: s.id_sala, nombre: s.nombre, tipo: s.tipo || '' });
                }
            }
        });
        var calMap = {};
        calificaciones.forEach(function(cal) { calMap[String(cal.id_pelicula)] = cal; });
        var grupos = {};
        trans.forEach(function(c) {
            var raw = c.codigo_qr || 'cmp_' + c.id_compra;
            var qr = raw.replace(/_\d+$/, '');
            if (!grupos[qr]) {
                grupos[qr] = { compra: c, asientos: [], filas: [], precio: 0 };
            }
            var g = grupos[qr];
            g.precio += c.precio || 0;
            if (c.asiento) {
                g.asientos.push(c.asiento);
                g.filas.push(c.asiento.fila + c.asiento.numero);
            }
        });
        tbody.innerHTML = Object.keys(grupos).map(function(qr) {
            var g = grupos[qr];
            var c = g.compra;
            var badge = { completada: 'bg-success', pendiente: 'bg-warning text-dark', cancelada: 'bg-danger' };
            var badgeMetodo = { 'Tarjeta Visa': 'bg-primary', 'Tarjeta Mastercard': 'bg-primary', Yape: 'bg-purple text-white', Plin: 'bg-info text-dark', Efectivo: 'bg-secondary' };
            var idPeli = c.funcion ? c.funcion.pelicula.id_pelicula : null;
            var calExistente = idPeli ? calMap[String(idPeli)] : null;
            var colCal = '';
            var anyCompletada = g.asientos.some(function() { return true; }) && c.estado === 'completada';
            if (c.estado === 'completada' && idPeli) {
                if (calExistente) {
                    var est = '';
                    for (var i = 0; i < 5; i++) est += i < calExistente.puntuacion ? '<i class="bi bi-star-fill text-warning"></i>' : '<i class="bi bi-star text-warning"></i>';
                    colCal = '<span style="cursor:default;white-space:nowrap;" title="Tu calificaci\u00F3n: ' + calExistente.puntuacion + '/5">' + est + '</span>';
                } else {
                    colCal = '<button class="btn btn-sm btn-outline-warning" onclick="calificar(' + idPeli + ',\'' + escapeHtmlAttr(c.funcion.pelicula.titulo) + '\')">Calificar</button>';
                }
            } else {
                colCal = '<span class="text-muted small">?</span>';
            }
            return '<tr>' +
                '<td><strong>' + (c.funcion ? c.funcion.pelicula.titulo : '-') + '</strong></td>' +
                '<td>' + (c.funcion ? c.funcion.sala.nombre : '-') + '</td>' +
                '<td><span class="badge bg-secondary">' + g.filas.join(', ') + '</span></td>' +
                '<td>' + formatearFecha(c.fecha_compra) + '</td>' +
                '<td>S/ ' + (g.precio || 0).toFixed(2) + '</td>' +
                '<td><span class="badge ' + (badgeMetodo[c.metodo_pago] || 'bg-secondary') + '">' + (c.metodo_pago || '-') + '</span></td>' +
                '<td><span class="badge ' + (badge[c.estado] || 'bg-secondary') + '">' + (c.estado || '-') + '</span></td>' +
                '<td>' + colCal + '</td>' +
            '</tr>';
        }).join('');
    })
    .catch(function() {
        document.getElementById('compras-body').innerHTML = '<tr><td colspan="8" class="text-center text-danger">Error</td></tr>';
    });

    return html;
}

function calificar(idPelicula, titulo) {
    var modal = document.getElementById('calificar-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'calificar-modal';
        modal.className = 'modal fade';
        modal.tabIndex = -1;
        modal.setAttribute('aria-hidden', 'true');
        modal.innerHTML =
            '<div class="modal-dialog modal-sm modal-dialog-centered">' +
                '<div class="modal-content bg-dark text-white">' +
                    '<div class="modal-header border-secondary">' +
                        '<h5 class="modal-title">Calificar Pel\u00EDcula</h5>' +
                        '<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>' +
                    '</div>' +
                    '<div class="modal-body text-center">' +
                        '<p class="mb-3" id="cal-titulo"></p>' +
                        '<div class="d-flex justify-content-center gap-1 mb-3" id="estrellas-contenedor">' +
                            '<span class="estrella-cal" data-val="1" style="font-size:1.8rem;cursor:pointer;"><i class="bi bi-star"></i></span>' +
                            '<span class="estrella-cal" data-val="2" style="font-size:1.8rem;cursor:pointer;"><i class="bi bi-star"></i></span>' +
                            '<span class="estrella-cal" data-val="3" style="font-size:1.8rem;cursor:pointer;"><i class="bi bi-star"></i></span>' +
                            '<span class="estrella-cal" data-val="4" style="font-size:1.8rem;cursor:pointer;"><i class="bi bi-star"></i></span>' +
                            '<span class="estrella-cal" data-val="5" style="font-size:1.8rem;cursor:pointer;"><i class="bi bi-star"></i></span>' +
                        '</div>' +
                        '<button class="btn btn-warning fw-bold w-100" id="btn-enviar-cal" disabled onclick="enviarCalificacion()">Enviar Calificaci\u00F3n</button>' +
                    '</div>' +
                '</div>' +
            '</div>';
        document.body.appendChild(modal);
        document.getElementById('estrellas-contenedor').addEventListener('click', function(e) {
            var estrella = e.target.closest('.estrella-cal');
            if (!estrella) return;
            var val = parseInt(estrella.dataset.val);
            document.querySelectorAll('.estrella-cal').forEach(function(el) {
                el.innerHTML = parseInt(el.dataset.val) <= val ? '<i class="bi bi-star-fill text-warning"></i>' : '<i class="bi bi-star"></i>';
            });
            document.getElementById('btn-enviar-cal').disabled = false;
            document.getElementById('btn-enviar-cal').dataset.puntuacion = val;
        });
    }
    document.getElementById('cal-titulo').textContent = titulo;
    document.getElementById('btn-enviar-cal').dataset.idPelicula = idPelicula;
    document.getElementById('btn-enviar-cal').disabled = true;
    document.querySelectorAll('.estrella-cal').forEach(function(el) { el.innerHTML = '<i class="bi bi-star"></i>'; });
    var bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

function enviarCalificacion() {
    var btn = document.getElementById('btn-enviar-cal');
    var idPelicula = btn.dataset.idPelicula;
    var puntuacion = btn.dataset.puntuacion;
    var clienteId = localStorage.getItem('clienteId');
    var params = new URLSearchParams();
    params.append('action', 'insertar');
    params.append('id_cliente', clienteId);
    params.append('id_pelicula', idPelicula);
    params.append('puntuacion', puntuacion);
    fetch(API_URL + '/CalificacionController', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(function(r) { if (!r.ok) throw new Error(); return r.json(); })
    .then(function(res) {
        if (res.success) {
            var modal = bootstrap.Modal.getInstance(document.getElementById('calificar-modal'));
            if (modal) modal.hide();
            location.reload();
        } else {
            showError('Error al guardar calificaci\u00F3n');
        }
    })
    .catch(function() { showError('Error de conexi\u00F3n'); });
}

var _salasVisitadas = [];

function reportarIncidencia() {
    var modal = document.getElementById('incidencia-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'incidencia-modal';
        modal.className = 'modal fade';
        modal.tabIndex = -1;
        modal.setAttribute('aria-hidden', 'true');
        modal.innerHTML =
            '<div class="modal-dialog modal-dialog-centered">' +
                '<div class="modal-content bg-dark text-white">' +
                    '<div class="modal-header border-secondary">' +
                        '<h5 class="modal-title">Reportar Incidencia</h5>' +
                        '<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>' +
                    '</div>' +
                    '<div class="modal-body">' +
                        '<form id="form-incidencia-cliente">' +
                            '<div class="mb-3">' +
                                '<label class="form-label fw-semibold">Tipo:</label>' +
                                '<select class="form-select" id="inc-tipo" required>' +
                                    '<option value="">Seleccionar</option>' +
                                    '<option value="fallo_tecnico">Fallo T\u00e9cnico</option>' +
                                    '<option value="limpieza">Limpieza</option>' +
                                    '<option value="seguridad">Seguridad</option>' +
                                    '<option value="otro">Otro</option>' +
                                '</select>' +
                            '</div>' +
                            '<div class="mb-3">' +
                                '<label class="form-label fw-semibold">Sala:</label>' +
                                '<select class="form-select" id="inc-sala">' +
                                    '<option value="">Seleccionar sala</option>' +
                                '</select>' +
                            '</div>' +
                            '<div class="mb-3">' +
                                '<label class="form-label fw-semibold">Descripci\u00f3n:</label>' +
                                '<textarea class="form-control" id="inc-descripcion" rows="3" placeholder="Describe el problema..." required></textarea>' +
                            '</div>' +
                            '<div class="d-flex gap-2">' +
                                '<button type="submit" class="btn btn-warning fw-bold">Enviar Reporte</button>' +
                                '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>' +
                            '</div>' +
                        '</form>' +
                    '</div>' +
                '</div>' +
            '</div>';
        document.body.appendChild(modal);
        modal.addEventListener('hidden.bs.modal', function() {
            if (document.activeElement) document.activeElement.blur();
        });
        document.getElementById('form-incidencia-cliente').onsubmit = function(e) {
            e.preventDefault();
            var tipo = document.getElementById('inc-tipo').value;
            var idSala = document.getElementById('inc-sala').value;
            var desc = document.getElementById('inc-descripcion').value;
            if (!tipo || !desc) { showError('Completa todos los campos'); return; }
            var params = new URLSearchParams();
            params.append('action', 'insertar');
            params.append('tipo', tipo);
            params.append('descripcion', desc);
            params.append('id_cliente', localStorage.getItem('clienteId'));
            if (idSala) params.append('id_sala', idSala);
            params.append('estado', 'reportado');
            fetch(API_URL + '/IncidenciaController', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(function(r) { return r.json(); })
            .then(function(res) {
                if (res.success) {
                    var bsModal = bootstrap.Modal.getInstance(modal);
                    if (bsModal) bsModal.hide();
                    showSuccess('Incidencia reportada. Gracias!');
                    document.getElementById('form-incidencia-cliente').reset();
                } else {
                    showError(res.mensaje || 'Error al reportar');
                }
            })
            .catch(function() { showError('Error de conexi\u00f3n'); });
        };
    }

    var selSala = document.getElementById('inc-sala');
    selSala.innerHTML = '<option value="">Seleccionar sala</option>';
    var added = {};
    _salasVisitadas.forEach(function(s) {
        var key = s.id_sala;
        if (!added[key]) {
            added[key] = true;
            var opt = document.createElement('option');
            opt.value = key;
            opt.textContent = s.nombre + ' (' + s.tipo + ')';
            selSala.appendChild(opt);
        }
    });

    document.getElementById('form-incidencia-cliente').reset();
    var bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

function formatearFecha(dt) {
    if (!dt) return '-';
    try {
        var d = new Date(dt);
        return d.toLocaleDateString('es-PE', { day: 'numeric', month: 'short', year: 'numeric' }) +
            ' ' + d.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
    } catch(e) { return dt; }
}
