package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TpaToggleCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpaToggleCommand(EasyTPA plugin) {
        super("tpatoggle", "Toggle TPA requests on/off");
        this.plugin = plugin;
        requirePermission("tpa.use");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        boolean enabled = plugin.getRequestManager().toggleTpa(playerData.getUuid());

        if (enabled) {
            playerData.sendMessage(Messages.tpaEnabled());
        } else {
            playerData.sendMessage(Messages.tpaDisabled());
        }
    }
}
