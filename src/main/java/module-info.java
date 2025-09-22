module org.example.todolist {
    requires javafx.fxml;
    requires java.sql;
    requires javafx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.materialdesign2;
    requires io.github.cdimascio.dotenv.java;


    opens org.example.todolist to javafx.fxml;
    exports org.example.todolist;
}