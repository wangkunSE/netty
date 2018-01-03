package com.soul.wk.nettyInAction.chapter2.client;

import com.soul.wk.constants.Constants;
import com.soul.wk.nettyInAction.chapter2.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/***
 * @author wangkun1
 * @version 2018/1/3 
 */
public class EchoClient implements Runnable {

    static CountDownLatch countDownLatch = new CountDownLatch(Constants.MAX_THREAD_NUMS);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < Constants.MAX_THREAD_NUMS; i++) {
            new Thread(new EchoClient()).start();
        }
        countDownLatch.await();
    }

    private void connectServer(Integer myLocalPort) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(Constants.LOCAL_HOST_ADDRESS,Constants.MY_LOCAL_PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }

    @Override
    public void run() {
        try {
            countDownLatch.countDown();
            connectServer(Constants.MY_LOCAL_PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
