document.addEventListener('DOMContentLoaded', function() {
    fetch('includes/header.html')
        .then(r => r.text())
        .then(d => { document.getElementById('header-placeholder').innerHTML = d; setActiveNavLink(); })
        .catch(function(e) { console.error(e); });
    fetch('includes/footer.html')
        .then(r => r.text())
        .then(d => { document.getElementById('footer-placeholder').innerHTML = d; })
        .catch(function(e) { console.error(e); });

    fetch(API_URL + '/ProductoController?activos=true')
        .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(productos => {
            const container = document.getElementById('productos-container');
            if (productos.length === 0) {
                container.innerHTML = '<div class="col-12 text-center text-muted">No hay productos disponibles</div>';
                return;
            }
            const badges = { Comida: 'bg-warning text-dark', Bebida: 'bg-info text-dark', Combo: 'bg-danger text-white' };
            container.innerHTML = productos.map(p => {
                const imgSrc = p.imagen_url || '';
                const badge = badges[p.categoria] || 'bg-secondary';
                return `<div class="col-md-6 col-lg-4">
                    <div class="card border-0 shadow-sm h-100 text-center p-4 food-card">
                        ${imgSrc ? '<img src="' + escapeHtml(imgSrc) + '" alt="' + escapeHtml(p.nombre) + '" class="food-img mb-3" onerror="this.style.display=\'none\'">' : ''}
                        <h3 class="fs-5 fw-bold">${escapeHtml(p.nombre)}</h3>
                        <p class="text-muted small">${escapeHtml(p.descripcion || '')}</p>
                        <p class="fw-bold text-warning fs-4">S/ ${(p.precio || 0).toFixed(2)}</p>
                        <span class="badge ${badge} align-self-center">${escapeHtml(p.categoria || '')}</span>
                    </div>
                </div>`;
            }).join('');
        })
        .catch(() => {
            document.getElementById('productos-container').innerHTML = '<div class="col-12 text-center text-danger">Error al cargar productos</div>';
        });
});
