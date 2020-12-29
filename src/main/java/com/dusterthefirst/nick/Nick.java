package com.dusterthefirst.nick;

import com.dusterthefirst.nick.commands.ReloadNicknames;
import com.dusterthefirst.nick.config.NicknamesConfig;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** The main class for the nick plugin. */
public class Nick extends JavaPlugin {
    NicknamesConfig nicknames = new NicknamesConfig(getDataFolder());

    public void broadcast(String s) {
        getLogger().info(s);
        for (Player op : getServer().getOnlinePlayers()) {
            if (op.isOp())
                op.sendMessage(s);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        nicknames.load();

        getCommand("reload-nicknames").setExecutor(new ReloadNicknames(nicknames));

        getServer().getPluginManager().registerEvents(new EventListeners(this, nicknames), this);
    }
}
