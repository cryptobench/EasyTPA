package com.easytpa;

import com.easytpa.commands.*;
import com.easytpa.config.TpaConfig;
import com.easytpa.data.RequestManager;
import com.easytpa.util.WarmupManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class EasyTPA extends JavaPlugin {

    private TpaConfig config;
    private RequestManager requestManager;
    private WarmupManager warmupManager;

    public EasyTPA(JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {
        this.config = new TpaConfig(getDataDirectory());
        this.requestManager = new RequestManager(this);
        this.warmupManager = new WarmupManager(this);

        getCommandRegistry().registerCommand(new TpaCommand(this));
        getCommandRegistry().registerCommand(new TpaHereCommand(this));
        getCommandRegistry().registerCommand(new TpAcceptCommand(this));
        getCommandRegistry().registerCommand(new TpDenyCommand(this));
        getCommandRegistry().registerCommand(new TpaCancelCommand(this));
        getCommandRegistry().registerCommand(new TpaToggleCommand(this));
        getCommandRegistry().registerCommand(new TpaHelpCommand(this));
        getCommandRegistry().registerCommand(new TpaAdminCommand(this));
        getCommandRegistry().registerCommand(new RtpCommand(this));
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        if (warmupManager != null) {
            warmupManager.shutdown();
        }
        if (requestManager != null) {
            requestManager.shutdown();
        }
    }

    public TpaConfig getConfig() {
        return config;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public WarmupManager getWarmupManager() {
        return warmupManager;
    }
}
