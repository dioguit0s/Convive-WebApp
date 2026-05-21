document.addEventListener('DOMContentLoaded', () => {
    const buscaInput = document.getElementById('morador-busca');
    const hiddenId = document.getElementById('morador-id');
    const lista = document.getElementById('morador-lista');

    if (!buscaInput || !hiddenId || !lista) return;

    let indiceAtivo = -1;
    let debounceTimer = null;
    let paginaBusca = 0;
    let termoAtual = '';

    function limparSelecao() {
        hiddenId.value = '';
        delete buscaInput.dataset.selecionadoLabel;
    }

    function selecionarMorador(id, label) {
        hiddenId.value = id;
        buscaInput.value = label;
        buscaInput.dataset.selecionadoLabel = label;
        fecharLista();
        indiceAtivo = -1;
    }

    function fecharLista() {
        lista.classList.add('hidden');
        indiceAtivo = -1;
    }

    function itensVisiveis() {
        return Array.from(lista.querySelectorAll('.morador-opcao'));
    }

    function destacarItem(index) {
        const visiveis = itensVisiveis();
        visiveis.forEach(el => el.classList.remove('bg-surface-container-high'));
        if (index >= 0 && index < visiveis.length) {
            visiveis[index].classList.add('bg-surface-container-high');
            visiveis[index].scrollIntoView({ block: 'nearest' });
        }
    }

    function vincularEventosOpcoes() {
        lista.querySelectorAll('.morador-opcao').forEach(item => {
            item.addEventListener('mousedown', (e) => {
                e.preventDefault();
                selecionarMorador(item.getAttribute('data-id'), item.getAttribute('data-label'));
            });
        });

        const btnMais = document.getElementById('morador-btn-carregar-mais');
        if (btnMais) {
            btnMais.addEventListener('click', (e) => {
                e.preventDefault();
                const proxima = parseInt(btnMais.getAttribute('data-pagina-atual'), 10) + 1;
                buscarMoradores(termoAtual, proxima, true);
            });
        }
    }

    async function buscarMoradores(termo, page, append) {
        paginaBusca = page;
        termoAtual = termo;
        const params = new URLSearchParams();
        if (termo.trim()) params.set('q', termo.trim());
        params.set('page', String(page));

        try {
            const response = await fetch('/moderador/advertencias/moradores?' + params.toString());
            if (!response.ok) return;

            const html = await response.text();
            if (append) {
                const carregarItem = document.getElementById('morador-carregar-mais-item');
                if (carregarItem) carregarItem.remove();
                lista.insertAdjacentHTML('beforeend', html);
            } else {
                lista.innerHTML = html;
            }
            lista.classList.remove('hidden');
            vincularEventosOpcoes();
        } catch (err) {
            console.error(err);
        }
    }

    buscaInput.addEventListener('input', () => {
        if (hiddenId.value && buscaInput.value !== buscaInput.dataset.selecionadoLabel) {
            limparSelecao();
        }
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => buscarMoradores(buscaInput.value, 0, false), 300);
        indiceAtivo = -1;
    });

    buscaInput.addEventListener('focus', () => {
        if (lista.querySelectorAll('.morador-opcao').length === 0) {
            buscarMoradores(buscaInput.value, 0, false);
        } else {
            lista.classList.remove('hidden');
        }
    });

    buscaInput.addEventListener('keydown', (e) => {
        const visiveis = itensVisiveis();

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            if (lista.classList.contains('hidden')) buscarMoradores(buscaInput.value, 0, false);
            indiceAtivo = Math.min(indiceAtivo + 1, visiveis.length - 1);
            destacarItem(indiceAtivo);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            indiceAtivo = Math.max(indiceAtivo - 1, 0);
            destacarItem(indiceAtivo);
        } else if (e.key === 'Enter') {
            e.preventDefault();
            if (indiceAtivo >= 0 && visiveis[indiceAtivo]) {
                const el = visiveis[indiceAtivo];
                selecionarMorador(el.getAttribute('data-id'), el.getAttribute('data-label'));
            }
        } else if (e.key === 'Escape') {
            fecharLista();
        }
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('#morador-combobox')) {
            fecharLista();
        }
    });

    buscaInput.closest('form')?.addEventListener('submit', (e) => {
        if (!hiddenId.value) {
            e.preventDefault();
            buscaInput.focus();
            buscarMoradores(buscaInput.value, 0, false);
            buscaInput.classList.add('ring-2', 'ring-error');
            setTimeout(() => buscaInput.classList.remove('ring-2', 'ring-error'), 2000);
        }
    });
});
