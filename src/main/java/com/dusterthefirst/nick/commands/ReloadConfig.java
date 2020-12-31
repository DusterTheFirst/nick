package com.dusterthefirst.nick.commands;

import com.dusterthefirst.nick.Players;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class ReloadConfig implements CommandExecutor {
    Players players;
    JavaPlugin plugin;

    public ReloadConfig(JavaPlugin plugin, Players players) {
        this.players = players;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            return false;
        }

        if (!sender.isOp()) {
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "Reloading config...");
        players.load();
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Done");
        return true;
    }
}
