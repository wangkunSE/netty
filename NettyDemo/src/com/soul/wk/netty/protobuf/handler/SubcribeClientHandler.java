package com.soul.wk.netty.protobuf.handler;

import com.soul.wk.netty.protobuf.firstdemo.SubscribeReqProto;
import com.soul.wk.netty.protobuf.firstdemo.SubscribeRespProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;

public class SubcribeClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("client active");
        for (int i = 0; i < 100; i++) {
            ctx.write(subSeq(i));
        }

        ctx.flush();
    }

    private SubscribeReqProto.SubscribeReq subSeq(int i) {

        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqId(i);
        builder.setUserName("mei mei");
        builder.setAddress("bei jing " + i);
        builder.setProductName("Netty Book");
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client read");
        SubscribeRespProto.SubscribeResp resp = (SubscribeRespProto.SubscribeResp) msg;
        System.out.println("------------------------------------");
        System.out.println("订单ID： " + resp.getSubReqId());
        System.out.println("状态码: " + resp.getRespCode());
        System.out.println("订单描述： " + resp.getDesc());

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client read complete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
