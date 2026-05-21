function isMobileTriagem() {
  return window.matchMedia('(max-width: 767px)').matches;
}

function setDetalheField(baseId, prop, value, transform) {
  ['', '-mobile'].forEach(function (suffix) {
    var el = document.getElementById(baseId + suffix);
    if (!el) return;
    var v = transform ? transform(value) : value;
    if (prop === 'value') el.value = v;
    else el[prop] = v;
  });
}

function aplicarBadgePrioridade(prioridade) {
  var classes = 'text-label-md font-label-md px-sm py-xs rounded-full uppercase ';
  if (prioridade === 'ALTA') classes += 'bg-error-container text-on-error-container';
  else if (prioridade === 'MEDIA') classes += 'bg-surface-container-highest text-on-surface';
  else if (prioridade === 'BAIXA') classes += 'bg-surface-container-low text-on-surface-variant border border-outline-variant';
  else classes += 'bg-surface text-outline border border-outline-variant';

  ['detalhe-badge-prioridade', 'detalhe-badge-prioridade-mobile'].forEach(function (id) {
    var badge = document.getElementById(id);
    if (badge) {
      badge.innerText = prioridade.replace('_', ' ');
      badge.className = classes;
    }
  });
}

function aplicarEvidencia(urlEvidencia) {
  ['', '-mobile'].forEach(function (suffix) {
    var container = document.getElementById('container-evidencia' + suffix);
    var img = document.getElementById('detalhe-evidencia-img' + suffix);
    if (!container || !img) return;
    if (urlEvidencia && urlEvidencia.trim() !== '') {
      img.src = urlEvidencia;
      container.classList.remove('hidden');
    } else {
      img.src = '';
      container.classList.add('hidden');
    }
  });
}

function preencherDetalhes(card) {
  var id = card.getAttribute('data-id');
  var protocolo = card.getAttribute('data-protocolo');
  var prioridade = card.getAttribute('data-prioridade');
  var status = card.getAttribute('data-status');
  var descricao = card.getAttribute('data-descricao');
  var morador = card.getAttribute('data-morador');
  var data = card.getAttribute('data-data') || '';
  var resposta = card.getAttribute('data-resposta') || '';
  var urlEvidencia = card.getAttribute('data-evidencia') || '';

  setDetalheField('detalhe-id', 'value', id);
  setDetalheField('detalhe-protocolo', 'innerText', protocolo, function (p) { return '#' + p; });
  setDetalheField('detalhe-morador', 'innerText', morador);
  setDetalheField('detalhe-data', 'innerText', data);
  setDetalheField('detalhe-descricao', 'innerText', descricao);
  setDetalheField('input-resposta', 'value', resposta);
  setDetalheField('select-prioridade', 'value', prioridade);
  setDetalheField('select-status', 'value', status);

  aplicarBadgePrioridade(prioridade);
  aplicarEvidencia(urlEvidencia);
}

function selecionarOcorrencia(card) {
  document.querySelectorAll('.ocorrencia-card').forEach(function (c) { c.classList.remove('active'); });
  card.classList.add('active');

  preencherDetalhes(card);

  if (isMobileTriagem()) {
    var modal = document.getElementById('modal-detalhes-triagem');
    if (modal && typeof modal.showModal === 'function') modal.showModal();
    return;
  }

  var telaVazia = document.getElementById('tela-vazia');
  var form = document.getElementById('form-detalhes');
  if (telaVazia) telaVazia.classList.add('hidden');
  if (form) {
    form.classList.remove('hidden');
    form.classList.add('flex');
  }
}

function filtrarOcorrencias() {
  var termo = document.getElementById('filtro-busca').value.toLowerCase();
  var filtroStatus = document.getElementById('filtro-status').value;
  var filtroPrioridade = document.getElementById('filtro-prioridade').value;

  document.querySelectorAll('.ocorrencia-card').forEach(function (card) {
    var protocolo = card.getAttribute('data-protocolo').toLowerCase();
    var descricao = card.getAttribute('data-descricao').toLowerCase();
    var status = card.getAttribute('data-status');
    var prioridade = card.getAttribute('data-prioridade');

    var matchBusca = protocolo.includes(termo) || descricao.includes(termo);
    var matchStatus = (filtroStatus === 'ALL' || status === filtroStatus);
    var matchPrioridade = (filtroPrioridade === 'ALL' || prioridade === filtroPrioridade);

    card.style.display = (matchBusca && matchStatus && matchPrioridade) ? 'block' : 'none';
  });
}

function ordenarOcorrencias() {
  var ordem = document.getElementById('filtro-ordenacao').value;
  var container = document.getElementById('lista-ocorrencias');
  var cards = Array.from(container.querySelectorAll('.ocorrencia-card'));

  cards.sort(function (a, b) {
    var dataA = a.getAttribute('data-data-sort');
    var dataB = b.getAttribute('data-data-sort');
    return ordem === 'DESC' ? dataB.localeCompare(dataA) : dataA.localeCompare(dataB);
  });

  cards.forEach(function (card) { container.appendChild(card); });
}

function selecionarPorQueryParam() {
  var params = new URLSearchParams(window.location.search);
  var ocorrenciaId = params.get('ocorrenciaId');
  if (!ocorrenciaId) return;

  var card = document.querySelector('.ocorrencia-card[data-id="' + ocorrenciaId + '"]');
  if (card) {
    card.scrollIntoView({ behavior: 'smooth', block: 'center' });
    selecionarOcorrencia(card);
  }
}

document.addEventListener('DOMContentLoaded', function () {
  ordenarOcorrencias();
  selecionarPorQueryParam();

  var modal = document.getElementById('modal-detalhes-triagem');
  var fecharBtn = document.getElementById('modal-detalhes-triagem-fechar');
  if (fecharBtn && modal) {
    fecharBtn.addEventListener('click', function () { modal.close(); });
  }
});
