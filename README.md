# Book-Management-System
Overview
This project is a shopping platform built using JAVA and JAVAFX, database is MUSQL access via JDBC.
It is the course project of Wenzhou-Kean university Cps3250, Professer Sangi give me guidence.
  During the proposal stage, we initially chose C++ for development, but Professor Sangi pointed out that implementing a user interface with C++ would be challenging. We switched to Java and used JavaFX.
Originally, our statistics interface displayed all data in a simple list. Professor Sangi suggested adding data visualization to make the information clearer and improve user experience. Following his advice, we implemented pie charts to visualize inventory data.
To further enhance the system’s functionality, Professor Sangi encouraged us to add more chart types. In the final version, we added bar charts and pie charts.
Throughout the process, Professor Sangi's guidance made us realize that the choice of technology is as important as the design of the user experience. Data visualization is not only a technical improvement, but also an in-depth response to user needs.

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
