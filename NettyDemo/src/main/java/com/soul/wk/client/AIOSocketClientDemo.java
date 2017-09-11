package com.soul.wk.client;

import com.soul.wk.clienthandler.AsyncTimeClientHandler;

import java.awt.print.Pageable;

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
