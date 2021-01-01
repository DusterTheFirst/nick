package com.dusterthefirst.nick;

import com.dusterthefirst.nick.commands.Color;
import com.dusterthefirst.nick.commands.ReloadConfig;
import com.dusterthefirst.nick.commands.Whois;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** The main class for the nick plugin. */
public class Nick extends JavaPlugin {
    Players players = new Players(getDataFolder());

    public void broadcast(String s) {
        getLogger().info(s);
        for (Player op : getServer().getOnlinePlayers()) {
            if (op.isOp())
                op.sendMessage(s);
        }
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(PlayerInfo.class);

        UpdateManager.checkForUpdate(this);

        saveDefaultConfig();
        players.load();

        getCommand("reload-config").setExecutor(new ReloadConfig(this, players));
        getCommand("whois").setExecutor(new Whois(this, players));
        getCommand("color").setExecutor(new Color(this, players));

        getServer().getPluginManager().registerEvents(new EventListeners(this, players), this);
    }

    @Override
    public void onDisable() {
        saveConfig();
        players.save();
    }
}
