document.addEventListener('DOMContentLoaded', function() {
    fetch('includes/header.html')
        .then(function(r) { return r.text(); })
        .then(function(data) {
            document.getElementById('header-placeholder').innerHTML = data;
            setActiveNavLink();
        })
        .catch(function(e) { console.error('Error cargando header:', e); });

    fetch('includes/footer.html')
        .then(function(r) { return r.text(); })
        .then(function(data) {
            document.getElementById('footer-placeholder').innerHTML = data;
        })
        .catch(function(e) { console.error('Error cargando footer:', e); });

    cargarDestacadas();
});

function cargarDestacadas() {
    fetch(API_URL + '/PeliculaController')
        .then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(function(peliculas) {
            var destacadas = peliculas.filter(function(p) { return p.destacado == 1 && p.activo == 1; });
            renderCarousel(destacadas);
            renderGrid(destacadas);
        })
        .catch(function() {
            document.getElementById('carousel-inner').innerHTML = '<div class="text-center py-5 text-danger">Error al cargar</div>';
            document.getElementById('destacadas-grid').innerHTML = '<div class="col-12 text-center text-danger py-3">Error al cargar</div>';
        });
}

function renderCarousel(lista) {
    var inner = document.getElementById('carousel-inner');
    var indicators = document.getElementById('carousel-indicators');
    if (lista.length === 0) {
        inner.innerHTML = '<div class="text-center py-5 text-muted">No hay pel\u00EDculas destacadas</div>';
        return;
    }
    var slidesHtml = '';
    var indHtml = '';
    lista.forEach(function(p, i) {
        var active = i === 0 ? ' active' : '';
        var imgSrc = p.imagen_url || '';
        slidesHtml += '<div class="carousel-item' + active + '">' +
            (imgSrc ? '<img src="' + imgSrc + '" class="d-block w-100" alt="' + p.titulo + '" style="max-height:500px;object-fit:cover;">' : '<div class="d-flex align-items-center justify-content-center" style="height:500px;background:linear-gradient(135deg,#1a1a2e,#16213e);"><h2 class="text-warning fw-bold">' + p.titulo + '</h2></div>') +
            '<div class="carousel-caption d-none d-md-block" style="background:rgba(0,0,0,0.5);border-radius:12px;padding:16px;">' +
                '<h3 class="fw-bold">' + p.titulo + '</h3>' +
                '<p>' + (p.genero || '') + ' &middot; ' + p.duracion_minutos + ' min</p>' +
                '<a href="cartelera.html" class="btn btn-warning fw-bold btn-sm">Comprar Entradas</a>' +
            '</div></div>';
        indHtml += '<button type="button" data-bs-target="#carouselPrincipal" data-bs-slide-to="' + i + '"' + (i === 0 ? ' class="active"' : '') + '></button>';
    });
    inner.innerHTML = slidesHtml;
    indicators.innerHTML = indHtml;
}

function renderGrid(lista) {
    var grid = document.getElementById('destacadas-grid');
    if (lista.length === 0) {
        grid.innerHTML = '<div class="col-12 text-center text-muted py-3">No hay pel\u00EDculas destacadas</div>';
        return;
    }
    grid.innerHTML = lista.map(function(p) {
        var imgSrc = p.imagen_url || '';
        return '<div class="col-md-6 col-lg-4">' +
            '<div class="card h-100 border-0 shadow-sm movie-card-custom">' +
                (imgSrc ? '<img src="' + imgSrc + '" class="card-img-top" alt="' + p.titulo + '" style="height:300px;object-fit:cover;">' : '<div class="d-flex align-items-center justify-content-center" style="height:300px;background:linear-gradient(135deg,#1a1a2e,#16213e);"><h4 class="text-warning fw-bold">' + p.titulo + '</h4></div>') +
                '<div class="card-body">' +
                    '<h3 class="card-title h4">' + p.titulo + '</h3>' +
                    '<p class="card-text text-muted">' + (p.genero || '') + ' &middot; ' + p.duracion_minutos + ' min</p>' +
                    '<a href="cartelera.html" class="btn btn-warning w-100 fw-bold">Comprar Entradas</a>' +
                '</div>' +
            '</div></div>';
    }).join('');
}
