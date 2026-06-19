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

(function() {
    var spinnerHtml = '<div class="text-center py-5"><div class="spinner-border text-warning" role="status" style="width:3rem;height:3rem;"><span class="visually-hidden">Cargando...</span></div><p class="text-muted mt-2">Cargando...</p></div>';
    window.mostrarSpinner = function(id) { var el = document.getElementById(id); if (el) el.innerHTML = spinnerHtml; };
    window.ocultarSpinner = function(id) { };
})();

(function() {
    window.showConfirm = function(msg, onConfirm) {
        var existing = document.getElementById('confirm-modal');
        if (existing) existing.remove();
        var modal = document.createElement('div');
        modal.id = 'confirm-modal';
        modal.className = 'modal fade';
        modal.setAttribute('tabindex','-1');
        modal.innerHTML = '<div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">Confirmar</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div><div class="modal-body"><p class="mb-0">' + msg + '</p></div><div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button><button type="button" class="btn btn-warning" id="confirm-yes">S\u00ED, continuar</button></div></div></div>';
        document.body.appendChild(modal);
        var bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        document.getElementById('confirm-yes').addEventListener('click', function() { bsModal.hide(); if (onConfirm) onConfirm(); });
        modal.addEventListener('hidden.bs.modal', function() { modal.remove(); });
    };
})();
