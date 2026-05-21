var debounceFiltrosReservaTimer = null;

function montarQueryReservas(statusOverride) {
    var params = new URLSearchParams();
    params.set('page', '0');
    var status = statusOverride || new URLSearchParams(window.location.search).get('status') || 'PENDENTE';
    params.set('status', status);
    var busca = document.getElementById('filtro-busca-reserva');
    if (busca && busca.value.trim()) {
        params.set('busca', busca.value.trim());
    }
    var ordem = new URLSearchParams(window.location.search).get('ordem');
    if (ordem) params.set('ordem', ordem);
    var reservaId = new URLSearchParams(window.location.search).get('reservaId');
    if (reservaId) params.set('reservaId', reservaId);
    return params.toString();
}

function mudarAba(statusSelecionado) {
    window.location.search = montarQueryReservas(statusSelecionado);
}

function debounceAplicarFiltrosReserva() {
    clearTimeout(debounceFiltrosReservaTimer);
    debounceFiltrosReservaTimer = setTimeout(function () {
        window.location.search = montarQueryReservas();
    }, 400);
}

function abrirModalRejeicao(id) {
    document.getElementById('rejeicao-reserva-id').value = id;
    document.getElementById('modal-rejeicao').showModal();
}

function restaurarBotaoCarregarMais(btn, icon, texto) {
    texto.innerText = 'Carregar mais reservas';
    icon.classList.remove('animate-spin');
    btn.disabled = false;
}

async function carregarMaisReservas() {
    var btn = document.getElementById('btnCarregarMaisReservas');
    if (!btn) return;
    var icon = document.getElementById('iconCarregarMaisReservas');
    var texto = document.getElementById('textoCarregarMaisReservas');

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
        var response = await fetch('/moderador/triagemReservas/mais?' + params.toString());
        if (response.ok) {
            var fragmentoHtml = await response.text();
            var loadMore = document.getElementById('containerCarregarMaisReservas');
            if (loadMore) {
                loadMore.insertAdjacentHTML('beforebegin', fragmentoHtml);
            } else {
                document.getElementById('container-reservas').insertAdjacentHTML('beforeend', fragmentoHtml);
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

function selecionarReservaPorQueryParam() {
    var params = new URLSearchParams(window.location.search);
    var reservaId = params.get('reservaId');
    if (!reservaId) return;

    var card = document.querySelector('.reserva-card[data-id="' + reservaId + '"]');
    if (card) {
        card.scrollIntoView({ behavior: 'smooth', block: 'center' });
        card.classList.add('ring-2', 'ring-primary');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    selecionarReservaPorQueryParam();
});
