package org.ulpgc.dacd.thecodeknights.ui;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.ulpgc.dacd.thecodeknights.datamart.DatamartRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class DashboardServer {

    private final DatamartRepository datamart;
    private final int port;
    private final Gson gson = new Gson();

    public DashboardServer(DatamartRepository datamart, int port) {
        this.datamart = datamart;
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/games", this::handleGames);
        server.createContext("/", this::handleDashboard);
        server.start();
    }

    private void handleGames(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        respond(exchange, 200, "application/json", gson.toJson(datamart.getAllAnalytics()));
    }

    private void handleDashboard(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        respond(exchange, 200, "text/html; charset=UTF-8", buildHtml());
    }

    private void respond(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String buildHtml() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>GamesTrends – Dashboard</title>
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
                <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
                <style>
                    body            { background:#0d1117; color:#c9d1d9; }
                    .navbar         { background:#161b22; border-bottom:1px solid #30363d; }
                    .card           { background:#161b22; border:1px solid #30363d; }
                    .table          { color:#c9d1d9; }
                    .table thead th { background:#21262d; border-color:#30363d; color:#f0f6fc;
                                      cursor:pointer; user-select:none; white-space:nowrap; }
                    .table thead th:hover { background:#2d333b; }
                    .table td       { border-color:#30363d; vertical-align:middle; }
                    .table tbody tr:hover { background:#21262d; }
                    .metric-card    { background:#21262d; border-radius:8px; padding:16px; text-align:center; }
                    .metric-value   { font-size:2rem; font-weight:700; color:#58a6ff; }
                    .metric-label   { font-size:.8rem; color:#8b949e; margin-top:4px; }
                    .info-box       { background:#21262d; border-left:4px solid #58a6ff;
                                      padding:12px 16px; border-radius:4px; font-size:.88rem; }
                    .controls-card  { background:#161b22; border:1px solid #30363d; border-radius:8px; padding:20px; }
                    .badge-high     { background:#238636; }
                    .badge-mid      { background:#9a6700; }
                    .badge-low      { background:#b91c1c; }
                    .sort-arrow     { color:#58a6ff; font-size:.75rem; margin-left:4px; }
                    .form-select    { background:#21262d; color:#c9d1d9; border-color:#30363d; }
                    .form-select:focus { background:#21262d; color:#c9d1d9; border-color:#58a6ff; box-shadow:none; }
                    h2, h5          { color:#f0f6fc; }
                </style>
            </head>
            <body>

            <nav class="navbar px-4 py-3 mb-4 d-flex justify-content-between align-items-center">
                <span class="fs-4 fw-bold text-white">🎮 GamesTrends Dashboard</span>
                <div class="d-flex align-items-center gap-3">
                    <span class="text-secondary small" id="lastUpdate">Cargando...</span>
                    <button class="btn btn-sm" onclick="loadData()"
                            style="background:#21262d;color:#58a6ff;border:1px solid #58a6ff;">
                        ↻ Actualizar
                    </button>
                </div>
            </nav>

            <div class="container-fluid px-4">

                <!-- Tarjetas de resumen -->
                <div class="row g-3 mb-4">
                    <div class="col-6 col-md-3">
                        <div class="metric-card">
                            <div class="metric-value" id="totalGames">–</div>
                            <div class="metric-label">Juegos con datos Steam</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="metric-card">
                            <div class="metric-value" id="gamesWithTwitch">–</div>
                            <div class="metric-label">Con presencia en Twitch</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="metric-card">
                            <div class="metric-value" id="topRatio">–</div>
                            <div class="metric-label">Mejor Stream Potential Ratio</div>
                        </div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="metric-card">
                            <div class="metric-value" id="totalViewers">–</div>
                            <div class="metric-label">Viewers totales en Twitch</div>
                        </div>
                    </div>
                </div>

                <!-- Panel de controles -->
                <div class="controls-card mb-4">
                    <div class="row align-items-end g-4">
                        <div class="col-md-2">
                            <label class="form-label small fw-bold mb-1">Juegos a mostrar</label>
                            <select class="form-select form-select-sm" id="limitSelect">
                                <option value="20" selected>20</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                                <option value="99999">Todos</option>
                            </select>
                        </div>
                        <div class="col-md-5">
                            <label class="form-label small fw-bold mb-1">
                                Peso Stream Potential Ratio:
                                <span class="text-info" id="wRatioVal">70%</span>
                                <span class="text-secondary ms-2" style="font-size:.75rem;font-weight:normal">
                                    demanda del juego en Twitch
                                </span>
                            </label>
                            <input type="range" class="form-range" id="wRatio" min="0" max="100" value="70">
                        </div>
                        <div class="col-md-5">
                            <label class="form-label small fw-bold mb-1">
                                Peso Viewers / Stream:
                                <span class="text-info" id="wViewersVal">30%</span>
                                <span class="text-secondary ms-2" style="font-size:.75rem;font-weight:normal">
                                    audiencia media por streamer
                                </span>
                            </label>
                            <input type="range" class="form-range" id="wViewers" min="0" max="100" value="30">
                        </div>
                    </div>
                </div>

                <!-- Gráficos -->
                <div class="row g-3 mb-4">
                    <div class="col-lg-6">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="mb-1">Ratio vs Viewers / Stream</h5>
                                <p class="text-secondary small mb-3">
                                    Cada punto es un juego. Arriba a la derecha = mejor oportunidad.
                                </p>
                                <canvas id="scatterChart"></canvas>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="mb-1">Top 10 por Score de Oportunidad</h5>
                                <p class="text-secondary small mb-3">
                                    Score ponderado según los sliders. Se actualiza en tiempo real.
                                </p>
                                <canvas id="barChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Info box -->
                <div class="info-box mb-4">
                    <strong>📊 Métricas de valor añadido (cruzan datos Steam + Twitch)</strong><br>
                    <b>Stream Potential Ratio</b> = Viewers Twitch / Jugadores Steam · Mayor → más gente prefiere <em>ver</em> que jugar.<br>
                    <b>Viewers / Stream</b> = Viewers totales / Streams activos · Audiencia media disponible por streamer.<br>
                    <b>Score</b> = (peso ratio × ratio) + (peso viewers/stream × viewers/stream÷1000), ambos normalizados a 0-1.<br>
                    <b>Oportunidad</b>: <span class="badge badge-high">Alta</span> score &gt; 0.35 &nbsp;
                    <span class="badge badge-mid">Media</span> score &gt; 0.17 &nbsp;
                    <span class="badge badge-low">Baja</span> resto.
                </div>

                <!-- Tabla -->
                <div class="card mb-5">
                    <div class="card-body">
                        <div class="d-flex align-items-baseline gap-3 mb-3">
                            <h2 class="mb-0">🏆 Recomendaciones para Streamers</h2>
                            <span class="text-secondary fs-6" id="showingCount"></span>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="recoTable">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th data-col="gameName" data-type="str">Juego</th>
                                        <th data-col="steamPlayers" data-type="num">Jugadores Steam</th>
                                        <th data-col="twitchViewers" data-type="num">Viewers Twitch</th>
                                        <th data-col="twitchStreams" data-type="num">Streams activos</th>
                                        <th data-col="streamPotentialRatio" data-type="num">Stream Potential Ratio</th>
                                        <th data-col="viewerPerStream" data-type="num">Viewers / Stream</th>
                                        <th data-col="_score" data-type="num">Score</th>
                                        <th data-col="_score" data-type="num">Oportunidad</th>
                                    </tr>
                                </thead>
                                <tbody id="recoBody">
                                    <tr>
                                        <td colspan="9" class="text-center text-secondary">Cargando...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                let allData       = [];
                let sortCol       = 'streamPotentialRatio';
                let sortDir       = 'desc';
                let wRatio        = 70;
                let wViewers      = 30;
                let scatterInst   = null;
                let barInst       = null;

                // ── Utilidades ──────────────────────────────────────────────
                function fmt(n, dec = 0) {
                    if (n == null) return '–';
                    if (n >= 1_000_000) return (n / 1_000_000).toFixed(1) + 'M';
                    if (n >= 1_000)     return (n / 1_000).toFixed(1) + 'K';
                    return dec > 0 ? Number(n).toFixed(dec) : String(Math.round(n));
                }

                function computeScore(g) {
                    const normRatio   = Math.min(g.streamPotentialRatio, 1.0);
                    const normViewers = Math.min(g.viewerPerStream / 1000, 1.0);
                    return (wRatio / 100) * normRatio + (wViewers / 100) * normViewers;
                }

                function badge(s) {
                    if (s > 0.35) return { label: 'Alta',  cls: 'badge-high', color: '#238636' };
                    if (s > 0.17) return { label: 'Media', cls: 'badge-mid',  color: '#9a6700' };
                    return              { label: 'Baja',  cls: 'badge-low',  color: '#b91c1c' };
                }

                function enriched() {
                    return allData.map(g => ({ ...g, _score: computeScore(g) }));
                }

                function getLimit() {
                    return parseInt(document.getElementById('limitSelect').value);
                }

                // ── Tabla ───────────────────────────────────────────────────
                function renderTable() {
                    const lim  = getLimit();
                    const data = enriched()
                        .sort((a, b) => {
                            const va = a[sortCol], vb = b[sortCol];
                            if (typeof va === 'string')
                                return sortDir === 'asc' ? va.localeCompare(vb) : vb.localeCompare(va);
                            return sortDir === 'asc' ? va - vb : vb - va;
                        })
                        .slice(0, lim);

                    document.getElementById('showingCount').textContent =
                        `— mostrando ${data.length} de ${allData.length}`;

                    document.querySelectorAll('thead th[data-col]').forEach(th => {
                        const existing = th.querySelector('.sort-arrow');
                        if (existing) existing.remove();
                        if (th.dataset.col === sortCol) {
                            const arrow = document.createElement('span');
                            arrow.className   = 'sort-arrow';
                            arrow.textContent = sortDir === 'asc' ? '↑' : '↓';
                            th.appendChild(arrow);
                        }
                    });

                    const tbody = document.getElementById('recoBody');
                    if (!data.length) {
                        tbody.innerHTML =
                            '<tr><td colspan="9" class="text-center text-secondary">Sin datos. Inicia los feeders.</td></tr>';
                        return;
                    }
                    tbody.innerHTML = data.map((g, i) => {
                        const s  = g._score;
                        const op = badge(s);
                        return `<tr>
                            <td><strong>${i + 1}</strong></td>
                            <td><strong>${g.gameName}</strong></td>
                            <td>${fmt(g.steamPlayers)}</td>
                            <td>${fmt(g.twitchViewers)}</td>
                            <td>${fmt(g.twitchStreams)}</td>
                            <td><strong>${g.streamPotentialRatio.toFixed(4)}</strong></td>
                            <td>${Math.round(g.viewerPerStream)}</td>
                            <td>${s.toFixed(3)}</td>
                            <td><span class="badge ${op.cls}">${op.label}</span></td>
                        </tr>`;
                    }).join('');
                }

                // ── Gráficos ────────────────────────────────────────────────
                function renderCharts() {
                    const data = enriched().sort((a, b) => b._score - a._score);

                    const byOpportunity = { Alta: [], Media: [], Baja: [] };
                    data.forEach(g => byOpportunity[badge(g._score).label].push(g));

                    // Scatter: ratio vs viewers/stream
                    const scatterDatasets = [
                        { label: 'Alta',  color: '#238636' },
                        { label: 'Media', color: '#9a6700' },
                        { label: 'Baja',  color: '#b91c1c' },
                    ].map(({ label, color }) => ({
                        label,
                        backgroundColor: color,
                        pointRadius: 6,
                        pointHoverRadius: 9,
                        data: byOpportunity[label].map(g => ({
                            x: g.streamPotentialRatio,
                            y: Math.min(g.viewerPerStream, 2000),
                            name: g.gameName
                        }))
                    }));

                    if (scatterInst) scatterInst.destroy();
                    scatterInst = new Chart(document.getElementById('scatterChart'), {
                        type: 'scatter',
                        data: { datasets: scatterDatasets },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: { labels: { color: '#c9d1d9' } },
                                tooltip: {
                                    callbacks: {
                                        label: ctx =>
                                            `${ctx.raw.name} (ratio: ${ctx.raw.x.toFixed(3)}, v/stream: ${ctx.raw.y})`
                                    }
                                }
                            },
                            scales: {
                                x: {
                                    title: { display: true, text: 'Stream Potential Ratio', color: '#8b949e' },
                                    ticks: { color: '#8b949e' }, grid: { color: '#30363d' }
                                },
                                y: {
                                    title: { display: true, text: 'Viewers / Stream (cap 2000)', color: '#8b949e' },
                                    ticks: { color: '#8b949e' }, grid: { color: '#30363d' }
                                }
                            }
                        }
                    });

                    // Bar: top 10 por score
                    const top10 = data.slice(0, 10);
                    if (barInst) barInst.destroy();
                    barInst = new Chart(document.getElementById('barChart'), {
                        type: 'bar',
                        data: {
                            labels: top10.map(g => g.gameName.length > 20 ? g.gameName.slice(0, 18) + '…' : g.gameName),
                            datasets: [{
                                label: 'Score',
                                data: top10.map(g => parseFloat(g._score.toFixed(3))),
                                backgroundColor: top10.map(g => badge(g._score).color),
                                borderRadius: 4
                            }]
                        },
                        options: {
                            indexAxis: 'y',
                            responsive: true,
                            plugins: { legend: { display: false } },
                            scales: {
                                x: { max: 1, ticks: { color: '#8b949e' }, grid: { color: '#30363d' } },
                                y: { ticks: { color: '#c9d1d9' }, grid: { color: '#30363d' } }
                            }
                        }
                    });
                }

                // ── Resumen ─────────────────────────────────────────────────
                function updateSummary() {
                    const withTwitch = allData.filter(g => g.twitchStreams > 0).length;
                    const topRatio   = allData.length
                        ? allData.slice().sort((a, b) => b.streamPotentialRatio - a.streamPotentialRatio)[0]
                              .streamPotentialRatio.toFixed(4)
                        : '–';
                    const totalViewers = allData.reduce((s, g) => s + g.twitchViewers, 0);

                    document.getElementById('totalGames').textContent    = fmt(allData.length);
                    document.getElementById('gamesWithTwitch').textContent = fmt(withTwitch);
                    document.getElementById('topRatio').textContent      = topRatio;
                    document.getElementById('totalViewers').textContent  = fmt(totalViewers);
                    document.getElementById('lastUpdate').textContent    =
                        'Actualizado: ' + new Date().toLocaleTimeString();
                }

                // ── Carga de datos ──────────────────────────────────────────
                async function loadData() {
                    try {
                        allData = await fetch('/api/games').then(r => r.json());
                        updateSummary();
                        renderTable();
                        renderCharts();
                    } catch (err) {
                        document.getElementById('recoBody').innerHTML =
                            `<tr><td colspan="9" class="text-center text-danger">Error: ${err.message}</td></tr>`;
                    }
                }

                // ── Eventos de UI ───────────────────────────────────────────
                function linkSliders(changedId, otherId, valId, otherValId) {
                    document.getElementById(changedId).addEventListener('input', function () {
                        const v = parseInt(this.value);
                        document.getElementById(otherId).value    = 100 - v;
                        document.getElementById(valId).textContent      = v + '%';
                        document.getElementById(otherValId).textContent = (100 - v) + '%';
                        wRatio   = parseInt(document.getElementById('wRatio').value);
                        wViewers = parseInt(document.getElementById('wViewers').value);
                        renderTable();
                        renderCharts();
                    });
                }
                linkSliders('wRatio',   'wViewers', 'wRatioVal',   'wViewersVal');
                linkSliders('wViewers', 'wRatio',   'wViewersVal', 'wRatioVal');

                document.getElementById('limitSelect').addEventListener('change', renderTable);

                document.querySelectorAll('thead th[data-col]').forEach(th => {
                    th.addEventListener('click', () => {
                        const col = th.dataset.col;
                        sortDir = (sortCol === col && sortDir === 'desc') ? 'asc' : 'desc';
                        sortCol = col;
                        renderTable();
                    });
                });

                loadData();
                setInterval(loadData, 10 * 60 * 1000);
            </script>
            </body>
            </html>
        """;
    }
}
