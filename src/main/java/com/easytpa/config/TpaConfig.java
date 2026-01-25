package com.easytpa.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TpaConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configFile;
    private ConfigData data;

    public TpaConfig(Path dataDirectory) {
        this.configFile = dataDirectory.resolve("config.json");
        load();
    }

    private void load() {
        if (Files.exists(configFile)) {
            try (Reader reader = Files.newBufferedReader(configFile)) {
                data = GSON.fromJson(reader, ConfigData.class);
                if (data == null) {
                    data = new ConfigData();
                }
            } catch (Exception e) {
                data = new ConfigData();
            }
        } else {
            data = new ConfigData();
            save();
        }
    }

    public void save() {
        try {
            Files.createDirectories(configFile.getParent());
            try (Writer writer = Files.newBufferedWriter(configFile)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    public int getRequestTimeoutSeconds() {
        return data.requestTimeoutSeconds;
    }

    public int getWarmupSeconds() {
        return data.warmupSeconds;
    }

    public int getCooldownSeconds() {
        return data.cooldownSeconds;
    }

    public double getMovementThreshold() {
        return data.movementThreshold;
    }

    public void setRequestTimeoutSeconds(int seconds) {
        data.requestTimeoutSeconds = Math.max(10, Math.min(600, seconds));
        save();
    }

    public void setWarmupSeconds(int seconds) {
        data.warmupSeconds = Math.max(0, Math.min(30, seconds));
        save();
    }

    public void setCooldownSeconds(int seconds) {
        data.cooldownSeconds = Math.max(0, Math.min(300, seconds));
        save();
    }

    public int getRtpMinDistance() {
        return data.rtpMinDistance;
    }

    public int getRtpMaxDistance() {
        return data.rtpMaxDistance;
    }

    public int getRtpMaxAttempts() {
        return data.rtpMaxAttempts;
    }

    public int getRtpCooldownSeconds() {
        return data.rtpCooldownSeconds;
    }

    public void setRtpMinDistance(int distance) {
        data.rtpMinDistance = Math.max(100, Math.min(10000, distance));
        save();
    }

    public void setRtpMaxDistance(int distance) {
        data.rtpMaxDistance = Math.max(500, Math.min(50000, distance));
        save();
    }

    public void setRtpMaxAttempts(int attempts) {
        data.rtpMaxAttempts = Math.max(1, Math.min(50, attempts));
        save();
    }

    public void setRtpCooldownSeconds(int seconds) {
        data.rtpCooldownSeconds = Math.max(0, Math.min(3600, seconds));
        save();
    }

    private static class ConfigData {
        int requestTimeoutSeconds = 60;
        int warmupSeconds = 3;
        int cooldownSeconds = 5;
        double movementThreshold = 0.5;
        int rtpMinDistance = 500;
        int rtpMaxDistance = 5000;
        int rtpMaxAttempts = 10;
        int rtpCooldownSeconds = 60;
    }
}
