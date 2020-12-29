package com.dusterthefirst.nick;

import java.util.UUID;

import com.dusterthefirst.nick.config.NicknamesConfig;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class EventListeners implements Listener {
    Nick plugin;
    NicknamesConfig nicknames;

    public EventListeners(Nick plugin, NicknamesConfig nicknames) {
        this.plugin = plugin;
        this.nicknames = nicknames;
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
            String previousNickname = nicknames.get(uuid);

            String nickname = WordUtils.capitalize(hostname.replace(hostnamePostfix, "").replace("_", " "));

            if (previousNickname == null || previousNickname.length() == 0) {
                if (nicknames.exists(nickname)) {
                    plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'"
                            + ChatColor.RED + " has attempted to join with the nickname " + ChatColor.GOLD + "'"
                            + nickname + "'" + ChatColor.RED + " but the nickname has already been taken");
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The nickname " + ChatColor.GOLD
                            + "'" + nickname + "'" + ChatColor.RED
                            + " has already been taken by someone else. Choose another nickname or consult an admin");
                    nicknames.set(uuid, "");
                    return;
                }
                nicknames.set(uuid, nickname);

                String message = ChatColor.GREEN + "New player (" + ChatColor.YELLOW + username + ChatColor.GREEN
                        + ") joined with nickname: " + ChatColor.YELLOW + nickname;

                plugin.broadcast(message);
            } else if (!previousNickname.equals(nickname)) {
                plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'"
                        + ChatColor.RED + " has attempted to join with the nickname " + ChatColor.GOLD + "'" + nickname
                        + "'" + ChatColor.RED + " which does not match their previous nickname " + ChatColor.GOLD + "'"
                        + previousNickname + "'");
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "You have connected using the nickname " + ChatColor.GOLD + "'" + nickname + "'"
                                + ChatColor.RED + " which does not match your previous nickname " + ChatColor.GOLD + "'"
                                + previousNickname + "'" + ChatColor.RED
                                + ". Change back to the previous nickname or ask an admin to update your nickname");
                return;
            }

            player.setDisplayName(nickname);
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
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String nickname = nicknames.get(player.getUniqueId());

        if (nickname == null) {
            plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'" + ChatColor.RED
                    + " has no nickname.");

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.kickPlayer(ChatColor.RED
                        + "Please rejoin, the nickname plugin somehow has failed to register your user and requires that you log in again. Sorry for the inconvenience");
            });
        } else {
            player.setDisplayName(nickname);
        }
    }
}
