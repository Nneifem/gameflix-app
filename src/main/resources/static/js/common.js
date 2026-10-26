// Empty = same server as the page (the site is served by Spring Boot).
const API = "";

const PLANS = [
    { id: "BASIC",    perks: ["Full game catalog", "Play on one screen"] },
    { id: "STANDARD", perks: ["Full game catalog", "Play on two screens"] },
    { id: "PREMIUM",  perks: ["Full game catalog", "Play on four screens", "Early access to new releases"] }
];

const $ = id => document.getElementById(id);

const state = {
    token: localStorage.getItem("gf_token") || null,
    me: null, games: [], sub: null, mode: "login", gameId: null
};

// signing out (or an expired session) always lands on the sign-in page
function logout() {
    localStorage.removeItem("gf_token");
    location.href = "index.html";
}

// calls the backend, adds the JWT, and turns errors into Error(message)
async function api(path, method = "GET", body) {
    const headers = {};
    if (body) headers["Content-Type"] = "application/json";
    if (state.token) headers["Authorization"] = "Bearer " + state.token;
    let res;
    try {
        res = await fetch(API + path, { method, headers, body: body ? JSON.stringify(body) : undefined });
    } catch (e) {
        throw new Error("Can’t reach the server. Is the Spring Boot app running?");
    }
    let data = null;
    try { data = await res.json(); } catch (e) {}
    if (res.status === 401 && state.token && path !== "/api/auth/login" && path !== "/api/auth/register") {
        logout();
        throw new Error("Your session expired. Please sign in again.");
    }    if (!res.ok) {
        const err = new Error((data && data.message) || "Something went wrong (" + res.status + ")");
        err.status = res.status;
        throw err;
    }
    return data;
}

function toast(msg) {
    $("toastMsg").textContent = msg;
    bootstrap.Toast.getOrCreateInstance($("toast"), { delay: 2800 }).show();
}

const esc = s => String(s ?? "").replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

function hue(s) { let h = 0; for (const c of s) h = (h * 31 + c.charCodeAt(0)) % 360; return h; }
function coverStyle(g) { const h = hue(g.title); return `background:linear-gradient(135deg,hsl(${h} 70% 45%),hsl(${(h + 50) % 360} 70% 28%))`; }
const kidBadge = g => g.kidFriendly ? `<span class="badge text-bg-warning align-self-end">Kid-friendly</span>` : "";

function fmt(d) {
    return d ? new Date(d + "T00:00:00").toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" }) : "";
}

// Draws the three plan cards into `el`. `sub` is the current subscription (or null).
// `onDone` runs after a successful subscribe / upgrade / downgrade.
function renderPlans(el, sub, onDone) {
    const active = !!sub && sub.status === "ACTIVE";
    const curIdx = active ? PLANS.findIndex(p => p.id === sub.paymentPlan) : -1;

    el.innerHTML = PLANS.map((p, i) => {
        const cur = active && i === curIdx;
        const label = !active ? "Subscribe" : cur ? "Current plan" : i > curIdx ? "Upgrade" : "Downgrade";
        return `<div class="col"><div class="card h-100 ${cur ? "border-success border-2" : ""}">
      <div class="card-body d-flex flex-column">
        <h3 class="card-title">${p.id[0] + p.id.slice(1).toLowerCase()}</h3>
        <ul class="text-body-secondary ps-3 flex-grow-1">${p.perks.map(x => `<li>${esc(x)}</li>`).join("")}</ul>
        <button class="btn ${cur ? "btn-outline-secondary" : "btn-primary"}" data-plan="${p.id}" data-act="${label}" type="button" ${cur ? "disabled" : ""}>${label}</button>
      </div></div></div>`;
    }).join("");

    el.querySelectorAll("button[data-plan]").forEach(b => b.onclick = async () => {
        const path = { Subscribe: "subscribe", Upgrade: "upgrade", Downgrade: "downgrade" }[b.dataset.act];
        try {
            state.sub = await api("/api/subscription/" + path, "POST", { paymentPlan: b.dataset.plan });
            toast(b.dataset.act === "Subscribe" ? "You’re subscribed" : b.dataset.act === "Upgrade" ? "Plan upgraded" : "Plan downgraded");
            onDone();
        } catch (e) {
            toast(e.message);
            if (e.status === 409) {
                // the page was out of date: load the real subscription and redraw
                try { state.sub = await api("/api/subscription"); onDone(); } catch (_) {}
            }
        }
    });
}