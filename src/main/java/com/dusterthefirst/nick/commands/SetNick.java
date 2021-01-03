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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class SetNick implements TabExecutor {
    Players players;
    NickPlugin plugin;

    public SetNick(NickPlugin plugin, Players players) {
        this.players = players;
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        TreeSet<String> completions = new TreeSet<>();

        for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
            completions.add(player.getName());
        }

        return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return false;
        }

        String playerName = args[0];
        String nickname = args[1];

        OfflinePlayer player = null;
        for (OfflinePlayer p : Arrays.asList(plugin.getServer().getOfflinePlayers())) {
            plugin.getLogger().info(p.getName());
            if (p.getName().equals(playerName)) {
                player = p;
            }
        }
        if (player == null) {
            sender.sendMessage("That player does not exist");
            return true;
        }

        UUID uuid = player.getUniqueId();
        PlayerInfo info = players.getInfo(uuid);

        if (info == null) {
            plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'" + ChatColor.RED
                    + " has no nickname.");
            sender.sendMessage(ChatColor.RED
                    + "This player has no configured nickname, either they have never joined or need to rejoin. Try again once the user has (re)joined");

            if (player.isOnline()) {
                Player onlinePlayer = (Player) player;

                Bukkit.getScheduler().runTask(plugin, () -> {
                    onlinePlayer.kickPlayer(ChatColor.RED
                            + "Please rejoin, the nickname plugin somehow has failed to register your user and requires that you log in again. Sorry for the inconvenience");
                });
            }

            return true;
        }

        info.setNickname(nickname);

        if (player.isOnline()) {
            Player onlinePlayer = (Player) player;

            info.applyTo(onlinePlayer);
            Bukkit.getScheduler().runTask(plugin, () -> {
                onlinePlayer.kickPlayer(ChatColor.RED + "Please rejoin, your nickname has been changed to "
                        + info.getNicknameColored());
            });
        }

        sender.sendMessage(ChatColor.GREEN + player.getName() + "'s nickname has been successfully updated to "
                + info.getNicknameColored());

        return true;
    }

}
