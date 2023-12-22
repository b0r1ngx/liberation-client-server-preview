package ru.tinkoff.semenov;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.tinkoff.semenov.controllers.RoomController;
import ru.tinkoff.semenov.game.EnemyConnectedAction;

import java.util.HashMap;
import java.util.Map;

import static ru.tinkoff.semenov.Utils.getArgs;
import static ru.tinkoff.semenov.Utils.getArgsAsStringInRoom;
import static ru.tinkoff.semenov.Utils.getPlayerInRoomMessages;
import static ru.tinkoff.semenov.Utils.getStatusInRoomMessages;


public class ClientRoomHandler extends SimpleChannelInboundHandler<String> {

    private static final String SEPARATOR = "|";
    /**
     * Ссылка на стандартный хендлер работы со строковыми командами в лобби, при завершении файлового обмена переключимся на него
     */
    private final DefaultClientHandler defaultHandler;
    private final RoomController controller;
    private final Map<String, Action> gameActions = new HashMap<>() {{
        put("ENEMY_CONNECTED", new EnemyConnectedAction(ClientRoomHandler.this));
    }};

    private boolean isFirstPlayer = false;
    private String enemyNickname;

    public ClientRoomHandler(String enemyNickname, RoomController controller, DefaultClientHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        this.enemyNickname = enemyNickname;
        this.controller = controller;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        String player = getPlayerInRoomMessages(msg);
        String action = getStatusInRoomMessages(msg);
        String args = getArgsAsStringInRoom(msg);
        System.out.println(msg);
        if (player.equals("P1") && isFirstPlayer || player.equals("P2") && !isFirstPlayer)
            gameActions.get(action).handle(args);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switchToDefaultHandler(ctx);

    }

    private void switchToDefaultHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().replace("roomHandler", "defaultHandler", defaultHandler);

    }

    public void setEnemyNickname(String enemyNickname) {
        this.enemyNickname = enemyNickname;
        controller.setSecondPlayer(enemyNickname);
    }

    public void setFirstPlayer(boolean firstPlayer) {
        isFirstPlayer = firstPlayer;
    }
}
