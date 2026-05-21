function abrirModalNova() {
    document.getElementById('modalNovaArea')?.classList.remove('hidden');
}

function fecharModalNova() {
    document.getElementById('modalNovaArea')?.classList.add('hidden');
}

function abrirModalEditar(botao) {
    const id = botao.getAttribute('data-id');
    const nome = botao.getAttribute('data-nome');
    const capacidade = botao.getAttribute('data-capacidade');
    const status = botao.getAttribute('data-status');

    document.getElementById('edit-nome').value = nome || '';
    document.getElementById('edit-capacidade').value = capacidade || '';
    document.getElementById('edit-status').value = status || 'ATIVA';

    const form = document.getElementById('formEditarArea');
    if (form && id) {
        form.action = `/moderador/areasComuns/${id}/editar`;
    }

    document.getElementById('modalEditarArea')?.classList.remove('hidden');
}

function fecharModalEditar() {
    document.getElementById('modalEditarArea')?.classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', () => {
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            fecharModalNova();
            fecharModalEditar();
        }
    });
});
