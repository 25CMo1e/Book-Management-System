// src/main/java/com/example/supermarket/server/ClientHandler.java
package com.example.bookManagementDEMO2.server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;




public class ClientHandler implements Runnable {
    private Socket socket; // 客户端套接字 / Client socket
    private BufferedReader in; // 输入流，用于接收客户端请求 / Input stream for receiving client requests
    private BufferedWriter out; // 输出流，用于向客户端发送响应 / Output stream for sending responses to the client

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            // 初始化输入输出流 / Initialize input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息 / Print exception details
        }
    }

    @Override
    public void run() {
        String request; // 客户端请求消息 / Client request message
        try {
            while ((request = in.readLine()) != null) { // 读取客户端的每一行请求 / Read each line of client request
                System.out.println("Receive the request: " + request); // 打印收到的请求 / Print received request
                String response = handleRequest(request); // 处理请求并生成响应 / Process the request and generate a response
                out.write(response); // 发送响应 / Send the response
                out.newLine(); // 换行，确保响应发送完整 / Add a newline to ensure the response is complete
                out.flush(); // 刷新缓冲区 / Flush the buffer
            }
        } catch (IOException e) {
            System.out.println("The client is disconnected: " + socket.getInetAddress());
            // 客户端断开连接时的提示信息 / Message indicating client disconnection
        } finally {
            try {
                socket.close(); // 关闭套接字 / Close the socket
                System.out.println("The client connection is closed");
                // 客户端连接已关闭的提示信息 / Message indicating client connection closure
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息 / Print exception details
            }
        }
    }

    private String handleRequest(String request) {
        // 请求格式假设为: COMMAND|param1|param2|... / Request format assumed as: COMMAND|param1|param2|...
        String[] parts = request.split("\\|"); // 分割请求字符串 / Split request string
        String command = parts[0]; // 提取命令部分 / Extract command part

        try {
            switch (command) {
                case "ADD":
                    return addProduct(parts); // 处理商品请求 / Handle product request
                case "QUERY":
                    return queryProducts(parts);
                case "DELETE":
                    return deleteProduct(parts);
                case "UPDATE":
                    return updateProduct(parts);
                case "SALE":
                    return saleProduct(parts);
                case "STAT":
                    return statProducts(parts);
                case "SAVE":
                    return saveProducts();
                case "LOAD":
                    return loadProducts();
                default:
                    return "ERROR|Unknown command";
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息 / Print exception details
            return "ERROR|An error occurred while processing the request";
        }
    }



    private String addProduct(String[] params) throws SQLException {
        // 检查参数数量是否足够 / Check if there are sufficient parameters
        if (params.length < 7) return "ERROR|Insufficient parameter";

        // 提取商品信息 / Extract product information
        String category = params[1];
        String name = params[2];
        double price = Double.parseDouble(params[3]);
        int stock = Integer.parseInt(params[4]);
        String manufacturer = params[5];
        String brand = params[6];

        // SQL 插入语句 / SQL insert statement
        String sql = "INSERT INTO products (category, name, price, stock, manufacturer, brand) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); // 获取数据库连接 / Get database connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 设置 SQL 参数 / Set SQL parameters
            stmt.setString(1, category);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, manufacturer);
            stmt.setString(6, brand);
            stmt.executeUpdate(); // 执行插入操作 / Execute the insert operation
        }
        return "SUCCESS|Book added successfully"; // 返回成功信息 / Return success message
    }

    private String queryProducts(String[] params) throws SQLException {
        // 支持按类别、名称、厂家查询 / Support query by category, name, or manufacturer
        if (params.length < 3) return "ERROR|Insufficient parameter"; // 检查参数是否足够 / Check if parameters are sufficient
        String queryType = params[1]; // Query type
        String queryValue = params[2]; //  Query value

        String sql = ""; // SQL 查询语句 / SQL query statement
        switch (queryType) {
            case "category":
                sql = "SELECT * FROM products WHERE category LIKE ?";
                break; // 按类别查询 / Query by category
            case "name":
                sql = "SELECT * FROM products WHERE name LIKE ?";
                break;
            case "manufacturer":
                sql = "SELECT * FROM products WHERE manufacturer LIKE ?";
                break;
            default:
                return "ERROR|Unknown query type";
        }

        List<String> results = new ArrayList<>(); // 用于存储查询结果 / List to store query results
        try (Connection conn = Database.getConnection(); // 获取数据库连接 / Get database connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + queryValue + "%"); // 使用模糊匹配 / Use fuzzy matching
            ResultSet rs = stmt.executeQuery(); // 执行查询 / Execute the query
            while (rs.next()) {
                // 拼接查询结果 / Concatenate query results
                String product = rs.getInt("id") + "," +
                        rs.getString("category") + "," +
                        rs.getString("name") + "," +
                        rs.getDouble("price") + "," +
                        rs.getInt("stock") + "," +
                        rs.getString("manufacturer") + "," +
                        rs.getString("brand");
                results.add(product); // 添加到结果列表 / Add to results list
            }
        }

        if (results.isEmpty()) {
            return "NOT_FOUND|This record does not exist!"; // 没有找到匹配记录 / No matching record found
        } else {
            StringBuilder sb = new StringBuilder("FOUND"); // 初始化返回结果 / Initialize return result
            for (String prod : results) {
                sb.append("|").append(prod); // 将结果拼接到返回值中 / Append results to the return value
            }
            return sb.toString(); // 返回查询结果 / Return query results
        }
    }

    private String deleteProduct(String[] params) throws SQLException {
        // 检查参数是否足够 / Check if parameters are sufficient
        if (params.length < 3) return "ERROR|Insufficient parameter";
        String category = params[1]; //  Category
        String name = params[2]; // Name

        // 查询记录是否存在 / Check if the record exists
        String checkSql = "SELECT * FROM products WHERE category = ? AND name = ?";
        try (Connection conn = Database.getConnection(); // Get database connection
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, category); //  Set category parameter
            checkStmt.setString(2, name); // Set name parameter
            ResultSet rs = checkStmt.executeQuery(); // 执行查询 / Execute query
            if (!rs.next()) { //  If no record found
                return "NOT_FOUND|The item does not exist"; //  Return not found message
            }
        }

        // 删除记录 / Delete the record
        String deleteSql = "DELETE FROM products WHERE category = ? AND name = ?";
        try (Connection conn = Database.getConnection(); // 获取数据库连接 / Get database connection
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setString(1, category); //  Set category parameter
            deleteStmt.setString(2, name); //  Set name parameter
            deleteStmt.executeUpdate(); // 执行删除操作 / Execute delete operation
        }
        return "SUCCESS|Book deleted successfully"; // 返回成功信息 / Return success message
    }

    private String updateProduct(String[] params) throws SQLException {
        // 假设参数为: UPDATE|id|field|newValue / Assume parameters: UPDATE|id|field|newValue
        if (params.length < 4) return "ERROR|Insufficient parameter"; // 检查参数是否足够 / Check if parameters are sufficient
        int id = Integer.parseInt(params[1]); // 提取商品ID / Extract product ID
        String field = params[2]; // 需要更新的字段 / Field to be updated
        String newValue = params[3]; // 更新的值 / New value

        // 验证字段名，防止SQL注入 / Validate field name to prevent SQL injection
        if (!field.matches("category|name|price|stock|manufacturer|brand")) {
            return "ERROR|Invalid field name"; // 返回字段无效错误 / Return invalid field error
        }

        // 构造更新语句 / Construct update statement
        String sql = "UPDATE products SET " + field + " = ? WHERE id = ?";
        try (Connection conn = Database.getConnection(); // 获取数据库连接 / Get database connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 根据字段类型设置参数 / Set parameter based on field type
            if (field.equals("price")) {
                stmt.setDouble(1, Double.parseDouble(newValue)); // 设置价格参数 / Set price parameter
            } else if (field.equals("stock")) {
                stmt.setInt(1, Integer.parseInt(newValue)); // 设置库存参数 / Set stock parameter
            } else {
                stmt.setString(1, newValue); // 设置字符串参数 / Set string parameter
            }
            stmt.setInt(2, id); // 设置商品ID参数 / Set product ID parameter
            int rows = stmt.executeUpdate(); // 执行更新操作 / Execute update operation
            if (rows > 0) { // 检查是否有记录被更新 / Check if any record was updated
                return "SUCCESS|Book update success"; // 返回成功信息 / Return success message
            } else {
                return "NOT_FOUND|No corresponding book found"; // 返回未找到信息 / Return not found message
            }
        }
    }


    private String saleProduct(String[] params) throws SQLException {
        // 检查参数是否足够 / Check if parameters are sufficient
        if (params.length < 4) return "ERROR|Insufficient parameter";
        String category = params[1]; // 商品类别 / Product category
        String name = params[2]; // 商品名称 / Product name
        int quantity = Integer.parseInt(params[3]); // 购买数量 / Quantity to purchase

        // 查询库存和价格 / Query stock and price
        String selectSql = "SELECT stock, price FROM products WHERE category = ? AND name = ?";
        try (Connection conn = Database.getConnection(); // Get database connection
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, category); //  Set category parameter
            selectStmt.setString(2, name); // Set name parameter
            ResultSet rs = selectStmt.executeQuery(); //  Execute query
            if (rs.next()) { // 检查记录是否存在 / Check if record exists
                int stock = rs.getInt("stock"); // 当前库存 / Current stock
                double price = rs.getDouble("price"); // 单价 / Unit price
                if (stock < quantity) { // 检查库存是否充足 / Check if stock is sufficient
                    return "INSUFFICIENT|understock";
                } else {
                    int newStock = stock - quantity; // 计算更新后的库存 / Calculate updated stock
                    String updateSql = "UPDATE products SET stock = ? WHERE category = ? AND name = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newStock); // 更新库存参数 / Set updated stock parameter
                        updateStmt.setString(2, category);
                        updateStmt.setString(3, name);
                        updateStmt.executeUpdate(); // 执行更新操作 / Execute update operation
                    }
                    double total = price * quantity; // 计算总价 / Calculate total price
                    return "SUCCESS|Purchase successful, total price: " + total;
                }
            } else {
                return "NOT_FOUND|The book does not exist"; // 未找到商品 / Product not found
            }
        }
    }

    private String statProducts(String[] params) throws SQLException {
        // 支持按价格、库存量、生产厂家排序 / Support sorting by price, stock, or manufacturer
        String sortBy = params.length > 1 ? params[1] : "price"; // 获取排序字段 / Get sorting field
        String sql = "SELECT * FROM products ORDER BY ";

        // 构造排序语句 / Construct sorting query
        switch (sortBy) {
            case "price":
                sql += "price DESC"; // 按价格降序 / Sort by price descending
                break;
            case "stock":
                sql += "stock DESC"; // 按库存量降序 / Sort by stock descending
                break;
            case "manufacturer":
                sql += "manufacturer DESC"; // 按生产厂家降序 / Sort by manufacturer descending
                break;
            default:
                sql += "id DESC"; // 默认按ID降序 / Default sort by ID descending
                break;
        }

        List<String> results = new ArrayList<>();
        try (Connection conn = Database.getConnection(); // 获取数据库连接 / Get database connection
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql); // 执行查询 / Execute query
            while (rs.next()) { // 遍历结果集 / Iterate through result set
                String product = rs.getInt("id") + "," +
                        rs.getString("category") + "," +
                        rs.getString("name") + "," +
                        rs.getDouble("price") + "," +
                        rs.getInt("stock") + "," +
                        rs.getString("manufacturer") + "," +
                        rs.getString("brand");
                results.add(product);
            }
        }

        if (results.isEmpty()) { // 检查结果集是否为空 / Check if result set is empty
            return "EMPTY|Stock empty";
        } else {
            StringBuilder sb = new StringBuilder("STAT"); // 构造返回信息 / Construct return message
            for (String prod : results) {
                sb.append("|").append(prod);
            }
            return sb.toString();
        }
    }

    private String saveProducts() {
        // 示例：将所有商品信息保存到文件 / Example: Save all product information to a file
        // 具体实现根据需求添加 / Specific implementation depends on requirements
        return "SUCCESS|Book information has been saved";
    }

    private String loadProducts() {
        // 示例：从文件加载商品信息到数据库 / Example: Load product information from a file into the database
        // 具体实现根据需求添加 / Specific implementation depends on requirements
        return "SUCCESS|Book information is loaded";
    }

}
