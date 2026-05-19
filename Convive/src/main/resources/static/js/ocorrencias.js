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

function filtrarOcorrencias() {
    const termoBusca = (document.getElementById('filtro-busca-ocorrencia')?.value || '').toLowerCase();
    const filtroStatus = document.getElementById('filtro-status-ocorrencia')?.value || 'ALL';
    const filtroPrioridade = document.getElementById('filtro-prioridade-ocorrencia')?.value || 'ALL';

    const cards = document.querySelectorAll('.ocorrencia-card');

    cards.forEach(card => {
        const protocolo = (card.getAttribute('data-protocolo') || '').toLowerCase();
        const descricao = (card.getAttribute('data-descricao') || '').toLowerCase();
        const status = card.getAttribute('data-status') || '';
        const prioridade = card.getAttribute('data-prioridade') || '';

        const matchBusca = protocolo.includes(termoBusca) || descricao.includes(termoBusca);
        const matchStatus = (filtroStatus === 'ALL' || status === filtroStatus);
        const matchPrioridade = (filtroPrioridade === 'ALL' || prioridade === filtroPrioridade);

        if (matchBusca && matchStatus && matchPrioridade) {
            card.classList.remove('hidden');
        } else {
            card.classList.add('hidden');
        }
    });

    ordenarOcorrencias();
}

function ordenarOcorrencias() {
    const ordenacao = document.getElementById('filtro-ordenacao-ocorrencia')?.value || 'DESC';
    const container = document.querySelector('.ocorrencia-card')?.parentElement;

    if(!container) return;

    const cards = Array.from(container.querySelectorAll('.ocorrencia-card'));

    cards.sort((a, b) => {
        const dataA = a.getAttribute('data-data-sort') || '';
        const dataB = b.getAttribute('data-data-sort') || '';

        if (ordenacao === 'ASC') {
            return dataA.localeCompare(dataB);
        } else {
            return dataB.localeCompare(dataA);
        }
    });

    cards.forEach(card => container.appendChild(card));
}

document.addEventListener('DOMContentLoaded', () => {
    const inputBusca = document.getElementById('filtro-busca-ocorrencia');
    if (inputBusca) inputBusca.addEventListener('keyup', filtrarOcorrencias);

    const selectStatus = document.getElementById('filtro-status-ocorrencia');
    if (selectStatus) selectStatus.addEventListener('change', filtrarOcorrencias);

    const selectPrioridade = document.getElementById('filtro-prioridade-ocorrencia');
    if (selectPrioridade) selectPrioridade.addEventListener('change', filtrarOcorrencias);

    const selectOrdenacao = document.getElementById('filtro-ordenacao-ocorrencia');
    if (selectOrdenacao) selectOrdenacao.addEventListener('change', ordenarOcorrencias);
});