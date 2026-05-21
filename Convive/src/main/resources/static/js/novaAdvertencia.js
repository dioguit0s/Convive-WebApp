document.addEventListener('DOMContentLoaded', () => {
    const buscaInput = document.getElementById('morador-busca');
    const hiddenId = document.getElementById('morador-id');
    const lista = document.getElementById('morador-lista');
    const vazio = document.getElementById('morador-lista-vazio');

    if (!buscaInput || !hiddenId || !lista) return;

    const itens = Array.from(lista.querySelectorAll('.morador-opcao'));
    let indiceAtivo = -1;

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
        return itens.filter(item => !item.classList.contains('hidden'));
    }

    function renderLista(termo) {
        const t = termo.trim().toLowerCase();
        let visiveis = 0;

        itens.forEach(item => {
            const search = (item.getAttribute('data-search') || '').toLowerCase();
            const match = t.length === 0 || search.includes(t);
            item.classList.toggle('hidden', !match);
            item.classList.remove('bg-surface-container-high');
            if (match) visiveis++;
        });

        if (vazio) {
            vazio.classList.toggle('hidden', visiveis > 0);
        }

        lista.classList.remove('hidden');
    }

    function destacarItem(index) {
        const visiveis = itensVisiveis();
        visiveis.forEach(el => el.classList.remove('bg-surface-container-high'));
        if (index >= 0 && index < visiveis.length) {
            visiveis[index].classList.add('bg-surface-container-high');
            visiveis[index].scrollIntoView({ block: 'nearest' });
        }
    }

    buscaInput.addEventListener('input', () => {
        if (hiddenId.value && buscaInput.value !== buscaInput.dataset.selecionadoLabel) {
            limparSelecao();
        }
        renderLista(buscaInput.value);
        indiceAtivo = -1;
    });

    buscaInput.addEventListener('focus', () => {
        renderLista(buscaInput.value);
    });

    buscaInput.addEventListener('keydown', (e) => {
        const visiveis = itensVisiveis();

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            if (lista.classList.contains('hidden')) renderLista(buscaInput.value);
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

    lista.addEventListener('mousedown', (e) => {
        const item = e.target.closest('.morador-opcao');
        if (!item || item.classList.contains('hidden')) return;
        e.preventDefault();
        selecionarMorador(item.getAttribute('data-id'), item.getAttribute('data-label'));
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
            renderLista(buscaInput.value);
            buscaInput.classList.add('ring-2', 'ring-error');
            setTimeout(() => buscaInput.classList.remove('ring-2', 'ring-error'), 2000);
        }
    });
});
