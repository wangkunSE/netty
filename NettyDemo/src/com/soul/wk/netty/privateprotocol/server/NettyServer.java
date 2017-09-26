package com.soul.wk.netty.privateprotocol.server;

import com.soul.wk.netty.privateprotocol.domain.NettyConstant;
import com.soul.wk.netty.privateprotocol.edecoder.NettyMessageDecoder;
import com.soul.wk.netty.privateprotocol.edecoder.NettyMessageEncoder;
import com.soul.wk.netty.privateprotocol.handler.HeartBeatRespHandler;
import com.soul.wk.netty.privateprotocol.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {

    public static void main(String[] args) {
        try {
            new NettyServer().bind();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void bind() throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast(new NettyMessageEncoder());
                        ch.pipeline().addLast("ReadTimeOutHandler", new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        ch.pipeline().addLast("HeartBeatRespHandler", new HeartBeatRespHandler());
                    }
                });
        serverBootstrap.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
        System.out.println("Netty server start ok: " + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));

    }
}
