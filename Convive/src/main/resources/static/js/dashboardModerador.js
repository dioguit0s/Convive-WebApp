const CHART_COLORS = [
    '#131b2e',
    '#94ccff',
    '#95f8a7',
    '#ffdad6',
    '#006d30',
    '#7c839b',
    '#bec6e0',
    '#d3e4fe'
];

function parseChartData(raw) {
    if (typeof raw === 'string') {
        try {
            return JSON.parse(raw);
        } catch {
            return [];
        }
    }
    return raw || [];
}

function buildDonutChart(canvasId, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;

    const slices = parseChartData(data);
    const labels = slices.map(s => s.label);
    const values = slices.map(s => s.value);
    const total = values.reduce((a, b) => a + b, 0);

    if (total === 0) {
        labels.push('Sem dados');
        values.push(1);
    }

    new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: CHART_COLORS.slice(0, labels.length),
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { font: { family: 'Manrope', size: 11 }, boxWidth: 12 }
                }
            },
            cutout: '60%'
        }
    });
}

function buildBarChart(canvasId, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;

    const slices = parseChartData(data);
    const labels = slices.length > 0 ? slices.map(s => s.label) : ['Sem dados'];
    const values = slices.length > 0 ? slices.map(s => s.value) : [0];

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Reservas aprovadas',
                data: values,
                backgroundColor: '#131b2e',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1, font: { family: 'Manrope' } } },
                x: { ticks: { font: { family: 'Manrope', size: 10 } } }
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    buildDonutChart('chartOcorrencias', chartOcorrenciasData);
    buildBarChart('chartReservas', chartReservasData);
    buildDonutChart('chartComunicados', chartComunicadosData);
});
