var debounceFiltrosTimer = null;

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
  var titulo = card.getAttribute('data-titulo');
  var categoriaRotulo = card.getAttribute('data-categoria-rotulo');
  var categoria = card.getAttribute('data-categoria');
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
  setDetalheField('detalhe-titulo', 'innerText', titulo);
  setDetalheField('detalhe-categoria', 'innerText', categoriaRotulo);
  setDetalheField('detalhe-descricao', 'innerText', descricao);
  setDetalheField('input-resposta', 'value', resposta);
  setDetalheField('select-prioridade', 'value', prioridade);
  setDetalheField('select-status', 'value', status);

  var mostrarAviso = categoria === 'OUTRO' && prioridade === 'NAO_DEFINIDA';
  ['detalhe-aviso-prioridade', 'detalhe-aviso-prioridade-mobile'].forEach(function (id) {
    var aviso = document.getElementById(id);
    if (!aviso) return;
    if (mostrarAviso) aviso.classList.remove('hidden');
    else aviso.classList.add('hidden');
  });

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

function montarQueryFiltros() {
  var params = new URLSearchParams();
  params.set('page', '0');
  var busca = document.getElementById('filtro-busca');
  var status = document.getElementById('filtro-status');
  var prioridade = document.getElementById('filtro-prioridade');
  var ordem = document.getElementById('filtro-ordenacao');
  if (busca && busca.value.trim()) params.set('busca', busca.value.trim());
  if (status) params.set('status', status.value);
  if (prioridade) params.set('prioridade', prioridade.value);
  if (ordem) params.set('ordem', ordem.value);
  var ocorrenciaId = new URLSearchParams(window.location.search).get('ocorrenciaId');
  if (ocorrenciaId) params.set('ocorrenciaId', ocorrenciaId);
  return params.toString();
}

function aplicarFiltros() {
  window.location.search = montarQueryFiltros();
}

function debounceAplicarFiltros() {
  clearTimeout(debounceFiltrosTimer);
  debounceFiltrosTimer = setTimeout(aplicarFiltros, 400);
}

function restaurarBotaoCarregarMais(btn, icon, texto) {
  texto.innerText = 'Carregar mais';
  icon.classList.remove('animate-spin');
  btn.disabled = false;
}

async function carregarMaisTriagem() {
  var btn = document.getElementById('btnCarregarMais');
  if (!btn) return;
  var icon = document.getElementById('iconCarregarMais');
  var texto = document.getElementById('textoCarregarMais');

  var paginaAtual = parseInt(btn.getAttribute('data-pagina-atual'), 10);
  var totalPaginas = parseInt(btn.getAttribute('data-total-paginas'), 10);
  var proximaPagina = paginaAtual + 1;

  if (proximaPagina >= totalPaginas) return;

  icon.classList.add('animate-spin');
  texto.innerText = 'Carregando...';
  btn.disabled = true;

  var params = new URLSearchParams(window.location.search);
  params.set('page', String(proximaPagina));

  try {
    var response = await fetch('/moderador/triagemOcorrencias/mais?' + params.toString());
    if (response.ok) {
      var fragmentoHtml = await response.text();
      var container = document.getElementById('lista-ocorrencias');
      var loadMore = document.getElementById('containerCarregarMais');
      if (loadMore) {
        loadMore.insertAdjacentHTML('beforebegin', fragmentoHtml);
      } else {
        container.insertAdjacentHTML('beforeend', fragmentoHtml);
      }
      btn.setAttribute('data-pagina-atual', String(proximaPagina));
      if (proximaPagina + 1 >= totalPaginas) {
        if (loadMore) loadMore.style.display = 'none';
      } else {
        restaurarBotaoCarregarMais(btn, icon, texto);
      }
    } else {
      restaurarBotaoCarregarMais(btn, icon, texto);
    }
  } catch (e) {
    console.error(e);
    restaurarBotaoCarregarMais(btn, icon, texto);
  }
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
  selecionarPorQueryParam();

  var modal = document.getElementById('modal-detalhes-triagem');
  var fecharBtn = document.getElementById('modal-detalhes-triagem-fechar');
  if (fecharBtn && modal) {
    fecharBtn.addEventListener('click', function () { modal.close(); });
  }
});
