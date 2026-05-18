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