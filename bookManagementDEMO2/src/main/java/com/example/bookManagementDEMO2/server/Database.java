// src/main/java/com/example/supermarket/server/Database.java
package com.example.bookManagementDEMO2.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/bookManagementDEMO2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // 替换为你的数据库用户名change to your MySQL user name
    private static final String PASSWORD = "2004"; // 替换为你的数据库密码change to your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
