package com.dusterthefirst.nick;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class EventListeners implements Listener {
    NickPlugin plugin;
    Players players;

    public EventListeners(NickPlugin plugin, Players players) {
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
        String hostname = fullHostname.substring(0, colonPos).toLowerCase();
        // String port = fullHostname.substring(colonPos);

        // Get the hostname postfix that should follow the nickname
        String hostnamePostfix = "." + plugin.getConfig().getString("hostname");

        // Ensure the user is connecting using the right host
        if (hostname.endsWith(hostnamePostfix)) {
            PlayerInfo previousInfo = players.getInfo(uuid);

            String nickname = WordUtils.capitalize(hostname.replace(hostnamePostfix, ""));

            if (previousInfo == null || previousInfo.getNickname().length() == 0) {
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
            } else if (!previousInfo.getNickname().equals(nickname)) {
                plugin.broadcast(ChatColor.RED + "Player " + ChatColor.GOLD + "'" + player.getName() + "'"
                        + ChatColor.RED + " has attempted to join with the nickname " + ChatColor.GOLD + "'" + nickname
                        + "'" + ChatColor.RED + " which does not match their set nickname " + ChatColor.GOLD + "'"
                        + previousInfo.getNickname() + "'");
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "You have connected using the nickname " + ChatColor.GOLD + "'" + nickname + "'"
                                + ChatColor.RED + " which does not match your set nickname " + ChatColor.GOLD + "'"
                                + previousInfo.getNickname() + "'" + ChatColor.RED
                                + ". Change back to the set nickname or ask an admin to update your nickname");
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
        info.applyTo(player);
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        UUID deadPlayerUuid = deadPlayer.getUniqueId();

        String deathMessage = event.getDeathMessage().replace(deadPlayer.getName(),
                players.getInfo(deadPlayerUuid).getNicknameColored());

        Player killer = deadPlayer.getKiller();

        if (killer != null) {
            UUID killerUuid = killer.getUniqueId();

            deathMessage = deathMessage.replace(killer.getName(), players.getInfo(killerUuid).getNicknameColored());
        }

        // event.getEntity().sendMessage("L");
        event.setDeathMessage(deathMessage);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        List<String> completions = event.getCompletions();
        for (int i = 0; i < completions.size(); i++) {
            completions.set(i, ChatColor.stripColor(completions.get(i)));
            plugin.getLogger().info(completions.get(i));
        }
        event.setCompletions(completions);
    }
}
