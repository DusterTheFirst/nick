package com.dusterthefirst.nick;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Players {
    private File file;
    private FileConfiguration config;

    public UUID searchByNick(String nickname) {
        Map<String, Object> players = config.getValues(false);

        for (Entry<String, Object> entry : players.entrySet()) {
            PlayerInfo playerInfo = (PlayerInfo) entry.getValue();
            UUID uuid = UUID.fromString(entry.getKey());

            if (playerInfo.nick.toLowerCase().equals(nickname.toLowerCase())) {
                return uuid;
            }
        }

        return null;
    }

    public Map<UUID, PlayerInfo> all() {
        Map<String, Object> players = config.getValues(false);
        Map<UUID, PlayerInfo> all = new HashMap<>();

        for (Entry<String, Object> entry : players.entrySet()) {
            PlayerInfo playerInfo = (PlayerInfo) entry.getValue();
            UUID uuid = UUID.fromString(entry.getKey());

            all.put(uuid, playerInfo);
        }

        return all;
    }

    public void createInfo(UUID uuid, String nickname) {
        config.set(uuid.toString(), new PlayerInfo(nickname));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerInfo getInfo(UUID uuid) {
        return config.getObject(uuid.toString(), PlayerInfo.class);
    }

    public void setInfo(UUID uuid, PlayerInfo info) {
        config.set(uuid.toString(), info);
        save();
    }

    public void load() {
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Players(File dataFolder) {
        file = new File(dataFolder, "players.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
