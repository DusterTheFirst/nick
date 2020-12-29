package com.dusterthefirst.nick.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class NicknamesConfig {
    private File nicknamesConfigFile;
    private FileConfiguration nicknamesConfig;

    public String get(UUID uuid) {
        return this.nicknamesConfig.getString(uuid.toString());
    }

    public boolean exists(String nickname) {
        return this.nicknamesConfig.getValues(false).values().contains(nickname);
    }

    public void set(UUID uuid, String nickname) {
        this.nicknamesConfig.set(uuid.toString(), nickname);
        try {
            this.nicknamesConfig.save(nicknamesConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        nicknamesConfig = new YamlConfiguration();
        try {
            nicknamesConfig.load(nicknamesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public NicknamesConfig(File dataFolder) {
        nicknamesConfigFile = new File(dataFolder, "nicknames.yml");

        if (!nicknamesConfigFile.exists()) {
            nicknamesConfigFile.getParentFile().mkdirs();
            try {
                nicknamesConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
