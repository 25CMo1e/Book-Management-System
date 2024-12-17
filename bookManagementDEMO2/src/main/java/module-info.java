// src/main/java/module-info.java
module com.example.supermarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // 开放主模块给 javafx.graphics，以允许 LauncherImpl 访问 MainApp
    opens com.example.bookManagementDEMO2 to javafx.graphics;

    // 开放客户端包给 javafx.fxml，以允许 FXML 加载器访问控制器类
    opens com.example.bookManagementDEMO2.client to javafx.fxml;

    // 导出客户端和服务器端包
    exports com.example.bookManagementDEMO2.client;
    exports com.example.bookManagementDEMO2.server;
}
