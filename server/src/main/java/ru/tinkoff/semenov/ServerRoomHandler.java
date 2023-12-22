package ru.tinkoff.semenov;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.tinkoff.semenov.commands.Command;
import ru.tinkoff.semenov.commands.EnemyConnectedCommand;

import java.util.HashMap;
import java.util.Map;

import static ru.tinkoff.semenov.Utils.getArgsAsStringInRoom;
import static ru.tinkoff.semenov.Utils.getPlayerInRoomMessages;
import static ru.tinkoff.semenov.Utils.getCommandInRoomMessages;


@ChannelHandler.Sharable
public class ServerRoomHandler extends SimpleChannelInboundHandler<String> {

    public static final String SEPARATOR = "|";
    private final String roomName;
    private final PlayerData firstPlayer;
    private PlayerData secondPlayer;
    private final Map<String, Command> commands = new HashMap<>(){{
        put("ENEMY_CONNECTED", new EnemyConnectedCommand());
    }};

    public ServerRoomHandler(String roomName, PlayerData firstPlayer) {
        this.roomName = roomName;
        this.firstPlayer = firstPlayer;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        String player = getEnemyPlayer(getPlayerInRoomMessages(msg));
        String command = getCommandInRoomMessages(msg);
        String args = getArgsAsStringInRoom(msg);
        System.out.println(msg);
        String response = commands.get(command).execute(args);
        ctx.channel().writeAndFlush(player + SEPARATOR + response);
    }

    private String getEnemyPlayer(String player) {
        if (player.equals("P1"))
            return "P2";
        return "P1";
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switchToDefaultHandler(ctx);

    }

    private void switchToDefaultHandler(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        pipeline.replace("roomHandler", "defaultHandler", firstPlayer.playerHandler());
        pipeline.replace("roomHandler", "defaultHandler", secondPlayer.playerHandler());
    }

    public void setSecondPlayer(PlayerData secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getRoomName() {
        return roomName;
    }
    public PlayerData getFirstPlayer() {
        return firstPlayer;
    }
}
