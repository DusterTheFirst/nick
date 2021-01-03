package com.dusterthefirst.nick.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import com.dusterthefirst.nick.NickPlugin;
import com.dusterthefirst.nick.PlayerInfo;
import com.dusterthefirst.nick.Players;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class SetColor implements TabExecutor {
    Players players;
    NickPlugin plugin;

    static final List<ChatColor> allowedChatColors = Arrays.asList(ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE,
            ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE,
            ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED,
            ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RESET);

    public SetColor(NickPlugin plugin, Players players) {
        this.players = players;
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        TreeSet<String> completions = new TreeSet<>();

        for (ChatColor color : allowedChatColors) {
            String colorName = color.name().toLowerCase();
            if (colorName.contains(args[0]))
                completions.add(colorName);
        }

        return new ArrayList<>(completions);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be executed by a player");
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerInfo info = players.getInfo(uuid);

        if (info == null) {
            plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName().toLowerCase() + "'" + ChatColor.RED
                    + " has no nickname.");

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.kickPlayer(ChatColor.RED
                        + "Please rejoin, the nickname plugin somehow has failed to register your user and requires that you log in again. Sorry for the inconvenience");
            });

            return true;
        }

        if (args.length != 1) {
            return false;
        }

        try {
            ChatColor color = ChatColor.valueOf(args[0].toUpperCase());

            if ((new ArrayList<ChatColor>(allowedChatColors)).contains(color)) {
                info.setColor(color);
                info.applyTo(player);
                sender.sendMessage(ChatColor.GREEN + "Your color has been changed to " + color + color.name());
            } else {
                sender.sendMessage(ChatColor.RED + "Only color codes are allowed");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid color");
        }
        return true;
    }

}
