package com.dusterthefirst.nick;

import com.dusterthefirst.nick.commands.SetColor;
import com.dusterthefirst.nick.commands.SetNick;
import com.dusterthefirst.nick.commands.Whois;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** The main class for the nick plugin. */
public class NickPlugin extends JavaPlugin {
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
        saveResource("README.md", true);

        getCommand("whois").setExecutor(new Whois(this, players));
        getCommand("color").setExecutor(new SetColor(this, players));
        getCommand("nick").setExecutor(new SetNick(this, players));

        getServer().getPluginManager().registerEvents(new EventListeners(this, players), this);
    }

    @Override
    public void onDisable() {
        players.save();
    }
}
