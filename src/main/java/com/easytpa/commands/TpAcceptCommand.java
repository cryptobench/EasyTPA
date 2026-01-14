package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.data.RequestManager;
import com.easytpa.data.TpaRequest;
import com.easytpa.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TpAcceptCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpAcceptCommand(EasyTPA plugin) {
        super("tpaccept", "Accept a teleport request");
        this.plugin = plugin;
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        RequestManager requestManager = plugin.getRequestManager();
        TpaRequest request = requestManager.getLatestRequest(playerData.getUuid());

        if (request == null) {
            playerData.sendMessage(Messages.noPendingRequests());
            return;
        }

        PlayerRef senderData = Universe.get().getPlayer(request.getSenderId());
        if (senderData == null) {
            requestManager.removeRequest(request);
            playerData.sendMessage(Messages.senderOffline());
            return;
        }

        Ref<EntityStore> senderRef = senderData.getReference();
        if (senderRef == null) {
            requestManager.removeRequest(request);
            playerData.sendMessage(Messages.senderOffline());
            return;
        }

        requestManager.removeRequest(request);

        playerData.sendMessage(Messages.requestAccepted(senderData.getUsername()));
        senderData.sendMessage(Messages.yourRequestAccepted(playerData.getUsername()));

        Player player = store.getComponent(playerRef, Player.getComponentType());
        boolean bypassWarmup = player != null && player.hasPermission("tpa.bypass.warmup");

        int warmupSeconds = plugin.getConfig().getWarmupSeconds();

        if (request.getType() == TpaRequest.Type.TPA) {
            // Sender teleports to target (me)
            plugin.getWarmupManager().startWarmup(
                    senderData, senderRef, store, world,
                    playerData, playerRef,
                    warmupSeconds, bypassWarmup
            );
        } else {
            // I teleport to sender
            plugin.getWarmupManager().startWarmup(
                    playerData, playerRef, store, world,
                    senderData, senderRef,
                    warmupSeconds, bypassWarmup
            );
        }
    }
}
