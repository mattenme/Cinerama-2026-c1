var selectedMethod = null;
var _seats = '', _total = '0', _food = '', _grandTotal = '0', _idFuncion = '', _idButaca = '';
var _descuento = 0, _codigoPromo = '';

(function() {
    var urlParams = new URLSearchParams(window.location.search);
    _seats = urlParams.get('seats') || 'Ninguno';
    _total = urlParams.get('total') || '0';
    _food = urlParams.get('food') || '';
    _grandTotal = urlParams.get('grandTotal') || _total;
    _idFuncion = urlParams.get('id_funcion') || '';
    _idButaca = urlParams.get('id_butaca') || '';

    document.getElementById('summary-seats').textContent = _seats;
    document.getElementById('summary-tickets').textContent = '$' + _total;
    document.getElementById('summary-grand-total').textContent = '$' + _grandTotal;

    if (_food) {
        var foodContainer = document.getElementById('summary-food-container');
        var foodList = document.getElementById('summary-food-list');
        var foodTotal = 0;
        try {
            JSON.parse(_food).forEach(function(item) {
                var li = document.createElement('li');
                li.textContent = item.name + ' x' + item.qty;
                foodList.appendChild(li);
                foodTotal += (item.price || 0) * item.qty;
            });
        } catch(e) {
            foodList.innerHTML = '<li class="text-muted">' + _food + '</li>';
        }
        document.getElementById('summary-food-total').textContent = '$' + foodTotal.toFixed(2);
        foodContainer.classList.remove('d-none');
    }
})();

function selectPago(method) {
    selectedMethod = method;
    document.querySelectorAll('.payment-option').forEach(function(el) {
        el.classList.toggle('sel', el.dataset.method === method);
    });
    document.querySelectorAll('.form-section').forEach(function(el) {
        el.classList.toggle('open', el.id === method + '-form');
    });
}

function aplicarPromo() {
    var codigo = document.getElementById('codigo-promo').value.trim().toUpperCase();
    if (!codigo) return;
    document.getElementById('codigo-promo').value = codigo;

    fetch(API_URL + '/PromocionController?codigo=' + encodeURIComponent(codigo))
    .then(function(r) {
        if (!r.ok) throw new Error('Error del servidor');
        return r.json();
    })
    .then(function(data) {
        if (data.success) {
            _descuento = data.descuento;
            _codigoPromo = codigo;
            var original = parseFloat(_grandTotal);
            var finalPrecio = original - (original * _descuento / 100);
            var grandEl = document.getElementById('summary-grand-total');
            var discountRow = document.getElementById('discount-row');
            if (!discountRow) {
                discountRow = document.createElement('div');
                discountRow.id = 'discount-row';
                discountRow.className = 'd-flex justify-content-between small text-success fw-bold mt-1 pt-1 border-top border-success';
                discountRow.innerHTML = '<span>Descuento (' + _descuento + '%)</span><span>-$' + (original - finalPrecio).toFixed(2) + '</span>';
                grandEl.parentNode.insertBefore(discountRow, grandEl.parentNode.querySelector('.fw-bold'));
            }
            grandEl.textContent = '$' + finalPrecio.toFixed(2);
            document.getElementById('promo-mensaje').innerHTML = '<span class="text-success"><i class="bi bi-check-circle-fill"></i> Descuento del ' + _descuento + '% aplicado</span>';
            document.getElementById('btn-aplicar-promo').disabled = true;
            document.getElementById('codigo-promo').disabled = true;
        } else {
            document.getElementById('promo-mensaje').innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-circle-fill"></i> ' + data.error + '</span>';
        }
    })
    .catch(function(e) {
        document.getElementById('promo-mensaje').innerHTML = '<span class="text-danger">Error al validar c\u00F3digo promocional</span>';
        console.error('Error promo:', e);
    });
}

function validarCampos() {
    if (selectedMethod === 'visa' || selectedMethod === 'mastercard') {
        var prefix = selectedMethod === 'visa' ? 'visa' : 'mc';
        var num = document.getElementById(prefix + '-number').value.trim();
        var exp = document.getElementById(prefix + '-expiry').value.trim();
        var cvv = document.getElementById(prefix + '-cvv').value.trim();
        var name = document.getElementById(prefix + '-name').value.trim();
        if (!num) { showError('Ingresa el n\u00FAmero de tarjeta'); return false; }
        if (!exp) { showError('Ingresa la fecha de vencimiento'); return false; }
        if (!cvv) { showError('Ingresa el CVV'); return false; }
        if (!name) { showError('Ingresa el nombre del titular'); return false; }
        if (exp.length < 5) { showError('Formato de fecha inv\u00E1lido (MM/AA)'); return false; }
        if (cvv.length < 3) { showError('CVV inv\u00E1lido'); return false; }
    }
    if (selectedMethod === 'yape') {
        var tel = document.getElementById('yape-phone').value.trim();
        if (!tel) { showError('Ingresa tu n\u00FAmero de tel\u00E9fono Yape'); return false; }
    }
    if (selectedMethod === 'plin') {
        var tel = document.getElementById('plin-phone').value.trim();
        if (!tel) { showError('Ingresa tu n\u00FAmero de tel\u00E9fono Plin'); return false; }
    }
    return true;
}

function procesarPago() {
    if (!selectedMethod) {
        showError('Selecciona un m\u00E9todo de pago.');
        return;
    }
    if (!validarCampos()) return;

    var clienteId = localStorage.getItem('clienteId');

    var precioFinal = parseFloat(_grandTotal);
    if (_descuento > 0) {
        precioFinal = precioFinal - (precioFinal * _descuento / 100);
    }

    var params = new URLSearchParams();
    params.append('action', 'insertar');
    if (clienteId) params.append('id_cliente', clienteId);
    if (_idFuncion) params.append('id_funcion', _idFuncion);
    var butacaIds = _idButaca.split(',').filter(function(b) { return b; });
    if (butacaIds.length === 0) {
        showError('Error: no se encontraron butacas seleccionadas. Vuelve a la cartelera.');
        return;
    }
    params.append('id_butaca', butacaIds.join(','));
    params.append('precio', precioFinal.toFixed(2));
    var metodoMap = { 'visa': 'Tarjeta Visa', 'mastercard': 'Tarjeta Mastercard', 'yape': 'Yape', 'plin': 'Plin', 'efectivo': 'Efectivo' };
    params.append('metodo_pago', metodoMap[selectedMethod] || selectedMethod);
    params.append('estado', 'completada');
    if (_codigoPromo) params.append('codigo_promo', _codigoPromo);

    if (_food) {
        try {
            var items = JSON.parse(_food);
            var prodStr = items.map(function(item) {
                var safeName = String(item.name).replace(/:/g, ' ').replace(/,/g, ' ');
                return item.id + ':' + safeName + ':' + item.qty + ':' + item.price;
            }).join(',');
            params.append('productos', prodStr);
        } catch(e) {
            console.warn('Error parsing food data', e);
        }
    }

    fetch(API_URL + '/CompraController', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(function(r) {
        if (!r.ok) throw new Error('Error del servidor: ' + r.status);
        return r.json();
    })
    .then(function(data) {
        if (data.success) {
            window._pagoConfirmado = true;
            showSuccess('\u00A1Pago exitoso! Disfruta tu pel\u00EDcula.');
            setTimeout(function() { window.location.href = 'index.html'; }, 2000);
        } else {
            showError(data.mensaje || 'Error al procesar el pago');
        }
    })
    .catch(function(err) {
        showError('Error de conexi\u00F3n con el servidor. Intenta nuevamente.');
        console.error('Error al procesar pago:', err);
    });
}
