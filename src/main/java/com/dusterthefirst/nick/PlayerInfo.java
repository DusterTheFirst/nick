package com.dusterthefirst.nick;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PlayerInfo implements ConfigurationSerializable {
    String nick;
    ChatColor color;

    public PlayerInfo(String nick) {
        this.nick = nick;
        this.color = null;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public String getNickname() {
        return this.nick;
    }

    public String getNicknameColored() {
        if (color != null) {
            return color + getNickname();
        } else {
            return getNickname();
        }
    }

    public PlayerInfo(Map<String, Object> map) {
        this.nick = (String) map.get("nick");
        this.color = (ChatColor) map.get("color");
    }

    public PlayerInfo valueOf(Map<String, Object> map) {
        return new PlayerInfo(map);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("nick", nick);
        map.put("color", color);

        return map;
    }
}
