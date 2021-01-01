package com.dusterthefirst.nick.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.UUID;

import com.dusterthefirst.nick.PlayerInfo;
import com.dusterthefirst.nick.Players;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;

public class Whois implements TabExecutor {
    Players players;
    JavaPlugin plugin;

    public Whois(JavaPlugin plugin, Players players) {
        this.players = players;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        Server server = plugin.getServer();

        String person = args[0];

        String player = null;
        UUID player_uuid = players.searchByNick(person);
        if (player_uuid != null) {
            OfflinePlayer player_player = server.getOfflinePlayer(player_uuid);

            if (player_player != null) {
                player = player_player.getName();
            }
        }

        String nick = null;
        Player nick_player = server.getPlayer(person);
        if (nick_player != null) {
            UUID nick_uuid = nick_player.getUniqueId();

            if (nick_uuid != null) {
                PlayerInfo nick_info = players.getInfo(nick_uuid);

                if (nick_info != null) {
                    nick = nick_info.getNickname();
                }
            }
        }

        if (player == null && nick == null) {
            sender.sendMessage(
                    ChatColor.YELLOW + "Could not find any player with the nickname or username '" + person + "'");
        } else {
            if (nick != null) {
                sender.sendMessage(ChatColor.YELLOW + "The player with the username '" + person
                        + "' has their nickname as '" + nick + "'");
            }
            if (player != null) {
                sender.sendMessage(
                        ChatColor.YELLOW + "The player with the nickname '" + person + "' is '" + player + "'");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return null;
        }

        Server server = plugin.getServer();

        TreeSet<String> completions = new TreeSet<>();
        Map<UUID, PlayerInfo> all = players.all();

        for (Entry<UUID, PlayerInfo> entry : all.entrySet()) {
            PlayerInfo playerInfo = entry.getValue();
            UUID uuid = entry.getKey();

            String nick = playerInfo.getNickname();
            String name = server.getPlayer(uuid).getName();

            completions.add(nick);
            completions.add(name);
        }

        return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
    }

}
