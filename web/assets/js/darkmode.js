(function() {
    var key = 'cin-dark-mode';
    var saved = localStorage.getItem(key) === 'true';
    if (saved) document.documentElement.classList.add('dark-mode');

    var checkExist = setInterval(function() {
        if (!document.body) return;
        clearInterval(checkExist);

        var btn = document.createElement('button');
        btn.id = 'darkModeToggle';
        btn.title = 'Modo oscuro';
        btn.setAttribute('aria-label', 'Modo oscuro');
        btn.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;width:44px;height:44px;border-radius:10px;border:2px solid rgba(255,255,255,0.2);display:flex;align-items:center;justify-content:center;font-size:1.3rem;cursor:pointer;transition:all .25s;box-shadow:0 4px 16px rgba(0,0,0,0.3);background:#1e1e1e;color:#fff;';
        btn.innerHTML = saved ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-fill"></i>';

        btn.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
            this.style.borderColor = 'var(--cine-yellow)';
        });
        btn.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
            this.style.borderColor = 'rgba(255,255,255,0.2)';
        });

        btn.addEventListener('click', function() {
            var on = document.documentElement.classList.toggle('dark-mode');
            localStorage.setItem(key, on);
            this.innerHTML = on ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-fill"></i>';
        });

        document.body.appendChild(btn);
    }, 50);
})();
