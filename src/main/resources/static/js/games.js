function shuffle(list) {
    const a = [...list];
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}
const canvas = () => bootstrap.Offcanvas.getOrCreateInstance($("gameCanvas"));

async function loadGames() {
    try {
        state.games = shuffle(await api("/api/games"));
        renderGames();
    } catch (e) {
        toast(e.message);
    }
}

function renderGames() {
    const list = state.games.filter(g => !$("kidOnly").checked || g.kidFriendly);
    $("grid").innerHTML = list.length ? list.map(g => `
    <div class="col">
      <div class="card game-card h-100 overflow-hidden" data-id="${g.gameId}" tabindex="0" role="button" aria-label="Open ${esc(g.title)}">
        <div class="cover" style="${coverStyle(g)}">${kidBadge(g)}</div>
        <div class="card-body">
          <h3 class="h6 card-title mb-1">${esc(g.title)}</h3>
          <p class="card-text small text-body-secondary mb-0">${esc(g.genre)}${g.releaseDate ? ", " + g.releaseDate.slice(0, 4) : ""}</p>
        </div>
      </div>
    </div>`).join("") : "<p class='text-body-secondary'>No games match this filter yet.</p>";

    $("grid").querySelectorAll(".game-card").forEach(c => {
        c.onclick = () => openGame(+c.dataset.id);
        c.onkeydown = e => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); openGame(+c.dataset.id); } };
    });
}
$("kidOnly").onchange = renderGames;

function openGame(id) {
    const g = state.games.find(x => x.gameId === id);
    state.gameId = id;
    $("gTitle").textContent = g.title;
    $("gCover").style.cssText = coverStyle(g);
    $("gCover").innerHTML = kidBadge(g);
    $("gMeta").textContent = g.genre + (g.releaseDate ? ", released " + fmt(g.releaseDate) : "");
    $("gDesc").textContent = g.description || "";
    $("playResult").innerHTML = "";
    $("rtext").value = ""; $("rtext").placeholder = "What did you think of " + g.title + "?";
    loadRevs();
    canvas().show();
}

$("play").onclick = async () => {
    const out = $("playResult");
    try {
        const r = await api(`/api/games/${state.gameId}/play`, "POST");
        out.innerHTML = `<div class="alert alert-success mb-0"><strong>${esc(r.message)}</strong><br>Have fun!</div>`;
    } catch (e) {
        out.innerHTML = `<div class="alert alert-danger mb-0">${esc(e.message)}
      ${e.status === 403 ? `<div class="mt-2"><button class="btn btn-sm btn-primary" id="goSub" type="button">View plans</button></div>` : ""}</div>`;
        const go = $("goSub");
        if (go) go.onclick = () => { canvas().hide(); show("sub"); loadSub(); };
    }
};

async function loadRevs() {
    const revs = $("revs");
    try {
        const list = await api(`/api/games/${state.gameId}/reviews`);
        revs.innerHTML = list.length ? list.map(r =>
                `<div class="list-group-item px-0"><strong>${esc(r.reviewer)}</strong> <small class="text-body-secondary ms-1">${fmt(r.dateOfComment)}</small><p class="mb-0">${esc(r.comment)}</p></div>`).join("")
            : "<p class='text-body-secondary'>No reviews yet. Be the first to write one.</p>";
    } catch (e) { revs.innerHTML = `<div class="text-danger">${esc(e.message)}</div>`; }
}

$("post").onclick = async () => {
    try {
        await api(`/api/games/${state.gameId}/reviews`, "POST", { comment: $("rtext").value });
        $("rtext").value = ""; toast("Review posted"); loadRevs();
    } catch (e) { toast(e.message); }
};