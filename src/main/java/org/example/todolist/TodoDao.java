package org.example.todolist;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDao {

    public String databasePath;
    private final String dbUser;
    private final String dbPassword;

    public TodoDao( ){
        Dotenv dotenv = Dotenv.load();  // loads .env from project root
        this.databasePath = dotenv.get("DB_URL");
        this.dbUser = dotenv.get("DB_USER");
        this.dbPassword = dotenv.get("DB_PASSWORD");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databasePath, dbUser, dbPassword);
    }

    public List<Todo> list() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try {
            Connection connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id_todo,name,description,priority,due_date,is_done FROM TODOS");

            while (resultSet.next()) {
                java.sql.Date sqlDueDate = resultSet.getDate("due_date");
                LocalDate dueDate = (sqlDueDate != null) ? sqlDueDate.toLocalDate() : null;

                todos.add(new Todo(// id// idTodos
                        resultSet.getInt("id_todo"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("priority"),
                        dueDate,
                        resultSet.getInt("is_done")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todos;
    }

    public int size() throws SQLException {
        return this.list().size();

    }

    public void add(Todo todo) throws SQLException{
        try {
            Connection connection = getConnection();

            PreparedStatement resultSet = connection.prepareStatement("INSERT INTO TODOS (name,description,priority,due_date,is_done) VALUES (?,?,?,?,?)");
            resultSet.setString(1, todo.getName());
            resultSet.setString(2, todo.getDescription());
            resultSet.setString(3,todo.getPriority());
            LocalDate localDate = todo.getDueDate();
            java.sql.Date sqlDate = (localDate != null) ? java.sql.Date.valueOf(localDate) : null;
            resultSet.setDate(4, sqlDate);
            resultSet.setInt(5,1);
            resultSet.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) throws SQLException{
        try {
            Connection connection = getConnection();

            PreparedStatement resultSet = connection.prepareStatement("DELETE FROM todos WHERE id_todo = ?");
            resultSet.setInt(1,id);
            resultSet.executeUpdate();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void edit(int id, String name, String description, String priority, LocalDate date){
        try {
            Connection connection = getConnection();

            PreparedStatement resultSet = connection.prepareStatement("Update todos SET name = ?, description = ?, priority = ?, due_date = ? where id_todo = ?");

            resultSet.setString(1, name);
            resultSet.setString(2,description);
            resultSet.setString(3,priority);

            LocalDate localDate = date;
            java.sql.Date sqlDate = (localDate != null) ? java.sql.Date.valueOf(localDate) : null;
            resultSet.setDate(4,sqlDate);
            resultSet.setInt(5,id);
            resultSet.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAsDone(int id, int value){
        try {
            Connection connection = getConnection();

            PreparedStatement resultSet = connection.prepareStatement("Update todos SET is_done = ? where id_todo = ?");

            resultSet.setInt(1, value);
            resultSet.setInt(2,id);
            resultSet.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
