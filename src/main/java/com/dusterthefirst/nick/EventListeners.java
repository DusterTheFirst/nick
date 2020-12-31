package com.dusterthefirst.nick;

import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListeners implements Listener {
    Nick plugin;
    Players players;

    public EventListeners(Nick plugin, Players players) {
        this.plugin = plugin;
        this.players = players;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String username = player.getName();

        // Get the hostname provided on login
        String fullHostname = event.getHostname();

        // Get the colon position to separate hostname and port
        int colonPos = fullHostname.lastIndexOf(":");

        // Get the hostname and port
        String hostname = fullHostname.substring(0, colonPos);
        // String port = fullHostname.substring(colonPos);

        // Get the hostname postfix that should follow the nickname
        String hostnamePostfix = "." + plugin.getConfig().getString("hostname");

        // Ensure the user is connecting using the right host
        if (hostname.endsWith(hostnamePostfix)) {
            PlayerInfo previousInfo = players.getInfo(uuid);

            String nickname = WordUtils.capitalize(hostname.replace(hostnamePostfix, "").replace("_", " "));

            if (previousInfo == null || previousInfo.nick.length() == 0) {
                if (players.searchByNick(nickname) != null) {
                    plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'"
                            + ChatColor.RED + " has attempted to join with the nickname " + ChatColor.GOLD + "'"
                            + nickname + "'" + ChatColor.RED + " but the nickname has already been taken");
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The nickname " + ChatColor.GOLD
                            + "'" + nickname + "'" + ChatColor.RED
                            + " has already been taken by someone else. Choose another nickname or consult an admin");
                    players.setInfo(uuid, new PlayerInfo(""));
                    return;
                }
                players.setInfo(uuid, new PlayerInfo(nickname));

                String message = ChatColor.GREEN + "New player (" + ChatColor.YELLOW + username + ChatColor.GREEN
                        + ") joined with nickname: " + ChatColor.YELLOW + nickname;

                plugin.broadcast(message);
            } else if (!previousInfo.nick.equals(nickname)) {
                plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'"
                        + ChatColor.RED + " has attempted to join with the nickname " + ChatColor.GOLD + "'" + nickname
                        + "'" + ChatColor.RED + " which does not match their previous nickname " + ChatColor.GOLD + "'"
                        + previousInfo.nick + "'");
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "You have connected using the nickname " + ChatColor.GOLD + "'" + nickname + "'"
                                + ChatColor.RED + " which does not match your previous nickname " + ChatColor.GOLD + "'"
                                + previousInfo.nick + "'" + ChatColor.RED
                                + ". Change back to the previous nickname or ask an admin to update your nickname");
                return;
            }
        } else {
            plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'" + ChatColor.RED
                    + " has attempted to join with the hostname " + ChatColor.GOLD + "'" + hostname + "'"
                    + ChatColor.RED + " which does not match the hostname layout " + ChatColor.GOLD + "'<Your Nickname>"
                    + hostnamePostfix + "'");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "You have connected using an invalid hostname " + ChatColor.GOLD + "'" + hostname
                            + "'" + ChatColor.RED + ", please reconnect using the following hostname: " + ChatColor.GOLD
                            + "'<Your Nickname>" + hostnamePostfix + "'");
            return;
        }

        PlayerInfo info = players.getInfo(uuid);
        player.setDisplayName(info.getNicknameColored());
        player.setPlayerListName(info.getNicknameColored());
        // player.setCustomName(info.getNicknameColored());
        // player.setCustomNameVisible(true);
    }

    // @EventHandler
    // public void onPlayerChat(AsyncPlayerChatEvent event) {
    // Player player = event.getPlayer();
    // String nickname = nicknames.get(player.getUniqueId());

    // if (nickname == null) {
    // plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" +
    // player.getName() + "'" + ChatColor.RED
    // + " has no nickname.");

    // Bukkit.getScheduler().runTask(plugin, () -> {
    // player.kickPlayer(ChatColor.RED
    // + "Please rejoin, the nickname plugin somehow has failed to register your
    // user and requires that you log in again. Sorry for the inconvenience");
    // });
    // } else {
    // player.setDisplayName(nickname);
    // }
    // }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerInfo info = players.getInfo(uuid);

        if (info != null)
            event.setJoinMessage(ChatColor.YELLOW + info.getNicknameColored() + ChatColor.YELLOW + " joined the game");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerInfo info = players.getInfo(uuid);

        if (info != null)
            event.setQuitMessage(ChatColor.YELLOW + info.getNicknameColored() + ChatColor.YELLOW + " left the game");
    }
}
