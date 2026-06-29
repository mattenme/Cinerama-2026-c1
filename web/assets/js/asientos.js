function escapeHtml(str) {
    if (str == null || typeof str !== 'string') return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

const urlParams = new URLSearchParams(window.location.search);
const idFuncion = urlParams.get('id_funcion');
const seatPrice = 5;

let selectedSeats = [];
let selectedFood = {};
let asientos = [];
let funcionData = null;
let idSala = null;
let pollingInterval = null;
let timerInterval = null;
const TIMEOUT_SEC = 600;
let timerSeconds = TIMEOUT_SEC;
let isConfirming = false;

document.addEventListener('DOMContentLoaded', function() {
    if (!idFuncion) {
        document.getElementById('seats-grid').innerHTML = '<div class="col-12 text-center text-danger">Error: No se especific\u00F3 funci\u00F3n</div>';
        document.getElementById('confirm-btn').disabled = true;
        return;
    }

    fetch(API_URL + '/FuncionController?id=' + idFuncion)
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(funcion => {
            funcionData = funcion;
            if (!funcion || !funcion.sala) throw new Error('Funci\u00F3n no encontrada');
            idSala = funcion.sala.id_sala;
            mostrarInfoPelicula(funcion);
            return fetch(API_URL + '/AsientoController?action=liberarReservadas&idSala=' + idSala).then(function() {
                return fetch(API_URL + '/AsientoController?idSala=' + idSala);
            });
        })
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(data => {
            asientos = data;
            renderizarGrilla(asientos);
            renderFoodMenu();
            if (pollingInterval) clearInterval(pollingInterval);
            pollingInterval = setInterval(refreshSeats, 5000);
            iniciarTimer();
        })
        .catch(err => {
            console.error('Error:', err);
            document.getElementById('seats-grid').innerHTML = '<div class="col-12 text-center text-danger">Error al cargar los datos</div>';
        });

    document.getElementById('confirm-btn').addEventListener('click', confirmarSeleccion);

    document.getElementById('seats-grid').addEventListener('click', function(e) {
        const seat = e.target.closest('.seat');
        if (!seat || seat.classList.contains('occupied') || seat.classList.contains('maintenance')) return;
        const id = parseInt(seat.dataset.id);
        const key = seat.dataset.fila + seat.dataset.numero;
        if (selectedSeats.includes(key)) {
            deselectSeat(id, key);
        } else {
            selectSeat(id, key);
        }
    });

    window.addEventListener('beforeunload', function(e) {
        if (selectedSeats.length > 0 && !isConfirming) {
            e.preventDefault();
            e.returnValue = '';
        }
    });

    document.addEventListener('click', function(e) {
        var link = e.target.closest('.navbar-nav a');
        if (link && selectedSeats.length > 0 && !isConfirming) {
            e.preventDefault();
            showConfirm('Hay asientos seleccionados sin confirmar. \u00BFSalir de todas formas?', function() {
                isConfirming = true;
                liberarTodosAsientos();
                window.location.href = link.href;
            });
        }
    });

    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function(el) {
        try { new bootstrap.Tooltip(el); } catch(e) {}
    });
});

function iniciarTimer() {
    timerSeconds = TIMEOUT_SEC;
    actualizarTimerDisplay();
    if (timerInterval) clearInterval(timerInterval);
    timerInterval = setInterval(function() {
        timerSeconds--;
        actualizarTimerDisplay();
            if (timerSeconds <= 0) {
                clearInterval(timerInterval);
                timerInterval = null;
                if (selectedSeats.length > 0) {
                    showWarning('Se acab\u00F3 el tiempo. Los asientos seleccionados se liberar\u00E1n.');
                    liberarTodosAsientos();
                }
            }
    }, 1000);
}

function actualizarTimerDisplay() {
    var el = document.getElementById('timer-display');
    if (!el) return;
    var min = Math.floor(timerSeconds / 60);
    var seg = timerSeconds % 60;
    el.textContent = (min < 10 ? '0' : '') + min + ':' + (seg < 10 ? '0' : '') + seg;
    if (timerSeconds <= 60) {
        el.className = 'badge bg-danger ms-2';
    } else {
        el.className = 'badge bg-warning text-dark ms-2';
    }
}

function liberarTodosAsientos() {
    selectedSeats.forEach(function(key) {
        var asiento = asientos.find(function(b) { return (b.fila + b.numero) === key; });
                if (asiento && asiento.estado === 'Seleccionada') {
                    var params = new URLSearchParams({ action: 'cambiarEstado', id: asiento.id_asiento, estado: 'Disponible' });
            fetch(API_URL + '/AsientoController', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            }).catch(function() {});
        }
    });
    selectedSeats = [];
    actualizarResumen();
    refreshSeats();
}

