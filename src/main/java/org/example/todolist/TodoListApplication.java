package org.example.todolist;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TodoListApplication extends Application {

    private TodoDao todoDao;
    public static final int WIDTH = 1100;
    public static int HEIGHT = 600;

    public TodoListApplication(){
        this.todoDao = new TodoDao();
    }

    public static void main(String[] args) {
        launch(TodoListApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        TodoListView todoListView = new TodoListView(todoDao, stage);

        Image image = new Image(getClass().getResource("/images/icon.png").toExternalForm());

        Scene scene = todoListView.getView();
        stage.setTitle("Todo-List");
        stage.getIcons().add(image);

        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }
}
