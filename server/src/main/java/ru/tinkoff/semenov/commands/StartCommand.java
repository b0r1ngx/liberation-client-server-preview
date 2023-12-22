package ru.tinkoff.semenov.commands;

import ru.tinkoff.semenov.ServerRoomHandler;

public class StartCommand implements Command {
    private final ServerRoomHandler serverRoomHandler;

    public StartCommand(ServerRoomHandler serverRoomHandler) {
        this.serverRoomHandler = serverRoomHandler;
    }

    @Override
    public String execute(String args) {
        serverRoomHandler.getFirstPlayer().channel().writeAndFlush("ENEMY_CONNECTED" + ARGS_SEPARATOR + args);
        return "";
    }
}