function selectSeat(id, key) {
    selectedSeats.push(key);
    actualizarResumen();
    actualizarClaseAsiento(key, 'selected');
    const params = new URLSearchParams({ action: 'cambiarEstado', id: id, estado: 'Seleccionada' });
    fetch(API_URL + '/AsientoController', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
    .then(data => {
        if (!data.success) throw new Error('Error del servidor');
        refreshSeats();
    })
    .catch(err => {
        console.error('Error al seleccionar asiento:', err);
        const idx = selectedSeats.indexOf(key);
        if (idx > -1) selectedSeats.splice(idx, 1);
        actualizarResumen();
        refreshSeats();
        showWarning('Este asiento ya no est\u00E1 disponible');
    });
}

function deselectSeat(id, key) {
    const idx = selectedSeats.indexOf(key);
    if (idx > -1) selectedSeats.splice(idx, 1);
    actualizarResumen();
    const params = new URLSearchParams({ action: 'cambiarEstado', id: id, estado: 'Disponible' });
    fetch(API_URL + '/AsientoController', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
    .then(data => {
        if (!data.success) throw new Error('Error del servidor');
        refreshSeats();
    })
    .catch(err => {
        console.error('Error al liberar asiento:', err);
        refreshSeats();
    });
}

function refreshSeats() {
    if (!idSala) return;
    fetch(API_URL + '/AsientoController?idSala=' + idSala)
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(data => {
            asientos = data;
            renderizarGrilla(asientos);
        })
        .catch(err => console.error('Error al refrescar asientos:', err));
}

function actualizarClaseAsiento(key, extraClass) {
    document.querySelectorAll('#seats-grid .seat').forEach(el => {
        if ((el.dataset.fila + el.dataset.numero) === key) {
            el.classList.add(extraClass);
        }
    });
}

function mostrarInfoPelicula(funcion) {
    const info = document.getElementById('movie-info');
    const fecha = new Date(funcion.hora_inicio);
    const fechaStr = fecha.toLocaleDateString('es-PE', { day: 'numeric', month: 'short', year: 'numeric' });
    const horaStr = fecha.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
    info.innerHTML = `
        <span><strong>Pel\u00EDcula:</strong> ${escapeHtml(funcion.pelicula ? funcion.pelicula.titulo : '-')}</span>
        <span><strong>Fecha:</strong> ${escapeHtml(fechaStr)}</span>
        <span><strong>Horario:</strong> ${escapeHtml(horaStr)}</span>
        <span><strong>Sala:</strong> ${escapeHtml(funcion.sala ? funcion.sala.nombre : '-')}</span>
    `;
}

function renderizarGrilla(asientosList) {
    const grid = document.getElementById('seats-grid');
    grid.innerHTML = '';

    const grupos = {};
    asientosList.forEach(b => {
        if (!grupos[b.fila]) grupos[b.fila] = [];
        grupos[b.fila].push(b);
    });

    const filas = Object.keys(grupos).sort();
    let html = '';
    filas.forEach(fila => {
        grupos[fila].sort((a, b) => a.numero - b.numero);
        const asientos = grupos[fila].map(b => {
            const key = b.fila + b.numero;
            const isSelected = selectedSeats.includes(key);
            var est = b.estado ? b.estado.toLowerCase() : '';
            var clases = 'seat';
            if (isSelected) {
                clases += ' available selected';
            } else if (est === 'disponible') {
                clases += ' available';
            } else if (est === 'mantenimiento') {
                clases += ' maintenance';
            } else {
                clases += ' occupied';
            }
            return `<div class="${clases}" data-id="${b.id_asiento}" data-fila="${b.fila}" data-numero="${b.numero}" data-estado="${b.estado}">${b.fila}${b.numero}</div>`;
        }).join('');
        html += `<div class="d-inline-flex align-items-center mb-1"><span class="fw-bold me-2" style="min-width:20px;display:inline-block;">${fila}</span>${asientos}</div>`;
    });
    grid.innerHTML = html;
}

function actualizarResumen() {
    const container = document.getElementById('selected-seats');
    const countBadge = document.getElementById('selected-count');
    container.innerHTML = '';
    var count = selectedSeats.length;
    if (countBadge) countBadge.textContent = count;
    if (count === 0) {
        container.innerHTML = '<span class="text-muted">Ninguno seleccionado</span>';
    } else {
        var badges = selectedSeats.map(function(s) {
            return '<span class="badge bg-warning text-dark me-1 mb-1">' + escapeHtml(s) + '</span>';
        }).join(' ');
        container.innerHTML = '<div class="d-flex flex-wrap align-items-center gap-1">' + badges + '<span class="ms-auto text-nowrap fw-semibold">S/ ' + (count * seatPrice) + '</span></div>';
    }
    actualizarTotal();
    document.getElementById('confirm-btn').disabled = count === 0;
}

function actualizarTotal() {
    const totalEl = document.getElementById('total-price');
    const totalSeats = selectedSeats.length * seatPrice;
    const totalFood = calcFoodTotal();
    totalEl.textContent = totalSeats + totalFood;
}

let foodMenu = [];

function renderFoodMenu() {
    const container = document.getElementById('food-items');
    mostrarSpinner('food-items');
    fetch(API_URL + '/ProductoController?activos=true')
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(lista => {
            foodMenu = lista;
            container.innerHTML = '';
            if (foodMenu.length === 0) {
                container.innerHTML = '<div class="text-muted small">No hay productos disponibles</div>';
                return;
            }
            foodMenu.forEach(item => {
                const div = document.createElement('div');
                div.className = 'd-flex align-items-center gap-2 mb-2 pb-2 border-bottom border-secondary';
                const imgSrc = item.imagen_url || '';
                div.innerHTML = `
                    ${imgSrc ? '<img src="' + escapeHtml(imgSrc) + '" alt="' + escapeHtml(item.nombre) + '" class="rounded" style="width:40px;height:40px;object-fit:cover;" onerror="this.style.display=\'none\'">' : ''}
                    <div class="flex-grow-1">
                        <small class="d-block fw-semibold">${escapeHtml(item.nombre)}</small>
                        <small class="text-muted">S/ ${(item.precio || 0).toFixed(2)}</small>
                    </div>
                    <div class="d-flex align-items-center">
                        <button class="btn btn-sm btn-outline-secondary px-1 food-minus" data-id="${item.id_producto}">\u2212</button>
                        <span class="mx-2 food-qty" id="qty-${item.id_producto}" style="color:var(--text-body);">0</span>
                        <button class="btn btn-sm btn-outline-secondary px-1 food-plus" data-id="${item.id_producto}">+</button>
                    </div>
                `;
                container.appendChild(div);
            });

            container.addEventListener('click', function(e) {
                const target = e.target;
                const id = target.dataset.id;
                if (!id) return;
                if (target.classList.contains('food-plus')) {
                    selectedFood[id] = (selectedFood[id] || 0) + 1;
                } else if (target.classList.contains('food-minus')) {
                    if (selectedFood[id] > 0) selectedFood[id]--;
                }
                document.getElementById('qty-' + id).textContent = selectedFood[id] || 0;
                actualizarTotal();
            });
        })
        .catch(() => {
            container.innerHTML = '<div class="text-muted small">Error al cargar productos</div>';
        });
}

function calcFoodTotal() {
    return Object.keys(selectedFood).reduce((sum, id) => {
        const item = foodMenu.find(f => String(f.id_producto) === id);
        return sum + (selectedFood[id] || 0) * (item ? item.precio : 0);
    }, 0);
}

function getSelectedFood() {
    const items = Object.keys(selectedFood).filter(id => selectedFood[id] > 0);
    if (items.length === 0) return '';
    return JSON.stringify(items.map(id => {
        const item = foodMenu.find(f => String(f.id_producto) === id);
        return { id: parseInt(id), qty: selectedFood[id], name: item ? item.nombre : id, price: item ? item.precio : 0 };
    }));
}

async function confirmarSeleccion() {
    if (selectedSeats.length === 0 || !funcionData) return;

    var btn = document.getElementById('confirm-btn');
    if (btn.disabled) return;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Procesando...';

    const asientosSeleccionados = asientos.filter(b => {
        const key = b.fila + b.numero;
        return selectedSeats.includes(key);
    });

    const params = new URLSearchParams();
    params.append('seats', selectedSeats.join(','));
    params.append('total', selectedSeats.length * seatPrice);
    params.append('id_funcion', idFuncion);
    if (asientosSeleccionados.length > 0) params.append('id_asiento', asientosSeleccionados.map(b => b.id_asiento).join(','));
    const foodData = getSelectedFood();
    if (foodData) params.append('food', foodData);
    params.append('grandTotal', String(selectedSeats.length * seatPrice + calcFoodTotal()));
    isConfirming = true;
    window.location.href = 'pago.html?' + params.toString();
}
