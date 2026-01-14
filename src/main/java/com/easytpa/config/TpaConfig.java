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

    private static class ConfigData {
        int requestTimeoutSeconds = 60;
        int warmupSeconds = 3;
        int cooldownSeconds = 5;
        double movementThreshold = 0.5;
    }
}
