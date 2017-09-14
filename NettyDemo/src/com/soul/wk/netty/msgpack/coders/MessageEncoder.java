package com.soul.wk.netty.msgpack.coders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        MessagePack messagePack = new MessagePack();
        byteBuf.writeBytes(messagePack.write(o));
    }
}
