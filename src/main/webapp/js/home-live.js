/* =====================================================================
   NepseInsider — Live Market Data
   ---------------------------------------------------------------------
   Fetches NEPSE prices via the server-side ApiProxyServlet, then
   populates the live ticker, market stats, and top-movers tables.
   Auto-refreshes every 30 seconds.
   ===================================================================== */
(function () {
    "use strict";

    /* The proxy lives on our own origin so this works regardless
       of the war-exploded context path. */
    var API_URL = (function () {
        var path = window.location.pathname.split("/")[1] || "";
        return "/" + path + "/api-proxy/stocks";
    })();

    var REFRESH_MS = 30000;

    /* ---------- Helpers ---------- */
    function num(s) {
        if (s === null || s === undefined) return 0;
        return parseFloat(String(s).replace(/,/g, "")) || 0;
    }

    function fmtPrice(n) {
        return n.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function fmtPct(n) {
        var sign = n >= 0 ? "+" : "";
        return sign + n.toFixed(2) + "%";
    }

    function fmtVolume(n) {
        if (n >= 10000000) return (n / 10000000).toFixed(2) + " Cr";
        if (n >= 100000)   return (n / 100000).toFixed(2) + " L";
        if (n >= 1000)     return (n / 1000).toFixed(1) + "K";
        return n.toLocaleString();
    }

    function $(id) { return document.getElementById(id); }

    /* ---------- Render: ticker tape ---------- */
    function renderTicker(stocks) {
        var track = $("tickerTrack");
        if (!track) return;

        // Pick a representative slice — first 25 stocks, sorted by symbol
        var sample = stocks.slice().sort(function (a, b) {
            return a.symbol.localeCompare(b.symbol);
        }).slice(0, 25);

        var html = sample.map(function (s) {
            var cls = s.change >= 0 ? "tk-up" : "tk-down";
            var arrow = s.change >= 0 ? "▲" : "▼";
            return '<span class="ticker-item">' +
                '<span class="tk-sym">' + s.symbol + '</span> ' +
                '<span class="tk-price">Rs.' + fmtPrice(s.ltp) + '</span> ' +
                '<span class="' + cls + '">' + arrow + ' ' + fmtPct(s.change) + '</span>' +
                '</span>';
        }).join("");

        // Duplicate for seamless infinite scroll
        track.innerHTML = html + html;
    }

    /* ---------- Render: market stats ---------- */
    function renderStats(stocks) {
        var gainers = stocks.filter(function (s) { return s.change > 0; }).length;
        var losers  = stocks.filter(function (s) { return s.change < 0; }).length;
        var volume  = stocks.reduce(function (sum, s) { return sum + s.volume; }, 0);

        animateValue("statCount",   stocks.length, function (v) { return v.toLocaleString(); });
        animateValue("statGainers", gainers,       function (v) { return v.toLocaleString(); });
        animateValue("statLosers",  losers,        function (v) { return v.toLocaleString(); });
        $("statVolume").textContent = fmtVolume(volume);

        var heroCount = $("heroStockCount");
        if (heroCount) heroCount.textContent = stocks.length.toLocaleString();
    }

    function animateValue(id, target, formatter) {
        var el = $(id);
        if (!el) return;
        var start    = parseInt(el.getAttribute("data-val") || "0", 10);
        var duration = 600;
        var startTs  = performance.now();

        function step(ts) {
            var p = Math.min((ts - startTs) / duration, 1);
            var eased = 1 - Math.pow(1 - p, 3);
            var v = Math.round(start + (target - start) * eased);
            el.textContent = formatter(v);
            if (p < 1) requestAnimationFrame(step);
        }
        el.setAttribute("data-val", target);
        requestAnimationFrame(step);
    }

    /* ---------- Render: top-movers tables ---------- */
    function renderTable(tableId, stocks, getValue, isPercent) {
        var tbl = $(tableId);
        if (!tbl) return;

        var rows = stocks.map(function (s, i) {
            var changeCls = s.change >= 0 ? "tk-up" : "tk-down";
            var rightVal  = isPercent ? fmtPct(getValue(s)) : fmtVolume(getValue(s));
            var rightCls  = isPercent ? changeCls : "tk-vol";

            return '<tr>' +
                '<td class="rank-cell">' + (i + 1) + '</td>' +
                '<td>' +
                '<div class="sym-cell">' +
                '<strong>' + s.symbol + '</strong>' +
                '<span class="sym-price">Rs. ' + fmtPrice(s.ltp) + '</span>' +
                '</div>' +
                '</td>' +
                '<td class="' + rightCls + '">' + rightVal + '</td>' +
                '</tr>';
        }).join("");

        tbl.querySelector("tbody").innerHTML = rows;
    }

    /* ---------- Master render ---------- */
    function render(stocks) {
        // Filter out anything with zero/missing price
        stocks = stocks.filter(function (s) { return s.ltp > 0; });

        var byChangeDesc = stocks.slice().sort(function (a, b) { return b.change - a.change; });
        var byChangeAsc  = stocks.slice().sort(function (a, b) { return a.change - b.change; });
        var byVolumeDesc = stocks.slice().sort(function (a, b) { return b.volume - a.volume; });

        renderTicker(stocks);
        renderStats(stocks);
        renderTable("topGainers", byChangeDesc.slice(0, 7),  function (s) { return s.change; }, true);
        renderTable("topLosers",  byChangeAsc.slice(0, 7),   function (s) { return s.change; }, true);
        renderTable("mostTraded", byVolumeDesc.slice(0, 7),  function (s) { return s.volume; }, false);
    }

    /* ---------- Fetch ---------- */
    function loadAndRender() {
        fetch(API_URL, { cache: "no-store" })
            .then(function (r) {
                if (!r.ok) throw new Error("HTTP " + r.status);
                return r.json();
            })
            .then(function (json) {
                var raw = (json && json.data) || [];
                var stocks = raw.map(function (s) {
                    return {
                        symbol: s.symbol || "",
                        ltp:    num(s.ltp),
                        open:   num(s.open),
                        high:   num(s.high),
                        low:    num(s.low),
                        change: num(s.percent_change),
                        volume: num(s.volume),
                        time:   s.time || ""
                    };
                });
                if (!stocks.length) throw new Error("Empty data");
                render(stocks);
            })
            .catch(function (err) {
                console.warn("[NepseInsider] live feed unavailable:", err.message);
                showOfflineState();
            });
    }

    function showOfflineState() {
        var t = $("tickerTrack");
        if (t) t.innerHTML = '<span class="ticker-loading">' +
            '⚠️ Live feed temporarily unavailable. Showing cached data.</span>';

        ["topGainers", "topLosers", "mostTraded"].forEach(function (id) {
            var tbl = $(id);
            if (tbl) tbl.querySelector("tbody").innerHTML =
                '<tr><td colspan="3" class="loading-cell">Live feed unavailable</td></tr>';
        });
    }

    /* ---------- Boot + auto-refresh ---------- */
    loadAndRender();
    setInterval(loadAndRender, REFRESH_MS);
})();
