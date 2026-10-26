package com.GameFlix.controller;

import com.GameFlix.model.Subscription;
import com.GameFlix.model.User;
import com.GameFlix.service.CurrentUser;
import com.GameFlix.service.SubscriptionService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final CurrentUser currentUser;

    public SubscriptionController(SubscriptionService subscriptionService, CurrentUser currentUser) {
        this.subscriptionService = subscriptionService;
        this.currentUser = currentUser;
    }

    // GET /api/subscription
    @GetMapping
    public Map<String, Object> currentSubscription(Authentication auth) {
        User user = currentUser.from(auth);
        return subscriptionService.find(user).map(this::toMap)
                .orElseGet(() -> new LinkedHashMap<>(Map.of("status", "NONE")));
    }

    // POST /subscribe
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(Authentication auth, @RequestBody Map<String, String> body) {
        Subscription sub = subscriptionService.subscribe(currentUser.from(auth), body.get("paymentPlan"));
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(sub));
    }

    // POST /upgrade
    @PostMapping("/upgrade")
    public Map<String, Object> upgrade(Authentication auth, @RequestBody Map<String, String> body) {
        return toMap(subscriptionService.upgrade(currentUser.from(auth), body.get("paymentPlan")));
    }

    // POST /downgrade
    @PostMapping("/downgrade")
    public Map<String, Object> downgrade(Authentication auth, @RequestBody Map<String, String> body) {
        return toMap(subscriptionService.downgrade(currentUser.from(auth), body.get("paymentPlan")));
    }

    // POST /cancel
    @PostMapping("/cancel")
    public Map<String, Object> cancel(Authentication auth) {
        return toMap(subscriptionService.cancel(currentUser.from(auth)));
    }

    private Map<String, Object> toMap(Subscription s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("subscriptionId", s.getSubscriptionId());
        m.put("status", s.getStatus());
        m.put("paymentPlan", s.getPaymentPlan());
        m.put("startDate", s.getStartDate());
        m.put("endDate", s.getEndDate());
        return m;
    }
}