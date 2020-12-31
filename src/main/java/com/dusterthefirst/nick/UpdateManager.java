package com.dusterthefirst.nick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

import com.dusterthefirst.nick.UpdateManager.Release.Asset;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class UpdateManager {
    static final String releaseUrl = "https://api.github.com/repos/DusterTheFirst/nick/releases/latest";
    static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    static public void checkForUpdate(JavaPlugin plugin) {
        try {
            Release latest = getLatestRelease();

            PluginDescriptionFile desc = plugin.getDescription();
            String version = desc.getVersion();
            String name = desc.getName();

            if (latest.tagName.equals(version)) {
                plugin.getLogger().info(ChatColor.GREEN + name + " is fully up to date!");
            } else {
                plugin.getLogger().warning(ChatColor.GOLD + name + " is running build '" + version
                        + "' where as the latest build is '" + latest.tagName + "'!");
                for (Asset asset : latest.assets) {
                    plugin.getLogger().warning(ChatColor.GOLD + "Visit " + asset.browserDownloadUrl + " to download the latest version of " + name);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static private Release getLatestRelease() throws IOException {
        URLConnection connection = new URL(releaseUrl).openConnection();

        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        InputStream is = connection.getInputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

        return gson.fromJson(rd, Release.class);
    }

    class Release {
        public URL url;
        public URL htmlUrl;
        public int id;
        public String tagName;
        public String name;
        public Date createdAt;
        public ArrayList<Asset> assets;

        class Asset {
            public URL url;
            public int id;
            public String name;
            public String contentType;
            public int size;
            public Date createdAt;
            public URL browserDownloadUrl;
        }
    }
}
