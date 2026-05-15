let filtroAtual = 'Todos';
const btnNovoComunicado = document.getElementById("btnNovoComunicado")

function selecionarFiltro(botaoClicado, tipo) {
    filtroAtual = tipo;

    const botoes = document.querySelectorAll('.filtro-btn');
    botoes.forEach(btn => {
        btn.classList.remove('bg-surface-container-highest', 'text-primary', 'border-outline-variant/20');
        btn.classList.add('bg-surface-container-lowest', 'text-on-surface-variant', 'border-outline-variant/50');
    });

    botaoClicado.classList.remove('bg-surface-container-lowest', 'text-on-surface-variant', 'border-outline-variant/50');
    botaoClicado.classList.add('bg-surface-container-highest', 'text-primary', 'border-outline-variant/20');

    filtrarComunicados();
}

function filtrarComunicados() {
    const termoBusca = document.getElementById('searchInput').value.toLowerCase();
    const cards = document.querySelectorAll('.comunicado-card');

    cards.forEach(card => {
        const titulo = card.getAttribute('data-titulo').toLowerCase();
        const tipoCard = card.getAttribute('data-tipo');

        const correspondeBusca = titulo.includes(termoBusca);

        const correspondeFiltro = (filtroAtual === 'Todos' || tipoCard === filtroAtual);

        if (correspondeBusca && correspondeFiltro) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });
}


function abrirModalComunicado(elemento) {
    const id = elemento.getAttribute('data-id');
    const titulo = elemento.getAttribute('data-titulo');
    const conteudo = elemento.getAttribute('data-conteudo');
    const autor = elemento.getAttribute('data-autor');
    const data = elemento.getAttribute('data-data');
    const tipo = elemento.getAttribute('data-tipo');

    document.getElementById('modal-titulo').innerText = titulo;
    document.getElementById('modal-conteudo').innerText = conteudo;
    document.getElementById('modal-autor').innerText = autor;
    document.getElementById('modal-data').innerText = data;
    document.getElementById('modal-tipo').innerText = tipo;

    const inputExcluir = document.getElementById('comunicado-id-excluir');
    if (inputExcluir) {
        inputExcluir.value = id;
    }

    document.getElementById('modal-detalhes-comunicado').showModal();
}

function abrirModalNovoComunicado() {
    document.getElementById('modal-novo-comunicado').showModal();
}