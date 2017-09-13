package com.soul.wk.netty.noframe.client;


import com.soul.wk.netty.noframe.handler.TimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    private static final String HOST = "localhost";
    private static final int PORT = 11123;

    public static void main(String[] args) {
        try {
            new TimeClient().connect(PORT, HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect(int port, String host) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            ChannelFuture channelFutere = bootstrap.connect(host, port).sync();
            channelFutere.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
