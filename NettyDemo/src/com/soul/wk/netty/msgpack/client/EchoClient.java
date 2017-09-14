package com.soul.wk.netty.msgpack.client;

import com.soul.wk.netty.msgpack.coders.MessageDecoder;
import com.soul.wk.netty.msgpack.coders.MessageEncoder;
import com.soul.wk.netty.msgpack.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {

    private static final String HOST = "localhost";
    private static final int PORT = 11123;
    private static final int SENDNUMBER = 5;

    public void connect(int port, String host) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("frameDecoder ", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            socketChannel.pipeline().addLast("message decoder", new MessageDecoder());
                            socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("message encoder", new MessageEncoder());
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();


        } finally {

            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        try {
            new EchoClient().connect(PORT, HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
