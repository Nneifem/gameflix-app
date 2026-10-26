// index.html only: sign in, create account, pick a plan, then go to the homepage.

// already signed in? skip this page
if (state.token) location.replace("homepage.html");

function gotoHomepage() {
    location.href = "homepage.html";
}

function setMode(m) {
    state.mode = m;
    const reg = m === "register";
    $("tabLogin").classList.toggle("active", !reg);
    $("tabReg").classList.toggle("active", reg);
    $("flName").classList.toggle("d-none", !reg);
    $("userName").classList.toggle("d-none", !reg);
    $("authGo").textContent = reg ? "Create Account" : "Sign In";
    $("authError").textContent = "";
}
$("tabLogin").onclick = () => setMode("login");
$("tabReg").onclick = () => setMode("register");

$("authGo").onclick = async () => {
    const email = $("em").value.trim();
    const password = $("pw").value;
    const isNew = state.mode === "register";

    state.token = null;
    localStorage.removeItem("gf_token");
    $("authError").textContent = "";
    try {
        if (isNew) {
            await api("/api/auth/register", "POST", {
                firstName: $("fn").value.trim(), lastName: $("ln").value.trim(),
                username: $("un").value.trim(), email, password
            });
        }
        const r = await api("/api/auth/login", "POST", { email: email.toLowerCase(), password });
        state.token = r.token;
        localStorage.setItem("gf_token", r.token);

        if (isNew) showSubscriptionPlan();   // new account: choose a plan first
        else gotoHomepage();                 // returning user: straight to the homepage
    } catch (e) {
        $("authError").textContent = e.message;
    }
};

["em", "pw"].forEach(id => $(id).addEventListener("keydown", e => {
    if (e.key === "Enter") $("authGo").click();
}));

// shown once, right after creating an account
function showSubscriptionPlan() {
    $("auth").classList.add("d-none");
    $("picker").classList.remove("d-none");
    renderPlans($("pickPlans"), null, gotoHomepage);   // subscribing goes to the homepage
}
$("skipPlan").onclick = gotoHomepage;