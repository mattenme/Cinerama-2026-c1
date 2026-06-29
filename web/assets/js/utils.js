(function() {
    var css = document.createElement('style');
    css.textContent = '.checkmark-circle,.x-circle{stroke-dasharray:166;stroke-dashoffset:166;stroke-width:2;stroke-miterlimit:10;stroke:#fff;fill:none;animation:stroke .6s cubic-bezier(.65,0,.45,1) forwards}.checkmark-check{transform-origin:50% 50%;stroke-dasharray:48;stroke-dashoffset:48;stroke-width:3;stroke:#fff;fill:none;animation:stroke .3s cubic-bezier(.65,0,.45,1) .3s forwards}.x-line{transform-origin:50% 50%;stroke-dasharray:22.6;stroke-dashoffset:22.6;stroke-width:3;stroke:#fff;fill:none;stroke-linecap:round}.x-line:nth-child(2){animation:stroke .3s cubic-bezier(.65,0,.45,1) .3s forwards}.x-line:nth-child(3){animation:stroke .3s cubic-bezier(.65,0,.45,1) .45s forwards}@keyframes stroke{to{stroke-dashoffset:0}}.toast-progress{height:3px;border-radius:0 0 .5rem .5rem}.toast-enhanced{box-shadow:0 8px 32px rgba(0,0,0,.18)!important;border-radius:.5rem!important;overflow:hidden}.toast-progress-inner{height:3px;border-radius:0 0 .5rem .5rem;animation:shrink 3s linear forwards}@keyframes shrink{0%{width:100%}to{width:0%}}';
    document.head.appendChild(css);
})();

(function() {
    var c = document.getElementById('toast-container');
    if (!c) {
        c = document.createElement('div');
        c.id = 'toast-container';
        c.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px;';
        document.body.appendChild(c);
    }

    function iconSVG(type) {
        if (type === 'success') {
            return '<svg width="28" height="28" viewBox="0 0 28 28" style="flex-shrink:0"><circle class="checkmark-circle" cx="14" cy="14" r="12"/><path class="checkmark-check" d="M8 14l4 4 8-8"/></svg>';
        }
        if (type === 'error') {
            return '<svg width="28" height="28" viewBox="0 0 28 28" style="flex-shrink:0"><circle class="x-circle" cx="14" cy="14" r="12"/><path class="x-line" d="M10 10l8 8"/><path class="x-line" d="M18 10l-8 8"/></svg>';
        }
        return '';
    }

    var bg = {success:'bg-success', error:'bg-danger', warning:'bg-warning text-dark', info:'bg-info text-dark'};

    window.showToast = function(msg, type) {
        type = type || 'success';
        var t = document.createElement('div');
        t.className = 'toast toast-enhanced align-items-center text-white border-0 show ' + (bg[type]||'bg-success');
        t.setAttribute('role','alert');
        t.innerHTML = '<div class="d-flex align-items-center w-100" style="padding:12px 12px 10px 16px"><div class="me-3 d-flex align-items-center">' + iconSVG(type) + '</div><div class="toast-body fw-semibold" style="padding:0;font-size:.95rem;flex:1">' + msg + '</div><button type="button" class="btn-close btn-close-white ms-3" style="font-size:.7rem;opacity:.7" data-bs-dismiss="toast"></button></div><div class="toast-progress w-100" style="background:rgba(0,0,0,.12)"><div class="toast-progress-inner" style="background:rgba(255,255,255,.5)"></div></div>';
        c.appendChild(t);
        setTimeout(function() { t.classList.add('show'); }, 10);
        setTimeout(function() {
            if (t.parentNode) {
                t.style.transition = 'opacity .3s ease';
                t.style.opacity = '0';
                setTimeout(function() { if (t.parentNode) t.remove(); }, 300);
            }
        }, 3200);
    };
    window.showSuccess = function(m) { showToast(m,'success'); };
    window.showError = function(m) { showToast(m,'error'); };
    window.showWarning = function(m) { showToast(m,'warning'); };
    window.showInfo = function(m) { showToast(m,'info'); };
})();

function setLoading(btn, loading) {
    if (!btn) return;
    if (loading) {
        btn.disabled = true;
        btn._origHtml = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Cargando...';
    } else {
        btn.disabled = false;
        if (btn._origHtml) btn.innerHTML = btn._origHtml;
    }
}

function setActiveNavLink() {
    var path = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(function(link) {
        link.classList.remove('active');
        if (link.getAttribute('href') === path || (path === '/Cinerama_1/' && link.getAttribute('href') === '/Cinerama_1/index.html')) link.classList.add('active');
    });
}

(function() {
    var spinnerHtml = '<div class="text-center py-5"><div class="spinner-border text-warning" role="status" style="width:3rem;height:3rem;"><span class="visually-hidden">Cargando...</span></div><p class="text-muted mt-2">Cargando...</p></div>';
    window.mostrarSpinner = function(id) { var el = document.getElementById(id); if (el) el.innerHTML = spinnerHtml; };
    window.ocultarSpinner = function(id) { };
})();

window.showConfirm = function(msg, onConfirm) {
    var d = document.getElementById('confirm-dialog');
    if (d) d.remove();
    d = document.createElement('div');
    d.id = 'confirm-dialog';
    d.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;z-index:10000;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,0.5);';
    d.innerHTML = '<div style="background:#1e1e2e;color:#fff;border-radius:12px;padding:24px;max-width:400px;width:90%;box-shadow:0 8px 32px rgba(0,0,0,0.3);text-align:center;">' +
        '<h5 class="mb-3" style="margin:0 0 12px 0;font-size:1.1rem;">Confirmar</h5>' +
        '<p class="mb-4" style="margin:0 0 20px 0;color:#aaa;">' + msg + '</p>' +
        '<div style="display:flex;gap:8px;justify-content:flex-end;">' +
        '<button id="confirm-no" style="padding:8px 20px;border-radius:6px;border:1px solid #444;background:transparent;color:#fff;cursor:pointer;">Cancelar</button>' +
        '<button id="confirm-yes" style="padding:8px 20px;border-radius:6px;border:none;background:#ffc107;color:#000;font-weight:600;cursor:pointer;">S\u00ED, continuar</button>' +
        '</div></div>';
    document.body.appendChild(d);
    d.addEventListener('click', function(e) { if (e.target === d) cerrarConfirm(); });
    document.getElementById('confirm-yes').addEventListener('click', function() { cerrarConfirm(); if (onConfirm) onConfirm(); });
    document.getElementById('confirm-no').addEventListener('click', cerrarConfirm);
    function cerrarConfirm() { if (d.parentNode) d.remove(); }
};
