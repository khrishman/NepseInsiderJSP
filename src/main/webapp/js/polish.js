/* =====================================================================
   NepseInsider — Visual Polish (JavaScript layer)
   ---------------------------------------------------------------------
   Adds two effects that pure CSS can't do:
     1. Count-up animation on number values
     2. A pulse on the sentiment widget right after a vote lands

   Self-contained. Safe to remove — nothing else depends on it.
   Load this AFTER the page's own scripts, at the end of header.jsp or
   footer.jsp.
   ===================================================================== */
(function () {
    "use strict";

    /* Respect reduced-motion users — skip all JS animation. */
    var reduceMotion = window.matchMedia &&
        window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    /* -----------------------------------------------------------------
       1. COUNT-UP for numeric values
       -----------------------------------------------------------------
       Watches elements with class .np-countup (or the known stat IDs)
       and animates from 0 to their text value the first time they get
       a real number. Re-animates when the value changes.
    ----------------------------------------------------------------- */
    function parseNumeric(text) {
        if (!text) return null;
        // Strip everything except digits, minus, dot
        var cleaned = String(text).replace(/[^0-9.\-]/g, "");
        if (cleaned === "" || cleaned === "-" || cleaned === ".") return null;
        var n = parseFloat(cleaned);
        return isNaN(n) ? null : n;
    }

    function formatLike(original, value) {
        // Preserve a trailing % if the original had one
        var suffix = /%\s*$/.test(original) ? "%" : "";
        // Preserve leading non-numeric (like "Rs. ")
        var prefixMatch = original.match(/^[^\d\-]*/);
        var prefix = prefixMatch ? prefixMatch[0] : "";
        // If original had decimals, keep one decimal place
        var hasDecimal = /\.\d/.test(original);
        var shown = hasDecimal ? value.toFixed(1)
                               : Math.round(value).toLocaleString();
        return prefix + shown + suffix;
    }

    function countUp(el, target, original) {
        if (reduceMotion) { el.textContent = original; return; }
        var start = 0;
        var t0 = performance.now();
        var dur = 650;
        el.classList.add("np-counting");

        function frame(now) {
            var p = Math.min((now - t0) / dur, 1);
            var eased = 1 - Math.pow(1 - p, 3);   // ease-out cubic
            el.textContent = formatLike(original, target * eased);
            if (p < 1) {
                requestAnimationFrame(frame);
            } else {
                el.textContent = original;        // snap to exact original
                el.classList.remove("np-counting");
            }
        }
        requestAnimationFrame(frame);
    }

    /* Track last-seen value per element so we only animate on change. */
    var seen = new WeakMap();

    function maybeAnimate(el) {
        if (!el) return;
        var text = el.textContent.trim();
        var num  = parseNumeric(text);
        if (num === null) return;                 // not a number yet (e.g. "—")

        var prev = seen.get(el);
        if (prev === text) return;                // unchanged, skip
        seen.set(el, text);

        // Only count up if it's a meaningfully different value
        countUp(el, num, text);
    }

    /* Elements we want to count-up: known stat IDs + any .np-countup */
    function collectTargets() {
        var ids = ["statCount", "statGainers", "statLosers",
                   "heroStockCount"];
        var list = [];
        ids.forEach(function (id) {
            var el = document.getElementById(id);
            if (el) list.push(el);
        });
        var extra = document.querySelectorAll(".np-countup");
        for (var i = 0; i < extra.length; i++) list.push(extra[i]);
        return list;
    }

    /* Poll for changes — the live data scripts update these async,
       so we check a few times after load rather than once. */
    function watchCountUps() {
        var targets = collectTargets();
        if (!targets.length) return;

        targets.forEach(maybeAnimate);

        // Re-check periodically for the first ~10s (covers the async
        // API fetch populating values), then back off.
        var checks = 0;
        var iv = setInterval(function () {
            collectTargets().forEach(maybeAnimate);
            if (++checks > 20) clearInterval(iv);   // 20 × 500ms = 10s
        }, 500);
    }

    /* -----------------------------------------------------------------
       2. VOTE PULSE
       -----------------------------------------------------------------
       When the user clicks a vote button, briefly add .np-just-voted
       to the .vote-widget so the CSS pulse fires.
    ----------------------------------------------------------------- */
    function wireVotePulse() {
        var widget = document.querySelector(".vote-widget");
        if (!widget) return;

        var buttons = widget.querySelectorAll(".vote-btn");
        for (var i = 0; i < buttons.length; i++) {
            buttons[i].addEventListener("click", function () {
                widget.classList.remove("np-just-voted");
                // force reflow so the animation can restart
                void widget.offsetWidth;
                widget.classList.add("np-just-voted");
                setTimeout(function () {
                    widget.classList.remove("np-just-voted");
                }, 750);
            });
        }
    }

    /* -----------------------------------------------------------------
       Boot
    ----------------------------------------------------------------- */
    function init() {
        watchCountUps();
        wireVotePulse();
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
