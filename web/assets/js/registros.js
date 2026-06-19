document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('register-form');

    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const dni = document.getElementById('dni').value;
        const nombre = document.getElementById('nombre').value;

        const params = new URLSearchParams();
        params.append('action', 'insertar');
        params.append('dni', dni);
        params.append('nombre', nombre);
        params.append('email', document.getElementById('email').value);
        params.append('telefono', document.getElementById('telefono').value);

        fetch(API_URL + '/ClienteController', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        })
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(data => {
            if (data.success) {
                showSuccess('Registro exitoso. Redirigiendo al login...');
                setTimeout(function() { window.location.href = 'login.html'; }, 1500);
            } else {
                showError(data.mensaje || 'Error al registrarse');
            }
        })
            .catch(err => {
                showError('Error de conexi\u00F3n con el servidor');
            console.error(err);
        });
    });
});
