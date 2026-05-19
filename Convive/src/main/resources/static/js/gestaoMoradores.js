function abrirModal(botao) {
    document.getElementById('edit-id').value = botao.getAttribute('data-id');
    document.getElementById('edit-nome').value = botao.getAttribute('data-nome');
    document.getElementById('edit-email').value = botao.getAttribute('data-email');

    const apartamento = botao.getAttribute('data-apartamento');
    const tipo = botao.getAttribute('data-tipo');

    const campoApartamento = document.getElementById('edit-apartamento');
    const containerApartamento = campoApartamento.closest('div');

    if (tipo === 'morador') {
        campoApartamento.value = apartamento;
        campoApartamento.required = true;
        containerApartamento.style.display = 'block';
    } else {
        campoApartamento.value = '';
        campoApartamento.required = false;
        containerApartamento.style.display = 'none';
    }

    document.getElementById('modalEdicao').classList.remove('hidden');
}

    function fecharModal() {
    document.getElementById('modalEdicao').classList.add('hidden');
}
