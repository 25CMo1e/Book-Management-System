// src/main/java/com/example/supermarket/client/Client.java
package com.example.bookManagementDEMO2.client;

import java.io.*;
import java.net.Socket;

public class Client {
    // 单例实例
    private static Client instance;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    // 私有构造函数，防止外部实例化
    private Client() {
    }

    // 获取单例实例
    public static synchronized Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    // 连接到服务器
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
    }

    // 发送请求并接收响应
    public String sendRequest(String request) throws IOException {
        out.write(request);
        out.newLine();
        out.flush();
        return in.readLine();
    }

    // 关闭连接
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
