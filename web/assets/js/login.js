document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const dni = document.getElementById('dni').value;
        const nombre = document.getElementById('nombre').value;

        fetch(API_URL + '/ClienteController?dni=' + encodeURIComponent(dni))
            .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
            .then(data => {
                if (data && data.id_cliente && data.nombre === nombre) {
                    localStorage.setItem('clienteId', data.id_cliente);
                    localStorage.setItem('clienteNombre', data.nombre);
                    localStorage.setItem('clienteAvatar', '');
                    if (data.esAdmin) {
                        localStorage.setItem('clienteAdmin', 'true');
                        window.location.href = 'admin/indexAdmin.html';
                    } else {
                        localStorage.removeItem('clienteAdmin');
                        window.location.href = 'index.html';
                    }
                } else {
                    showError('Credenciales incorrectas');
                }
            })
            .catch(err => {
                showError('Error de conexi\u00F3n con el servidor');
                console.error(err);
            });
    });
});
