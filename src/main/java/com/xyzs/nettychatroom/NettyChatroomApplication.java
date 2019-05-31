package com.xyzs.nettychatroom;

import com.xyzs.nettychatroom.server.NettyWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyChatroomApplication implements CommandLineRunner {
    @Autowired
    private  NettyWebSocketServer nettyWebSocketServer;

    public static void main(String[] args) {

        SpringApplication.run(NettyChatroomApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            nettyWebSocketServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
