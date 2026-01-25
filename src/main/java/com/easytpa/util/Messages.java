package com.easytpa.util;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;

public class Messages {
    private static final Color GREEN = new Color(85, 255, 85);
    private static final Color RED = new Color(255, 85, 85);
    private static final Color YELLOW = new Color(255, 255, 85);
    private static final Color GOLD = new Color(255, 170, 0);
    private static final Color GRAY = new Color(170, 170, 170);
    private static final Color AQUA = new Color(85, 255, 255);

    public static Message requestSent(String playerName) {
        return Message.raw("Teleport request sent to " + playerName + "!").color(GREEN);
    }

    public static Message requestReceived(String playerName) {
        return Message.raw(playerName + " wants to teleport to you!").color(GOLD);
    }

    public static Message requestHereReceived(String playerName) {
        return Message.raw(playerName + " wants you to teleport to them!").color(GOLD);
    }

    public static Message acceptHint() {
        return Message.raw("Type /tpaccept to accept or /tpdeny to deny").color(GRAY);
    }

    public static Message requestAccepted(String playerName) {
        return Message.raw("Accepted teleport request from " + playerName + "!").color(GREEN);
    }

    public static Message yourRequestAccepted(String playerName) {
        return Message.raw(playerName + " accepted your request!").color(GREEN);
    }

    public static Message requestDenied(String playerName) {
        return Message.raw("Denied request from " + playerName + ".").color(YELLOW);
    }

    public static Message yourRequestDenied(String playerName) {
        return Message.raw(playerName + " denied your request.").color(RED);
    }

    public static Message requestCancelled() {
        return Message.raw("Request cancelled.").color(YELLOW);
    }

    public static Message teleporting(int seconds) {
        if (seconds == 0) {
            return Message.raw("Teleporting...").color(GREEN);
        }
        return Message.raw("Teleporting in " + seconds + " seconds... Don't move!").color(YELLOW);
    }

    public static Message teleportComplete() {
        return Message.raw("Teleported!").color(GREEN);
    }

    public static Message teleportCancelled() {
        return Message.raw("Teleport cancelled - you moved!").color(RED);
    }

    public static Message tpaEnabled() {
        return Message.raw("TPA requests ON.").color(GREEN);
    }

    public static Message tpaDisabled() {
        return Message.raw("TPA requests OFF.").color(YELLOW);
    }

    public static Message playerNotFound(String playerName) {
        return Message.raw("Player '" + playerName + "' not found.").color(RED);
    }

    public static Message cannotTpaSelf() {
        return Message.raw("You can't TPA to yourself!").color(RED);
    }

    public static Message noPendingRequests() {
        return Message.raw("No pending requests.").color(RED);
    }

    public static Message noOutgoingRequest() {
        return Message.raw("No outgoing request to cancel.").color(RED);
    }

    public static Message playerHasTpaDisabled(String playerName) {
        return Message.raw(playerName + " has TPA disabled.").color(RED);
    }

    public static Message onCooldown(int seconds) {
        return Message.raw("Wait " + seconds + " seconds.").color(RED);
    }

    public static Message senderOffline() {
        return Message.raw("That player is offline.").color(RED);
    }

    public static Message usagePlayer(String command) {
        return Message.raw("Usage: /" + command + " <player>").color(RED);
    }

    public static Message helpHeader() {
        return Message.raw("=== EasyTPA Help ===").color(GOLD);
    }

    public static Message helpCommand(String command, String description) {
        return Message.raw("/" + command + " - " + description).color(GRAY);
    }

    public static Message configHeader() {
        return Message.raw("=== EasyTPA Config ===").color(GOLD);
    }

    public static Message configValue(String key, String value) {
        return Message.raw(key + ": " + value).color(AQUA);
    }

    public static Message configUpdated(String key, String value) {
        return Message.raw("Set " + key + " to " + value).color(GREEN);
    }

    public static Message noPermission() {
        return Message.raw("No permission.").color(RED);
    }

    public static Message adminUsage() {
        return Message.raw("Usage: /easytpa admin <config|set|reload>").color(RED);
    }

    public static Message rtpSearching() {
        return Message.raw("Searching for a safe location...").color(YELLOW);
    }

    public static Message rtpTeleporting(int seconds) {
        if (seconds == 0) {
            return Message.raw("Random teleporting...").color(GREEN);
        }
        return Message.raw("Random teleporting in " + seconds + " seconds... Don't move!").color(YELLOW);
    }

    public static Message rtpComplete(int x, int z) {
        return Message.raw("Teleported to X: " + x + ", Z: " + z + "!").color(GREEN);
    }

    public static Message rtpNoSafeLocation() {
        return Message.raw("Could not find a safe location. Try again!").color(RED);
    }

    public static Message rtpOnCooldown(int seconds) {
        return Message.raw("RTP cooldown: wait " + seconds + " seconds.").color(RED);
    }
}
