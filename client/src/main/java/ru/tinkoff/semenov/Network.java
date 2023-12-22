package ru.tinkoff.semenov;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.tinkoff.semenov.controllers.RoomController;
import ru.tinkoff.semenov.enums.Command;
import ru.tinkoff.semenov.enums.GameCommand;

/**
 * Основной класс сетевого взаимодействия с сервером. Здесь настраивается соединение, устанавливаются
 * обработчики ответов, выполняется отправка команд на сервер.
 * <br>*ВАЖНО*: даже в команду без аргументов необходимо добавить {@link Network#SEPARATOR}!
 */
public class Network {

    public static final String SEPARATOR = "|";

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private final DefaultClientHandler defaultHandler = new DefaultClientHandler();

    private SocketChannel channel;
    private ClientRoomHandler roomHandler;
    private boolean loadCanceled;


    public Network() {
        Thread t = new Thread(() -> {
            System.out.println("Hello cycle !");
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                ChannelPipeline pipeline = socketChannel.pipeline();

                                pipeline.addLast("stringDecoder", new StringDecoder());
                                pipeline.addLast("stringEncoder", new StringEncoder());
                                pipeline.addLast(new ChunkedWriteHandler());
                                pipeline.addLast("defaultHandler", defaultHandler);
                            }
                        });
//                        .handler(new LoggingHandler(LogLevel.INFO));
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void register(String login, String password) {
        channel.writeAndFlush(Command.REGISTER.name() + SEPARATOR + login.length() + login + password);
    }

    public void authorize(String login, String password) {
        channel.writeAndFlush(
                Command.AUTH.name()
                        + SEPARATOR
                        + login.length()
                        + login + password
        );
    }

    public void cutFile(String file, String destination) {
        channel.writeAndFlush(Command.CUT.name() + SEPARATOR + file + SEPARATOR + destination);
    }

    public void copyFile(String file, String destination) {
        channel.writeAndFlush(Command.COPY.name() + SEPARATOR + file + SEPARATOR + destination);
    }

    public void loadFile(ChunkedFile file, String destination) {
        channel.writeAndFlush(Command.LOAD.name() + SEPARATOR + destination + SEPARATOR + file.length());
        new Thread(() -> {
            try {
                while (!file.isEndOfInput() && !loadCanceled) {
                    channel.writeAndFlush(file.readChunk(ByteBufAllocator.DEFAULT));
                }
                file.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void createNewRoom(String roomName, String nickname) {
        channel.writeAndFlush(Command.NEW_ROOM.name() + SEPARATOR + roomName + SEPARATOR + nickname);
    }

    public void deleteFile(String path) {
        channel.writeAndFlush(Command.DELETE.name() + SEPARATOR + path);
    }

    public void close() {
        channel.close();
    }

    public DefaultClientHandler getDefaultHandler() {
        return defaultHandler;
    }

    public ClientRoomHandler getRoomHandler() {
        return roomHandler;
    }

    public void setLoadCanceled(boolean loadCanceled) {
        this.loadCanceled = loadCanceled;
    }

    public void connectToExistedRoom(String nickname, String enemyNickname, RoomController controller) {
        this.roomHandler = new ClientRoomHandler(nickname, controller, defaultHandler);
        roomHandler.setEnemyNickname(enemyNickname);
        channel.pipeline().replace("defaultHandler", "roomHandler", roomHandler);
        channel.writeAndFlush("P2" + SEPARATOR + "ENEMY_CONNECTED" + SEPARATOR + nickname);
    }

    public void connectToCreatedRoom(String nickname, RoomController controller) {
        this.roomHandler = new ClientRoomHandler(nickname, controller, defaultHandler);
        channel.pipeline().replace("defaultHandler", "roomHandler", roomHandler);
        roomHandler.setFirstPlayer(true);
        // channel.writeAndFlush("START");
    }

    public void connect(String room, String nickname) {
        channel.writeAndFlush(Command.CONNECT.name() + SEPARATOR + room + SEPARATOR + nickname);
    }

    public void buttonPressed(String nickname) {
        channel.writeAndFlush("button pressed by " + nickname);
    }

    public void skipTurn() {
        channel.writeAndFlush(GameCommand.SKIP.name());
    }
}
