(function() {
    function actualizarAuth() {
        var icono = document.querySelector('.login-icon');
        if (!icono) return false;
        var clienteId = localStorage.getItem('clienteId');
        var esAdmin = localStorage.getItem('clienteAdmin') === 'true';
        var nombre = localStorage.getItem('clienteNombre') || 'Usuario';
        var avatar = localStorage.getItem('clienteAvatar') || '';

        var badge = document.getElementById('admin-badge');
        if (badge) badge.classList.toggle('d-none', !esAdmin);

        if (clienteId) {
            var li = icono.closest('li');
            var dropdownItems = '<li><a class="dropdown-item" href="' + BASE_PATH + '/perfil.html">Mi Perfil</a></li>';
            if (esAdmin) {
                dropdownItems = '<li><a class="dropdown-item" href="' + BASE_PATH + '/admin/indexAdmin.html">Panel Admin</a></li>' +
                    '<li><hr class="dropdown-divider"></li>' + dropdownItems;
            }
            li.innerHTML =
                '<div class="dropdown">' +
                    '<button class="btn dropdown-toggle text-white d-flex align-items-center gap-2 border-0" data-bs-toggle="dropdown" style="background:transparent;">' +
                        '<img src="' + (avatar || BASE_PATH + '/assets/img/usuario.ico') + '" class="rounded-circle" style="width:28px;height:28px;object-fit:cover;" onerror="this.src=\'' + BASE_PATH + '/assets/img/usuario.ico\'">' +
                        '<span class="d-none d-md-inline small">' + nombre + '</span>' +
                    '</button>' +
                    '<ul class="dropdown-menu dropdown-menu-end">' +
                        dropdownItems +
                        '<li><hr class="dropdown-divider"></li>' +
                        '<li><a class="dropdown-item text-danger" href="#" onclick="var dm=localStorage.getItem(\'cin-dark-mode\');localStorage.clear();if(dm)localStorage.setItem(\'cin-dark-mode\',dm);location.href=\'' + BASE_PATH + '/\'">Cerrar Sesi\u00F3n</a></li>' +
                    '</ul>' +
                '</div>';
        }
        return true;
    }
    var pollAttempts = 0;
    var poll = setInterval(function() {
        pollAttempts++;
        if (actualizarAuth() || pollAttempts > 200) clearInterval(poll);
    }, 100);
})();
