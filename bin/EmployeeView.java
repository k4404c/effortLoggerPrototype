import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EmployeeView extends VBox {
    private String username;
    private ArrayList<LogEntry> logEntries = new ArrayList<>();
    private ListView<String> logListView;

    //private ArrayList<LogEntry> logEntries = new ArrayList<>();
    public EmployeeView(Stage primaryStage, String username) {
        this.username = username;
        Label titleLabel = new Label("Employee View");
        Label usernameLabel = new Label("Username: " + username);
        Label teamLabel = new Label("Team: " );
        Label effortLabel = new Label("Effort: " );

        TextField teamField = new TextField();
        TextArea effortArea = new TextArea();

        logListView = new ListView<>();
        logListView.setPrefHeight(150);

        Button editButton = new Button("Edit Selected Log");
        editButton.setDisable(true);  // Initially disable the button
        
        logListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);  // Enable the button when an item is selected
            }
        });
        
        editButton.setOnAction(event -> {
            int selectedIndex = logListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                LogEntry selectedEntry = logEntries.get(selectedIndex);
                if (selectedEntry.getUsername().equals(username)) {
                    String updatedTeam = teamField.getText();
                    String updatedEffort = effortArea.getText();
                    selectedEntry.setTeam(updatedTeam);
                    selectedEntry.setEffort(updatedEffort);
                    selectedEntry.setTimestamp(generateTimestamp());
                    updateLogListView(logListView, logEntries);
                } else {
                    displayErrorMessage("You are not allowed to edit this log entry.");
                }
            }
        });

        Button submitButton = new Button("Submit");

        submitButton.setOnAction(event -> {
            //String name = nameField.getText();
            String team = teamField.getText();
            String effort = effortArea.getText();

            // Remove the name for anonymization
            //nameField.clear();

            // Save the data to a text file
            saveData(team, effort);
            teamField.clear();
            effortArea.clear();
        });

        updateLogListView(logListView, logEntries);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            primaryStage.close();
            openLoginPage();
        });

        this.getChildren().addAll(titleLabel, usernameLabel, teamLabel, teamField, effortLabel, effortArea, logListView, submitButton, editButton, logoutButton);
        timeout times = new timeout(primaryStage);
    }

        private String generateTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private void updateLogListView(ListView<String> logListView, ArrayList<LogEntry> logEntries) {
        logListView.getItems().clear();
        for (LogEntry entry : logEntries) {
            logListView.getItems().add(entry.toString());
        }
    }

    private void displayErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveData(String team, String effort) {
        String logId = generateUniqueLogId();
        LogEntry logEntry = new LogEntry(username, logId, team, effort, generateTimestamp());
        logEntries.add(logEntry);
    
        // Update the logListView with the new log entry
        updateLogListView(logListView, logEntries);
    
        try (FileWriter fileWriter = new FileWriter("employee_data.txt", true)) {
            fileWriter.write(logEntry.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateUniqueLogId() {
        // Generate a random UUID (Universally Unique Identifier)
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void openLoginPage() {
        Stage loginStage = new Stage();
        LoginPage loginPage = new LoginPage(loginStage);

        Scene loginScene = new Scene(loginPage, 400, 200);
        loginStage.setScene(loginScene);

        loginStage.show();
    }

   

    private static class LogEntry {
        private String username;  // Store the username of the submitter
        private String logId;  // Unique identifier for the log
        private String team;
        private String effort;
        private String timestamp;

        public LogEntry(String username, String logId, String team, String effort, String timestamp) {
            this.username = username;
            this.logId = logId;
            this.team = team;
            this.effort = effort;
            this.timestamp = timestamp;
        }

        public String getUsername() {
            return username;
        }

        public String getLogId() {
            return logId;
        }

        public String getTeam() {
            return team;
        }

        public String getEffort() {
            return effort;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public void setEffort(String effort) {
            this.effort = effort;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "Log ID: " + logId + "\nTimestamp: " + timestamp + "\nTeam: " + team + "\nEffort: " + effort;
        }
    }



}
