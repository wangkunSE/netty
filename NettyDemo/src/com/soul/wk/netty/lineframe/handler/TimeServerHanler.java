package com.soul.wk.netty.lineframe.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHanler extends ChannelHandlerAdapter {

    private int counter;

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        String body = (String) msg;
        System.out.println("The TimeServer receive req: " + body +
                " ; the counter is: " + ++counter);
        String currentTime = "QUERY TIME ORDER".equals(body.trim()) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";

        currentTime = currentTime + System.getProperty("line.separator");

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes("utf-8"));
        ctx.writeAndFlush(resp);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
