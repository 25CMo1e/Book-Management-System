// src/main/java/com/example/supermarket/client/UIController.java
package com.example.bookManagementDEMO2.client;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;


import java.io.IOException;

public class UIController {
    @FXML
    private TextField categoryField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField manufacturerField;

    @FXML
    private TextField brandField;

    @FXML
    private Label addResultLabel;

    @FXML
    private ComboBox<String> queryTypeBox;

    @FXML
    private TextField queryValueField;

    @FXML
    private TextArea queryResultArea;

    // 删除功能控件
    @FXML
    private TextField deleteCategoryField;

    @FXML
    private TextField deleteNameField;

    @FXML
    private Label deleteResultLabel;

    // 修改功能控件
    @FXML
    private TextField updateIdField;

    @FXML
    private ComboBox<String> updateFieldBox;

    @FXML
    private TextField updateValueField;

    @FXML
    private Label updateResultLabel;

    // 销售功能控件
    @FXML
    private TextField saleCategoryField;

    @FXML
    private TextField saleNameField;

    @FXML
    private TextField saleQuantityField;

    @FXML
    private Label saleResultLabel;

    // 统计功能控件
    @FXML
    private ComboBox<String> statTypeBox;

    @FXML
    private ComboBox<String> chartTypeBox;



    @FXML
    private TextArea statResultArea;

    @FXML
    private Label statResultLabel;

    @FXML
    private VBox statChartContainer;


