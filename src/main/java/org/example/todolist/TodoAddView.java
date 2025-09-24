package org.example.todolist;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TodoAddView {
    private TodoDao todoDao;
    private TodoListView todoListView;
    private Stage stage;

    public TodoAddView(TodoDao todoDao, Stage stage, TodoListView todoListView) {
        this.todoDao = todoDao;
        this.todoListView = todoListView;
        this.stage  =stage;
        stage.setWidth(TodoListApplication.WIDTH);
        stage.setHeight(TodoListApplication.HEIGHT);
    }

    public Scene getView(){
        VBox layout = new VBox();

        layout.setAlignment(Pos.CENTER);
        layout.setFillWidth(false);

        VBox order = new VBox();
        order.setAlignment(Pos.CENTER);
        order.setSpacing(10);
        order.setPadding(new Insets(20,20,20,20));
        order.setStyle("-fx-border-color: #A3AFC9;-fx-border-width: 2;-fx-background-color: #f0f4f8; -fx-background-radius: 10 10 10 10;-fx-border-radius: 10 10 10 10");

        Font fontTitle  = Font.font("Verdana", FontWeight.BOLD,20);

        Label title = new Label("Todo");
        title.setFont(fontTitle);

        Line line = new Line();
        line.startXProperty().set(100);
        line.endXProperty().set(725);
        line.setStyle("-fx-stroke: #A3AFC9;-fx-stroke-width: 3;");

        GridPane todoAttibutes = new GridPane();
        todoAttibutes.setVgap(20);
        todoAttibutes.setHgap(20);
        todoAttibutes.setAlignment(Pos.CENTER);

        Font fontLabel = Font.font("Verdana", FontWeight.BOLD,14);

        Label name = new Label("Name:");
        name.setFont(fontLabel);
        todoAttibutes.add(name,0,0);
        Label description = new Label("Description:");
        description.setFont(fontLabel);
        todoAttibutes.add(description,0,1);
        Label priority = new Label("Priority:");
        priority.setFont(fontLabel);
        todoAttibutes.add(priority,0,2);
        Label duedate = new Label("Due Date:");
        duedate.setFont(fontLabel);

        todoAttibutes.add(duedate,0,3);

        Font fontField  = Font.font("Verdana",12);

        TextField nameField = new TextField();
        nameField.setFont(fontField);
        todoAttibutes.add(nameField,1,0);

        TextArea descriptionField = new TextArea();
        descriptionField.setFont(fontField);
        descriptionField.setPromptText("Enter description");
        descriptionField.setWrapText(true);
        todoAttibutes.add(descriptionField,1,1);

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getStyleClass().add("combo-box");
        todoAttibutes.add(priorityCombo,1,2);
        priorityCombo.setValue("Medium");

        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("date-picker");
        todoAttibutes.add(datePicker,1,3);

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setPadding(new Insets(10,10,10,10));

        Button add = new Button("Add");
        add.getStyleClass().add("my-button");
        FontIcon icon = new FontIcon("mdi2b-book-plus");
        icon.setIconSize(20);
        add.setGraphic(icon);

        Button showTodoList = new Button("Todo-List");
        showTodoList.getStyleClass().add("my-button");
        FontIcon icon2 = new FontIcon("mdi2f-format-list-numbered");
        icon2.setIconSize(20);
        showTodoList.setGraphic(icon2);
        buttons.getChildren().addAll(add,showTodoList);

        order.getChildren().addAll(title,line,todoAttibutes, buttons);
        Label message = new Label("");
        message.setPadding(new Insets(10,10,10,10));
        message.setFont(fontLabel);

        layout.getChildren().addAll(order,message);

        //button action

        add.setOnAction(actionEvent -> {
            if(!nameField.getText().isEmpty() && !descriptionField.getText().isEmpty()){
                if(nameField.getText().getBytes(StandardCharsets.UTF_8).length < 256 && descriptionField.getText().getBytes(StandardCharsets.UTF_8).length < 65536){
                    try {
                        todoDao.add(new Todo(nameField.getText(), descriptionField.getText(), priorityCombo.getValue(), datePicker.getValue()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    nameField.clear();
                    descriptionField.clear();
                    priorityCombo.setValue("Medium");
                    datePicker.setValue(null);
                    message.setStyle("-fx-text-fill: green;");
                    message.setText("todo added succsessfully");
                } else{
                    message.setStyle("-fx-text-fill: red;");
                    message.setText("todo-name or todo-description to long!");
                }

            }else{
                message.setStyle("-fx-text-fill: red;");
                message.setText("todo could not be added. Check if the fields name and description are empty!");
            }
        });
        Scene scene = new Scene(layout,TodoListApplication.WIDTH,TodoListApplication.HEIGHT);
        showTodoList.setOnAction(actionEvent -> {
            try {
                stage.setScene(todoListView.getView());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }
}
