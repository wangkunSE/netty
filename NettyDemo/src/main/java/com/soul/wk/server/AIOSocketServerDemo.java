package com.soul.wk.server;

import com.soul.wk.clienthandler.AsyncTimeServerHandler;

public class AIOSocketServerDemo {

    private static final int PORT = 11123;

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {

        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(PORT);
        new Thread(timeServerHandler,"AIO-ServerHandler-001").start();

    }
}
