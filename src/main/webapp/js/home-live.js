/* =====================================================================
   NepseInsider — Live home page data
   ---------------------------------------------------------------------
   Powers the ticker (sector indices), market stats, top movers, and
   community pulse. Caches in localStorage so revisits render instantly
   instead of flashing a "Connecting..." message.
   ===================================================================== */
(function () {
    "use strict";

    /* ---------- Endpoints (relative — works on any context path) ---------- */
    var CTX = (function () {
        var first = window.location.pathname.split("/")[1] || "";
        return first ? "/" + first : "";
    })();
    var URL_STOCKS    = CTX + "/api-proxy/stocks";
    var URL_SENTIMENT = CTX + "/sentiment?global=true";

    var REFRESH_MS    = 30_000;
    var CACHE_KEY     = "nepse:home:cache:v3";
    var CACHE_MAX_AGE = 5 * 60_000;          // accept up to 5 min old on first paint

    /* ---------- Sector mapping (covers the major NEPSE sectors) ----------
       This is the heart of the synthesised "indices" — we average each
       sector's % change to produce a sub-index value, the same shape of
       data NEPSE itself publishes. */
    var SECTORS = {
        "Commercial Banks": ["NABIL","NIMB","ADBL","NBL","GBIME","KBL","MBL","HBL","EBL",
            "NICA","SBI","SCB","PRVU","SANIMA","NMB","CZBIL","SBL","LBBL",
            "PCBL","RBB","HDL","NBBL","KBSH"],
        "Hydropower":       ["AHL","AHPC","AKJCL","AKPL","API","BARUN","BEDC","BHCL","BHDC",
            "BHL","BHPL","BJHL","BNHC","BPCL","BUNGAL","CHCL","CKHL","CYCL",
            "DHEL","DHPL","DOLTI","DORDI","EHPL","GHL","GVL","HDHPC","HHL",
            "HIDCL","HPPL","HURJA","IHL","JOSHI","KKHC","KPCL","LEC","MCHL",
            "MEN","MEHL","MHCL","MHL","MHNL","MKHC","MKHL","MKJC","MMKJL",
            "MPFL","NGPL","NHDL","NHPC","NRM","NWCL","NYADI","OMPL","PHCL",
            "PMHPL","PPCL","PPL","RADHI","RFPL","RHGCL","RHPL","RIDI","RLEL",
            "RURU","SAGAR","SAHAS","SANVI","SARBTM","SGHC","SHEL","SHPC",
            "SIKLES","SINDU","SJCL","SMH","SMHL","SMJC","SMPDA","SOHL","SPDL",
            "SPHL","SPIL","SPL","TAMOR","TPC","TRH","TSHL","TTL","TVCL","UAIL",
            "UHEWA","ULHC","UMRH","UNHPL","UPCL","UPPER","USHL","USHEC","SHL",
            "BBC","BGWT","NABBC","HEI","SAIL","SIPD","CGH"],
        "Microfinance":     ["ALBSL","CBBL","DDBL","FMDBL","GBLBS","GILB","GLBSL","GMFBS","HLBSL",
            "ILBS","JBLB","JSLBB","KMCDB","LLBS","MERO","MLBBL","MLBS","MSLB",
            "MSFL","NESDO","NMFBS","NMIC","NMLBBL","NUBL","RSDC","SABBL","SHLB",
            "SKBBL","SLBBL","SLBSL","SMATA","SMFBS","SWBBL","ULBSL","UMHL",
            "UNLB","USLB","VLBS","WNLB","DDBL","GMFIL"],
        "Life Insurance":   ["ALICL","CLI","GMLI","ILI","LICN","NLICL","NLIC","PMLI","RNLI",
            "SJLIC","SNLI","SRLI","CREST"],
        "Non-Life Insurance":["IGI","NICL","NICLBSL","NIL","NLG","PRIN","SALICO","SGIC",
            "SHIVM","SICL","UAIL","HEIP"],
        "Development Banks":["GBBL","KSBBL","MNBBL","JBBL","SADBL","SAPDBL","SHINE","EDBL",
            "GRDBL","NABIL","SINDU","KMCDB"],
        "Finance":          ["BFC","CFCL","GFCL","ICFC","MFIL","NFS","PFL","PROFL","RLFL",
            "SFCL","USHEC","GUFL","HFIN","RBCL","SIFC","JFL"],
        "Hotels":           ["CGH","KDL","OHL","SHL","TRH","HIMSTAR","HATHY","KDBY"],
        "Manufacturing":    ["BNL","BNT","HRL","JFL","NRIC","RBCL","RSML","SHIVM","STC",
            "SWASTIK","UNL","HDL"],
        "Investment":       ["CHDC","CIT","ENL","NIFRA","NRN","HFIN"],
        "Trading":          ["NTC","BNT","STC"]
    };

    /* ---------- Helpers ---------- */
    var $ = function (id) { return document.getElementById(id); };

    function num(v) {
        if (v == null) return 0;
        return parseFloat(String(v).replace(/,/g, "")) || 0;
    }
    function fmt2(n)   { return n.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 }); }
    function pct(n)    { return (n >= 0 ? "+" : "") + n.toFixed(2) + "%"; }
    function compact(n) {
        if (n >= 10_000_000) return (n / 10_000_000).toFixed(2) + " Cr";
        if (n >= 100_000)    return (n / 100_000).toFixed(2)   + " L";
        if (n >= 1000)       return (n / 1000).toFixed(1)      + "K";
        return Math.round(n).toLocaleString();
    }

    /* Common-stock filter — strips out bonds, mutual funds, IPO shares */
    function isCommonStock(sym) {
        if (!sym) return false;
        if (/D\d{2}/.test(sym))                                    return false; // debentures
        if (/(MF\d*|^[A-Z]+F\d+|SY\d*|EY\d*|BY\d*)$/.test(sym))    return false; // mutual funds
        if (/PO$/.test(sym))                                       return false; // IPO listings
        if (sym.length > 4 && /[A-Z]+P$/.test(sym) &&
            !/^(API|UPP|UMP|UPCL|UNL)/.test(sym))                  return false; // promoter shares
        return true;
    }

    /* ---------- Sector index computation ---------- */
    function computeSectorIndices(stocks) {
        var bySym = {};
        stocks.forEach(function (s) { bySym[s.symbol] = s; });

        var out = [];
        Object.keys(SECTORS).forEach(function (sectorName) {
            var members = SECTORS[sectorName];
            var sumPct = 0, count = 0;
            members.forEach(function (sym) {
                var s = bySym[sym];
                if (s && s.ltp > 0) { sumPct += s.change; count++; }
            });
            if (count >= 3) {
                out.push({
                    name:   sectorName,
                    avg:    sumPct / count,
                    count:  count
                });
            }
        });
        return out;
    }

    /* ---------- Render: ticker tape (sector indices) ---------- */
    function renderTicker(sectorIndices) {
        var track = $("tickerTrack");
        if (!track) return;

        if (!sectorIndices.length) {
            track.innerHTML = '<span class="ticker-loading">No sector data available.</span>';
            return;
        }

        var html = sectorIndices.map(function (idx) {
            var cls   = idx.avg >= 0 ? "tk-up" : "tk-down";
            var arrow = idx.avg >= 0 ? "▲"   : "▼";
            return '<span class="ticker-item">' +
                '<span class="tk-sym">' + idx.name + '</span> ' +
                '<span class="tk-count">' + idx.count + ' stocks</span> ' +
                '<span class="' + cls + '">' + arrow + ' ' + pct(idx.avg) + '</span>' +
                '</span>';
        }).join("");
        // Duplicate for seamless infinite scroll
        track.innerHTML = html + html;
    }

    /* ---------- Render: market stats ---------- */
    function renderStats(stocks) {
        var common  = stocks.filter(function (s) { return isCommonStock(s.symbol); });
        var gainers = stocks.filter(function (s) { return s.change > 0; }).length;
        var losers  = stocks.filter(function (s) { return s.change < 0; }).length;

        // Turnover (Rs.) is more meaningful than raw share count — sum of (price × volume)
        var turnover = stocks.reduce(function (a, s) { return a + (s.ltp * s.volume); }, 0);

        animateInt("statCount",   common.length);
        animateInt("statGainers", gainers);
        animateInt("statLosers",  losers);

        var v = $("statVolume");
        if (v) v.textContent = "Rs. " + compact(turnover);

        var hc = $("heroStockCount");
        if (hc) hc.textContent = common.length;
    }

    function animateInt(id, target) {
        var el = $(id);
        if (!el) return;
        var start = parseInt(el.getAttribute("data-val") || "0", 10);
        var t0 = performance.now();
        var dur = 600;
        function step(now) {
            var p = Math.min((now - t0) / dur, 1);
            var eased = 1 - Math.pow(1 - p, 3);
            el.textContent = Math.round(start + (target - start) * eased).toLocaleString();
            if (p < 1) requestAnimationFrame(step);
        }
        el.setAttribute("data-val", target);
        requestAnimationFrame(step);
    }

    /* ---------- Render: top-movers tables ---------- */
    function renderTable(tableId, stocks, valueFn, isPercent) {
        var tbl = $(tableId);
        if (!tbl) return;
        var rows = stocks.map(function (s, i) {
            var changeCls = s.change >= 0 ? "tk-up" : "tk-down";
            var rightVal  = isPercent ? pct(valueFn(s)) : compact(valueFn(s));
            var rightCls  = isPercent ? changeCls : "tk-vol";
            return '<tr>' +
                '<td class="rank-cell">' + (i + 1) + '</td>' +
                '<td>' +
                '<div class="sym-cell">' +
                '<strong>' + s.symbol + '</strong>' +
                '<span class="sym-price">Rs. ' + fmt2(s.ltp) + '</span>' +
                '</div>' +
                '</td>' +
                '<td class="' + rightCls + '">' + rightVal + '</td>' +
                '</tr>';
        }).join("");
        tbl.querySelector("tbody").innerHTML = rows;
    }

    /* ---------- Master render ---------- */
    function renderAll(stocks) {
        stocks = stocks.filter(function (s) { return s.ltp > 0; });

        var common      = stocks.filter(function (s) { return isCommonStock(s.symbol); });
        var byChangeDes = common.slice().sort(function (a, b) { return b.change - a.change; });
        var byChangeAsc = common.slice().sort(function (a, b) { return a.change - b.change; });
        var byVolume    = common.slice().sort(function (a, b) { return b.volume - a.volume; });

        renderTicker(computeSectorIndices(stocks));
        renderStats(stocks);
        renderTable("topGainers", byChangeDes.slice(0, 7), function (s) { return s.change; }, true);
        renderTable("topLosers",  byChangeAsc.slice(0, 7), function (s) { return s.change; }, true);
        renderTable("mostTraded", byVolume.slice(0, 7),    function (s) { return s.volume; }, false);
    }

    /* ---------- Cache layer (localStorage) ---------- */
    function loadCache() {
        try {
            var raw = localStorage.getItem(CACHE_KEY);
            if (!raw) return null;
            var obj = JSON.parse(raw);
            if (!obj || !obj.stocks || !obj.savedAt) return null;
            if ((Date.now() - obj.savedAt) > CACHE_MAX_AGE) return null;
            return obj.stocks;
        } catch (e) { return null; }
    }
    function saveCache(stocks) {
        try {
            localStorage.setItem(CACHE_KEY, JSON.stringify({
                savedAt: Date.now(),
                stocks:  stocks
            }));
        } catch (e) { /* quota exceeded — ignore */ }
    }

    /* ---------- Fetch ---------- */
    function fetchStocks() {
        return fetch(URL_STOCKS, { cache: "no-store" })
            .then(function (r) {
                if (!r.ok) throw new Error("HTTP " + r.status);
                return r.json();
            })
            .then(function (json) {
                var raw = (json && json.data) || [];
                return raw.map(function (s) {
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
            });
    }

    /* ---------- Sentiment (Community Pulse) ---------- */
    function refreshSentiment() {
        fetch(URL_SENTIMENT, { cache: "no-store" })
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (!data) return;
                var bull = +data.bullishPct || 50;
                var bear = +data.bearishPct || 50;
                var total = +data.total || 0;

                var fb = $("pulseBull");  if (fb) fb.style.width = bull + "%";
                var fr = $("pulseBear");  if (fr) fr.style.width = bear + "%";
                var lb = $("pulseBullLbl");
                var ll = $("pulseBearLbl");
                if (lb) lb.textContent = bull.toFixed(0) + "% Bullish";
                if (ll) ll.textContent = bear.toFixed(0) + "% Bearish";
                var meta = $("pulseMeta");
                if (meta) {
                    meta.textContent = total === 0
                        ? "Be the first to cast your sentiment on a stock."
                        : "Based on " + total.toLocaleString() + " community vote" +
                        (total === 1 ? "" : "s") + " across all NEPSE stocks.";
                }
            })
            .catch(function () { /* silent */ });
    }

    /* ---------- Boot ---------- */
    var cached = loadCache();
    if (cached) renderAll(cached);   // instant paint — no flash

    function refreshAll() {
        fetchStocks()
            .then(function (stocks) {
                if (!stocks || !stocks.length) return;
                saveCache(stocks);
                renderAll(stocks);
            })
            .catch(function (err) {
                console.warn("[NepseInsider] live feed:", err.message);
                if (!cached) {
                    // First visit AND fetch failed → tell the user
                    var t = $("tickerTrack");
                    if (t) t.innerHTML = '<span class="ticker-loading">' +
                        '⚠️ Live feed unavailable. Try again shortly.</span>';
                }
            });
        refreshSentiment();
    }

    refreshAll();
    setInterval(refreshAll, REFRESH_MS);
})();
