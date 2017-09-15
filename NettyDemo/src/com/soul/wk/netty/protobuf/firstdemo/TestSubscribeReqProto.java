package com.soul.wk.netty.protobuf.firstdemo;

import com.google.protobuf.InvalidProtocolBufferException;


public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {

        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubcirbeReq() {

        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqId(1);
        builder.setAddress("bei jing");
        builder.setUserName("美美");
        builder.setProductName("Netty book");
        return builder.build();
    }

    public static void main(String[] args) {
        try {
            SubscribeReqProto.SubscribeReq req = createSubcirbeReq();
            System.out.println("Before Code : " + req.toString());
            SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
            System.out.println("After code : " + req2.toString());
            System.out.println("Assert equal----->" + req.equals(req2));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
