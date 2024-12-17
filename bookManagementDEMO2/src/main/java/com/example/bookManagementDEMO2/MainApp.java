// src/main/java/com/example/supermarket/MainApp.java
package com.example.bookManagementDEMO2;

import com.example.bookManagementDEMO2.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Server server; // 服务器对象 (Server object)
    private Thread serverThread; // 服务器线程 (Server thread)

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 启动服务器 (Start the server)
        server = new Server(); // 创建服务器实例 (Create server instance)
        serverThread = new Thread(server); // 将服务器对象包装成线程 (Wrap server instance into a thread)
        serverThread.start(); // 启动服务器线程 (Start the server thread)
        System.out.println("服务器已启动，监听端口 12345"); // 控制台输出服务器启动信息 (Print to console that server is running on port 12345)

        // 加载JavaFX界面 (Load the JavaFX UI)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bookManagementDEMO2/client/main.fxml")); // 加载FXML界面文件 (Load the FXML file for the UI)
        try {
            Scene scene = new Scene(loader.load()); // 创建场景并加载FXML文件 (Create a Scene and load the FXML)
            primaryStage.setScene(scene); // 设置主舞台的场景 (Set the scene to the primary stage)
            primaryStage.setTitle("Book Management System"); // 设置主舞台的标题 (Set the title of the primary stage)
            primaryStage.show(); // 显示主舞台 (Show the primary stage)
            System.out.println("JavaFX page has been loaded"); // 控制台输出界面加载成功信息 (Print to console that the JavaFX page has been loaded)
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace if an exception occurs)
            throw e; // 重新抛出异常，以触发上层的异常处理 (Re-throw the exception to trigger upper-level exception handling)
        }

        // 在应用关闭时停止服务器 (Stop the server when the application is closed)
        primaryStage.setOnCloseRequest(event -> {
            server.stop(); // 停止服务器 (Stop the server)
            Platform.exit(); // 退出JavaFX应用 (Exit the JavaFX application)
            System.exit(0); // 退出JVM (Exit the JVM)
        });
    }

    public static void main(String[] args) {
        launch(args); // 启动JavaFX应用 (Launch the JavaFX application)
    }
}
