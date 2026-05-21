(function () {
  function initDrawer(config) {
    var toggle = document.getElementById(config.toggleId);
    var drawer = document.getElementById(config.drawerId);
    var backdrop = document.getElementById(config.backdropId);
    var closeBtn = document.getElementById(config.closeId);
    if (!toggle || !drawer) return;

    function open() {
      drawer.classList.remove('translate-x-full');
      drawer.setAttribute('aria-hidden', 'false');
      toggle.setAttribute('aria-expanded', 'true');
      document.body.classList.add('overflow-hidden');
      if (backdrop) backdrop.classList.remove('hidden');
    }

    function close() {
      drawer.classList.add('translate-x-full');
      drawer.setAttribute('aria-hidden', 'true');
      toggle.setAttribute('aria-expanded', 'false');
      document.body.classList.remove('overflow-hidden');
      if (backdrop) backdrop.classList.add('hidden');
    }

    toggle.addEventListener('click', function () {
      if (toggle.getAttribute('aria-expanded') === 'true') close();
      else open();
    });

    if (closeBtn) closeBtn.addEventListener('click', close);
    if (backdrop) backdrop.addEventListener('click', close);

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && toggle.getAttribute('aria-expanded') === 'true') close();
    });

    drawer.querySelectorAll('a').forEach(function (link) {
      link.addEventListener('click', close);
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initDrawer({
      toggleId: 'nav-drawer-toggle',
      drawerId: 'nav-drawer',
      backdropId: 'nav-drawer-backdrop',
      closeId: 'nav-drawer-close'
    });
    initDrawer({
      toggleId: 'public-nav-toggle',
      drawerId: 'public-nav-drawer',
      backdropId: 'public-nav-backdrop',
      closeId: 'public-nav-close'
    });
  });
})();
