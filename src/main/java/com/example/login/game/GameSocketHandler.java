package com.example.login.game;

import cn.dev33.satoken.stp.StpUtil;
import com.example.login.game.model.Answer;
import com.example.login.game.model.GameMessage;
import com.example.login.game.model.GameUser;
import com.example.login.game.model.MessageType;
import com.example.login.utils.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class GameSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private final StringRedisTemplate redisTemplate;

	private final GameManager gameManager;

	private final MatchPool matchPool;

	public GameSocketHandler(StringRedisTemplate redisTemplate, GameManager gameManager, MatchPool matchPool) {
		this.redisTemplate = redisTemplate;
		this.gameManager = gameManager;
		this.matchPool = matchPool;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception { // (1)

		String text = msg.text();

		ObjectMapper objectMapper = null;
		try {
			objectMapper = JSONUtils.getObjectMapper();
			GameMessage gameMessage = objectMapper.readValue(text, GameMessage.class);

			// 分发处理任务
			if (gameMessage.getType() == MessageType.START_MATCHING) {
				// 检查有没有登陆
				String token = gameMessage.getData();
				if (StpUtil.getLoginIdByToken(token) == null) {
					System.out.println("未登录");
					ctx.channel().close();
					ctx.close();
					return;
				}

				// 玩家已经在游戏中，不能进行匹配
				Integer opponent = gameManager.getOpponent(gameMessage.getId());
				if (opponent != null) {
					GameMessage message = new GameMessage(-1, MessageType.CANCEL_MATCHING, "上一局还未结束，请等待！");
					ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(message)));
					ctx.close();
					return;
				}

				// 防止玩家重复加入匹配池
				ChannelHandlerContext channel = gameManager.getChannelById(gameMessage.getId());
				if (channel != null) {
					System.out.println("repeat");
					matchPool.remove(gameMessage.getId());
					gameManager.removeChannel(gameMessage.getId());
					GameMessage message = new GameMessage(-1, MessageType.CANCEL_MATCHING, "");
					channel.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(message)));
					channel.close();
				}

				System.out.println(gameMessage);
				Integer id = gameMessage.getId();

				startMatching(id, ctx);
			} else if (gameMessage.getType() == MessageType.READY) {
				playerOnReady(ctx);
			} else if (gameMessage.getType() == MessageType.ANSWER) {
				Answer answer = objectMapper.readValue(gameMessage.getData(), Answer.class);
				checkAndSend(gameMessage.getId(), answer);
			} else if (gameMessage.getType() == MessageType.COMMIT) {
				commitGame();
			} else if (gameMessage.getType() == MessageType.CANCEL_MATCHING) {
				matchPool.remove(gameMessage.getId());
				gameManager.removeChannel(gameMessage.getId());
			} else {

				ctx.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (objectMapper != null) {
				JSONUtils.returnObjectMapper(objectMapper);
			}
		}

	}

	private void playerOnReady(ChannelHandlerContext ctx) {
//		System.out.println("ready");
		gameManager.setGamedReady(ctx);

	}

	private void startMatching(Integer playerId, ChannelHandlerContext ctx) {
		System.out.println("start matching : " + playerId + "  ip: " + ctx.channel().remoteAddress());

		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		String score = opsForValue.get(playerId + ":score");
		String name = opsForValue.get(playerId + ":name");

		if (score == null) {
			score = "1000";
		}

		GameUser gameUser = new GameUser(playerId, name, Integer.parseInt(score), 0);

		matchPool.add(gameUser, ctx);

	}

	private void checkAndSend(Integer id, Answer answer) {
		gameManager.validateAnswer(id, answer);
//		System.out.println("check and send : " + answer);
	}

	private void commitGame() {
		System.out.println("commit game");
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception { // (2)
		Channel incoming = ctx.channel();
		log.info("Client:" + incoming.remoteAddress() + "加入");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { // (3)
		Channel incoming = ctx.channel();
		Integer channel = gameManager.getIdByChannel(ctx);
		log.info("Client:" + incoming.remoteAddress() + "离开");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		log.info("Client:" + incoming.remoteAddress() + "在线");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		log.info("Client:" + incoming.remoteAddress() + "掉线");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) // (7)
			throws Exception {
		Channel incoming = ctx.channel();
		log.error("Client:" + incoming.remoteAddress() + "异常");
		gameManager.removeChannel(ctx);
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}

}
