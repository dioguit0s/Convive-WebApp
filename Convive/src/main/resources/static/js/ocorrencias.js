var debounceFiltrosMoradorTimer = null;

function abrirConsulta(elemento) {
    const id = elemento.getAttribute('data-id');
    const protocolo = elemento.getAttribute('data-protocolo');
    const data = elemento.getAttribute('data-data');
    const prioridade = elemento.getAttribute('data-prioridade');
    const status = elemento.getAttribute('data-status').replace('_', ' ');
    const descricao = elemento.getAttribute('data-descricao');
    const comentarioModerador = elemento.getAttribute('data-comentario');

    const campoProtocolo = document.getElementById('detalhe-protocolo');
    const campoData = document.getElementById('detalhe-data');
    const campoPrioridade = document.getElementById('detalhe-prioridade');
    const campoStatus = document.getElementById('detalhe-status');
    const campoDescricao = document.getElementById('detalhe-descricao');
    const inputExcluir = document.getElementById('ocorrencia-id-excluir');
    const campoComentarioModerador = document.getElementById('detalhe-comentario-moderador');
    const containerComentario = document.getElementById('container-comentario-moderador');

    if (campoProtocolo) campoProtocolo.innerText = '#' + protocolo;
    if (campoData) campoData.innerText = data;
    if (campoPrioridade) campoPrioridade.innerText = prioridade;
    if (campoStatus) campoStatus.innerText = status;
    if (campoDescricao) campoDescricao.innerText = descricao;
    if (inputExcluir) inputExcluir.value = id;
    if (campoComentarioModerador) campoComentarioModerador.innerText = comentarioModerador;

    const urlEvidencia = elemento.getAttribute('data-evidencia');
    const containerEvidencia = document.getElementById('container-evidencia');
    const imgEvidencia = document.getElementById('detalhe-evidencia-img');

    if (containerEvidencia && imgEvidencia) {
        if (urlEvidencia && urlEvidencia.trim() !== '') {
            imgEvidencia.src = urlEvidencia;
            containerEvidencia.classList.remove('hidden');
        } else {
            imgEvidencia.src = '';
            containerEvidencia.classList.add('hidden');
        }
    }

    if (comentarioModerador && comentarioModerador.trim() !== '') {
        if (campoComentarioModerador) campoComentarioModerador.innerText = comentarioModerador;
        if (containerComentario) containerComentario.classList.remove('hidden');
    } else {
        if (campoComentarioModerador) campoComentarioModerador.innerText = '';
        if (containerComentario) containerComentario.classList.add('hidden');
    }

    const modal = document.getElementById('modal-detalhes-ocorrencia');
    if (modal) {
        modal.showModal();
    }
}

function montarQueryFiltrosMorador() {
    const params = new URLSearchParams();
    params.set('page', '0');
    const busca = document.getElementById('filtro-busca-ocorrencia');
    const status = document.getElementById('filtro-status-ocorrencia');
    const prioridade = document.getElementById('filtro-prioridade-ocorrencia');
    const ordem = document.getElementById('filtro-ordenacao-ocorrencia');
    if (busca && busca.value.trim()) params.set('busca', busca.value.trim());
    if (status) params.set('status', status.value);
    if (prioridade) params.set('prioridade', prioridade.value);
    if (ordem) params.set('ordem', ordem.value);
    return params.toString();
}

function aplicarFiltrosMorador() {
    window.location.search = montarQueryFiltrosMorador();
}

function debounceAplicarFiltrosMorador() {
    clearTimeout(debounceFiltrosMoradorTimer);
    debounceFiltrosMoradorTimer = setTimeout(aplicarFiltrosMorador, 400);
}

async function carregarMaisOcorrenciasMorador() {
    const btn = document.getElementById('btnCarregarMaisOcorrencias');
    if (!btn) return;
    const icon = document.getElementById('iconCarregarMaisOcorrencias');
    const texto = document.getElementById('textoCarregarMaisOcorrencias');

    const paginaAtual = parseInt(btn.getAttribute('data-pagina-atual'), 10);
    const totalPaginas = parseInt(btn.getAttribute('data-total-paginas'), 10);
    const proximaPagina = paginaAtual + 1;

    if (proximaPagina >= totalPaginas) return;

    icon.classList.add('animate-spin');
    texto.innerText = 'Carregando...';
    btn.disabled = true;

    const params = new URLSearchParams(window.location.search);
    params.set('page', String(proximaPagina));

    try {
        const response = await fetch('/morador/ocorrencias/mais?' + params.toString());
        if (response.ok) {
            const html = await response.text();
            const loadMore = document.getElementById('containerCarregarMaisOcorrencias');
            if (loadMore) {
                loadMore.insertAdjacentHTML('beforebegin', html);
            }
            btn.setAttribute('data-pagina-atual', String(proximaPagina));
            if (proximaPagina + 1 >= totalPaginas) {
                if (loadMore) loadMore.style.display = 'none';
            } else {
                texto.innerText = 'Carregar mais ocorrências';
                icon.classList.remove('animate-spin');
                btn.disabled = false;
            }
        } else {
            texto.innerText = 'Carregar mais ocorrências';
            icon.classList.remove('animate-spin');
            btn.disabled = false;
        }
    } catch (e) {
        console.error(e);
        texto.innerText = 'Carregar mais ocorrências';
        icon.classList.remove('animate-spin');
        btn.disabled = false;
    }
}
