function escapeHtml(str) {
    if (str == null || typeof str !== 'string') return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

document.addEventListener('DOMContentLoaded', function() {
    mostrarSpinner('cartelera-container');
    fetch(API_URL + '/PeliculaController')
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(peliculas => {
            const container = document.getElementById('cartelera-container');
            if (!peliculas || peliculas.length === 0) {
                container.innerHTML = '<div class="col-12 text-center text-muted py-5"><h4>No hay pel\u00EDculas disponibles en cartelera</h4><p class="small">Agrega pel\u00EDculas desde el panel de administraci\u00F3n.</p></div>';
                return;
            }
            container.innerHTML = '';
            peliculas.forEach(p => {
                const col = document.createElement('div');
                col.className = 'col-md-6 col-lg-4';
                col.innerHTML = `
                    <div class="card h-100 border-0 shadow-sm movie-card-custom">
                        <div class="card-body d-flex flex-column text-center py-4">
                            ${p.imagen_url ? '<img src="' + escapeHtml(p.imagen_url) + '" alt="' + escapeHtml(p.titulo) + '" class="movie-card-img">' : '<div class="display-1 mb-3">??</div>'}
                            <h3 class="card-title h4">${escapeHtml(p.titulo)}</h3>
                            <p class="card-text text-muted">${p.duracion_minutos || ''} min${p.genero ? ' ? ' + escapeHtml(p.genero) : ''}</p>
                            <button class="btn btn-warning fw-bold mt-auto btn-select-movie" data-pelicula-id="${p.id_pelicula}" data-titulo="${escapeHtml(p.titulo)}">
                                Seleccionar Asientos
                            </button>
                        </div>
                    </div>
                `;
                container.appendChild(col);
            });
        })
        .catch(err => {
            console.error('Error al cargar pel\u00EDculas:', err);
            document.getElementById('cartelera-container').innerHTML = '<div class="col-12 text-center text-muted">Error al cargar la cartelera</div>';
        });
});

document.addEventListener('click', function(e) {
    const btn = e.target.closest('.btn-select-movie');
    if (!btn) return;

    const peliculaId = btn.dataset.peliculaId;
    const titulo = btn.dataset.titulo;

    document.getElementById('funcionModalLabel').textContent = 'Horarios - ' + titulo;
    const list = document.getElementById('funcion-list');
    list.innerHTML = '<div class="text-center text-muted">Cargando horarios...</div>';

    const modal = new bootstrap.Modal(document.getElementById('funcionModal'));
    modal.show();

    fetch(API_URL + '/FuncionController?idPelicula=' + peliculaId)
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(funciones => {
            if (funciones.length === 0) {
                list.innerHTML = '<div class="text-center text-muted">No hay horarios disponibles</div>';
                return;
            }
            list.innerHTML = funciones.map(f => `
                <button class="btn btn-outline-warning text-start py-3" onclick="location.href='asientos.html?id_funcion=${f.id_funcion}'">
                    <strong>${f.sala ? f.sala.nombre : 'Sala'}</strong><br>
                    <small>${formatFecha(f.hora_inicio)}</small>
                </button>
            `).join('');
        })
        .catch(() => {
            list.innerHTML = '<div class="text-center text-danger">Error al cargar horarios</div>';
        });
});

function formatFecha(dt) {
    if (!dt) return '';
    const d = new Date(dt);
    return d.toLocaleDateString('es-PE', { day: 'numeric', month: 'short', year: 'numeric' }) +
        ' ' + d.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
}
