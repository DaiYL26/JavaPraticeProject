package com.example.login.game;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GameServerInitializer extends
		ChannelInitializer<SocketChannel> {

	private final ApplicationContext context;

	public GameServerInitializer(ApplicationContext context) {
		this.context = context;
	}

	@Override
    public void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64*1024));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new WebSocketServerProtocolHandler("/game"));
		pipeline.addLast(context.getBean(GameSocketHandler.class));

    }
}
