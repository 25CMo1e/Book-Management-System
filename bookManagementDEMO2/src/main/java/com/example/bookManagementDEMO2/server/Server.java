// src/main/java/com/example/supermarket/server/Server.java
package com.example.bookManagementDEMO2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private static final int PORT = 12345; // 服务器监听端口
    private volatile boolean running = true;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server is started, listening port " + PORT);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("The client is connected: " + clientSocket.getInetAddress());
                // 为每个客户端连接创建一个新线程
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            } else {
                System.out.println("Server stopped");
            }
        }
    }

    public void stop() {
        running = false;
        try {
            // 关闭服务器套接字以停止阻塞的 accept()
            new Socket("localhost", PORT).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
