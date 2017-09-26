package com.soul.wk.netty.privateprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ProtocolTest {

    public static void main(String[] args) {
        byteBufTest();
    }

    private static void byteBufTest() {

        ByteBuf buf = Unpooled.buffer(101);
        ByteBuf tarbuf = Unpooled.buffer(100);

        for (int i = 0; i < 101; i++) {
            buf.writeByte(i);
        }

//        buf.readByte();
//        buf.skipBytes(1);
//        tarbuf.writeBytes(buf);
//        buf.writeBytes(tarbuf);
        buf.readBytes(tarbuf);

        System.out.println("buf readerIndex:" + buf.readerIndex());
        System.out.println("buf writerIndex:" + buf.writerIndex());


        System.out.println("tarbuf readerIndex:" + tarbuf.readerIndex());
        System.out.println("tarbuf writerIndex:" + tarbuf.writerIndex());


        while (tarbuf.isReadable()) {
            System.out.println(tarbuf.readByte());
        }


    }
}
