package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.data.RequestManager;
import com.easytpa.data.TpaRequest;
import com.easytpa.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TpaCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpaCommand(EasyTPA plugin) {
        super("tpa", "Ask to teleport to a player");
        this.plugin = plugin;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        String[] args = ctx.getInputString().trim().split("\\s+");
        if (args.length < 2) {
            playerData.sendMessage(Messages.usagePlayer("tpa"));
            return;
        }

        String targetName = args[1];
        RequestManager requestManager = plugin.getRequestManager();

        if (requestManager.isOnCooldown(playerData.getUuid())) {
            int remaining = requestManager.getRemainingCooldown(playerData.getUuid());
            playerData.sendMessage(Messages.onCooldown(remaining));
            return;
        }

        PlayerRef targetData = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
        if (targetData == null) {
            playerData.sendMessage(Messages.playerNotFound(targetName));
            return;
        }

        if (targetData.getUuid().equals(playerData.getUuid())) {
            playerData.sendMessage(Messages.cannotTpaSelf());
            return;
        }

        if (requestManager.isTpaDisabled(targetData.getUuid())) {
            playerData.sendMessage(Messages.playerHasTpaDisabled(targetData.getUsername()));
            return;
        }

        TpaRequest request = new TpaRequest(
                playerData.getUuid(), playerData.getUsername(),
                targetData.getUuid(), targetData.getUsername(),
                TpaRequest.Type.TPA
        );

        requestManager.createRequest(request);

        playerData.sendMessage(Messages.requestSent(targetData.getUsername()));
        targetData.sendMessage(Messages.requestReceived(playerData.getUsername()));
        targetData.sendMessage(Messages.acceptHint());
    }
}
