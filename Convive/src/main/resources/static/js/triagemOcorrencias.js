function selecionarOcorrencia(card) {
    document.querySelectorAll('.ocorrencia-card').forEach(c => c.classList.remove('active'));
    card.classList.add('active');

    document.getElementById('tela-vazia').classList.add('hidden');
    const form = document.getElementById('form-detalhes');
    form.classList.remove('hidden');
    form.classList.add('flex');

    const id = card.getAttribute('data-id');
    const protocolo = card.getAttribute('data-protocolo');
    const prioridade = card.getAttribute('data-prioridade');
    const status = card.getAttribute('data-status');
    const descricao = card.getAttribute('data-descricao');
    const morador = card.getAttribute('data-morador');
    const data = card.getAttribute('data-data');
    const resposta = card.getAttribute('data-resposta');

    document.getElementById('detalhe-id').value = id;
    document.getElementById('detalhe-protocolo').innerText = '#' + protocolo;
    document.getElementById('detalhe-morador').innerText = morador;
    document.getElementById('detalhe-data').innerText = data;
    document.getElementById('detalhe-descricao').innerText = descricao;
    document.getElementById('input-resposta').value = resposta;

    document.getElementById('select-prioridade').value = prioridade;
    document.getElementById('select-status').value = status;

    const badge = document.getElementById('detalhe-badge-prioridade');
    badge.innerText = prioridade.replace('_', ' ');
    badge.className = "text-label-md font-label-md px-sm py-xs rounded-full uppercase ";

    if(prioridade === 'ALTA') badge.classList.add('bg-error-container', 'text-on-error-container');
    else if (prioridade === 'MEDIA') badge.classList.add('bg-surface-container-highest', 'text-on-surface');
    else if (prioridade === 'BAIXA') badge.classList.add('bg-surface-container-low', 'text-on-surface-variant', 'border', 'border-outline-variant');
    else badge.classList.add('bg-surface', 'text-outline', 'border', 'border-outline-variant');
}

function filtrarOcorrencias() {
    const termo = document.getElementById('filtro-busca').value.toLowerCase();
    const filtroStatus = document.getElementById('filtro-status').value;
    const filtroPrioridade = document.getElementById('filtro-prioridade').value;

    document.querySelectorAll('.ocorrencia-card').forEach(card => {
        const protocolo = card.getAttribute('data-protocolo').toLowerCase();
        const descricao = card.getAttribute('data-descricao').toLowerCase();
        const status = card.getAttribute('data-status');
        const prioridade = card.getAttribute('data-prioridade');

        const matchBusca = protocolo.includes(termo) || descricao.includes(termo);
        const matchStatus = (filtroStatus === 'ALL' || status === filtroStatus);
        const matchPrioridade = (filtroPrioridade === 'ALL' || prioridade === filtroPrioridade);

        if (matchBusca && matchStatus && matchPrioridade) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

function ordenarOcorrencias() {
    const ordem = document.getElementById('filtro-ordenacao').value;
    const container = document.getElementById('lista-ocorrencias');

    const cards = Array.from(container.querySelectorAll('.ocorrencia-card'));

    cards.sort((a, b) => {
        const dataA = a.getAttribute('data-data-sort');
        const dataB = b.getAttribute('data-data-sort');

        if (ordem === 'DESC') {
            return dataB.localeCompare(dataA);
        } else {
            return dataA.localeCompare(dataB);
        }
    });

    cards.forEach(card => container.appendChild(card));
}

document.addEventListener('DOMContentLoaded', () => {
    ordenarOcorrencias();
});