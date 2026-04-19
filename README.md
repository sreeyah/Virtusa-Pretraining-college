                                 ** ONLINE QUIZ SYSTEM & RETAIL SALES ANALYSIS**

## 📌 Overview
This repository contains two major academic projects developed to demonstrate skills in **Java, SQL, and database management**:
1. 🧠 Online Quiz System (Java-based application)
2. 📊 Retail Sales Analysis (SQL-based database project)

These projects showcase problem-solving, backend logic, and data analysis capabilities.

# 🧠 1. Online Quiz System

## 📖 Description
The Online Quiz System is a Java-based application designed to conduct quizzes efficiently. It allows users to attempt quizzes, view scores, and manage questions dynamically.

## 🎯 Features
* User login authentication
* Multiple-choice questions
* Timer-based quiz
* Automatic score calculation
* Result display
* Admin can add/update questions

## 🛠️ Technologies Used
* Java (Core + OOP concepts)
* Collections Framework
* File Handling / JDBC (optional)
* Eclipse IDE

## 📂 Project Structure

```
OnlineQuizSystem/
│── src/
│   ├── model/
│   ├── service/
│   ├── ui/
│── Main.java
```

## ▶️ How to Run
1. Open project in Eclipse/IntelliJ
2. Compile all Java files
3. Run `Main.java`
4. Follow on-screen instructions

## 📸 Screenshots

<img width="1920" height="1020" alt="Screenshot 2026-04-19 192109" src="https://github.com/user-attachments/assets/084dda28-eaad-40b4-ad6e-81e95ea17180" />
<img width="1920" height="1080" alt="Screenshot 2026-04-19 192117" src="https://github.com/user-attachments/assets/22755dc3-adf8-4988-878e-487f4b2f5635" />
<img width="1920" height="1020" alt="Screenshot 2026-04-19 192132" src="https://github.com/user-attachments/assets/e7edd962-fad4-4eba-87b3-421d71abf612" />
<img width="1920" height="1020" alt="Screenshot 2026-04-19 193335" src="https://github.com/user-attachments/assets/64f47f60-bbfb-452b-b6f1-d2e944dcbf31" />
<img width="1920" height="1080" alt="Screenshot 2026-04-19 193346" src="https://github.com/user-attachments/assets/e0d1082a-6d8d-43ff-8923-8569fa25811a" />



--------------------------------------------------------------------------

# 📊 2. Retail Sales Analysis (SQL Project)

## 📖 Description
This project focuses on analyzing retail sales data using SQL queries. It helps extract meaningful insights from customer and sales datasets.

## 🎯 Features
* Database creation and table design
* Customer and product management
* Sales analysis queries
* Aggregation and reporting
* SQL joins and filtering

## 🛠️ Technologies Used
* MySQL
* SQL Queries (JOIN, GROUP BY, Aggregate Functions)

## 📂 Files Included
* `retailsales.sql` → Contains complete database schema and queries

## ▶️ How to Run
1. Open MySQL Workbench
2. Create a new database
3. Import `retailsales.sql`
4. Execute queries

# 📈 Sample Queries

```sql
-- Total Sales
SELECT SUM(amount) FROM sales;

-- Top Customers
SELECT customer_id, SUM(amount)
FROM sales
GROUP BY customer_id
ORDER BY SUM(amount) DESC;
```
## 📸 Screenshots

<img width="376" height="148" alt="Screenshot 2026-04-09 131911" src="https://github.com/user-attachments/assets/7f31a3fb-bb27-4139-ab45-594d3f29a621" />
<img width="430" height="146" alt="Screenshot 2026-04-09 131921" src="https://github.com/user-attachments/assets/9d7c64e1-3fca-447c-bae8-0521cc160f8e" />
<img width="356" height="147" alt="Screenshot 2026-04-09 131929" src="https://github.com/user-attachments/assets/4819b392-1310-4317-b6a5-b310ea7421e4" />
<img width="330" height="150" alt="Screenshot 2026-04-09 131937" src="https://github.com/user-attachments/assets/3298b8c4-0218-4d79-80ac-ee7077fd601d" />
<img width="238" height="147" alt="Screenshot 2026-04-09 132013" src="https://github.com/user-attachments/assets/15fc5d30-b792-49a6-b911-3449dc006d9d" />
<img width="258" height="131" alt="Screenshot 2026-04-09 132021" src="https://github.com/user-attachments/assets/6630dc36-85bf-436d-8353-e126e5ade24b" />
<img width="256" height="156" alt="Screenshot 2026-04-09 132044" src="https://github.com/user-attachments/assets/39f6b658-4a9f-48c5-a701-903f45cff75e" />
<img width="288" height="117" alt="Screenshot 2026-04-09 132049" src="https://github.com/user-attachments/assets/e56fd89a-51b5-4022-833b-bf927f460438" />




# 💡 Key Concepts Demonstrated
* Object-Oriented Programming (Java)
* Database Design
* SQL Joins & Aggregations
* Problem Solving
* Real-world Application Development

# 🏆 Outcomes
* Developed a fully functional quiz system
* Designed and analyzed a relational database
* Gained hands-on experience with Java & SQL
* Improved logical thinking and coding skills

# 🔮 Future Enhancements
* Web-based Quiz System (React + Node.js)
* Leaderboard feature
* AI-based quiz recommendations
* Advanced data visualization for sales
