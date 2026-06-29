var _registroId = null;

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const toggleBtn = document.getElementById('toggle-password');
    const pwInput = document.getElementById('contrasena');

    toggleBtn.addEventListener('click', function() {
        const type = pwInput.getAttribute('type') === 'password' ? 'text' : 'password';
        pwInput.setAttribute('type', type);
        this.querySelector('i').classList.toggle('bi-eye');
        this.querySelector('i').classList.toggle('bi-eye-slash');
    });

    // Auto-advance code inputs
    document.querySelectorAll('#code-inputs input').forEach(function(inp) {
        inp.addEventListener('input', function() {
            var idx = parseInt(this.dataset.idx);
            if (this.value.length === 1 && idx < 5) {
                var next = document.querySelector('#code-inputs input[data-idx="' + (idx + 1) + '"]');
                if (next) next.focus();
            }
            document.getElementById('code-error').classList.add('d-none');
        });
        inp.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && this.value === '' && parseInt(this.dataset.idx) > 0) {
                var prev = document.querySelector('#code-inputs input[data-idx="' + (parseInt(this.dataset.idx) - 1) + '"]');
                if (prev) { prev.focus(); prev.value = ''; }
            }
            if (e.key === 'Enter') document.getElementById('btn-verify').click();
        });
    });

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const dni = document.getElementById('dni').value;
        const contrasena = document.getElementById('contrasena').value;

        const params = new URLSearchParams();
        params.append('action', 'login');
        params.append('dni', dni);
        params.append('contrasena', contrasena);

        fetch(API_URL + '/ClienteController?' + params.toString())
            .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
            .then(data => {
                if (data.success) {
                    localStorage.setItem('clienteId', data.id_cliente);
                    localStorage.setItem('clienteNombre', data.nombre);
                    localStorage.setItem('clienteAvatar', '');
                    localStorage.setItem('clienteRol', data.rol);
                    if (data.rol === 'admin') {
                        window.location.href = 'admin/indexAdmin.html';
                    } else {
                        window.location.href = 'index.html';
                    }
                } else if (data.needVerify) {
                    _registroId = data.id;
                    loginForm.classList.add('d-none');
                    document.getElementById('verify-section').classList.remove('d-none');
                    document.getElementById('verify-email').textContent = dni;
                    document.querySelector('#code-inputs input').focus();
                } else {
                    showError(data.mensaje || 'Credenciales incorrectas');
                }
            })
            .catch(err => {
                showError('Error de conexi\u00F3n con el servidor');
                console.error(err);
            });
    });

    document.getElementById('btn-verify').addEventListener('click', function() {
        var codigo = '';
        document.querySelectorAll('#code-inputs input').forEach(function(inp) { codigo += inp.value; });
        if (codigo.length !== 6) { showError('Ingresa el c\u00f3digo completo de 6 d\u00edgitos'); return; }

        var params = new URLSearchParams();
        params.append('action', 'verificar');
        params.append('id', _registroId);
        params.append('codigo', codigo);

        fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (data.success) {
                    showSuccess('Cuenta verificada correctamente');
                    setTimeout(function() { window.location.href = 'login.html'; }, 1500);
                } else {
                    var errEl = document.getElementById('code-error');
                    errEl.textContent = data.mensaje || 'C\u00f3digo incorrecto';
                    errEl.classList.remove('d-none');
                }
            })
            .catch(function() { showError('Error de conexi\u00F3n'); });
    });

    document.getElementById('btn-resend').addEventListener('click', function() {
        if (!_registroId) return;
        var params = new URLSearchParams();
        params.append('action', 'reenviarCodigo');
        params.append('id', _registroId);
        fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (data.success) showSuccess('C\u00f3digo reenviado');
                else showError(data.mensaje || 'Error');
            })
            .catch(function() { showError('Error de conexi\u00f3n'); });
    });
});
