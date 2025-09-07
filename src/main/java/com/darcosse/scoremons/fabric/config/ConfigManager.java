package com.darcosse.scoremons.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final String CONFIG_FILE = "scoremons_config.json";
    private static ScoremonsConfig config;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, ScoremonsConfig.class);
            } catch (IOException e) {
                config = new ScoremonsConfig();
                saveConfig();
            }
        } else {
            config = new ScoremonsConfig();
            saveConfig();
        }
    }

    public static void saveConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ScoremonsConfig getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    public static void reloadConfig() {
        config = null;
        loadConfig();
    }

    // Méthodes utilitaires pour accéder facilement aux configs
    public static boolean shouldBroadcastShinyCaught() {
        return getConfig().broadcastShinyCaught;
    }

    public static boolean shouldBroadcastLegendaryCaught() {
        return getConfig().broadcastLegendaryCaught;
    }

    public static boolean shouldBroadcastShinyFossilRevived() {
        return getConfig().broadcastShinyFossilRevived;
    }
}