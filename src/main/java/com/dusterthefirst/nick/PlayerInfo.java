package com.dusterthefirst.nick;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class PlayerInfo implements ConfigurationSerializable {
    private String nick;
    private ChatColor color;

    public PlayerInfo(String nick) {
        this.nick = WordUtils.capitalize(nick.toLowerCase());
        this.color = null;
    }

    public void applyTo(Player p) {
        p.setDisplayName(getNicknameColored());
        p.setPlayerListName(getNicknameColored());
        // player.setCustomName(info.getNicknameColored());
        // player.setCustomNameVisible(true);
    }

    public void setColor(ChatColor color) {
        if (color == ChatColor.RESET) {
            this.color = null;
        } else {
            this.color = color;
        }
    }

    public void setNickname(String nick) {
        this.nick = WordUtils.capitalize(nick.toLowerCase());
    }

    public String getNickname() {
        return this.nick;
    }

    public String getNicknameColored() {
        if (color != null) {
            return color + getNickname() + ChatColor.RESET;
        } else {
            return getNickname() + ChatColor.RESET;
        }
    }

    public PlayerInfo(Map<String, Object> map) {
        this.nick = (String) map.get("nick");
        String color = (String) map.get("color");
        if (color == null) {
            this.color = null;
        } else {
            this.color = ChatColor.valueOf(color);
        }
    }

    public PlayerInfo valueOf(Map<String, Object> map) {
        return new PlayerInfo(map);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("nick", nick);

        if (color == null) {
            map.put("color", null);
        } else {
            map.put("color", color.name());
        }

        return map;
    }
}
