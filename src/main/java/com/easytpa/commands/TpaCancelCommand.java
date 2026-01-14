package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.data.RequestManager;
import com.easytpa.data.TpaRequest;
import com.easytpa.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TpaCancelCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpaCancelCommand(EasyTPA plugin) {
        super("tpacancel", "Cancel your teleport request");
        this.plugin = plugin;
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        RequestManager requestManager = plugin.getRequestManager();
        TpaRequest cancelled = requestManager.cancelOutgoingRequest(playerData.getUuid());

        if (cancelled == null) {
            playerData.sendMessage(Messages.noOutgoingRequest());
            return;
        }

        playerData.sendMessage(Messages.requestCancelled());
    }
}
