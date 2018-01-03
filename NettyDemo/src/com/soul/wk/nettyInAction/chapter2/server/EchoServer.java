package com.soul.wk.nettyInAction.chapter2.server;

import com.soul.wk.nettyInAction.chapter2.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import static com.soul.wk.constants.Constants.MY_LOCAL_PORT;

/***
 * @author wangkun1
 * @version 2018/1/3 
 */
public class EchoServer {



    public static void main(String[] args) {
        try {
            new EchoServer().start(MY_LOCAL_PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start(Integer myLocalPort) throws InterruptedException {
        final EchoServerHandler handler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(myLocalPort))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handler);
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
