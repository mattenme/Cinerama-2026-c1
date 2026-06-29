var _registroId = null;

document.addEventListener('DOMContentLoaded', function() {
    var toggleBtn = document.getElementById('toggle-password');
    var pwInput = document.getElementById('contrasena');
    if (toggleBtn && pwInput) {
        toggleBtn.addEventListener('click', function() {
            var type = pwInput.getAttribute('type') === 'password' ? 'text' : 'password';
            pwInput.setAttribute('type', type);
            this.querySelector('i').classList.toggle('bi-eye');
            this.querySelector('i').classList.toggle('bi-eye-slash');
        });
    }

    var submitBtn = document.getElementById('register-form').querySelector('button[type=submit]');

    document.getElementById('register-form').addEventListener('submit', function(e) {
        e.preventDefault();
        if (submitBtn.disabled) return;
        var dni = document.getElementById('dni').value.trim();
        var nombre = document.getElementById('nombre').value.trim();
        var emailEl = document.getElementById('email');
        var email = emailEl.value.trim();
        var contrasena = document.getElementById('contrasena').value;
        if (!dni || !nombre || !email || !contrasena) { showError('Completa todos los campos obligatorios'); return; }

        submitBtn.disabled = true;
        submitBtn.textContent = 'Registrando...';

        var params = new URLSearchParams();
        params.append('action', 'insertar');
        params.append('dni', dni);
        params.append('nombre', nombre);
        params.append('email', email);
        params.append('telefono', document.getElementById('telefono').value);
        params.append('contrasena', contrasena);

        fetch(API_URL + '/ClienteController', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
            .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
            .then(function(data) {
                console.log('Registro response:', data);
                if (data.success) {
                    _registroId = data.id;
                    var form = document.getElementById('register-form');
                    if (form) form.classList.add('d-none');
                    var verifyEmail = document.getElementById('verify-email');
                    if (verifyEmail) verifyEmail.textContent = email;
                    var verifySection = document.getElementById('verify-section');
                    if (verifySection) verifySection.classList.remove('d-none');
                    var firstInput = document.querySelector('#code-inputs input');
                    if (firstInput) firstInput.focus();
                } else {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Crear cuenta';
                    showError(data.mensaje || 'Error al registrarse');
                }
            })
            .catch(function(err) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Crear cuenta';
                showError('Error de conexi\u00F3n con el servidor');
                console.error(err);
            });
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
