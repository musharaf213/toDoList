# TodoList Application

A **JavaFX-based TodoList application** for managing tasks, with icons from [Ikonli Material Design 2 Pack](https://kordamp.org/ikonli/).  
Built with **IntelliJ IDEA** as a demonstration of JavaFX GUI development and task management features.

The application allows you to add, edit, and track tasks with due dates and priorities, all within an intuitive graphical interface.

---

## Features
- **Task management**: Add, edit, and remove tasks.  
- **Completion tracking**: Mark tasks as completed or uncheck them.  
- **Task overview**: View your tasks with due dates and priority levels.  
- **Intuitive GUI**: Modern interface built with JavaFX.  
- **Icons**: Uses Ikonli Material Design 2 Pack for a polished look.  

---

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/musharaf213/toDoList.git
````

---
### 2. Database Setup

The application uses a MySQL database to store todos.
The connection is established using the credentials stored in .env (DB_URL, DB_USER, DB_PASSWORD).

Create the following table in your database:
````
CREATE TABLE TODOS (
    id_todo INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL,
    due_date DATE,
    is_done INT NOT NULL DEFAULT 0
);
````
Table Columns

- id_todo → Unique ID for each task
- name → Title of the task (required)
- description → Task description (required)
- priority → Priority level (required, e.g., Low, Medium, High)
- due_date → Due date (optional)
- is_done → Status (1 = open, 2 = pending, completed)
  
Data Access

- The TodoDao class provides all CRUD operations for this table:
- list() → Fetch all todos
- add() → Insert a new todo
- edit() → Update an existing todo
- remove() → Delete a todo
- markAsDone() → Mark a todo as done or undone

---
### 3. Create a `.env` file in the project root
Set the following parameters in your `.env` file:
```
DB_URL=your_database_url
DB_USER=your_username
DB_PASSWORD=your_password
````
