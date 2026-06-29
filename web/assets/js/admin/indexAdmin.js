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
            cargarDashboard();
        });

        const modulos = [
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" fill="currentColor"><path d="M96 160C96 124.7 124.7 96 160 96L480 96C515.3 96 544 124.7 544 160L544 480C544 515.3 515.3 544 480 544L160 544C124.7 544 96 515.3 96 480L96 160zM144 432L144 464C144 472.8 151.2 480 160 480L192 480C200.8 480 208 472.8 208 464L208 432C208 423.2 200.8 416 192 416L160 416C151.2 416 144 423.2 144 432zM448 416C439.2 416 432 423.2 432 432L432 464C432 472.8 439.2 480 448 480L480 480C488.8 480 496 472.8 496 464L496 432C496 423.2 488.8 416 480 416L448 416zM144 304L144 336C144 344.8 151.2 352 160 352L192 352C200.8 352 208 344.8 208 336L208 304C208 295.2 200.8 288 192 288L160 288C151.2 288 144 295.2 144 304zM448 288C439.2 288 432 295.2 432 304L432 336C432 344.8 439.2 352 448 352L480 352C488.8 352 496 344.8 496 336L496 304C496 295.2 488.8 288 480 288L448 288zM144 176L144 208C144 216.8 151.2 224 160 224L192 224C200.8 224 208 216.8 208 208L208 176C208 167.2 200.8 160 192 160L160 160C151.2 160 144 167.2 144 176zM448 160C439.2 160 432 167.2 432 176L432 208C432 216.8 439.2 224 448 224L480 224C488.8 224 496 216.8 496 208L496 176C496 167.2 488.8 160 480 160L448 160z"/></svg>', label: 'Películas', desc: 'Catálogo de películas', link: 'peliculas.html', color: 'bg-warning text-dark' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', label: 'Horarios', desc: 'Programación de funciones', link: 'horarios.html', color: 'bg-info text-dark' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128"><g transform="translate(0,128) scale(0.1,-0.1)" fill="currentColor"><path d="M67 1133 c-4 -3 -7 -112 -7 -240 l0 -234 33 3 c28 3 35 10 59 58 34 69 56 179 65 315 l6 105 -75 0 c-41 0 -78 -3 -81 -7z"/><path d="M260 1088 c0 -123 -34 -317 -67 -381 -8 -16 -12 -31 -10 -34 11 -10 87 38 116 74 68 82 121 237 121 354 l0 39 -80 0 -80 0 0 -52z"/><path d="M456 1060 c-16 -199 -109 -363 -238 -420 l-48 -21 24 -37 c25 -38 50 -111 60 -172 l6 -35 57 0 c32 0 68 -4 80 -8 20 -7 21 -6 11 35 -14 63 -50 120 -114 176 l-55 50 38 7 c61 11 157 61 214 112 58 53 124 148 134 193 4 17 10 30 15 30 5 0 11 -13 15 -30 10 -45 76 -140 134 -193 57 -51 153 -101 214 -112 l38 -7 -55 -50 c-64 -56 -100 -113 -114 -176 -10 -41 -9 -42 11 -35 12 4 48 8 80 8 l57 0 6 35 c10 61 35 134 60 172 l24 37 -48 21 c-129 57 -224 225 -239 423 l-6 77 -177 0 -177 0 -7 -80z"/><path d="M860 1101 c0 -111 45 -253 108 -338 29 -39 117 -101 128 -91 2 3 -4 25 -15 49 -28 63 -49 179 -57 307 l-7 112 -79 0 -78 0 0 -39z"/><path d="M1064 1028 c8 -130 37 -265 69 -323 18 -32 29 -41 55 -43 l32 -3 -2 238 -3 238 -79 3 -79 3 7 -113z"/><path d="M62 423 c3 -189 4 -198 23 -201 15 -2 26 9 44 42 13 25 39 55 57 68 33 22 34 25 28 68 -16 116 -80 220 -134 220 -20 0 -20 -5 -18 -197z"/><path d="M1146 590 c-39 -34 -59 -78 -77 -168 -11 -61 -11 -64 10 -78 34 -24 69 -65 82 -96 9 -21 18 -28 33 -26 20 3 21 8 24 201 2 191 2 197 -17 197 -11 0 -36 -14 -55 -30z"/><path d="M244 321 c-48 -22 -80 -59 -95 -112 -20 -67 -16 -69 134 -69 l133 0 12 58 c21 97 21 95 -28 120 -54 27 -101 28 -156 3z"/><path d="M564 321 c-48 -22 -80 -59 -95 -112 -21 -69 -20 -69 171 -69 191 0 192 0 171 69 -15 53 -47 90 -95 112 -52 24 -100 24 -152 0z"/><path d="M880 318 c-49 -25 -49 -23 -28 -120 l12 -58 133 0 c150 0 154 2 134 69 -15 53 -47 90 -95 112 -55 25 -102 24 -156 -3z"/></g></svg>', label: 'Salas', desc: 'Configuración de salas', link: 'salas.html', color: 'bg-primary text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10c0 2.2-1.8 4-4 4s-4-1.8-4-4"/></svg>', label: 'Productos', desc: 'Comidas, bebidas y combos', link: 'productos.html', color: 'bg-warning text-dark' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><path d="M525.2 82.9C536.7 88 544 99.4 544 112L544 528C544 540.6 536.7 552 525.2 557.1C513.7 562.2 500.4 560.3 490.9 552L444.3 511.3C400.7 473.2 345.6 451 287.9 448.3L287.9 544C287.9 561.7 273.6 576 255.9 576L223.9 576C206.2 576 191.9 561.7 191.9 544L191.9 448C121.3 448 64 390.7 64 320C64 249.3 121.3 192 192 192L276.5 192C338.3 191.8 397.9 169.3 444.4 128.7L491 88C500.4 79.7 513.9 77.8 525.3 82.9zM288 384L288 384.2C358.3 386.9 425.8 412.7 480 457.6L480 182.3C425.8 227.2 358.3 253 288 255.7L288 384z" fill="currentColor"/></svg>', label: 'Promociones', desc: 'Códigos y descuentos', link: 'promociones.html', color: 'bg-danger text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128"><g transform="translate(0,128) scale(0.1,-0.1)" fill="currentColor"><path d="M294 1110 c-12 -4 -31 -20 -42 -35 -42 -53 -39 -75 41 -343 42 -138 83 -262 92 -275 33 -51 63 -57 309 -57 l227 0 24 -25 c14 -13 25 -35 25 -49 0 -77 -27 -86 -256 -86 -222 0 -250 8 -305 89 l-34 49 -43 -30 -42 -29 17 -31 c28 -49 90 -108 136 -129 37 -16 68 -19 270 -19 259 0 284 6 332 78 54 82 37 187 -39 248 -29 23 -36 36 -36 64 0 48 -30 100 -71 122 -28 15 -57 18 -171 18 l-137 0 -31 103 c-86 284 -93 300 -129 324 -35 24 -96 30 -137 13z"/></g></svg>', label: 'Asientos', desc: 'Asientos por sala', link: 'asientos.html', color: 'bg-success text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>', label: 'Calificaciones', desc: 'Valoraciones de películas', link: 'calificaciones.html', color: 'bg-danger text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128"><g transform="translate(0,128) scale(0.1,-0.1)" fill="currentColor"><path d="M565 1184 c-99 -36 -159 -146 -136 -249 44 -197 314 -231 404 -50 41 82 28 168 -34 239 -61 69 -148 92 -234 60z"/><path d="M141 1023 c-52 -27 -76 -66 -75 -122 1 -62 30 -104 89 -127 43 -16 47 -16 90 0 52 20 82 57 90 112 16 103 -102 186 -194 137z"/><path d="M1021 1023 c-52 -27 -76 -66 -75 -122 1 -62 30 -104 89 -127 43 -16 47 -16 90 0 52 20 82 57 90 112 16 103 -102 186 -194 137z"/><path d="M116 670 c-37 -12 -85 -56 -102 -97 -10 -23 -14 -79 -14 -197 0 -213 -1 -211 132 -211 l93 0 5 155 c6 166 17 209 74 283 14 19 26 36 26 39 0 3 -18 13 -40 22 -43 17 -126 20 -174 6z"/><path d="M525 666 c-93 -29 -159 -87 -197 -171 -19 -42 -23 -72 -26 -191 -5 -166 4 -196 66 -214 52 -16 539 -10 568 7 39 22 47 62 42 210 -4 117 -8 146 -27 191 -32 70 -115 146 -183 167 -62 18 -187 19 -243 1z"/><path d="M990 664 c-19 -8 -36 -17 -38 -19 -3 -2 7 -20 22 -40 59 -77 70 -118 76 -285 l5 -155 70 -3 c83 -4 109 2 135 28 18 18 20 34 20 184 0 92 -5 177 -11 193 -15 42 -68 92 -109 103 -51 14 -130 12 -170 -6z"/></g></svg>', label: 'Clientes', desc: 'Usuarios registrados', link: 'clientes.html', color: 'bg-dark text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128"><g transform="translate(0,128) scale(0.1,-0.1)" fill="currentColor"><path d="M281 1263 c-21 -19 -261 -548 -261 -577 0 -36 24 -59 96 -92 116 -53 111 -58 116 104 4 150 14 183 71 226 27 20 41 21 373 24 313 2 345 4 342 19 -3 18 -657 313 -694 313 -12 0 -31 -8 -43 -17z"/><path d="M353 870 c-12 -5 -26 -18 -32 -29 -7 -13 -11 -129 -11 -329 l0 -309 23 -21 c19 -18 35 -22 90 -22 l67 0 0 50 0 50 -40 0 -40 0 0 135 0 135 375 0 375 0 0 -135 0 -135 -43 0 -43 0 0 -50 1 -50 68 0 c59 0 71 3 92 25 l25 24 0 300 c0 318 -3 340 -47 360 -29 13 -826 14 -860 1z m807 -135 l0 -45 -375 0 -375 0 0 45 0 45 375 0 375 0 0 -45z"/><path d="M689 387 c-24 -13 -57 -43 -74 -66 -27 -38 -30 -51 -30 -115 0 -64 4 -78 29 -114 34 -50 112 -92 170 -92 106 1 206 100 206 205 0 148 -170 251 -301 182z m108 -53 c3 -8 16 -20 29 -24 34 -13 21 -43 -16 -35 -17 4 -31 1 -39 -9 -10 -12 -4 -18 31 -38 37 -20 43 -28 46 -60 3 -30 -1 -40 -21 -53 -14 -9 -27 -25 -30 -36 -6 -21 -37 -16 -37 6 0 7 -12 16 -26 19 -17 5 -24 12 -22 24 3 13 12 16 46 14 36 -3 42 -1 42 16 0 12 -11 24 -31 32 -62 26 -74 83 -23 116 13 9 24 23 24 30 0 19 19 18 27 -2z"/></g></svg>', label: 'Compras', desc: 'Historial de pagos', link: 'compras.html', color: 'bg-success text-white' },
            { icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" fill="currentColor"><path d="M320 64C334.7 64 348.2 72.1 355.2 85L571.2 485C577.9 497.4 577.6 512.4 570.4 524.5C563.2 536.6 550.1 544 536 544L104 544C89.9 544 76.8 536.6 69.6 524.5C62.4 512.4 62.1 497.4 68.8 485L284.8 85C291.8 72.1 305.3 64 320 64zM320 416C302.3 416 288 430.3 288 448C288 465.7 302.3 480 320 480C337.7 480 352 465.7 352 448C352 430.3 337.7 416 320 416zM320 224C301.8 224 287.3 239.5 288.6 257.7L296 361.7C296.9 374.2 307.4 384 319.9 384C332.5 384 342.9 374.3 343.8 361.7L351.2 257.7C352.5 239.5 338.1 224 319.8 224z"/></svg>', label: 'Incidencias', desc: 'Reportes y problemas', link: 'incidencias.html', color: 'bg-secondary text-white' },
        ];

        function cargarDashboard() {
            const container = document.getElementById('dashboard-cards');
            container.innerHTML = modulos.map(m => `
                <div class="col-md-6 col-lg-4">
                    <a href="${m.link}" class="text-decoration-none">
                        <div class="card border-0 shadow-sm h-100 admin-card">
                            <div class="card-body d-flex align-items-center gap-3 p-4">
                                <div class="admin-icon ${m.color}">${m.icon}</div>
                                <div>
                                    <h5 class="fw-bold mb-1 text-dark">${m.label}</h5>
                                    <p class="text-muted mb-0 small">${m.desc}</p>
                                </div>
                            </div>
                        </div>
                    </a>
                </div>
            `).join('');
        }

        function setActiveNavLink() {
            const path = window.location.pathname;
            let page = path.substring(path.lastIndexOf('/') + 1);
            if (!page) page = 'indexAdmin.html';
            document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href') === '/' + page) link.classList.add('active');
            });
        }
