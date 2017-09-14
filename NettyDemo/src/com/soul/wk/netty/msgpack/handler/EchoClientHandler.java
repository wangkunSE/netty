package com.soul.wk.netty.msgpack.handler;

import com.soul.wk.netty.msgpack.client.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {


    private static final int SENDNUM = 1000;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        User[] users = Users();

        for (int i = 0; i < users.length; i++) {

            ctx.write(users[i]);
        }
        ctx.flush();

    }

    private User[] Users() {
        User[] users = new User[SENDNUM];
        User user = null;
        for (int i = 0; i < SENDNUM; i++) {
            user = new User();
            user.setAge(i);
            user.setName("ABCDEFG--->" + i);
            users[i] = user;
        }
        return users;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("Client receive the msgpack message :" + msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
