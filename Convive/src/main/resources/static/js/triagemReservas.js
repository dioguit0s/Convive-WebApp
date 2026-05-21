function mudarAba(statusSelecionado) {
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('border-b-2', 'border-primary', 'text-primary');
            btn.classList.add('text-on-surface-variant');
        });
        const activeTab = document.getElementById('tab-' + statusSelecionado);
        activeTab.classList.remove('text-on-surface-variant');
        activeTab.classList.add('border-b-2', 'border-primary', 'text-primary');

        document.querySelectorAll('.reserva-card').forEach(card => {
            if (card.dataset.status === statusSelecionado) {
                card.style.display = 'flex';
            } else {
                card.style.display = 'none';
            }
        });
    }

    function abrirModalRejeicao(id) {
        document.getElementById('rejeicao-reserva-id').value = id;
        document.getElementById('modal-rejeicao').showModal();
    }

    function selecionarReservaPorQueryParam() {
        const params = new URLSearchParams(window.location.search);
        const reservaId = params.get('reservaId');
        if (!reservaId) return;

        const card = document.querySelector(`.reserva-card[data-id="${reservaId}"]`);
        if (card) {
            const status = card.dataset.status || 'PENDENTE';
            mudarAba(status);
            card.scrollIntoView({ behavior: 'smooth', block: 'center' });
            card.classList.add('ring-2', 'ring-primary');
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        mudarAba('PENDENTE');
        selecionarReservaPorQueryParam();
    });