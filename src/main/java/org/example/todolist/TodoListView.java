package org.example.todolist;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TodoListView {
    private TodoDao todoDao;
    private Stage stage;
    private TodoAddView todoAddView;

    public TodoListView(TodoDao todoDao, Stage stage) {
        this.todoDao = todoDao;
        this.stage = stage;
        this.todoAddView = new TodoAddView(todoDao, stage, this);
    }

    public Scene getView() throws SQLException {
        Font font  = Font.font("Verdana",12);

        BorderPane layout = new BorderPane();

        //tableView
        ObservableList<Todo> data = FXCollections.observableArrayList(todoDao.list());
        ObservableList<Todo> observableList = FXCollections.observableArrayList(data);

        TableView<Todo> tableView = new TableView<>(observableList);

        //defining columns
        TableColumn<Todo, Number> rowNumCol = new TableColumn<>("Row");
        rowNumCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createIntegerBinding(() ->
                        tableView.getItems().indexOf(cellData.getValue()) + 1,
                        tableView.getItems()
                )
        );
        rowNumCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.085));

        TableColumn<Todo, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(col -> new TableCell<>() {
            private final Text text = new Text();
            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(8));
                setGraphic(text);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty ? null : item);
            }
        });
        nameCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Todo, String> desCol = new TableColumn<>("Description");
        desCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        desCol.setCellFactory(col -> new TableCell<>() {
            private final Text text = new Text();
            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(16));
                setGraphic(text);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty ? null : item);
            }
        });
        desCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.4));

        TableColumn<Todo,String> priCol = new TableColumn<>("Priority");
        priCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Todo, LocalDate> dateCol = new TableColumn<>("Due Date");
        dateCol.setCellValueFactory((new PropertyValueFactory<>("dueDate")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dateCol.setCellFactory(column -> new TableCell<Todo, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        dateCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Todo, Integer> doneCol = new TableColumn<>("Is Done?");
        doneCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getDone()).asObject()
        );
        doneCol.setCellFactory(col -> new TableCell<Todo, Integer>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setDisable(true); // read-only
            }
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(value == 3);
                    setGraphic(checkBox);
                }
            }
        });

        tableView.setRowFactory(tv -> new TableRow<Todo>() {
            @Override
            protected void updateItem(Todo item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll("done", "progress");

                if (empty || item == null) return;

                if (item.getDone() == 3) {
                    getStyleClass().add("done");
                } else if (item.getDone() == 2) {
                    getStyleClass().add("progress");
                }
            }
        });
        doneCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        //tableView settings
        rowNumCol.setResizable(false);
        nameCol.setResizable(false);
        desCol.setResizable(false);
        priCol.setResizable(false);
        dateCol.setResizable(false);
        doneCol.setResizable(false);

        //tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getColumns().addAll(rowNumCol, nameCol, desCol, priCol,dateCol, doneCol);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setPadding(new Insets(10,10,10,10));
        tableView.setFixedCellSize(-1);

        HBox horizontal = new HBox();
        horizontal.setSpacing(10);
        horizontal.setPadding(new Insets(10,10,10,10));

        //Buttons
        Button add = new Button("Add");
        add.getStyleClass().add("my-button");
        FontIcon icon = new FontIcon("mdi2b-book-plus");
        icon.setIconSize(20);
        add.setGraphic(icon);

        Button edit = new Button("Edit");
        edit.getStyleClass().add("my-button");
        FontIcon icon2 = new FontIcon("mdi2a-application-edit");
        icon2.setIconSize(20);
        edit.setGraphic(icon2);

        Button markInProgress = new Button("In Progress");
        markInProgress.getStyleClass().add("my-button");
        FontIcon icon6 = new FontIcon("mdi2p-progress-check");
        icon6.setIconSize(20);
        markInProgress.setGraphic(icon6);

        Button markAsDone = new Button("Done");
        markAsDone.getStyleClass().add("my-button");
        FontIcon icon3 = new FontIcon("mdi2c-check-circle");
        icon3.setIconSize(20);
        markAsDone.setGraphic(icon3);

        Button markAsUndone = new Button("Uncheck");
        markAsUndone.setGraphic(new FontIcon("mdi2b-backspace"));
        markAsUndone.getStyleClass().add("my-button");
        FontIcon icon4 = new FontIcon("mdi2b-backspace");
        icon4.setIconSize(20);
        markAsUndone.setGraphic(icon4);

        Button delete = new Button("Delete");
        delete.getStyleClass().add("my-button");
        FontIcon icon5 = new FontIcon("mdi2d-delete");
        icon5.setIconSize(20);
        delete.setGraphic(icon5);

        Button showInformation = new Button("Info");
        showInformation.getStyleClass().add("my-button");
        FontIcon icon7 = new FontIcon("mdi2i-information-variant-circle");
        icon7.setIconSize(20);
        showInformation.setGraphic(icon7);

        Button reset = new Button("Reset");
        reset.getStyleClass().add("my-button");
        FontIcon icon8 = new FontIcon("mdi2r-restore");
        icon8.setIconSize(20);
        reset.setGraphic(icon8);

        horizontal.getChildren().addAll(add,edit,markInProgress,markAsDone, markAsUndone,delete,showInformation,reset);

        //button actions

        add.setOnAction(e -> {
            stage.setScene(todoAddView.getView());
        });

        delete.setOnAction(actionEvent -> {
            ObservableList<Todo> selectedTodos = tableView.getSelectionModel().getSelectedItems();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Todo?");
            alert.setContentText("Are you sure you want to delete this Todo?");

            // Set owner to main stage
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);

            FontIcon alertIcon = new FontIcon("mdi2d-delete-alert");
            alertIcon.setIconSize(40);
            alert.setGraphic(alertIcon);

            DialogPane dialogPaneAlert = alert.getDialogPane();
            dialogPaneAlert.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                for (Todo todo : selectedTodos){
                    try {
                        todoDao.remove(todo.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                tableView.getItems().removeAll(List.copyOf(selectedTodos));
            }else {
                System.out.println("Deletion is cancelled");
            }
        });

        edit.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Input Row-Number");
            dialog.setHeaderText("Input the id of the todo which should be edited");
            dialog.setContentText("Id:");

            dialog.initOwner(stage);
            dialog.initModality(Modality.WINDOW_MODAL);

            Label invalid = new Label("");
            invalid.setStyle("-fx-text-fill: red;");
            invalid.setFont(font);
            invalid.setPadding(new Insets(10,10,10,10));
            invalid.setVisible(false);

            FontIcon editIcon = new FontIcon("mdi2c-card-search");
            editIcon.setIconSize(40);
            dialog.setGraphic(editIcon);

            VBox contentBox  =new VBox(5);
            contentBox.getChildren().addAll(dialog.getEditor(), invalid);
            dialog.getDialogPane().setContent(contentBox);

            DialogPane dialogPaneEdit = dialog.getDialogPane();
            dialogPaneEdit.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

            // Add validation before closing
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                String input = dialog.getEditor().getText();
                try {
                    int id = Integer.parseInt(input);
                    if(todoDao.size() > 0 && todoDao.size() >= id ){
                        Todo selectedTodo = tableView.getItems().get(id-1);
                        TodoEditView editView = new TodoEditView(todoDao, this.stage, this, selectedTodo);
                        stage.setScene(editView.getView());
                    }else{
                        invalid.setText("Todo with the id " + id + " is not in the list");
                        invalid.setVisible(true);
                        event.consume();
                    }
                } catch (NumberFormatException e) {
                    // invalid number -> show error and prevent dialog from closing
                    invalid.setText("Invalid number!");
                    invalid.setVisible(true);
                    event.consume(); // <-- prevents the dialog from closing
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            dialog.showAndWait();
        });

        markAsDone.setOnAction(actionEvent -> {
            ObservableList<Todo> selectedTodos = tableView.getSelectionModel().getSelectedItems();
            for (Todo todo : selectedTodos){
                todoDao.markAsDone(todo.getId(), 3);
                todo.setDone(3);
            }
            tableView.refresh();
        });

        markInProgress.setOnAction(actionEvent -> {
            ObservableList<Todo> selectedTodos = tableView.getSelectionModel().getSelectedItems();
            for (Todo todo : selectedTodos){
                todoDao.markAsDone(todo.getId(), 2);
                todo.setDone(2);
            }
            tableView.refresh();
        });

        markAsUndone.setOnAction(actionEvent -> {
            ObservableList<Todo> selectedTodos = tableView.getSelectionModel().getSelectedItems();
            for (Todo todo : selectedTodos){
                todoDao.markAsDone(todo.getId(), 1);
                todo.setDone(1);
            }
            tableView.refresh();
        });

        showInformation.setOnAction(actionEvent -> {
            int countOpen = (int) observableList.stream().filter(t -> t.getDone() == 1).count();
            int countProgress = (int) observableList.stream().filter(t -> t.getDone() == 2).count();
            int countDone = (int) observableList.stream().filter(t -> t.getDone() == 3).count();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Todo Statistics");

            // Set owner to main stage
            popup.initOwner(stage);
            popup.initModality(Modality.WINDOW_MODAL);

            HBox pair = new HBox();
            pair.setSpacing(10);
            pair.setAlignment(Pos.CENTER);

            // Header
            Label infoIcon = new Label();
            FontIcon info = new FontIcon("mdi2i-information-slab-circle");
            info.setIconSize(30);
            infoIcon.setGraphic(info);

            Label header = new Label("Todo Statistics");
            header.getStyleClass().add("popup-header");

            pair.getChildren().addAll(header,infoIcon);

            // labels
            Label openLabel = new Label("Open Todos: " + countOpen);
            openLabel.getStyleClass().addAll("popup-label", "popup-open");

            Label progressLabel = new Label("Todos in Progress: " + countProgress);
            progressLabel.getStyleClass().addAll("popup-label", "popup-progress");

            Label doneLabel = new Label("Todos Done: " + countDone);
            doneLabel.getStyleClass().addAll("popup-label", "popup-done");

            HBox content = new HBox(10, openLabel, progressLabel, doneLabel);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(10,10,10,10));

            // Close button
            Button closeButton = new Button("Close");
            closeButton.getStyleClass().add("popup-button");
            closeButton.setOnAction(e -> popup.close());

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10));

            VBox root = new VBox(pair, content, buttonBox);
            root.getStyleClass().add("popup-root");
            root.setAlignment(Pos.CENTER);

            Image image = new Image(getClass().getResource("/images/icon.png").toExternalForm());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            popup.setScene(scene);
            popup.getIcons().add(image);
            popup.showAndWait();
        });

        reset.setOnAction( actionEvent -> {
            tableView.getSortOrder().clear();
            observableList.setAll(data);
        });

        Label placeholder = new Label("Keine Todos vorhanden");
        placeholder.setStyle("-fx-text-fill: gray; -fx-font-size: 14px; -fx-font-style: italic;");
        tableView.setPlaceholder(placeholder);

        layout.setCenter(tableView);
        layout.setTop(horizontal);

        Scene scene = new Scene(layout, TodoListApplication.WIDTH, TodoListApplication.HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();

            // move up through the node hierarchy until a TableRow or scene root is found
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            // clear selection on click anywhere but on a filled row
            if (source == null || (source instanceof TableRow && ((TableRow) source).isEmpty())) {
                tableView.getSelectionModel().clearSelection();
            }
        });

        if(stage.isMaximized()){
            stage.setMaximized(true);
        }
        return scene;
    }
}
