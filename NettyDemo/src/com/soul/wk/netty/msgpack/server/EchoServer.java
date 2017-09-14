package com.soul.wk.netty.msgpack.server;

import com.soul.wk.netty.msgpack.coders.MessageDecoder;
import com.soul.wk.netty.msgpack.coders.MessageEncoder;
import com.soul.wk.netty.msgpack.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    private static final int PORT = 11123;

    public void bind(int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                    .option(ChannelOption.SO_BACKLOG, 100)
//                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            socketChannel.pipeline().addLast("message decoder", new MessageDecoder());
                            socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("message encoder", new MessageEncoder());
                            socketChannel.pipeline().addLast(new EchoServerHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new EchoServer().bind(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
