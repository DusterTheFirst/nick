package com.dusterthefirst.nick.commands;

import com.dusterthefirst.nick.config.NicknamesConfig;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class ReloadNicknames implements CommandExecutor {
    NicknamesConfig nicknames;

    public ReloadNicknames(NicknamesConfig nicknames) {
        this.nicknames = nicknames;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "Reloading config...");
            nicknames.load();
            sender.sendMessage(ChatColor.GREEN + "Done");
            return true;
        } else {
            return false;
        }
    }
}
