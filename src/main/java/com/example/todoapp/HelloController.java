package com.example.todoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloController {

    private final String DATA_FILE = "tasks.json";
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML private VBox generalTaskList;
    @FXML private VBox dailyTaskList;
    @FXML private StackPane rootPane;
    @FXML private Label dateText;
    @FXML private Button button1, button2, button3, button4, button5;

    private ArrayList<Button> buttonArrayList = new ArrayList<>();
    private boolean isDarkMode = false;
    private int currentButton = 1;
    private ZonedDateTime dateFocus;

    @FXML
    public void initialize() {
        buttonArrayList.addAll(Arrays.asList(button1, button2, button3, button4, button5));
        dateFocus = ZonedDateTime.now();
        currentButton = 1;

        loadTasks();
        refreshDateButtons();
        refreshOverview();
    }

    private void saveData() {
        try (FileWriter fileWriter = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Task> taskList = new ArrayList<>(tasks);
            gson.toJson(taskList, fileWriter);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private void loadTasks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);
            if (loadedTasks != null) {
                tasks.clear();
                for (Task task : loadedTasks) {
                    task.initializeTransientFields();
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load tasks: " + e.getMessage());
        }
    }

    public void toggleTheme(ActionEvent actionEvent) {
        if (rootPane.getStyleClass().contains("dark-mode")) {
            rootPane.getStyleClass().remove("dark-mode");
            isDarkMode = false;
        } else {
            rootPane.getStyleClass().add("dark-mode");
            isDarkMode = true;
        }
    }

    private void refreshOverview() {
        ZonedDateTime selectedDate = dateFocus.plusDays(currentButton - 1);
        generalTaskList.getChildren().clear();
        for (Task task : tasks) {
            if (!task.isDone() && task.getDate().equals(selectedDate.toLocalDate())) {
                generalTaskList.getChildren().add(createSidebarTaskComponent(task));
            }
        }
    }

    private void refreshDailyTasks() {
        dailyTaskList.getChildren().clear();
        ZonedDateTime selectedDate = dateFocus.plusDays(currentButton - 1);
        java.time.LocalDate targetDate = selectedDate.toLocalDate();
        for (Task task : tasks) {
            if (task.getDate().equals(targetDate)) {
                dailyTaskList.getChildren().add(createMainTaskComponent(task));
            }
        }
    }

    private void refreshDateButtons() {
        for (int i = 0; i < buttonArrayList.size(); i++) {
            ZonedDateTime buttonDate = dateFocus.plusDays(i);
            Button button = buttonArrayList.get(i);
            button.setText(String.format("%02d", buttonDate.getDayOfMonth()));

            button.getStyleClass().removeAll("date-pill-active", "date-pill");
            button.getStyleClass().add((currentButton == i + 1) ? "date-pill-active" : "date-pill");
        }

        ZonedDateTime selectedDate = dateFocus.plusDays(currentButton - 1);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
        dateText.setText(selectedDate.format(formatter));

        refreshDailyTasks();
        refreshOverview();
    }

    private javafx.scene.Node createMainTaskComponent(Task task) {
        HBox card = new HBox(15);
        card.getStyleClass().add("task-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new javafx.geometry.Insets(10, 15, 10, 15));

        VBox textContainer = new VBox(2);
        Label title = new Label(task.getTitle());
        title.getStyleClass().add("section-subtitle");

        Label desc = new Label(task.getDescription());
        desc.setStyle("-fx-text-fill: text-muted; -fx-font-size: 12px;");
        textContainer.getChildren().addAll(title, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Runnable updateStyle = () -> {
            card.setOpacity(task.isDone() ? 0.5 : 1.0);
            title.setStyle("-fx-strikethrough: " + task.isDone() + ";");
        };
        updateStyle.run(); // Initial state

        task.isDoneProperty().addListener((obs, oldVal, newVal) -> {
            updateStyle.run();
            refreshOverview();
            saveData();
        });

        Button completeBtn = new Button("✓");
        completeBtn.getStyleClass().add("date-pill");
        completeBtn.setPrefSize(35, 35);
        completeBtn.setOnAction(e -> task.setDone(!task.isDone()));

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("date-pill");
        editBtn.setPrefSize(70, 35);
        editBtn.setOnAction(e -> {
            if (!task.isDone()) showTaskDialog(task);
        });

        card.getChildren().addAll(textContainer, spacer, completeBtn, editBtn);
        return card;
    }

    private javafx.scene.Node createSidebarTaskComponent(Task task) {
        HBox miniCard = new HBox(10);
        miniCard.setStyle("-fx-padding: 8; -fx-background-color: card-bg; -fx-background-radius: 5;");

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-text-fill: text-main; -fx-font-size: 13px;");

        miniCard.getChildren().add(title);
        return miniCard;
    }


    private void showTaskDialog(Task taskToEdit) {
        BoxBlur blur = new BoxBlur(10, 3, 3);
        rootPane.getChildren().get(0).setEffect(blur);

        Rectangle dimmer = new Rectangle();
        dimmer.widthProperty().bind(rootPane.widthProperty());
        dimmer.heightProperty().bind(rootPane.heightProperty());
        dimmer.setFill(Color.web("#000000", 0.5));
        dimmer.setOnMouseClicked(e -> e.consume()); 

        VBox popup = new VBox(15);
        popup.setMaxSize(320, 250);
        popup.getStyleClass().add("popup-window"); 
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new javafx.geometry.Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Task name");
        nameField.getStyleClass().add("custom-textfield");

        TextField descField = new TextField();
        descField.setPromptText("Description");
        descField.getStyleClass().add("custom-textfield");

        Button actionBtn = new Button();
        actionBtn.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        actionBtn.getStyleClass().add("add-button");

        Label errorLabel = new Label("Title cannot be empty!");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);


        if (taskToEdit != null) {
            actionBtn.setText("Save Changes");
            nameField.setText(taskToEdit.getTitle());
            descField.setText(taskToEdit.getDescription());
        } else {
            actionBtn.setText("Add New Task");
        }

        actionBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setVisible(true);
                return;
            }

            if (taskToEdit != null) {
                taskToEdit.setTitle(nameField.getText().trim());
                taskToEdit.setDescription(descField.getText().trim());
            } else {
                ZonedDateTime selectedDate = dateFocus.plusDays(currentButton - 1);
                Task newTask = new Task(nameField.getText().trim(), descField.getText().trim(), selectedDate.toLocalDate());
                tasks.add(newTask);
            }

            saveData();
            refreshDailyTasks();
            refreshOverview();

            rootPane.getChildren().removeAll(dimmer, popup);
            rootPane.getChildren().get(0).setEffect(null);
        });

        Button closeBtn = new Button("Cancel");
        closeBtn.getStyleClass().add("nav-button");
        closeBtn.setOnAction(e -> {
            rootPane.getChildren().removeAll(dimmer, popup);
            rootPane.getChildren().get(0).setEffect(null);
        });

        popup.getChildren().addAll(nameField, descField, errorLabel, actionBtn, closeBtn);
        rootPane.getChildren().addAll(dimmer, popup);
    }

    public void tButton(ActionEvent actionEvent) { showTaskDialog(null); }

    public void button1(ActionEvent actionEvent) { currentButton = 1; refreshDateButtons(); }
    public void button2(ActionEvent actionEvent) { currentButton = 2; refreshDateButtons(); }
    public void button3(ActionEvent actionEvent) { currentButton = 3; refreshDateButtons(); }
    public void button4(ActionEvent actionEvent) { currentButton = 4; refreshDateButtons(); }
    public void button5(ActionEvent actionEvent) { currentButton = 5; refreshDateButtons(); }

    public void rightButton(ActionEvent actionEvent) { dateFocus = dateFocus.plusDays(1); refreshDateButtons(); }
    public void leftButton(ActionEvent actionEvent) { dateFocus = dateFocus.minusDays(1); refreshDateButtons(); }
}
