package com.easytpa.commands;

import com.easytpa.EasyTPA;
import com.easytpa.config.TpaConfig;
import com.easytpa.util.Messages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TpaAdminCommand extends AbstractPlayerCommand {

    private final EasyTPA plugin;

    public TpaAdminCommand(EasyTPA plugin) {
        super("easytpa", "EasyTPA admin commands");
        this.plugin = plugin;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> playerRef,
                          @Nonnull PlayerRef playerData,
                          @Nonnull World world) {

        Player player = store.getComponent(playerRef, Player.getComponentType());
        if (player == null || !player.hasPermission("tpa.admin")) {
            playerData.sendMessage(Messages.noPermission());
            return;
        }

        String[] args = ctx.getInputString().trim().split("\\s+");

        if (args.length < 2 || !args[1].equalsIgnoreCase("admin")) {
            playerData.sendMessage(Messages.adminUsage());
            return;
        }

        if (args.length < 3) {
            playerData.sendMessage(Messages.adminUsage());
            return;
        }

        String subCommand = args[2].toLowerCase();
        TpaConfig config = plugin.getConfig();

        switch (subCommand) {
            case "config" -> showConfig(playerData, config);
            case "set" -> handleSet(playerData, args, config);
            default -> playerData.sendMessage(Messages.adminUsage());
        }
    }

    private void showConfig(PlayerRef playerData, TpaConfig config) {
        playerData.sendMessage(Messages.configHeader());
        playerData.sendMessage(Messages.configValue("Timeout", config.getRequestTimeoutSeconds() + "s"));
        playerData.sendMessage(Messages.configValue("Warmup", config.getWarmupSeconds() + "s"));
        playerData.sendMessage(Messages.configValue("Cooldown", config.getCooldownSeconds() + "s"));
        playerData.sendMessage(Messages.configValue("RTP Min", config.getRtpMinDistance() + " blocks"));
        playerData.sendMessage(Messages.configValue("RTP Max", config.getRtpMaxDistance() + " blocks"));
        playerData.sendMessage(Messages.configValue("RTP Attempts", String.valueOf(config.getRtpMaxAttempts())));
        playerData.sendMessage(Messages.configValue("RTP Cooldown", config.getRtpCooldownSeconds() + "s"));
    }

    private void handleSet(PlayerRef playerData, String[] args, TpaConfig config) {
        if (args.length < 5) {
            playerData.sendMessage(Messages.helpCommand("easytpa admin set timeout <seconds>", "Request expiry"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set warmup <seconds>", "Teleport delay"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set cooldown <seconds>", "Between requests"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set rtpmin <blocks>", "RTP min distance"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set rtpmax <blocks>", "RTP max distance"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set rtpattempts <num>", "RTP search attempts"));
            playerData.sendMessage(Messages.helpCommand("easytpa admin set rtpcooldown <seconds>", "RTP cooldown"));
            return;
        }

        String setting = args[3].toLowerCase();
        String value = args[4];

        try {
            switch (setting) {
                case "timeout" -> {
                    int seconds = Integer.parseInt(value);
                    config.setRequestTimeoutSeconds(seconds);
                    playerData.sendMessage(Messages.configUpdated("Timeout", seconds + "s"));
                }
                case "warmup" -> {
                    int seconds = Integer.parseInt(value);
                    config.setWarmupSeconds(seconds);
                    playerData.sendMessage(Messages.configUpdated("Warmup", seconds + "s"));
                }
                case "cooldown" -> {
                    int seconds = Integer.parseInt(value);
                    config.setCooldownSeconds(seconds);
                    playerData.sendMessage(Messages.configUpdated("Cooldown", seconds + "s"));
                }
                case "rtpmin" -> {
                    int distance = Integer.parseInt(value);
                    config.setRtpMinDistance(distance);
                    playerData.sendMessage(Messages.configUpdated("RTP Min", distance + " blocks"));
                }
                case "rtpmax" -> {
                    int distance = Integer.parseInt(value);
                    config.setRtpMaxDistance(distance);
                    playerData.sendMessage(Messages.configUpdated("RTP Max", distance + " blocks"));
                }
                case "rtpattempts" -> {
                    int attempts = Integer.parseInt(value);
                    config.setRtpMaxAttempts(attempts);
                    playerData.sendMessage(Messages.configUpdated("RTP Attempts", String.valueOf(attempts)));
                }
                case "rtpcooldown" -> {
                    int seconds = Integer.parseInt(value);
                    config.setRtpCooldownSeconds(seconds);
                    playerData.sendMessage(Messages.configUpdated("RTP Cooldown", seconds + "s"));
                }
                default -> playerData.sendMessage(Messages.adminUsage());
            }
        } catch (NumberFormatException e) {
            playerData.sendMessage(Messages.helpCommand("Error", "Must be a number"));
        }
    }
}
