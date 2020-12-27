package com.dusterthefirst.nick;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

/** The main class for the nick plugin. */
public class Nick extends JavaPlugin implements Listener {
    private File nicknamesConfigFile;
    private FileConfiguration nicknamesConfig;

    public String getNickname(UUID uuid) {
        return this.nicknamesConfig.getString(uuid.toString());
    }

    public void setNickname(UUID uuid, String nickname) {
        this.nicknamesConfig.set(uuid.toString(), nickname);
        try {
            this.nicknamesConfig.save(nicknamesConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNicknamesConfig() {
        nicknamesConfigFile = new File(getDataFolder(), "nicknames.yml");
        if (!nicknamesConfigFile.exists()) {
            nicknamesConfigFile.getParentFile().mkdirs();
            try {
                nicknamesConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        nicknamesConfig = new YamlConfiguration();
        try {
            nicknamesConfig.load(nicknamesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createNicknamesConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "Nick plugin has started!");
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
        String port = fullHostname.substring(colonPos);

        // Get the hostname postfix that should follow the nickname
        String hostnamePostfix = "." + getConfig().getString("hostname");

        // Ensure the user is connecting using the right host
        if (hostname.endsWith(hostnamePostfix)) {
            String previousNickname = getNickname(uuid);

            String nickname = StringUtils.capitalize(hostname.replace(hostnamePostfix, ""));

            if (previousNickname == null) {
                setNickname(uuid, nickname);
                saveConfig();

                getLogger().info(ChatColor.GREEN + "New player (" + ChatColor.YELLOW + username + ChatColor.GREEN
                        + ") joined with nickname: " + ChatColor.YELLOW + nickname);

            } else if (!previousNickname.equals(nickname)) {
                event.disallow(Result.KICK_OTHER, ChatColor.RED + "You have connected using the nickname "
                        + ChatColor.GOLD + "'" + nickname + "'" + ChatColor.RED
                        + " which does not match your previous nickname " + ChatColor.GOLD + "'" + previousNickname
                        + "'" + ChatColor.RED
                        + ". Change back to the previous nickname or ask an admin to update the nickname for the user "
                        + ChatColor.GOLD + "'" + uuid + "'");
            }
        } else {
            event.disallow(Result.KICK_OTHER,
                    ChatColor.RED + "You have connected using an invalid hostname " + ChatColor.GOLD + "'" + hostname
                            + "'" + ChatColor.RED + ", please reconnect using the following hostname: " + ChatColor.GOLD
                            + "'<Your Nickname>" + hostnamePostfix + "'");
            return;
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String nickname = getNickname(player.getUniqueId());

        if (nickname == null) {
            player.kickPlayer(ChatColor.RED
                    + "Please rejoin, the nickname plugin somehow has failed to register your user and requires that you log in again. Sorry for the inconvenience");
        } else {
            player.setDisplayName(nickname);
        }
    }
}
