package com.dusterthefirst.nick;

import java.util.Collections;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class PacketListener extends PacketAdapter {
    Players players;

    public PacketListener(NickPlugin plugin, Players players) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO);
        this.players = players;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        if (packet.getPlayerInfoAction().readSafely(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER)
            return;

        PlayerInfoData playerData = packet.getPlayerInfoDataLists().readSafely(0).get(0);
        WrappedGameProfile profile = playerData.getProfile();

        PlayerInfoData newPlayerData = new PlayerInfoData(
                profile.withName(players.getInfo(profile.getUUID()).getNicknameColored()), playerData.getLatency(),
                playerData.getGameMode(), playerData.getDisplayName());

        packet.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(newPlayerData));
    }
}
