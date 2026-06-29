let asientosActuales = [];

        document.addEventListener('DOMContentLoaded', function() {
            if (!localStorage.getItem('clienteId') || localStorage.getItem('clienteRol') !== 'admin') { window.location.href = '../login.html'; return; }
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
            Api.sala.listar().then(salas => {
                const sel = document.getElementById('sala-select');
                sel.innerHTML = '<option value="">Seleccionar sala</option>' +
                    salas.map(s => `<option value="${s.id_sala}">${s.nombre} (${s.tipo}) - ${s.capacidad_total} asientos</option>`).join('');
            });
        });

        function cargarAsientos() {
            var idSala = document.getElementById('sala-select').value;
            document.getElementById('btn-editar-sala').style.display = idSala ? 'inline-block' : 'none';
            document.getElementById('btn-eliminar-sala').style.display = idSala ? 'inline-block' : 'none';
            if (!idSala) return;
            fetch(API_URL + '/AsientoController?idSala=' + idSala)
                .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
                .then(function(data) {
                    asientosActuales = data;
                    renderizar(data);
                });
        }

        function renderizar(lista) {
            const container = document.getElementById('seat-container');
            if (lista.length === 0) {
                container.innerHTML = '<div class="text-center text-muted py-5">No hay asientos en esta sala. Agr\u00E9guelos manualmente o use "Generar 100".</div>';
                return;
            }
            const grupos = {};
            lista.forEach(b => {
                if (!grupos[b.fila]) grupos[b.fila] = [];
                grupos[b.fila].push(b);
            });
            const filas = Object.keys(grupos).sort();
            let html = '<div class="text-center mb-3"><span class="badge bg-success me-2">Disponible</span><span class="badge bg-danger me-2">Ocupado</span><span class="badge bg-primary me-2">Mantenimiento</span></div>';
            filas.forEach(f => {
                grupos[f].sort((a, b) => a.numero - b.numero);
                const asientos = grupos[f].map(b => {
                    const estado = b.estado ? b.estado.toLowerCase() : 'disponible';
                    return `<div class="seat-admin ${estado}" data-id="${b.id_asiento}" data-estado="${b.estado}" data-fila="${b.fila}" data-numero="${b.numero}" onclick="cambiarEstado(this)">${b.fila}${b.numero}</div>`;
                }).join('');
                html += `<div class="mb-1"><span class="fw-bold me-2" style="min-width:20px;display:inline-block;">${f}</span>${asientos}</div>`;
            });
            container.innerHTML = html;
        }

        function cambiarEstado(el) {
            var existing = document.getElementById('estado-selector');
            if (existing) existing.remove();

            var rect = el.getBoundingClientRect();
            var container = document.createElement('div');
            container.id = 'estado-selector';
            container.style.cssText = 'position:fixed;z-index:9999;top:' + (rect.bottom + 4) + 'px;left:' + Math.max(4, rect.left) + 'px;background:#fff;border:1px solid #ccc;border-radius:8px;padding:8px;box-shadow:0 4px 16px rgba(0,0,0,.2);min-width:180px;';

            var titulo = document.createElement('div');
            titulo.className = 'fw-bold small mb-2';
            titulo.textContent = 'Asiento ' + (el.dataset.fila || '?') + (el.dataset.numero || '?');
            container.appendChild(titulo);

            var lbl = document.createElement('label');
            lbl.className = 'form-label small mb-1';
            lbl.textContent = 'Estado:';
            container.appendChild(lbl);

            var sel = document.createElement('select');
            sel.className = 'form-select form-select-sm';
            var estados = [
                { label: 'Disponible', value: 'Disponible' },
                { label: 'Seleccionada', value: 'Seleccionada' },
                { label: 'Vendida', value: 'Vendida' },
                { label: 'Mantenimiento', value: 'Mantenimiento' }
            ];
            estados.forEach(function(e) {
                var opt = document.createElement('option');
                opt.value = e.value;
                opt.textContent = e.label;
                if (e.value === el.dataset.estado) opt.selected = true;
                sel.appendChild(opt);
            });
            container.appendChild(sel);

            var btnRow = document.createElement('div');
            btnRow.className = 'd-flex gap-1 mt-2';
            var btnDel = document.createElement('button');
            btnDel.className = 'btn btn-sm btn-outline-danger';
            btnDel.innerHTML = iconSVG('trash');
            btnDel.onclick = function() {
                showConfirm('Eliminar asiento ' + (el.dataset.fila || '') + (el.dataset.numero || '') + '?', function() {
                    Api.asiento.eliminar(el.dataset.id).then(function(r) {
                        if (r.success) {
                            container.remove();
                            cargarAsientos();
                        } else showError(r.mensaje || 'Error al eliminar asiento');
                    }).catch(function(e) { showError('Error de conexi\u00F3n'); });
                });
            };
            btnRow.appendChild(btnDel);

            var btnOk = document.createElement('button');
            btnOk.className = 'btn btn-sm btn-success';
            btnOk.innerHTML = iconSVG('check');
            btnOk.onclick = function() {
                var nuevo = sel.value;
                var params = new URLSearchParams({ action: 'cambiarEstado', id: el.dataset.id, estado: nuevo });
                fetch(API_URL + '/AsientoController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
                    .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
                    .then(function(r) {
                        if (r.success) {
                            el.dataset.estado = nuevo;
                            el.className = 'seat-admin ' + nuevo.toLowerCase();
                        }
                    });
                container.remove();
            };
            btnRow.appendChild(btnOk);
            var btnCancel = document.createElement('button');
            btnCancel.className = 'btn btn-sm btn-secondary';
            btnCancel.innerHTML = iconSVG('x');
            btnCancel.onclick = function() { container.remove(); };
            btnRow.appendChild(btnCancel);
            container.appendChild(btnRow);

            document.body.appendChild(container);
            sel.focus();
        }

        document.addEventListener('click', function(e) {
            var sel = document.getElementById('estado-selector');
            if (sel && !sel.contains(e.target) && !e.target.closest('.seat-admin')) {
                sel.remove();
            }
        });

        window.editarSalaActual = function() {
            var id = document.getElementById('sala-select').value;
            if (id) window.location.href = 'salas.html?editar=' + id;
        };

        window.eliminarSalaActual = function() {
            var id = document.getElementById('sala-select').value;
            if (!id) return;
            showConfirm('\u00BFEliminar sala #' + id + ' (y todos sus asientos, horarios y compras relacionadas)?', function() {
                eliminarSala(id);
            });
        };

        function eliminarSala(id) {
            Api.sala.eliminar(id).then(function(r) {
                if (r.success) {
                    Api.sala.listar().then(function(salas) {
                        var sel = document.getElementById('sala-select');
                        sel.innerHTML = '<option value="">Seleccionar sala</option>' +
                            salas.map(function(s) { return '<option value="' + s.id_sala + '">' + s.nombre + ' (' + s.tipo + ') - ' + s.capacidad_total + ' asientos</option>'; }).join('');
                        sel.value = '';
                        document.getElementById('seat-container').innerHTML = '<div class="text-center text-muted py-5">Selecciona una sala para ver sus asientos</div>';
                        document.getElementById('btn-editar-sala').style.display = 'none';
                        document.getElementById('btn-eliminar-sala').style.display = 'none';
                        showSuccess('Sala y asientos eliminados');
                    });
                } else showError(r.mensaje || 'Error al eliminar la sala');
            }).catch(function(e) { showError('Error de conexi\u00F3n: ' + e.message); });
        }

        function mostrarAgregar() {
            if (!document.getElementById('sala-select').value) { showWarning('Selecciona una sala primero'); return; }
            document.getElementById('form-agregar').classList.remove('d-none');
            document.getElementById('fila').focus();
        }

        window.guardarAsiento = function(e) {
            e.preventDefault();
            const idSala = document.getElementById('sala-select').value;
            if (!idSala) { showWarning('Selecciona una sala'); return; }
            const datos = {
                id_sala: idSala,
                fila: document.getElementById('fila').value.toUpperCase(),
                numero: document.getElementById('numero').value,
                estado: 'Disponible'
            };
                                Api.asiento.insertar(datos).then(r => {
                if (r.success) {
                    document.getElementById('form-agregar').classList.add('d-none');
                    document.getElementById('fila').value = '';
                    document.getElementById('numero').value = '';
                    cargarAsientos();
                } else showError(r.mensaje || 'Error');
            }).catch(() => showError('Error de conexi\u00F3n'));
        };

        function generarAutomatico() {
            const idSala = document.getElementById('sala-select').value;
            if (!idSala) { showWarning('Selecciona una sala primero'); return; }
            showConfirm('\u00BFGenerar asientos (filas A-J, n\u00FAmeros 1-10) para esta sala? Se generar\u00E1n hasta 100.', function() {
                fetch(API_URL + '/AsientoController?idSala=' + idSala)
                    .then(r => r.json())
                    .then(existentes => {
                        const set = new Set(existentes.map(b => b.fila + '-' + b.numero));
                        const filas = 'ABCDEFGHIJ'.split('');
                        let pendientes = 0;
                        let errores = 0;
                        let creadas = 0;
                        filas.forEach(f => {
                            for (let n = 1; n <= 10; n++) {
                                if (set.has(f + '-' + n)) continue;
                                pendientes++;
                                const datos = { id_sala: idSala, fila: f, numero: n, estado: 'Disponible' };
            Api.asiento.insertar(datos).then(r => {
                                    if (r.success) creadas++;
                                    else errores++;
                                    pendientes--;
                                    if (pendientes === 0) { cargarAsientos(); if (errores > 0) showWarning(creadas + ' creadas, ' + errores + ' errores'); }
                                }).catch(() => {
                                    errores++;
                                    pendientes--;
                                    if (pendientes === 0) { cargarAsientos(); showError('Error de conexi\u00F3n'); }
                                });
                            }
                        });
                        if (pendientes === 0 && creadas === 0) { showInfo('Ya existen todos los asientos posibles'); cargarAsientos(); }
                        document.getElementById('seat-container').innerHTML = '<div class="text-center py-5">Generando asientos...</div>';
                    });
            });
        }
