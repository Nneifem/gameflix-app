package com.GameFlix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.GameFlix.model.Subscription;
import java.time.LocalDate;
import com.GameFlix.model.User;
import com.GameFlix.repository.SubscriptionRepository;

@Service
public class SubscriptionService {
    // list of the different subscription plan
    private static final List<String> plans = List.of("BASIC", "STANDARD", "PREMIUM");

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Optional<Subscription> find(User user) {
        return subscriptionRepository.findByUserUserId(user.getUserId());
    }

    // will be true if the status is active and not past the end date
    public boolean hasActive(User user) {
        return find(user)
                .map(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()) && s.getEndDate() != null && !s.getEndDate().isBefore(LocalDate.now()))
                .orElse(false);
    }

    public Subscription subscribe(User user, String plan) {
        String p = validatePlan(plan);
        Subscription sub = find(user).orElseGet(() -> {
            Subscription s = new Subscription();
            s.setUser(user);
            return s;
        });
        if (hasActive(user) ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have an active subscription");
        }
        sub.setStatus("ACTIVE");
        sub.setPaymentPlan(p);
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusMonths(1));
        return subscriptionRepository.save(sub);
    }

    public Subscription upgrade(User user, String plan) {
        String p = validatePlan(plan);
        Subscription sub = activeOrThrow(user, "upgrade");
        if (plans.indexOf(p) <= plans.indexOf(sub.getPaymentPlan())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upgrade must be a higher plan");
        }
        sub.setPaymentPlan(p);
        return subscriptionRepository.save(sub);
    }

    public Subscription downgrade(User user, String plan) {
        String p = validatePlan(plan);
        Subscription sub = activeOrThrow(user, "downgrade");
        if (plans.indexOf(p) >= plans.indexOf(sub.getPaymentPlan())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upgrade must be a lower plan");
        }
        sub.setPaymentPlan(p);
        return subscriptionRepository.save(sub);
    }

    public Subscription cancel(User user) {
        Subscription sub = activeOrThrow(user, "cancel");
        sub.setStatus("CANCELLED");
        sub.setEndDate(LocalDate.now());
        return subscriptionRepository.save(sub);
    }

    private Subscription activeOrThrow(User user, String action) {
        return find(user)
                .filter(s -> hasActive(user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No active subscription to " + action));
    }

    private String validatePlan(String plan) {
        String p = plan == null ? "" : plan.trim().toUpperCase();
        if (!plans.contains(p)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment plan must be one of these " + plans);
        }
        return p;
    }
}
