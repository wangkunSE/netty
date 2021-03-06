package com.soul.wk.netty.protobuf.handler;

import com.soul.wk.netty.protobuf.firstdemo.SubscribeReqProto;
import com.soul.wk.netty.protobuf.firstdemo.SubscribeRespProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubscribeServerHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("server channel read");
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;

        if ("mei mei".equals(req.getUserName())) {
            System.out.println("Service accept client subscribe req: [ " + req.toString() + " ] ");
            ctx.writeAndFlush(resp(req));
        }
    }

    private SubscribeRespProto.SubscribeResp resp(SubscribeReqProto.SubscribeReq subReqId) {

        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setRespCode(200);
        builder.setSubReqId(subReqId.getSubReqId());
        builder.setDesc(subReqId.getProductName() + " order succeed , three days later , arrive at the address: " + subReqId.getAddress());

        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
