package com.easytpa.util;

import com.easytpa.EasyTPA;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class WarmupManager {

    private final EasyTPA plugin;
    private final ScheduledExecutorService scheduler;
    private final Map<UUID, WarmupData> activeWarmups = new ConcurrentHashMap<>();

    public WarmupManager(EasyTPA plugin) {
        this.plugin = plugin;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void shutdown() {
        for (UUID playerId : activeWarmups.keySet()) {
            cancelWarmup(playerId);
        }
        scheduler.shutdown();
    }

    public void startWarmup(PlayerRef playerData,
                           Ref<EntityStore> playerRef,
                           Store<EntityStore> store,
                           World world,
                           PlayerRef targetData,
                           Ref<EntityStore> targetRef,
                           int warmupSeconds,
                           boolean bypassWarmup) {

        UUID playerId = playerData.getUuid();
        cancelWarmup(playerId);

        if (bypassWarmup || warmupSeconds <= 0) {
            executeTeleport(playerData, playerRef, store, world, targetRef);
            return;
        }

        TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
        Vector3d startPos = transform.getPosition();

        playerData.sendMessage(Messages.teleporting(warmupSeconds));

        WarmupData data = new WarmupData(playerData, playerRef, store, world, targetData, targetRef,
                startPos.getX(), startPos.getY(), startPos.getZ(), plugin.getConfig().getMovementThreshold());

        ScheduledFuture<?> checkFuture = scheduler.scheduleAtFixedRate(() -> {
            checkMovement(playerId, data);
        }, 500, 500, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> teleportFuture = scheduler.schedule(() -> {
            doTeleport(playerId);
        }, warmupSeconds, TimeUnit.SECONDS);

        data.checkFuture = checkFuture;
        data.teleportFuture = teleportFuture;
        activeWarmups.put(playerId, data);
    }

    private void checkMovement(UUID playerId, WarmupData data) {
        if (!activeWarmups.containsKey(playerId)) {
            return;
        }

        try {
            data.world.execute(() -> {
                try {
                    TransformComponent transform = data.store.getComponent(data.playerRef, TransformComponent.getComponentType());
                    if (transform == null) {
                        return;
                    }

                    Vector3d currentPos = transform.getPosition();
                    double dx = currentPos.getX() - data.startX;
                    double dy = currentPos.getY() - data.startY;
                    double dz = currentPos.getZ() - data.startZ;
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (distance > data.movementThreshold) {
                        data.playerData.sendMessage(Messages.teleportCancelled());
                        cancelWarmup(playerId);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            });
        } catch (Exception e) {
            // Ignore
        }
    }

    private void doTeleport(UUID playerId) {
        WarmupData data = activeWarmups.remove(playerId);
        if (data == null) {
            return;
        }

        if (data.checkFuture != null) {
            data.checkFuture.cancel(false);
        }

        executeTeleport(data.playerData, data.playerRef, data.store, data.world, data.targetRef);
    }

    public void cancelWarmup(UUID playerId) {
        WarmupData data = activeWarmups.remove(playerId);
        if (data != null) {
            if (data.checkFuture != null) {
                data.checkFuture.cancel(false);
            }
            if (data.teleportFuture != null) {
                data.teleportFuture.cancel(false);
            }
        }
    }

    private void executeTeleport(PlayerRef playerData,
                                Ref<EntityStore> playerRef,
                                Store<EntityStore> store,
                                World world,
                                Ref<EntityStore> targetRef) {
        world.execute(() -> {
            try {
                TransformComponent targetTransform = store.getComponent(targetRef, TransformComponent.getComponentType());
                if (targetTransform == null) {
                    playerData.sendMessage(Messages.senderOffline());
                    return;
                }

                Vector3d position = targetTransform.getPosition();
                Vector3f rotation = targetTransform.getRotation();

                Teleport teleport = new Teleport(world, position, rotation);
                store.addComponent(playerRef, Teleport.getComponentType(), teleport);

                playerData.sendMessage(Messages.teleportComplete());
            } catch (Exception e) {
                playerData.sendMessage(Messages.senderOffline());
            }
        });
    }

    private static class WarmupData {
        final PlayerRef playerData;
        final Ref<EntityStore> playerRef;
        final Store<EntityStore> store;
        final World world;
        final PlayerRef targetData;
        final Ref<EntityStore> targetRef;
        final double startX, startY, startZ;
        final double movementThreshold;
        ScheduledFuture<?> checkFuture;
        ScheduledFuture<?> teleportFuture;

        WarmupData(PlayerRef playerData, Ref<EntityStore> playerRef, Store<EntityStore> store,
                  World world, PlayerRef targetData, Ref<EntityStore> targetRef,
                  double startX, double startY, double startZ, double movementThreshold) {
            this.playerData = playerData;
            this.playerRef = playerRef;
            this.store = store;
            this.world = world;
            this.targetData = targetData;
            this.targetRef = targetRef;
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.movementThreshold = movementThreshold;
        }
    }
}
