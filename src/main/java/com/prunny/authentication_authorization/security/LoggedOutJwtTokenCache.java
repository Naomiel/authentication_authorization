package com.prunny.authentication_authorization.security;

import com.accelerex.tasks_manager.eventListeners.OnUserLogoutSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoggedOutJwtTokenCache {
    private static final Logger logger = LoggerFactory.getLogger(LoggedOutJwtTokenCache.class);

    private final ExpiringMap<String, OnUserLogoutSuccessEvent> tokenEventMap;

    @Lazy
    private final JWTUtil jwtUtil;

    @Autowired
    public LoggedOutJwtTokenCache(JWTUtil tokenProvider) {
        this.jwtUtil = tokenProvider;
        this.tokenEventMap = ExpiringMap.builder().variableExpiration().build();
    }

    public void markLogoutEventForToken(OnUserLogoutSuccessEvent event) {
        String token = event.getToken();
        if (tokenEventMap.containsKey(token)) {
            logger.info(String.format("Log out token for user [%s] is already present in the cache", event.getUserEmail()));
        } else {
            Date tokenExpiryDate = jwtUtil.extractExpiration(token);
            long ttlForToken = getTTLForToken(tokenExpiryDate);
            logger.info(String.format("Logout token cache set for [%s] with a TTL of [%s] seconds. Token is due expiry at [%s]", event.getUserEmail(), ttlForToken, tokenExpiryDate));
            tokenEventMap.put(token, event, ttlForToken, TimeUnit.SECONDS);
        }
    }

    public OnUserLogoutSuccessEvent getLogoutEventForToken(String token) {
        return tokenEventMap.get(token);
    }

    private long getTTLForToken(Date date) {
        long secondAtExpiry = date.toInstant().getEpochSecond();
        long secondAtLogout = Instant.now().getEpochSecond();
        return Math.max(0, secondAtExpiry - secondAtLogout);
    }
}

