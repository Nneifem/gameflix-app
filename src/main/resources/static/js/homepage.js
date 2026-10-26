// home.html only: tabs, sign out, and startup.

function show(v) {
    $("vGames").classList.toggle("d-none", v !== "games");
    $("vSub").classList.toggle("d-none", v !== "sub");
    $("navGames").classList.toggle("active", v === "games");
    $("navSub").classList.toggle("active", v === "sub");
}
$("navGames").onclick = () => show("games");
$("navSub").onclick = () => { show("sub"); loadSub(); };
$("logout").onclick = logout;

async function init() {
    // not signed in? back to the sign-in page
    if (!state.token) { location.replace("index.html"); return; }
    try {
        state.me = await api("/api/users/me");
    } catch (e) { logout(); return; }
    $("who").textContent = state.me.firstName || state.me.email;
    show("games");
    await Promise.all([loadGames(), loadSub()]);
}
init();