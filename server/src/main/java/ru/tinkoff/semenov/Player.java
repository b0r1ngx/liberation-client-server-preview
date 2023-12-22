package ru.tinkoff.semenov;

import io.netty.channel.Channel;

public record Player(
        String nickname,
        Channel channel,
        MainHandler handler
) {
}
