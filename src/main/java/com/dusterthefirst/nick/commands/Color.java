package com.dusterthefirst.nick.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import com.dusterthefirst.nick.PlayerInfo;
import com.dusterthefirst.nick.Players;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class Color implements TabExecutor {
    Players players;
    JavaPlugin plugin;

    public Color(JavaPlugin plugin, Players players) {
        this.players = players;
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return null;
        }

        TreeSet<String> completions = new TreeSet<>();

        for (ChatColor color : ChatColor.values()) {
            completions.add(color.name().toLowerCase());
        }

        return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player");
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerInfo info = players.getInfo(uuid);

        if (args.length != 1) {
            return false;
        }

        ChatColor color = ChatColor.valueOf(args[0].toUpperCase());

        info.setColor(color);
        info.applyTo(player);
        // players.setInfo(uuid, info);

        return true;
    }

}
