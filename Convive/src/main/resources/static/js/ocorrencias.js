
function abrirConsulta(elemento) {
    const protocolo = elemento.getAttribute('data-protocolo');
    const data = elemento.getAttribute('data-data');
    const prioridade = elemento.getAttribute('data-prioridade');
    const status = elemento.getAttribute('data-status').replace('_', ' ');
    const descricao = elemento.getAttribute('data-descricao');

    const campoProtocolo = document.getElementById('detalhe-protocolo');
    const campoData = document.getElementById('detalhe-data');
    const campoPrioridade = document.getElementById('detalhe-prioridade');
    const campoStatus = document.getElementById('detalhe-status');
    const campoDescricao = document.getElementById('detalhe-descricao');

    if (campoProtocolo) campoProtocolo.innerText = '#' + protocolo;
    if (campoData) campoData.innerText = data;
    if (campoPrioridade) campoPrioridade.innerText = prioridade;
    if (campoStatus) campoStatus.innerText = status;
    if (campoDescricao) campoDescricao.innerText = descricao;

    const modal = document.getElementById('modal-detalhes-ocorrencia');
    if (modal) {
        modal.showModal();
    }
}