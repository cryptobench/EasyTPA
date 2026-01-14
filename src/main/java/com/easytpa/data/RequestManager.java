package com.easytpa.data;

import com.easytpa.EasyTPA;

import java.util.*;
import java.util.concurrent.*;

public class RequestManager {

    private final EasyTPA plugin;
    private final Map<UUID, List<TpaRequest>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, TpaRequest> outgoingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Set<UUID> tpaDisabled = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService cleanupExecutor;

    public RequestManager(EasyTPA plugin) {
        this.plugin = plugin;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpired, 10, 10, TimeUnit.SECONDS);
    }

    public void shutdown() {
        cleanupExecutor.shutdownNow();
        pendingRequests.clear();
        outgoingRequests.clear();
        cooldowns.clear();
    }

    public void createRequest(TpaRequest request) {
        UUID senderId = request.getSenderId();
        UUID targetId = request.getTargetId();

        cancelOutgoingRequest(senderId);
        pendingRequests.computeIfAbsent(targetId, k -> new CopyOnWriteArrayList<>()).add(request);
        outgoingRequests.put(senderId, request);

        int cooldownSeconds = plugin.getConfig().getCooldownSeconds();
        if (cooldownSeconds > 0) {
            cooldowns.put(senderId, System.currentTimeMillis() + cooldownSeconds * 1000L);
        }
    }

    public TpaRequest getLatestRequest(UUID targetId) {
        List<TpaRequest> requests = pendingRequests.get(targetId);
        if (requests == null || requests.isEmpty()) {
            return null;
        }

        int timeout = plugin.getConfig().getRequestTimeoutSeconds();
        for (int i = requests.size() - 1; i >= 0; i--) {
            TpaRequest request = requests.get(i);
            if (!request.isExpired(timeout)) {
                return request;
            }
        }
        return null;
    }

    public void removeRequest(TpaRequest request) {
        UUID targetId = request.getTargetId();
        UUID senderId = request.getSenderId();

        List<TpaRequest> requests = pendingRequests.get(targetId);
        if (requests != null) {
            requests.remove(request);
            if (requests.isEmpty()) {
                pendingRequests.remove(targetId);
            }
        }
        outgoingRequests.remove(senderId);
    }

    public TpaRequest cancelOutgoingRequest(UUID senderId) {
        TpaRequest request = outgoingRequests.remove(senderId);
        if (request != null) {
            List<TpaRequest> requests = pendingRequests.get(request.getTargetId());
            if (requests != null) {
                requests.remove(request);
                if (requests.isEmpty()) {
                    pendingRequests.remove(request.getTargetId());
                }
            }
        }
        return request;
    }

    public TpaRequest getOutgoingRequest(UUID senderId) {
        TpaRequest request = outgoingRequests.get(senderId);
        if (request != null && request.isExpired(plugin.getConfig().getRequestTimeoutSeconds())) {
            outgoingRequests.remove(senderId);
            return null;
        }
        return request;
    }

    public boolean isOnCooldown(UUID playerId) {
        Long cooldownEnd = cooldowns.get(playerId);
        if (cooldownEnd == null) {
            return false;
        }
        if (System.currentTimeMillis() >= cooldownEnd) {
            cooldowns.remove(playerId);
            return false;
        }
        return true;
    }

    public int getRemainingCooldown(UUID playerId) {
        Long cooldownEnd = cooldowns.get(playerId);
        if (cooldownEnd == null) {
            return 0;
        }
        long remaining = cooldownEnd - System.currentTimeMillis();
        return remaining > 0 ? (int) Math.ceil(remaining / 1000.0) : 0;
    }

    public boolean isTpaDisabled(UUID playerId) {
        return tpaDisabled.contains(playerId);
    }

    public boolean toggleTpa(UUID playerId) {
        if (tpaDisabled.contains(playerId)) {
            tpaDisabled.remove(playerId);
            return true;
        } else {
            tpaDisabled.add(playerId);
            return false;
        }
    }

    private void cleanupExpired() {
        int timeout = plugin.getConfig().getRequestTimeoutSeconds();

        for (Map.Entry<UUID, List<TpaRequest>> entry : pendingRequests.entrySet()) {
            entry.getValue().removeIf(request -> request.isExpired(timeout));
            if (entry.getValue().isEmpty()) {
                pendingRequests.remove(entry.getKey());
            }
        }

        outgoingRequests.entrySet().removeIf(entry -> entry.getValue().isExpired(timeout));

        long now = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
