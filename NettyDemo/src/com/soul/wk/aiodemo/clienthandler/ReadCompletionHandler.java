package com.soul.wk.aiodemo.clienthandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel socketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel result) {

        this.socketChannel = result;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {

        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);

        try {
            String req = new String(body, "utf-8");
            System.out.println("The Server has accept the req: " + req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";

            doWrite(currentTime);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void doWrite(String currentTime) {
        if (currentTime != null && currentTime.trim().length() > 0) {

            try {
                final byte[] timeBytes = currentTime.getBytes("utf-8");
                final ByteBuffer writerBuffer = ByteBuffer.allocate(timeBytes.length);
                writerBuffer.put(timeBytes);

                writerBuffer.flip();
                socketChannel.write(writerBuffer, writerBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        if( attachment.hasRemaining())
                            socketChannel.write(attachment,attachment,this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            socketChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

        try {
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
