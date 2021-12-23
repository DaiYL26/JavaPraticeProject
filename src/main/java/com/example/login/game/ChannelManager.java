package com.example.login.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelManager {

    private final ConcurrentHashMap<Integer, ChannelHandlerContext> idMapChannel = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<ChannelHandlerContext, Integer> channelMapId = new ConcurrentHashMap<>();

    public void addPlayer(Integer id, ChannelHandlerContext ctx) {
        idMapChannel.put(id, ctx);
        channelMapId.put(ctx, id);
    }

    public void removePlayer(ChannelHandlerContext channel) {
        Integer playerId = channelMapId.remove(channel);
        idMapChannel.remove(playerId);
    }

    public void removeChannel(Integer id) {
        ChannelHandlerContext remove = idMapChannel.remove(id);
        channelMapId.remove(remove);
    }

    public void removeChannel(ChannelHandlerContext ctx) {
        Integer remove = channelMapId.remove(ctx);
        idMapChannel.remove(remove);
    }

    public ChannelHandlerContext getChannelById(Integer id) {
        return idMapChannel.get(id);
    }

    public Integer getIdByChannel(ChannelHandlerContext ctx) {
        return channelMapId.get(ctx);
    }

    public void sizeInfo() {
        System.out.println(idMapChannel.size() + " " + channelMapId.size());
    }
}
