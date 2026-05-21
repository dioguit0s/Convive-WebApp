async function carregarMaisNotificacoes() {
    const btn = document.getElementById('btnCarregarMais');
    if (!btn) return;
    const icon = document.getElementById('iconCarregarMais');
    const texto = document.getElementById('textoCarregarMais');

    const paginaAtual = parseInt(btn.getAttribute('data-pagina-atual'), 10);
    const totalPaginas = parseInt(btn.getAttribute('data-total-paginas'), 10);
    const proximaPagina = paginaAtual + 1;

    if (proximaPagina >= totalPaginas) return;

    icon.classList.add('animate-spin');
    texto.innerText = 'Carregando...';
    btn.disabled = true;

    try {
        const response = await fetch('/morador/notificacoes/mais?page=' + proximaPagina);
        if (response.ok) {
            const html = await response.text();
            const container = document.getElementById('containerCarregarMais');
            if (container) {
                container.insertAdjacentHTML('beforebegin', html);
            } else {
                document.getElementById('lista-notificacoes').insertAdjacentHTML('beforeend', html);
            }
            btn.setAttribute('data-pagina-atual', String(proximaPagina));
            if (proximaPagina + 1 >= totalPaginas) {
                if (container) container.style.display = 'none';
            } else {
                texto.innerText = 'Carregar mais notificações';
                icon.classList.remove('animate-spin');
                btn.disabled = false;
            }
        } else {
            texto.innerText = 'Carregar mais notificações';
            icon.classList.remove('animate-spin');
            btn.disabled = false;
        }
    } catch (e) {
        console.error(e);
        texto.innerText = 'Carregar mais notificações';
        icon.classList.remove('animate-spin');
        btn.disabled = false;
    }
}
