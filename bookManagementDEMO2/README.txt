Overview
This project is a shopping platform built using Spring Boot, HTML, CSS, and JavaScript, with session-based shopping cart functionality.
It includes a front-end user shopping system and a back-end management system for administrators.
The system is implemented using the Java Spring Boot framework, utilizes JSP for views, and employs session storage for managing shopping cart data.

Setup Instructions
1.	Install JDK 1.8:
Ensure that JDK 1.8 is installed on your system and configure your IDE (e.g., IntelliJ IDEA) to use this version.

2.	Open the Project:
Open the project in IntelliJ IDEA and set the project JDK to 1.8.

3.	Configure Maven:
Go to File > Settings > Maven.
Set the Maven path to the appropriate local Maven directory.
Ensure Maven dependencies are downloaded by refreshing the Maven project.

4.	Update database.java Credentials:
Navigate to the database.java file in the project.
Update the PASSWORD field with your local MySQL database password.
Example:
private static final String PASSWORD = "YourMySQLPassword";

5.	Run the Project:
Locate the MainApp.java file in the project.
Run the file to start the project.
Alternatively, use the small triangle in the top-right corner of IntelliJ IDEA.


Usage Notes
Ensure the database schema matches the provided table structure.
Session management is implemented for handling user interactions, particularly for the shopping cart.
Product classifications must follow the parent-child hierarchy to display properly.



