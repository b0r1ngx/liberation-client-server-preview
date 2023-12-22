package ru.tinkoff.semenov.commands;

import ru.tinkoff.semenov.Response;

public class EnemyConnectedCommand implements Command {
    @Override
    public String execute(String args) {
        return Response.ENEMY_CONNECTED.name() + ARGS_SEPARATOR + args;
    }
}
