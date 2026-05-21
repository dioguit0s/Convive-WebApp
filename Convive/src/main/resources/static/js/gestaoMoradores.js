function abrirModal(botao) {
    document.getElementById('edit-id').value = botao.getAttribute('data-id');
    document.getElementById('edit-nome').value = botao.getAttribute('data-nome');
    document.getElementById('edit-email').value = botao.getAttribute('data-email');
    document.getElementById('edit-apartamento').value = botao.getAttribute('data-apartamento');

    const isInadimplente = botao.getAttribute('data-inadimplente') === 'true';
    document.getElementById('edit-inadimplente').checked = isInadimplente;

    document.getElementById('modalEdicao').classList.remove('hidden');
}

function fecharModal() {
    document.getElementById('modalEdicao').classList.add('hidden');
}

function abrirModalNovo() {
    document.getElementById('modalNovoCadastro').classList.remove('hidden');
}

function fecharModalNovo() {
    document.getElementById('modalNovoCadastro').classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    if (params.get('abrirCadastro') === 'true') {
        abrirModalNovo();
    }
});

function confirmarExclusao() {
    const id = document.getElementById('edit-id').value;
    const nome = document.getElementById('edit-nome').value;

    if (confirm(`Tem certeza absoluta que deseja excluir o usuário "${nome}"? Esta ação removerá o acesso dele e não poderá ser desfeita.`)) {
        document.getElementById('delete-id').value = id;
        document.getElementById('formExcluirUsuario').submit();
    }
}
