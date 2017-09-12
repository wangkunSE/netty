package com.soul.wk.aiodemo.client;

import com.soul.wk.aiodemo.clienthandler.AsyncTimeClientHandler;

/**
 * @author WK
 * AIO 客户端实例
 */
public class AIOSocketClientDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 11123;

    public static void main(String[] args) {
            startClient();
    }

    private static void startClient() {
        new Thread(new AsyncTimeClientHandler(HOST, PORT),"AIO-AsycTimeClientHandler-001").start();
    }

}
