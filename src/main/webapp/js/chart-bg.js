/* =====================================================================
   NepseInsider — Premium Animated Background
   ---------------------------------------------------------------------
   A multi-layer canvas animation built to look like a live trading
   dashboard. Pure JS, no libraries. Layers:
     1. Soft candlestick chart (deep background, blurred)
     2. Three flowing line charts (foreground)
     3. Glowing endpoint dots ("live" indicators)
     4. Floating ticker particles (rising stock symbols)
   ===================================================================== */
(function () {
    "use strict";

    var canvas = document.getElementById("chartBg");
    if (!canvas || !canvas.getContext) return;

    var ctx = canvas.getContext("2d");
    var dpr = Math.min(window.devicePixelRatio || 1, 2);
    var W = 0, H = 0;

    /* ---------- Configuration ---------- */
    var LINE_POINTS    = 100;
    var SCROLL_SPEED   = 0.45;
    var CANDLE_COUNT   = 36;
    var PARTICLE_COUNT = 18;

    var SYMBOLS = ["NABIL","NIMB","ADBL","API","NLIC","CHCL","UPPER","NTC",
        "NICA","HBL","SBI","EBL","KBL","SCB","NRIC","AHPC","BARUN"];

    /* ---------- State ---------- */
    var lines = [
        // RGB, weight, base, current points
        { color: [46,  204, 113], weight: 2.4, base: 0.50, points: [], glow: 18 },
        { color: [255, 215, 0],   weight: 1.8, base: 0.58, points: [], glow: 14 },
        { color: [88,  166, 255], weight: 1.6, base: 0.42, points: [], glow: 12 }
    ];

    var candles   = [];
    var particles = [];

    /* ---------- Setup ---------- */
    function resize() {
        W = canvas.offsetWidth  * dpr;
        H = canvas.offsetHeight * dpr;
        canvas.width  = W;
        canvas.height = H;
    }

    function seedLines() {
        lines.forEach(function (line) {
            line.points = [];
            var v = line.base;
            for (var i = 0; i < LINE_POINTS; i++) {
                v += (Math.random() - 0.5) * 0.05;
                v  = Math.max(0.18, Math.min(0.82, v));
                line.points.push(v);
            }
        });
    }

    function seedCandles() {
        candles = [];
        for (var i = 0; i < CANDLE_COUNT; i++) {
            var open  = 0.40 + Math.random() * 0.25;
            var close = open + (Math.random() - 0.5) * 0.18;
            var high  = Math.max(open, close) + Math.random() * 0.08;
            var low   = Math.min(open, close) - Math.random() * 0.08;
            candles.push({ open: open, close: close, high: high, low: low });
        }
    }

    function seedParticles() {
        particles = [];
        for (var i = 0; i < PARTICLE_COUNT; i++) {
            particles.push(spawnParticle(Math.random()));
        }
    }

    function spawnParticle(initialProgress) {
        var symbol = SYMBOLS[Math.floor(Math.random() * SYMBOLS.length)];
        var positive = Math.random() > 0.4;
        return {
            x:        Math.random() * W,
            y:        H + Math.random() * H * 0.5,
            vy:       0.25 + Math.random() * 0.35,
            life:     initialProgress || 0,
            symbol:   symbol,
            change:   (Math.random() * 5).toFixed(2),
            positive: positive,
            size:     11 + Math.random() * 5
        };
    }

    /* ---------- Drawing: grid ---------- */
    function drawGrid(scrollX, segW) {
        ctx.strokeStyle = "rgba(255,255,255,0.04)";
        ctx.lineWidth   = 1;

        for (var i = 1; i < 10; i++) {
            var y = (H / 10) * i;
            ctx.beginPath();
            ctx.moveTo(0, y); ctx.lineTo(W, y);
            ctx.stroke();
        }
        var step   = segW * 6;
        var offset = -(scrollX % step);
        for (var x = offset; x < W; x += step) {
            ctx.beginPath();
            ctx.moveTo(x, 0); ctx.lineTo(x, H);
            ctx.stroke();
        }
    }

    /* ---------- Drawing: candlesticks (blurred deep background) ---------- */
    function drawCandles(scrollX) {
        var candleW = W / CANDLE_COUNT;
        var bodyW   = candleW * 0.55;

        ctx.save();
        ctx.globalAlpha = 0.22;

        for (var i = 0; i < candles.length; i++) {
            var c  = candles[i];
            var cx = i * candleW + candleW / 2 - (scrollX * 0.3) % W;
            if (cx < -candleW) cx += W;

            var openY  = H - c.open  * H;
            var closeY = H - c.close * H;
            var highY  = H - c.high  * H;
            var lowY   = H - c.low   * H;

            var rising = c.close >= c.open;
            ctx.strokeStyle = rising ? "rgba(46,204,113,0.6)" : "rgba(231,76,60,0.6)";
            ctx.fillStyle   = rising ? "rgba(46,204,113,0.4)" : "rgba(231,76,60,0.4)";
            ctx.lineWidth   = 1.5 * dpr;

            // Wick
            ctx.beginPath();
            ctx.moveTo(cx, highY); ctx.lineTo(cx, lowY);
            ctx.stroke();

            // Body
            var bodyTop = Math.min(openY, closeY);
            var bodyH   = Math.max(2, Math.abs(closeY - openY));
            ctx.fillRect(cx - bodyW / 2, bodyTop, bodyW, bodyH);
        }

        ctx.restore();
    }

    /* ---------- Drawing: line + glow ---------- */
    function drawLine(line, scrollX, segW) {
        var pts  = line.points;
        var rgba = function (a) { return "rgba(" + line.color.join(",") + "," + a + ")"; };

        // Filled gradient area
        ctx.beginPath();
        for (var i = 0; i < pts.length; i++) {
            var x = i * segW - scrollX;
            var y = H - pts[i] * H;
            if (i === 0) ctx.moveTo(x, y);
            else         ctx.lineTo(x, y);
        }
        ctx.lineTo(W, H); ctx.lineTo(-segW, H); ctx.closePath();

        var grad = ctx.createLinearGradient(0, H * 0.3, 0, H);
        grad.addColorStop(0, rgba(0.22));
        grad.addColorStop(1, rgba(0));
        ctx.fillStyle = grad;
        ctx.fill();

        // Glowing line stroke
        ctx.save();
        ctx.shadowColor   = rgba(0.85);
        ctx.shadowBlur    = line.glow * dpr;
        ctx.strokeStyle   = rgba(0.9);
        ctx.lineWidth     = line.weight * dpr;
        ctx.lineJoin      = "round";
        ctx.lineCap       = "round";

        ctx.beginPath();
        for (var j = 0; j < pts.length; j++) {
            var sx = j * segW - scrollX;
            var sy = H - pts[j] * H;
            if (j === 0) ctx.moveTo(sx, sy);
            else         ctx.lineTo(sx, sy);
        }
        ctx.stroke();
        ctx.restore();

        // Glowing endpoint dot ("live" pulse)
        var lastX = (pts.length - 1) * segW - scrollX;
        var lastY = H - pts[pts.length - 1] * H;
        ctx.save();
        ctx.shadowColor = rgba(1);
        ctx.shadowBlur  = 25 * dpr;
        ctx.fillStyle   = rgba(1);
        ctx.beginPath();
        ctx.arc(lastX, lastY, 3.5 * dpr, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();
    }

    /* ---------- Drawing: floating ticker particles ---------- */
    function drawParticles() {
        ctx.save();
        ctx.font = (12 * dpr) + "px 'Segoe UI', sans-serif";
        ctx.textAlign = "center";

        for (var i = 0; i < particles.length; i++) {
            var p = particles[i];
            p.y    -= p.vy * dpr;
            p.life += 0.004;

            // Fade in then fade out
            var alpha = p.life < 0.15
                ? p.life / 0.15
                : (p.life > 0.85 ? (1 - p.life) / 0.15 : 1);
            alpha = Math.max(0, Math.min(1, alpha)) * 0.55;

            var color = p.positive ? "46,204,113" : "231,76,60";
            ctx.fillStyle = "rgba(" + color + "," + alpha + ")";
            ctx.fillText(
                p.symbol + " " + (p.positive ? "+" : "-") + p.change + "%",
                p.x,
                p.y
            );

            if (p.life >= 1 || p.y < -50) {
                particles[i] = spawnParticle(0);
                particles[i].y = H + 20;
            }
        }
        ctx.restore();
    }

    /* ---------- Animation loop ---------- */
    var scrollX = 0;

    function tick() {
        var segW = W / (LINE_POINTS - 1);
        scrollX += SCROLL_SPEED * dpr;

        if (scrollX >= segW) {
            scrollX -= segW;
            lines.forEach(function (line) {
                line.points.shift();
                var last = line.points[line.points.length - 1];
                var next = last + (Math.random() - 0.5) * 0.06;
                next = Math.max(0.18, Math.min(0.85, next));
                line.points.push(next);
            });
        }

        ctx.clearRect(0, 0, W, H);

        drawGrid(scrollX, segW);
        drawCandles(scrollX);
        lines.forEach(function (line) { drawLine(line, scrollX, segW); });
        drawParticles();

        requestAnimationFrame(tick);
    }

    /* ---------- Boot ---------- */
    resize();
    seedLines();
    seedCandles();
    seedParticles();

    var rt = null;
    window.addEventListener("resize", function () {
        clearTimeout(rt);
        rt = setTimeout(function () { resize(); seedCandles(); }, 150);
    });

    requestAnimationFrame(tick);
})();
