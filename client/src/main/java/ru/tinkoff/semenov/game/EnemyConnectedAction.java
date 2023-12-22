package ru.tinkoff.semenov.game;

import ru.tinkoff.semenov.ClientRoomHandler;

public class EnemyConnectedAction implements ru.tinkoff.semenov.Action {

    private final ClientRoomHandler clientRoomHandler;

    public EnemyConnectedAction(ClientRoomHandler clientRoomHandler) {
        this.clientRoomHandler = clientRoomHandler;
    }

    @Override
    public void handle(String args) {
        clientRoomHandler.setEnemyNickname(args);
        System.out.println(args);
    }
}
