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

public class TpaHelpCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpaHelpCommand(EasyTPA plugin) {
        super("tpahelp", "Show TPA help");
        this.plugin = plugin;
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        playerData.sendMessage(Messages.helpHeader());
        playerData.sendMessage(Messages.helpCommand("tpa <player>", "Ask to teleport to a player"));
        playerData.sendMessage(Messages.helpCommand("tpahere <player>", "Ask a player to teleport to you"));
        playerData.sendMessage(Messages.helpCommand("tpaccept", "Accept a teleport request"));
        playerData.sendMessage(Messages.helpCommand("tpdeny", "Deny a teleport request"));
        playerData.sendMessage(Messages.helpCommand("tpacancel", "Cancel your pending request"));
        playerData.sendMessage(Messages.helpCommand("tpatoggle", "Turn TPA requests on/off"));
        playerData.sendMessage(Messages.helpCommand("rtp", "Teleport to a random location"));
    }
}
