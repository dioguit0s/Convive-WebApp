(function () {
  'use strict';

  const main = document.getElementById('morador-reservas-main');
  if (!main) return;

  const weekLabel = document.getElementById('cal-semana-label');
  const prevBtn = document.getElementById('cal-semana-prev');
  const nextBtn = document.getElementById('cal-semana-next');
  const todayBtn = document.getElementById('cal-today');
  const daysContainer = document.getElementById('cal-dias-semana');
  const listContainer = document.getElementById('lista-reservas-filtrada');
  const source = document.getElementById('reservas-source');
  const modal = document.getElementById('modal-nova-reserva');
  const modalOverlay = document.getElementById('modal-nova-reserva-overlay');
  const formNovaReserva = document.getElementById('form-nova-reserva');
  const inputDataReserva = document.getElementById('nova-reserva-data');
  const openModalBtn = document.getElementById('btn-nova-reserva');
  const closeModalBtn = document.getElementById('modal-nova-reserva-fechar');
  const cancelModalBtn = document.getElementById('modal-nova-reserva-cancelar');
  const selectAmbiente = document.getElementById('nova-reserva-ambiente');
  const inputConvidados = document.getElementById('nova-reserva-convidados');
  const divErroModal = document.getElementById('modal-erro-reserva');

  const listaVaziaServidor = main.dataset.listaVaziaServidor === 'true';
  const csrfParam = main.getAttribute('data-csrf-parameter-name') || '';
  const csrfToken = main.getAttribute('data-csrf-token') || '';
  const moradorReservasBase =
    main.getAttribute('data-morador-reservas-base') || '/morador/reservas';

  const WEEKDAY_LABELS = ['Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb', 'Dom'];

  function startOfLocalDay(d) {
    const x = new Date(d.getFullYear(), d.getMonth(), d.getDate());
    return x;
  }

  function mondayOfWeek(d) {
    const x = startOfLocalDay(d);
    const day = x.getDay();
    const diffFromMonday = day === 0 ? -6 : 1 - day;
    x.setDate(x.getDate() + diffFromMonday);
    return x;
  }

  let weekStart = mondayOfWeek(new Date());
  let selectedDate = startOfLocalDay(new Date());

  function parseInicio(s) {
    if (!s) return null;
    const d = new Date(s);
    return Number.isNaN(d.getTime()) ? null : d;
  }

  function loadReservas() {
      if (!source) return [];
      return Array.from(source.querySelectorAll('.reserva-row'))
        .map(function (el) {
          return {
            id: el.getAttribute('data-reserva-id') || '',
            inicio: parseInicio(el.getAttribute('data-inicio')),
            fim: parseInicio(el.getAttribute('data-fim')),
            area: el.getAttribute('data-area') || '',
            status: el.getAttribute('data-status') || '',
            convidados: el.getAttribute('data-convidados') || 'Não informado',
            observacoes: el.getAttribute('data-observacoes') || 'Nenhuma observação',
            motivo: el.getAttribute('data-motivo') || 'Nenhum motivo de rejeição'
          };
        })
        .filter(function (r) {
          return r.inicio !== null && r.id;
        });
    }

  function badgeClasses(status) {
    if (status === 'APROVADO') return 'bg-[#dcfce7] text-[#166534]';
    if (status === 'PENDENTE') return 'bg-[#fef3c7] text-[#92400e]';
    return 'bg-[#fee2e2] text-[#991b1b]';
  }

  function emptyBox(message) {
    const box = document.createElement('div');
    box.className =
      'p-md text-on-surface-variant bg-surface-container rounded-xl border border-[#E2E8F0] text-center';
    const icon = document.createElement('span');
    icon.className = 'material-symbols-outlined text-[40px] text-on-surface-variant/40 block mb-sm mx-auto';
    icon.setAttribute('data-icon', 'event_busy');
    icon.textContent = 'event_busy';
    const p = document.createElement('p');
    p.className = 'font-body-md text-body-md';
    p.textContent = message;
    box.appendChild(icon);
    box.appendChild(p);
    return box;
  }

  function renderList() {
    if (!listContainer) return;
    listContainer.innerHTML = '';

    if (listaVaziaServidor) {
      listContainer.appendChild(
        emptyBox('Você não possui reservas registradas.')
      );
      return;
    }

    const threshold = startOfLocalDay(selectedDate);
    const filtered = loadReservas()
      .filter(function (r) {
        return r.inicio >= threshold;
      })
      .sort(function (a, b) {
        return a.inicio - b.inicio;
      });

    if (filtered.length === 0) {
      listContainer.appendChild(
        emptyBox(
          'Nenhuma reserva a partir desta data. Escolha outro dia ou outra semana.'
        )
      );
      return;
    }

    const fmt = new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short'
    });

    filtered.forEach(function (r) {
      const article = document.createElement('article');
      article.className =
'bg-surface-container-lowest border border-[#E2E8F0] rounded-xl p-md shadow-[0_4px_20px_-4px_rgba(0,0,0,0.02)] hover:shadow-md hover:border-surface-container-high transition-all cursor-pointer';      const wrap = document.createElement('div');
      wrap.className =
        'flex flex-wrap justify-between items-start gap-sm mb-sm';
      const left = document.createElement('div');
      const h3 = document.createElement('h3');
      h3.className = 'font-body-lg text-body-lg text-on-surface font-semibold';
      h3.textContent = r.area;
      const p = document.createElement('p');
      p.className = 'font-body-sm text-body-sm text-on-surface-variant mt-xs';
      if (r.fim && !Number.isNaN(r.fim.getTime())) {
        p.textContent =
          'De ' + fmt.format(r.inicio) + ' até ' + fmt.format(r.fim);
      } else {
        p.textContent = 'Datas a definir';
      }
      left.appendChild(h3);
      left.appendChild(p);
      const badge = document.createElement('span');
      badge.className =
        'inline-flex items-center px-sm py-[2px] rounded-full font-label-md text-[10px] uppercase shrink-0 ' +
        badgeClasses(r.status);
      badge.textContent = r.status;
      wrap.appendChild(left);
      wrap.appendChild(badge);
      article.appendChild(wrap);

      article.addEventListener('click', function(e) {
                if (e.target.closest('button') || e.target.closest('form')) {
                    return;
                }

                document.getElementById('detalhe-reserva-area').innerText = r.area;

                document.getElementById('detalhe-reserva-status').innerHTML =
                    `<span class="inline-flex items-center px-sm py-[2px] rounded-full font-label-md text-[10px] uppercase ${badgeClasses(r.status)}">${r.status}</span>`;

                document.getElementById('detalhe-reserva-inicio').innerText = fmt.format(r.inicio);
                document.getElementById('detalhe-reserva-fim').innerText = r.fim && !Number.isNaN(r.fim.getTime()) ? fmt.format(r.fim) : 'Não definido';
                document.getElementById('detalhe-reserva-convidados').innerText = r.convidados;
                document.getElementById('detalhe-reserva-observacoes').innerText = r.observacoes;

                const containerMotivo = document.getElementById('container-motivo-rejeicao');
                                const textMotivo = document.getElementById('detalhe-reserva-motivo');

                                if (r.status === 'REPROVADO' && r.motivo && r.motivo !== 'Nenhum motivo de rejeição' && r.motivo.trim() !== '') {
                                    textMotivo.innerText = r.motivo;
                                    containerMotivo.classList.remove('hidden');
                                } else {
                                    containerMotivo.classList.add('hidden');
                                    textMotivo.innerText = '';
                                }

                document.getElementById('modal-detalhes-reserva').showModal();
            });

      if (r.id && csrfParam && csrfToken) {
        const actions = document.createElement('div');
        actions.className = 'flex justify-end pt-sm border-t border-outline-variant/20';
        const form = document.createElement('form');
        form.method = 'post';
        form.action = moradorReservasBase + '/' + encodeURIComponent(r.id) + '/cancelar';
        form.className = 'inline';

        const hidden = document.createElement('input');
        hidden.type = 'hidden';
        hidden.name = csrfParam;
        hidden.value = csrfToken;
        form.appendChild(hidden);

        const btn = document.createElement('button');
        btn.type = 'submit';
        btn.className =
          'inline-flex items-center gap-xs px-sm py-xs rounded-lg border border-[#fecaca] bg-[#fef2f2] text-[#991b1b] font-body-sm text-body-sm hover:bg-[#fee2e2] transition-colors';
        const iconCancel = document.createElement('span');
        iconCancel.className = 'material-symbols-outlined text-[18px]';
        iconCancel.setAttribute('aria-hidden', 'true');
        iconCancel.textContent = 'delete';
        btn.appendChild(iconCancel);
        btn.appendChild(document.createTextNode(' Cancelar reserva'));
        btn.addEventListener('click', function (e) {
          if (
            !window.confirm(
              'Cancelar esta reserva? Ela será removida permanentemente.'
            )
          ) {
            e.preventDefault();
          }
        });
        form.appendChild(btn);
        actions.appendChild(form);
        article.appendChild(actions);
      }

      listContainer.appendChild(article);
    });
  }

  function formatWeekRange() {
    const end = new Date(weekStart);
    end.setDate(end.getDate() + 6);
    const dtf = new Intl.DateTimeFormat('pt-BR', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
    return dtf.format(weekStart) + ' – ' + dtf.format(end);
  }

  function renderWeek() {
    if (!weekLabel || !daysContainer) return;
    weekStart = mondayOfWeek(selectedDate);
    weekLabel.textContent = formatWeekRange();
    daysContainer.innerHTML = '';

    const sel = startOfLocalDay(selectedDate).getTime();

    for (var i = 0; i < 7; i++) {
      const d = new Date(weekStart);
      d.setDate(weekStart.getDate() + i);
      const dayStart = startOfLocalDay(d).getTime();

      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className =
        'flex flex-col items-center justify-center gap-xs p-sm rounded-xl border transition-colors min-h-[4.5rem] ' +
        (dayStart === sel
          ? 'border-primary bg-surface-container-highest ring-1 ring-primary/20'
          : 'border-outline-variant/30 bg-surface-container-lowest hover:bg-surface-container-high');

      const dayNum = document.createElement('span');
      dayNum.className = 'font-h3 text-h3 text-on-surface';
      dayNum.textContent = String(d.getDate());

      const wd = document.createElement('span');
      wd.className =
        'font-label-md text-label-md text-on-surface-variant uppercase tracking-wider';
      wd.textContent = WEEKDAY_LABELS[i];

      btn.appendChild(dayNum);
      btn.appendChild(wd);

      (function (dayCopy) {
        btn.addEventListener('click', function () {
          selectedDate = startOfLocalDay(dayCopy);
          weekStart = mondayOfWeek(selectedDate);
          renderWeek();
          renderList();
        });
      })(new Date(d));

      daysContainer.appendChild(btn);
    }
  }

  if (prevBtn) {
    prevBtn.addEventListener('click', function () {
      selectedDate.setDate(selectedDate.getDate() - 7);
      selectedDate = startOfLocalDay(selectedDate);
      weekStart = mondayOfWeek(selectedDate);
      renderWeek();
      renderList();
    });
  }

  if (nextBtn) {
    nextBtn.addEventListener('click', function () {
      selectedDate.setDate(selectedDate.getDate() + 7);
      selectedDate = startOfLocalDay(selectedDate);
      weekStart = mondayOfWeek(selectedDate);
      renderWeek();
      renderList();
    });
  }

  if (todayBtn) {
      todayBtn.addEventListener('click', function () {
        selectedDate = startOfLocalDay(new Date());
        weekStart = mondayOfWeek(selectedDate);
        renderWeek();
        renderList();
      });
    }

  function toInputDateLocal(d) {
    var y = d.getFullYear();
    var m = String(d.getMonth() + 1).padStart(2, '0');
    var day = String(d.getDate()).padStart(2, '0');
    return y + '-' + m + '-' + day;
  }

  function closeModalNovaReserva() {
    if (modal) {
      modal.close();
    }
  }

  if (openModalBtn && modal && typeof modal.showModal === 'function') {
    openModalBtn.addEventListener('click', function () {
      if (formNovaReserva) {
        formNovaReserva.reset();
      }
      if (inputDataReserva) {
        inputDataReserva.value = toInputDateLocal(selectedDate);
      }
      modal.showModal();
    });
  }

  if (closeModalBtn && modal) {
    closeModalBtn.addEventListener('click', closeModalNovaReserva);
  }

  if (cancelModalBtn && modal) {
    cancelModalBtn.addEventListener('click', closeModalNovaReserva);
  }

  if (modalOverlay && modal) {
    modalOverlay.addEventListener('click', function (e) {
      if (e.target === modalOverlay) {
        closeModalNovaReserva();
      }
    });
  }

  if (formNovaReserva) {
      formNovaReserva.addEventListener('submit', function (e) {
        if (divErroModal) {
          divErroModal.classList.add('hidden');
          divErroModal.textContent = '';
        }

        const selectedOption = selectAmbiente.options[selectAmbiente.selectedIndex];

        if (selectedOption && selectedOption.hasAttribute('data-capacidade')) {
          const capacidade = parseInt(selectedOption.getAttribute('data-capacidade'), 10);
          const convidados = parseInt(inputConvidados.value, 10);

          if (!isNaN(capacidade) && !isNaN(convidados) && convidados > capacidade) {
            e.preventDefault();

            if (divErroModal) {
              divErroModal.textContent = 'O número de convidados não pode exceder a capacidade do ambiente (' + capacidade + ').';
              divErroModal.classList.remove('hidden');

              document.querySelector('#form-nova-reserva .overflow-y-auto').scrollTop = 0;
            }
          }
          else if (!isNaN(convidados) && convidados < 1) {
            e.preventDefault();
            if (divErroModal) {
              divErroModal.textContent = 'O número de convidados deve ser pelo menos 1.';
              divErroModal.classList.remove('hidden');
              document.querySelector('#form-nova-reserva .overflow-y-auto').scrollTop = 0;
            }
          }
        }
      });
    }
    if (openModalBtn && modal && typeof modal.showModal === 'function') {
        openModalBtn.addEventListener('click', function () {
          if (formNovaReserva) {
            formNovaReserva.reset();
          }
          if (inputDataReserva) {
            inputDataReserva.value = toInputDateLocal(selectedDate);
          }
          if (divErroModal) {
              divErroModal.classList.add('hidden');
              divErroModal.textContent = '';
          }
          modal.showModal();
        });
      }

  renderWeek();
  renderList();
})();