    // 构造函数，初始化客户端连接 (Constructor to initialize the client connection)
    public UIController() {
        try {
            Client.getInstance().connect("localhost", 12345); // 连接到服务器 (Connect to the server )
            System.out.println("The client is connected: /127.0.0.1"); // Print to console
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace of the exception)
            showError("unable to connect to server");
        }
    }

    @FXML
    private void initialize() {
        // 初始化 ComboBox 选项 (Initialize ComboBox options)
        queryTypeBox.getItems().addAll("category", "name", "publisher"); // 设置查询类型 (Set query types)
        queryTypeBox.setValue("category"); // 默认选择类别查询 (Default selection is "category")

        updateFieldBox.getItems().addAll("category", "name", "price", "stock", "publisher", "brand"); // 设置更新字段类型 (Set fields for updating product information)
        updateFieldBox.setValue("category"); // 默认选择类别 (Default selection is "category")

        statTypeBox.getItems().addAll("price", "stock"); // 设置统计字段类型 (Set fields for statistics)
        statTypeBox.setValue("price"); // 默认选择价格 (Default selection is "price")

    }

    @FXML
    public void handleAddProduct() {
        // 获取用户输入的产品信息 (Get the product information entered by the user)
        String category = categoryField.getText().trim();
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();
        String brand = brandField.getText().trim();

        // 校验必填字段是否为空 (Check if required fields are empty)
        if (category.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            addResultLabel.setText("Please fill in all required fields"); // 提示用户填写必填字段 (Prompt the user to fill in required fields)
            return;
        }

        try {
            double price = Double.parseDouble(priceStr); // 将价格字符串转换为 double (Convert price from String to double)
            int stock = Integer.parseInt(stockStr); // 将库存字符串转换为 int (Convert stock from String to int)
            String request = String.format("ADD|%s|%s|%s|%s|%s|%s", category, name, price, stock, manufacturer, brand); // 构造添加请求字符串 (Construct the add request string)

            // 使用新的线程来发送请求，防止阻塞 JavaFX 应用线程 (Use a new thread to send the request to prevent blocking the JavaFX application thread)
            new Thread(() -> {
                try {
                    String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send request and get the response)
                    // 在 JavaFX 应用线程中更新 UI (Update the UI in the JavaFX application thread)
                    Platform.runLater(() -> {
                        if (response.startsWith("SUCCESS")) {
                            addResultLabel.setText("successfully added！"); // 成功添加产品 (Successfully added the product)
                            clearAddFields(); // 清空添加字段 (Clear the input fields)
                        } else {
                            addResultLabel.setText("fail to add: " + response.split("\\|")[1]); // 添加失败并显示错误信息 (Failed to add the product, show error message)
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                    Platform.runLater(() -> addResultLabel.setText("Communication error")); // 显示通信错误 (Show communication error)
                }
            }).start(); // 启动新的线程 (Start the new thread)
        } catch (NumberFormatException e) {
            addResultLabel.setText("Prices and inventory must be numbers"); // 价格和库存必须是数字 (Prices and inventory must be numbers)
        }
    }

    @FXML
    public void handleQueryProduct() {
        String queryType = queryTypeBox.getValue(); // 获取查询类型 (Get the query type)
        String queryValue = queryValueField.getText().trim(); // 获取查询值 (Get the query value)

        // 校验查询值是否为空 (Check if the query value is empty)
        if (queryValue.isEmpty()) {
            showError("Please fill in the query value"); // 提示用户填写查询值 (Prompt the user to fill in the query value)
            return;
        }

        String request = String.format("QUERY|%s|%s", queryType, queryValue); // 构造查询请求字符串 (Construct the query request string)
        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send the request and get the response)
                Platform.runLater(() -> {
                    if (response.startsWith("FOUND")) {
                        String[] products = response.split("\\|"); // 解析返回的产品信息 (Parse the returned product information)
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < products.length; i++) {
                            String[] attrs = products[i].split(","); // 解析每个产品的属性 (Parse the attributes of each product)
                            sb.append("ID: ").append(attrs[0]).append("\n")
                                    .append("category: ").append(attrs[1]).append("\n")
                                    .append("name: ").append(attrs[2]).append("\n")
                                    .append("price: ").append(attrs[3]).append("\n")
                                    .append("stock: ").append(attrs[4]).append("\n")
                                    .append("publisher: ").append(attrs[5]).append("\n")
                                    .append("brand: ").append(attrs[6]).append("\n\n");
                        }
                        queryResultArea.setText(sb.toString()); // 显示查询结果 (Display the query results)
                    } else if (response.startsWith("NOT_FOUND")) {
                        queryResultArea.setText("This record does not exist!"); // 显示未找到记录 (Display message when record not found)
                    } else {
                        queryResultArea.setText("Query failure: " + response.split("\\|")[1]); // 显示查询失败的错误信息 (Display query failure error message)
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                Platform.runLater(() -> showError("Communication error")); // 显示通信错误 (Show communication error)
            }
        }).start(); // 启动新的线程 (Start the new thread)
    }


    @FXML
    public void handleDeleteProduct() {
        String category = deleteCategoryField.getText().trim(); // 获取删除产品的类别 (Get the category of the product to be deleted)
        String name = deleteNameField.getText().trim(); // 获取删除产品的名称 (Get the name of the product to be deleted)

        if (category.isEmpty() || name.isEmpty()) { // 校验类别和名称是否为空 (Check if category and name are not empty)
            showError("Please fill in the category and name"); // 提示用户填写类别和名称 (Prompt user to fill in category and name)
            return;
        }

        String request = String.format("DELETE|%s|%s", category, name); // 构造删除请求 (Construct the delete request)
        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send request and get the response)
                Platform.runLater(() -> {
                    if (response.startsWith("SUCCESS")) { // 如果删除成功 (If delete successful)
                        deleteResultLabel.setText("successfully delete"); // 显示删除成功信息 (Show success message)
                        deleteCategoryField.clear(); // 清空类别输入框 (Clear category input field)
                        deleteNameField.clear(); // 清空名称输入框 (Clear name input field)
                    } else {
                        deleteResultLabel.setText("fail to delete: " + response.split("\\|")[1]); // 显示删除失败信息 (Show failure message)
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                Platform.runLater(() -> showError("Communication error")); // 显示通信错误 (Show communication error)
            }
        }).start(); // 启动新的线程 (Start the new thread)
    }

    @FXML
    public void handleUpdateProduct() {
        String id = updateIdField.getText().trim(); // 获取更新产品的ID (Get the ID of the product to be updated)
        String field = updateFieldBox.getValue(); // 获取更新的字段 (Get the field to be updated)
        String newValue = updateValueField.getText().trim(); // 获取更新后的新值 (Get the new value for the update)

        if (id.isEmpty() || field.isEmpty() || newValue.isEmpty()) { // 校验ID、字段和值是否为空 (Check if ID, field, and new value are not empty)
            showError("Please fill in all required fields"); // 提示用户填写所有必填字段 (Prompt user to fill in all required fields)
            return;
        }

        String request = String.format("UPDATE|%s|%s|%s", id, field, newValue); // 构造更新请求 (Construct the update request)
        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send request and get the response)
                Platform.runLater(() -> {
                    if (response.startsWith("SUCCESS")) { // 如果更新成功 (If update successful)
                        updateResultLabel.setText("update successfully"); // 显示更新成功信息 (Show success message)
                        updateIdField.clear(); // 清空ID输入框 (Clear ID input field)
                        updateValueField.clear();
                    } else {
                        updateResultLabel.setText("Update failed: " + response.split("\\|")[1]); // 显示更新失败信息 (Show failure message)
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                Platform.runLater(() -> showError("Communication error"));
            }
        }).start(); // 启动新的线程 (Start the new thread)
    }

    @FXML
    public void handleSaleProduct() {
        String category = saleCategoryField.getText().trim(); // 获取销售产品的类别 (Get the category of the product to be sold)
        String name = saleNameField.getText().trim(); // 获取销售产品的名称 (Get the name of the product to be sold)
        String quantity = saleQuantityField.getText().trim(); // 获取销售数量 (Get the quantity of the product to be sold)

        if (category.isEmpty() || name.isEmpty() || quantity.isEmpty()) { // 校验类别、名称和数量是否为空 (Check if category, name, and quantity are not empty)
            showError("Please fill in all required fields"); // 提示用户填写所有必填字段 (Prompt user to fill in all required fields)
            return;
        }

        String request = String.format("SALE|%s|%s|%s", category, name, quantity); // 构造销售请求 (Construct the sale request)
        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send request and get the response)
                Platform.runLater(() -> {
                    if (response.startsWith("SUCCESS")) { // 如果销售成功 (If sale successful)
                        saleResultLabel.setText("Successful sale" + response.substring(response.indexOf("总价"))); // 显示销售成功信息和总价 (Show success message and total price)
                        saleCategoryField.clear(); // 清空类别输入框 (Clear category input field)
                        saleNameField.clear();
                        saleQuantityField.clear();
                    } else if (response.startsWith("INSUFFICIENT")) { // 如果库存不足 (If insufficient stock)
                        saleResultLabel.setText("Sales failure: insufficient stock"); // 显示销售失败信息 (Show sales failure message)
                    } else {
                        saleResultLabel.setText("Sales failure: " + response.split("\\|")[1]); // 显示销售失败信息 (Show sales failure message)
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                Platform.runLater(() -> showError("Communication error")); // 显示通信错误 (Show communication error)
            }
        }).start(); // 启动新的线程 (Start the new thread)
    }

    /*@FXML
    public void handleStatProducts() {
        // 获取用户选择的统计类型，默认为 "Stock" (Get the selected statistical type, default is "Stock")
        String statType = statTypeBox.getValue();
        if (statType == null) { // 如果没有选择统计类型 (If no statistical type is selected)
            statResultArea.setText("Please select a statistics type."); // 提示用户选择统计类型 (Prompt user to select a statistics type)
            return;
        }

        String request = "STAT|" + statType.toLowerCase(); // 动态构建请求，选择 Stock 或 Price (Dynamically construct the request, choose Stock or Price)

        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应 (Send request and get the response)
                Platform.runLater(() -> {
                    // 清空旧内容 (Clear old content)
                    statResultArea.clear();
                    statChartContainer.getChildren().clear();

                    if (response.startsWith("STAT")) { // 如果统计数据返回 (If statistical data is returned)
                        String[] products = response.split("\\|"); // 解析返回的数据 (Parse the returned data)
                        if (products.length > 1) {
                            // 生成饼图数据 (Generate pie chart data)
                            PieChart pieChart = new PieChart();

                            for (int i = 1; i < products.length; i++) { // 遍历每个产品 (Iterate through each product)
                                String[] attrs = products[i].split(","); // 解析每个产品的属性 (Parse the attributes of each product)
                                String label = attrs[0]; // 书籍名称 (Book name)
                                int value = 0;
                                if ("stock".equals(statType.toLowerCase())) {
                                    value = Integer.parseInt(attrs[4]); // 库存量 (Stock quantity)
                                } else if ("price".equals(statType.toLowerCase())) {
                                    value = (int) Double.parseDouble(attrs[3]); // 价格 (Price)
                                }
                                pieChart.getData().add(new PieChart.Data(label, value)); // 将数据添加到饼图中 (Add the data to the pie chart)
                            }

                            // 显示饼状图 (Display the pie chart)
                            statChartContainer.getChildren().add(pieChart);
                        }
                    } else if (response.startsWith("EMPTY")) { // 如果没有产品 (If no products are available)
                        statResultArea.setText("No products available"); // 显示信息 (Show message)
                        statChartContainer.getChildren().clear();
                    } else {
                        statResultArea.setText("Statistical failure: " + response.split("\\|")[1]); // 显示统计失败信息 (Show statistical failure message)
                        statChartContainer.getChildren().clear();
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace(); // 打印异常堆栈信息 (Print the stack trace in case of an exception)
                Platform.runLater(() -> showError("Communication error"));
            }
        }).start(); // 启动新的线程 (Start the new thread)
    }

    // 创建条形图的方法 (create a bar chart)
    private BarChart<String, Number> createBarChart() {
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis()); // 创建条形图 (Create bar chart)
        barChart.setTitle("Statistics"); // Set title
        barChart.getXAxis().setLabel("Category"); // Set X-axis label
        barChart.getYAxis().setLabel("Count"); // Set Y-axis label
        return barChart;
    }*/

    @FXML
    public void handleStatProducts() {
        // 获取用户选择的统计类型，默认为 "Stock"
        String statType = statTypeBox.getValue();
        if (statType == null) {
            statResultArea.setText("Please select a statistics type.");
            return;
        }

        String request = "STAT|" + statType.toLowerCase(); // 动态构建请求，选择 Stock 或 Price

        // 获取用户选择的图表类型，默认为 BarChart

        String chartType = chartTypeBox.getValue();
        if (chartType == null) {
            chartType = "BarChart"; // 默认选择 BarChart
        }

        String finalChartType = chartType;
        new Thread(() -> {
            try {
                String response = Client.getInstance().sendRequest(request); // 发送请求并获取响应
                Platform.runLater(() -> {
                    // 清空旧内容
                    statResultArea.clear();
                    statChartContainer.getChildren().clear();

                    if (response.startsWith("STAT")) { // 如果统计数据返回
                        String[] products = response.split("\\|");
                        if (products.length > 1) {
                            // 根据统计类型生成相应的图表
                            if ("stock".equals(statType.toLowerCase()) || "price".equals(statType.toLowerCase())) {
                                if ("BarChart".equals(finalChartType)) {
                                    // 创建条形图
                                    BarChart<String, Number> barChart = createBarChart();

                                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                                    for (int i = 1; i < products.length; i++) {
                                        String[] attrs = products[i].split(",");
                                        String label = attrs[0]; // 产品名称
                                        int value = 0;
                                        if ("stock".equals(statType.toLowerCase())) {
                                            value = Integer.parseInt(attrs[4]); // 库存量
                                        } else if ("price".equals(statType.toLowerCase())) {
                                            value = (int) Double.parseDouble(attrs[3]); // 价格
                                        }
                                        series.getData().add(new XYChart.Data<>(label, value));
                                    }

                                    // 添加数据系列到条形图
                                    barChart.getData().add(series);

                                    // 显示条形图
                                    statChartContainer.getChildren().add(barChart);
                                } else if ("PieChart".equals(finalChartType)) {
                                    // 创建饼状图
                                    PieChart pieChart = new PieChart();

                                    for (int i = 1; i < products.length; i++) {
                                        String[] attrs = products[i].split(",");
                                        String label = attrs[0]; // 产品名称
                                        int value = 0;
                                        if ("stock".equals(statType.toLowerCase())) {
                                            value = Integer.parseInt(attrs[4]); // 库存量
                                        } else if ("price".equals(statType.toLowerCase())) {
                                            value = (int) Double.parseDouble(attrs[3]); // 价格
                                        }
                                        pieChart.getData().add(new PieChart.Data(label, value));
                                    }

                                    // 显示饼状图
                                    statChartContainer.getChildren().add(pieChart);
                                }
                            }
                        }
                    } else if (response.startsWith("EMPTY")) {
                        statResultArea.setText("No products available");
                        statChartContainer.getChildren().clear();
                    } else {
                        statResultArea.setText("Statistical failure: " + response.split("\\|")[1]);
                        statChartContainer.getChildren().clear();
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showError("Communication error"));
            }
        }).start();
    }

    // 创建条形图的方法
    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statistics");
        xAxis.setLabel("Category");
        yAxis.setLabel("Count");
        return barChart;
    }



    private void clearAddFields() {
        categoryField.clear(); // 清空类别输入框 (Clear category input field)
        nameField.clear();
        priceField.clear();
        stockField.clear();
        manufacturerField.clear();
        brandField.clear();
    }

    private void showError(String message) {
        // 显示错误信息 (Show error message)
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
