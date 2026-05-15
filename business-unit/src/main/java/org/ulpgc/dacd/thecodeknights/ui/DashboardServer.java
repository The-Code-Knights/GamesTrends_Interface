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
                <style>
                    body            { background:#0d1117; color:#c9d1d9; font-size:1.2rem; }
                    .navbar         { background:#161b22; border-bottom:1px solid #30363d; }
                    .card           { background:#161b22; border:1px solid #30363d; }
                    .table          { color:#c9d1d9; font-size:1.15rem; }
                    .table thead th { background:#21262d; border-color:#30363d; color:#f0f6fc;
                                      cursor:pointer; user-select:none; white-space:nowrap; font-size:1.15rem; }
                    .table thead th:hover { background:#2d333b; }
                    .table td       { border-color:#30363d; vertical-align:middle; padding:12px 14px; }
                    .table tbody tr:hover { background:#21262d; }
                    .info-box       { background:#21262d; border-left:4px solid #58a6ff;
                                      padding:28px 32px; border-radius:4px; font-size:1.35rem; line-height:2.3; }
                    .controls-card  { background:#161b22; border:1px solid #30363d; border-radius:8px; padding:24px; }
                    .sort-arrow     { color:#58a6ff; font-size:.9rem; margin-left:4px; }
                    .form-select    { background:#21262d; color:#c9d1d9; border-color:#30363d; font-size:1.1rem; }
                    .form-select:focus { background:#21262d; color:#c9d1d9; border-color:#58a6ff; box-shadow:none; }
                    h2, h5          { color:#f0f6fc; }
                    .score-alta     { background:#238636; color:#fff; padding:4px 14px; border-radius:6px; font-weight:700; display:inline-block; font-size:1.05rem; }
                    .score-media    { background:#9a6700; color:#fff; padding:4px 14px; border-radius:6px; font-weight:700; display:inline-block; font-size:1.05rem; }
                    .score-baja     { background:#6e3030; color:#fca5a5; padding:4px 14px; border-radius:6px; font-weight:700; display:inline-block; font-size:1.05rem; }

                    .podium-section  { padding:50px 0 70px; }
                    .podium-wrapper  { display:flex; justify-content:center; align-items:flex-end; gap:32px; flex-wrap:wrap; }
                    .podium-card     { background:#161b22; border:1px solid #30363d; border-radius:18px;
                                       padding:40px 32px; text-align:center; position:relative; }
                    .podium-card.p2  { width:380px; min-height:260px; }
                    .podium-card.p3  { width:350px; min-height:240px; }
                    .podium-card.p1  { width:480px; min-height:360px; border:3px solid #ffd700;
                                       box-shadow:0 0 60px rgba(255,215,0,0.3); }
                    .podium-medal    { font-size:5rem; }
                    .podium-card.p1 .podium-medal { font-size:7rem; }
                    .podium-name     { font-weight:700; color:#f0f6fc; margin:14px 0 8px; }
                    .podium-card.p1 .podium-name { font-size:1.9rem; }
                    .podium-card.p2 .podium-name,
                    .podium-card.p3 .podium-name { font-size:1.4rem; }
                    .podium-score    { color:#8b949e; font-size:1.15rem; }
                    .podium-score span { font-size:1.55rem; font-weight:700; color:#58a6ff; }
                    .podium-extra    { margin-top:10px; color:#8b949e; font-size:1.05rem; }

                    .confetti-piece  { position:absolute; font-size:2rem; pointer-events:none;
                                       animation:floatUp 2.8s ease-in-out infinite; }
                    @keyframes floatUp {
                        0%   { transform:translateY(0) rotate(0deg);    opacity:1; }
                        50%  { transform:translateY(-18px) rotate(18deg); opacity:.75; }
                        100% { transform:translateY(0) rotate(0deg);    opacity:1; }
                    }
                </style>
            </head>
            <body>

            <nav class="navbar px-4 py-3 mb-2 d-flex justify-content-between align-items-center">
                <span class="fs-4 fw-bold text-white">🎮 GamesTrends Dashboard</span>
                <div class="d-flex align-items-center gap-3">
                    <span class="text-secondary" id="lastUpdate">Cargando...</span>
                    <button class="btn btn-sm" onclick="loadData()"
                            style="background:#21262d;color:#58a6ff;border:1px solid #58a6ff;">
                        ↻ Actualizar
                    </button>
                </div>
            </nav>

            <div class="container-fluid px-4">

                <div class="podium-section">
                    <h2 class="text-center mb-4" style="font-size:2.4rem;">🏆 Top 3 Juegos Recomendados para Streamers</h2>
                    <div class="podium-wrapper" id="podiumWrapper">
                        <p class="text-secondary">Cargando...</p>
                    </div>
                </div>

                <div class="controls-card mb-4">
                    <div class="row align-items-center g-4">
                        <div class="col-md-4">
                            <div style="background:#0d1117; border-radius:10px; padding:18px 22px; font-size:1.15rem; text-align:center; white-space:nowrap;">
                                <strong style="font-size:1.2rem; color:#f0f6fc;">Puntuación =</strong>
                                <span style="color:#c9d1d9;"> (</span><span id="fW1" style="color:#58a6ff; font-weight:700; font-size:1.3rem;">0.70</span>
                                <span style="color:#c9d1d9;"> × Ratio)</span>
                                <span style="color:#8b949e;"> + </span>
                                <span style="color:#c9d1d9;">(</span><span id="fW2" style="color:#58a6ff; font-weight:700; font-size:1.3rem;">0.30</span>
                                <span style="color:#c9d1d9;"> × Espect. / Stream)</span>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold mb-1">Juegos a mostrar</label>
                            <select class="form-select form-select-sm" id="limitSelect">
                                <option value="20" selected>20</option>
                                <option value="50">50</option>
                                <option value="99999">Todos</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold mb-1">
                                Peso Ratio:
                                <span class="text-info" id="wRatioVal">70%</span>
                                <span class="text-secondary ms-2" style="font-size:.9rem;font-weight:normal;">demanda del juego en Twitch</span>
                            </label>
                            <input type="range" class="form-range" id="wRatio" min="0" max="100" value="70">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold mb-1">
                                Peso Espectadores / Stream:
                                <span class="text-info" id="wViewersVal">30%</span>
                                <span class="text-secondary ms-2" style="font-size:.9rem;font-weight:normal;">audiencia media por streamer</span>
                            </label>
                            <input type="range" class="form-range" id="wViewers" min="0" max="100" value="30">
                        </div>
                    </div>
                </div>

                <div class="info-box mb-4">
                    <strong style="font-size:1.55rem;">📊 Métricas</strong><br>
                    <b>Ratio</b> = Espectadores Twitch / Jugadores Steam &nbsp;·&nbsp; Cuanto mayor, más gente prefiere <em>ver</em> que jugar.<br>
                    <b>Espectadores / Stream</b> = Espectadores totales / Streams activos &nbsp;·&nbsp; Audiencia media disponible por streamer.<br>
                    <b>Puntuación</b> = (peso ratio × ratio) + (peso espectadores/stream × espectadores/stream ÷ 1000), ambos normalizados a 0-1.<br>
                    <b>Color:</b>&nbsp;
                    <span class="score-alta">Alta</span>&nbsp; puntuación &gt; 0.35 &nbsp;&nbsp;
                    <span class="score-media">Media</span>&nbsp; puntuación &gt; 0.17 &nbsp;&nbsp;
                    <span class="score-baja">Baja</span>&nbsp; resto.
                </div>

                <div class="card mb-5">
                    <div class="card-body">
                        <div class="d-flex align-items-baseline gap-3 mb-3">
                            <h2 class="mb-0">📋 Tabla completa de recomendaciones</h2>
                            <span class="text-secondary fs-6" id="showingCount"></span>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="recoTable">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th data-col="gameName" data-type="str">Juego</th>
                                        <th data-col="steamPlayers" data-type="num">Jugadores Steam</th>
                                        <th data-col="twitchViewers" data-type="num">Espectadores Twitch</th>
                                        <th data-col="twitchStreams" data-type="num">Streams Activos</th>
                                        <th data-col="streamPotentialRatio" data-type="num">Ratio</th>
                                        <th data-col="viewerPerStream" data-type="num">Espectadores / Stream</th>
                                        <th data-col="_score" data-type="num">Puntuación</th>
                                    </tr>
                                </thead>
                                <tbody id="recoBody">
                                    <tr><td colspan="8" class="text-center text-secondary">Cargando...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                let allData  = [];
                let sortCol  = 'streamPotentialRatio';
                let sortDir  = 'desc';
                let wRatio   = 70;
                let wViewers = 30;

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

                function scoreClass(s) {
                    if (s > 0.35) return 'score-alta';
                    if (s > 0.17) return 'score-media';
                    return 'score-baja';
                }

                function enriched() {
                    return allData.map(g => ({ ...g, _score: computeScore(g) }));
                }

                function getLimit() {
                    return parseInt(document.getElementById('limitSelect').value);
                }

                function renderPodium() {
                    const top3 = enriched().sort((a, b) => b._score - a._score).slice(0, 3);
                    if (!top3.length) return;

                    const medals   = ['🥇', '🥈', '🥉'];
                    const confetti = ['🎉', '✨', '⭐', '🎊', '💫', '🌟'];
                    const positions = [
                        {top:'-26px', left:'-26px'}, {top:'-26px', right:'-26px'},
                        {bottom:'-26px', left:'-26px'}, {bottom:'-26px', right:'-26px'},
                        {top:'-26px', left:'44%'}, {bottom:'-26px', left:'44%'}
                    ];

                    function card(g, rank) {
                        const isFirst = rank === 0;
                        const cls     = isFirst ? 'p1' : rank === 1 ? 'p2' : 'p3';
                        let conf = '';
                        if (isFirst) {
                            confetti.forEach((em, i) => {
                                const pos   = positions[i];
                                const delay = (i * 0.45).toFixed(2);
                                const sty   = Object.entries(pos).map(([k,v]) => k+':'+v).join(';');
                                conf += `<span class="confetti-piece" style="${sty};animation-delay:${delay}s">${em}</span>`;
                            });
                        }
                        return `<div class="podium-card ${cls}">
                            ${conf}
                            <div class="podium-medal">${medals[rank]}</div>
                            <div class="podium-name">${g.gameName}</div>
                            <div class="podium-score">Puntuación: <span>${g._score.toFixed(3)}</span></div>
                            <div class="podium-extra">${fmt(g.steamPlayers)} jugadores &nbsp;·&nbsp; ${fmt(g.twitchViewers)} espectadores</div>
                        </div>`;
                    }

                    const order = top3.length >= 3 ? [top3[1], top3[0], top3[2]] : top3.length === 2 ? [top3[1], top3[0]] : [top3[0]];
                    const ranks = top3.length >= 3 ? [1, 0, 2]                  : top3.length === 2 ? [1, 0]              : [0];
                    document.getElementById('podiumWrapper').innerHTML = order.map((g, i) => card(g, ranks[i])).join('');
                }

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

                    document.getElementById('showingCount').textContent = `— mostrando ${data.length} de ${allData.length}`;

                    document.querySelectorAll('thead th[data-col]').forEach(th => {
                        const ex = th.querySelector('.sort-arrow');
                        if (ex) ex.remove();
                        if (th.dataset.col === sortCol) {
                            const arr = document.createElement('span');
                            arr.className   = 'sort-arrow';
                            arr.textContent = sortDir === 'asc' ? '↑' : '↓';
                            th.appendChild(arr);
                        }
                    });

                    const tbody = document.getElementById('recoBody');
                    if (!data.length) {
                        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-secondary">Sin datos. Inicia los feeders.</td></tr>';
                        return;
                    }
                    tbody.innerHTML = data.map((g, i) => {
                        const s = g._score;
                        return `<tr>
                            <td><strong>${i + 1}</strong></td>
                            <td><strong>${g.gameName}</strong></td>
                            <td>${fmt(g.steamPlayers)}</td>
                            <td>${fmt(g.twitchViewers)}</td>
                            <td>${fmt(g.twitchStreams)}</td>
                            <td><strong>${g.streamPotentialRatio.toFixed(4)}</strong></td>
                            <td>${Math.round(g.viewerPerStream)}</td>
                            <td><span class="${scoreClass(s)}">${s.toFixed(3)}</span></td>
                        </tr>`;
                    }).join('');
                }

                async function loadData() {
                    try {
                        allData = await fetch('/api/games').then(r => r.json());
                        document.getElementById('lastUpdate').textContent = 'Actualizado: ' + new Date().toLocaleTimeString();
                        renderPodium();
                        renderTable();
                    } catch (err) {
                        document.getElementById('recoBody').innerHTML =
                            `<tr><td colspan="8" class="text-center text-danger">Error: ${err.message}</td></tr>`;
                    }
                }

                function updateFormula() {
                    document.getElementById('fW1').textContent = (wRatio   / 100).toFixed(2);
                    document.getElementById('fW2').textContent = (wViewers / 100).toFixed(2);
                }

                function linkSliders(changedId, otherId, valId, otherValId) {
                    document.getElementById(changedId).addEventListener('input', function () {
                        const v = parseInt(this.value);
                        document.getElementById(otherId).value          = 100 - v;
                        document.getElementById(valId).textContent      = v + '%';
                        document.getElementById(otherValId).textContent = (100 - v) + '%';
                        wRatio   = parseInt(document.getElementById('wRatio').value);
                        wViewers = parseInt(document.getElementById('wViewers').value);
                        updateFormula();
                        renderPodium();
                        renderTable();
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
