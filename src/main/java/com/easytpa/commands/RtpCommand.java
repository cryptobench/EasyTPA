package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.config.TpaConfig;
import com.easytpa.data.RequestManager;
import com.easytpa.util.Messages;
import com.easytpa.util.SafeLocationFinder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class RtpCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public RtpCommand(EasyTPA plugin) {
        super("rtp", "Teleport to a random location");
        this.plugin = plugin;
        requirePermission("rtp.use");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        RequestManager requestManager = plugin.getRequestManager();

        if (requestManager.isOnRtpCooldown(playerData.getUuid())) {
            int remaining = requestManager.getRemainingRtpCooldown(playerData.getUuid());
            playerData.sendMessage(Messages.rtpOnCooldown(remaining));
            return;
        }

        playerData.sendMessage(Messages.rtpSearching());

        TpaConfig config = plugin.getConfig();
        Vector3d safeLocation = SafeLocationFinder.findSafeLocation(
                world,
                config.getRtpMinDistance(),
                config.getRtpMaxDistance(),
                config.getRtpMaxAttempts()
        );

        if (safeLocation == null) {
            playerData.sendMessage(Messages.rtpNoSafeLocation());
            return;
        }

        requestManager.setRtpCooldown(playerData.getUuid(), config.getRtpCooldownSeconds());

        Player player = store.getComponent(playerRef, Player.getComponentType());
        boolean bypassWarmup = player != null && player.hasPermission("tpa.bypass.warmup");

        plugin.getWarmupManager().startRtpWarmup(
                playerData,
                playerRef,
                store,
                world,
                safeLocation,
                config.getWarmupSeconds(),
                bypassWarmup
        );
    }
}
