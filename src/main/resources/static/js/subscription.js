// homepage.html only: the Subscription tab (current plan + change/cancel).

async function loadSub() {
    const box = $("subStatus");
    box.innerHTML = `<span class="text-body-secondary">Loading your subscription…</span>`;
    try {
        state.sub = await api("/api/subscription");
        renderSub();
    } catch (e) {
        box.innerHTML = `<span class="text-danger">${esc(e.message)}</span>`;
    }
}

function renderSub() {
    const s = state.sub || { status: "NONE" };
    const status = String(s.status || "NONE").toUpperCase();
    const active = status === "ACTIVE";
    const planId = String(s.paymentPlan || "").toUpperCase();
    const planName = planId ? planId[0] + planId.slice(1).toLowerCase() : "";

    if (active) {
        $("subStatus").innerHTML = `
            <div>
                <div class="text-body-secondary small">Current plan</div>
                <div class="fs-3 fw-bold">${esc(planName)} <span class="badge text-bg-success fs-6 align-middle">Active</span></div>
                <div class="text-body-secondary">Started ${fmt(s.startDate)}, renews through ${fmt(s.endDate)}</div>
            </div>
            <button class="btn btn-outline-danger" id="cancel" type="button">Cancel subscription</button>`;
    } else {
        const was = s.status?.toUpperCase() === "CANCELLED"
            ? `Your ${esc(planName)} plan was cancelled on ${fmt(s.endDate)}.`
            : "You don’t have a plan yet.";
        $("subStatus").innerHTML = `
            <div>
                <div class="text-body-secondary small">Current plan</div>
                <div class="fs-3 fw-bold">None <span class="badge text-bg-danger fs-6 align-middle">${s.status?.toUpperCase() === "CANCELLED" ? "Cancelled" : "No plan"}</span></div>
                <div class="text-body-secondary">${was}</div>
            </div>`;
    }

    const heading = $("planHeading");
    if (heading) heading.textContent = active ? "Change your plan" : "Choose a plan";

    // Upgrade / Downgrade / Subscribe buttons; re-render the page section when done
    renderPlans($("plans"), s, renderSub);

    const c = $("cancel");
    if (c) c.onclick = async () => {
        if (!confirm("Cancel your subscription? You won’t be able to play until you subscribe again.")) return;
        try {
            state.sub = await api("/api/subscription/cancel", "POST");
            toast("Subscription cancelled");
            renderSub();
        } catch (e) { toast(e.message); }
    };
}