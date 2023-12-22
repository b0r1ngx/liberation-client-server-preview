package ru.tinkoff.semenov;

import io.netty.channel.*;
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
    private final Player firstPlayer;
    private Player secondPlayer;
    private final Map<String, Command> commands = new HashMap<>(){{
        put("ENEMY_CONNECTED", new EnemyConnectedCommand());
//        put("SKIP", new SkipCommand());
    }};

    public ServerRoomHandler(String roomName, Player firstPlayer) {
        this.roomName = roomName;
        this.firstPlayer = firstPlayer;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        Player player = getEnemyPlayer(ctx.channel());
        String command = getCommandInRoomMessages(msg);
        String args = getArgsAsStringInRoom(msg);
        System.out.println(msg);
        String response = commands.get(command).execute(args);
        player.channel().writeAndFlush(response);
    }

    private Player getEnemyPlayer(Channel playerChannel) {
        if (playerChannel == firstPlayer.channel())
            return secondPlayer;
        return firstPlayer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switchToDefaultHandler(ctx);

    }

    private void switchToDefaultHandler(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        pipeline.replace("roomHandler", "defaultHandler", firstPlayer.handler());
        pipeline.replace("roomHandler", "defaultHandler", secondPlayer.handler());
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getRoomName() {
        return roomName;
    }
    public Player getFirstPlayer() {
        return firstPlayer;
    }
}
