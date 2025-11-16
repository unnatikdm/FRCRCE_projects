import java.io.*;
import java.sql.*;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

// Simple Database Manager for Feedback
class FeedbackDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/tryinglocal";
    private static final String USER = "postgres";
    private static final String PASSWORD = "tanmay";
    
    public static boolean saveFeedback(String name, String feedback) {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "INSERT INTO feedback (name, feedback) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, feedback);
            pstmt.executeUpdate();
            conn.close();
            return true;
        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
    
    public static List<String> getAllFeedback() {
        List<String> feedbackList = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, feedback, created_at FROM feedback ORDER BY created_at DESC LIMIT 10");
            
            while (rs.next()) {
                String entry = rs.getString("name") + ": " + rs.getString("feedback") + " (" + rs.getTimestamp("created_at") + ")";
                feedbackList.add(entry);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Error loading feedback: " + e.getMessage());
        }
        return feedbackList;
    }
}

// Smart Device classes
abstract class SmartDevice {
    protected String name;
    protected boolean isOn;
    protected String room;
    
    public SmartDevice(String name, String room) {
        this.name = name;
        this.room = room;
        this.isOn = false;
    }
    
    public void turnOn() { this.isOn = true; }
    public void turnOff() { this.isOn = false; }
    public abstract String getStatus();
    public abstract String getType();
    public String getName() { return name; }
    public String getRoom() { return room; }
    public boolean isOn() { return isOn; }
}

class SmartLight extends SmartDevice {
    private int brightness;
    private String color;
    public SmartLight(String name, String room) {
        super(name, room);
        this.brightness = 100;
        this.color = "White";
    }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    public void setColor(String color) { this.color = color; }
    @Override public String getStatus() {
        return "💡 Light | " + room + " | " + (isOn ? "ON" : "OFF") + " | Brightness: " + brightness + "% | Color: " + color;
    }
    @Override public String getType() { return "Light"; }
    public int getBrightness() { return brightness; }
    public String getColor() { return color; }
}

class SmartThermostat extends SmartDevice {
    private double temperature;
    private String mode;
    public SmartThermostat(String name, String room) {
        super(name, room);
        this.temperature = 22.0;
        this.mode = "Auto";
    }
    public void setTemperature(double temp) { this.temperature = temp; }
    public void setMode(String mode) { this.mode = mode; }
    @Override public String getStatus() {
        return "🌡️ Thermostat | " + room + " | " + (isOn ? "ON" : "OFF") + " | Temp: " + temperature + "°C | Mode: " + mode;
    }
    @Override public String getType() { return "Thermostat"; }
    public double getTemperature() { return temperature; }
    public String getMode() { return mode; }
}

class SmartSecurity extends SmartDevice {
    private boolean isArmed;
    private String securityLevel;
    public SmartSecurity(String name, String room) {
        super(name, room);
        this.isArmed = false;
        this.securityLevel = "Normal";
    }
    public void armSystem() { this.isArmed = true; }
    public void disarmSystem() { this.isArmed = false; }
    public void setSecurityLevel(String level) { this.securityLevel = level; }
    @Override public String getStatus() {
        return "🛡️ Security | " + room + " | " + (isOn ? "Active" : "Inactive") + " | Armed: " + isArmed + " | Level: " + securityLevel;
    }
    @Override public String getType() { return "Security"; }
}

// Main JavaFX Application
public class SmartHomeGUI extends Application {
    private static final String FILE_NAME = "smart_home_data.txt";
    private List<SmartDevice> devices = new ArrayList<>();
    private TextArea statusArea;
    private FlowPane devicesPane;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        loadDevicesFromFile();
        if (devices.isEmpty()) setupDefaultDevices();
        
        // Main layout with tabs
        TabPane tabPane = new TabPane();
        
        // Tab 1: Smart Home Control
        Tab homeTab = new Tab("🏠 Smart Home");
        homeTab.setClosable(false);
        homeTab.setContent(createHomeTab());
        
        // Tab 2: Feedback Form
        Tab feedbackTab = new Tab("💬 Feedback");
        feedbackTab.setClosable(false);
        feedbackTab.setContent(createFeedbackTab());
        
        tabPane.getTabs().addAll(homeTab, feedbackTab);
        
        primaryStage.setTitle("Smart Home System with Feedback");
        primaryStage.setScene(new Scene(tabPane, 1000, 700));
        primaryStage.show();
        
        updateStatus("🚀 Smart Home System Started! Loaded " + devices.size() + " devices.");
    }
    
    private VBox createHomeTab() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        // Header
        Label header = new Label("🏠 SMART HOME SYSTEM");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        header.setTextFill(Color.WHITE);
        
        // Control buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button viewBtn = createStyledButton("📊 View All", this::viewAllDevices);
        Button addBtn = createStyledButton("➕ Add Device", this::showAddDeviceDialog);
        Button voiceBtn = createStyledButton("🎤 Voice Command", this::showVoiceCommand);
        Button saveBtn = createStyledButton("💾 Save", this::saveDevicesToFile);
        
        buttonBox.getChildren().addAll(viewBtn, addBtn, voiceBtn, saveBtn);
        
        // Devices area
        Label devicesLabel = new Label("📱 YOUR SMART DEVICES");
        devicesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        devicesLabel.setTextFill(Color.WHITE);
        
        devicesPane = new FlowPane();
        devicesPane.setHgap(15);
        devicesPane.setVgap(15);
        devicesPane.setPadding(new Insets(15));
        
        ScrollPane scrollPane = new ScrollPane(devicesPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background: transparent;");
        
        // Status area
        statusArea = new TextArea();
        statusArea.setEditable(false);
        statusArea.setPrefHeight(120);
        statusArea.setStyle("-fx-control-inner-background: #2d3748; -fx-text-fill: white; -fx-font-size: 12px;");
        
        // Add everything to main layout
        mainLayout.getChildren().addAll(header, buttonBox, devicesLabel, scrollPane, statusArea);
        
        // Update the devices display
        updateDevicesDisplay();
        
        return mainLayout;
    }
    
    private VBox createFeedbackTab() {
        VBox feedbackLayout = new VBox(20);
        feedbackLayout.setPadding(new Insets(30));
        feedbackLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #4facfe 0%, #00f2fe 100%);");
        feedbackLayout.setAlignment(Pos.TOP_CENTER);
        
        // Header
        Label header = new Label("💬 FEEDBACK FORM");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        header.setTextFill(Color.WHITE);
        
        // Form container
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(30));
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        formContainer.setMaxWidth(500);
        
        // Form title
        Label formTitle = new Label("Share Your Feedback");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        formTitle.setTextFill(Color.DARKBLUE);
        
        // Name field
        VBox nameBox = new VBox(8);
        Label nameLabel = new Label("Your Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setStyle("-fx-padding: 10; -fx-font-size: 14;");
        nameField.setPrefHeight(40);
        nameBox.getChildren().addAll(nameLabel, nameField);
        
        // Feedback field
        VBox feedbackBox = new VBox(8);
        Label feedbackLabel = new Label("Your Feedback:");
        feedbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextArea feedbackArea = new TextArea();
        feedbackArea.setPromptText("Share your thoughts, suggestions, or issues...");
        feedbackArea.setStyle("-fx-font-size: 14;");
        feedbackArea.setPrefHeight(120);
        feedbackArea.setWrapText(true);
        feedbackBox.getChildren().addAll(feedbackLabel, feedbackArea);
        
        // Submit button
        Button submitBtn = new Button("📤 Submit Feedback");
        submitBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 15 30;");
        submitBtn.setPrefWidth(200);
        
        // Status label for feedback
        Label feedbackStatus = new Label();
        feedbackStatus.setFont(Font.font("Arial", 12));
        
        // Submit action
        submitBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String feedback = feedbackArea.getText().trim();
            
            if (name.isEmpty() || feedback.isEmpty()) {
                feedbackStatus.setText("❌ Please fill in all fields!");
                feedbackStatus.setTextFill(Color.RED);
                return;
            }
            
            boolean success = FeedbackDB.saveFeedback(name, feedback);
            if (success) {
                feedbackStatus.setText("✅ Thank you! Your feedback has been submitted.");
                feedbackStatus.setTextFill(Color.GREEN);
                nameField.clear();
                feedbackArea.clear();
                updateStatus("📝 Feedback received from: " + name);
            } else {
                feedbackStatus.setText("❌ Error submitting feedback. Please try again.");
                feedbackStatus.setTextFill(Color.RED);
            }
        });
        
        // View Feedback button
        Button viewFeedbackBtn = new Button("👁️ View Recent Feedback");
        viewFeedbackBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        viewFeedbackBtn.setOnAction(e -> showRecentFeedback());
        
        // Button container
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(submitBtn, viewFeedbackBtn);
        
        // Add all components to form
        formContainer.getChildren().addAll(formTitle, nameBox, feedbackBox, buttonContainer, feedbackStatus);
        
        // Add to main layout
        feedbackLayout.getChildren().addAll(header, formContainer);
        
        return feedbackLayout;
    }
    
    private void showRecentFeedback() {
        Stage dialog = new Stage();
        dialog.setTitle("Recent Feedback");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("📋 Recent Feedback");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        ListView<String> feedbackList = new ListView<>();
        List<String> feedbacks = FeedbackDB.getAllFeedback();
        
        if (feedbacks.isEmpty()) {
            feedbacks.add("No feedback submitted yet.");
        }
        
        feedbackList.getItems().addAll(feedbacks);
        feedbackList.setPrefHeight(300);
        feedbackList.setPrefWidth(500);
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());
        
        layout.getChildren().addAll(title, feedbackList, closeBtn);
        
        Scene scene = new Scene(layout, 550, 400);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private Button createStyledButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4a5568; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 15;");
        button.setOnAction(e -> action.run());
        return button;
    }
    
    private void updateDevicesDisplay() {
        devicesPane.getChildren().clear();
        
        for (SmartDevice device : devices) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
            card.setPrefWidth(280);
            
            String icon = "";
            String type = device.getType();
            if (type.equals("Light")) icon = "💡";
            else if (type.equals("Thermostat")) icon = "🌡️";
            else if (type.equals("Security")) icon = "🛡️";
            
            Label nameLabel = new Label(icon + " " + device.getName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            Label roomLabel = new Label("🏠 " + device.getRoom());
            roomLabel.setFont(Font.font("Arial", 12));
            
            Label statusLabel = new Label(device.isOn() ? "🟢 ONLINE" : "🔴 OFFLINE");
            statusLabel.setTextFill(device.isOn() ? Color.GREEN : Color.RED);
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            HBox buttons = new HBox(10);
            Button toggleBtn = new Button(device.isOn() ? "⚫ Turn Off" : "🔆 Turn On");
            toggleBtn.setStyle(device.isOn() ? 
                "-fx-background-color: #e53e3e; -fx-text-fill: white;" : 
                "-fx-background-color: #38a169; -fx-text-fill: white;");
            toggleBtn.setOnAction(e -> {
                if (device.isOn()) device.turnOff();
                else device.turnOn();
                updateStatus("Toggled " + device.getName() + " - " + (device.isOn() ? "ON" : "OFF"));
                updateDevicesDisplay();
            });
            
            Button controlBtn = new Button("🎮 Control");
            controlBtn.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white;");
            controlBtn.setOnAction(e -> controlDevice(device));
            
            Button removeBtn = new Button("❌ Remove");
            removeBtn.setStyle("-fx-background-color: #c53030; -fx-text-fill: white;");
            removeBtn.setOnAction(e -> {
                devices.remove(device);
                updateStatus("Removed: " + device.getName());
                updateDevicesDisplay();
            });
            
            buttons.getChildren().addAll(toggleBtn, controlBtn, removeBtn);
            card.getChildren().addAll(nameLabel, roomLabel, statusLabel, buttons);
            devicesPane.getChildren().add(card);
        }
    }
    
    private void controlDevice(SmartDevice device) {
        Stage dialog = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("Control: " + device.getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        if (device instanceof SmartLight) {
            SmartLight light = (SmartLight) device;
            
            Label brightnessLabel = new Label("Brightness: " + light.getBrightness() + "%");
            Slider brightness = new Slider(0, 100, light.getBrightness());
            brightness.setShowTickLabels(true);
            brightness.setShowTickMarks(true);
            brightness.setMajorTickUnit(25);
            
            brightness.valueProperty().addListener((obs, oldVal, newVal) -> {
                light.setBrightness(newVal.intValue());
                brightnessLabel.setText("Brightness: " + newVal.intValue() + "%");
                updateStatus(light.getName() + " brightness: " + newVal.intValue() + "%");
            });
            
            Label colorLabel = new Label("Color: " + light.getColor());
            TextField colorField = new TextField(light.getColor());
            Button colorBtn = new Button("Change Color");
            colorBtn.setOnAction(e -> {
                light.setColor(colorField.getText());
                colorLabel.setText("Color: " + colorField.getText());
                updateStatus(light.getName() + " color: " + colorField.getText());
            });
            
            layout.getChildren().addAll(title, brightnessLabel, brightness, colorLabel, colorField, colorBtn);
            
        } else if (device instanceof SmartThermostat) {
            SmartThermostat thermo = (SmartThermostat) device;
            
            Label tempLabel = new Label("Temperature: " + thermo.getTemperature() + "°C");
            Slider temp = new Slider(10, 30, thermo.getTemperature());
            temp.setShowTickLabels(true);
            temp.setShowTickMarks(true);
            temp.setMajorTickUnit(5);
            
            temp.valueProperty().addListener((obs, oldVal, newVal) -> {
                thermo.setTemperature(newVal.doubleValue());
                tempLabel.setText("Temperature: " + String.format("%.1f", newVal) + "°C");
                updateStatus(thermo.getName() + " temperature: " + String.format("%.1f", newVal) + "°C");
            });
            
            Label modeLabel = new Label("Mode: " + thermo.getMode());
            ComboBox<String> modeCombo = new ComboBox<>();
            modeCombo.getItems().addAll("Heating", "Cooling", "Auto");
            modeCombo.setValue(thermo.getMode());
            modeCombo.setOnAction(e -> {
                thermo.setMode(modeCombo.getValue());
                modeLabel.setText("Mode: " + modeCombo.getValue());
                updateStatus(thermo.getName() + " mode: " + modeCombo.getValue());
            });
            
            layout.getChildren().addAll(title, tempLabel, temp, modeLabel, modeCombo);
        }
        
        Button close = new Button("Close");
        close.setOnAction(e -> dialog.close());
        layout.getChildren().add(close);
        
        dialog.setScene(new Scene(layout, 350, 400));
        dialog.setTitle("Device Control - " + device.getName());
        dialog.show();
    }
    
    private void showAddDeviceDialog() {
        Stage dialog = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("Add New Smart Device");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("💡 Smart Light", "🌡️ Smart Thermostat", "🛡️ Smart Security");
        type.setValue("💡 Smart Light");
        
        TextField name = new TextField();
        name.setPromptText("Enter device name");
        
        TextField room = new TextField();
        room.setPromptText("Enter room name");
        
        Button add = new Button("Add Device");
        add.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold;");
        add.setOnAction(e -> {
            if (!name.getText().isEmpty() && !room.getText().isEmpty()) {
                String deviceType = type.getValue();
                SmartDevice newDevice = null;
                
                if (deviceType.equals("💡 Smart Light")) {
                    newDevice = new SmartLight(name.getText(), room.getText());
                } else if (deviceType.equals("🌡️ Smart Thermostat")) {
                    newDevice = new SmartThermostat(name.getText(), room.getText());
                } else if (deviceType.equals("🛡️ Smart Security")) {
                    newDevice = new SmartSecurity(name.getText(), room.getText());
                }
                
                if (newDevice != null) {
                    devices.add(newDevice);
                    updateStatus("✅ Added " + deviceType + ": " + name.getText() + " in " + room.getText());
                    updateDevicesDisplay();
                    dialog.close();
                }
            } else {
                updateStatus("❌ Please fill in all fields!");
            }
        });
        
        layout.getChildren().addAll(
            title, 
            new Label("Device Type:"), type,
            new Label("Device Name:"), name,
            new Label("Room:"), room,
            add
        );
        
        dialog.setScene(new Scene(layout, 300, 300));
        dialog.setTitle("Add New Device");
        dialog.show();
    }
    
    private void showVoiceCommand() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("🎤 Voice Command");
        dialog.setHeaderText("Try commands like:");
        dialog.setContentText("'turn on lights', 'turn off lights', 'good night'");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cmd -> {
            cmd = cmd.toLowerCase();
            if (cmd.contains("turn on lights")) {
                devices.stream().filter(d -> d instanceof SmartLight).forEach(SmartDevice::turnOn);
                updateStatus("✅ Voice: All lights turned ON!");
            } else if (cmd.contains("turn off lights")) {
                devices.stream().filter(d -> d instanceof SmartLight).forEach(SmartDevice::turnOff);
                updateStatus("✅ Voice: All lights turned OFF!");
            } else if (cmd.contains("good night")) {
                devices.forEach(d -> {
                    if (d instanceof SmartLight) d.turnOff();
                    if (d instanceof SmartSecurity) {
                        d.turnOn();
                        ((SmartSecurity) d).armSystem();
                    }
                });
                updateStatus("🌙 Voice: Good night! Lights off, security armed.");
            } else {
                updateStatus("❌ Voice: Command not understood: " + cmd);
            }
            updateDevicesDisplay();
        });
    }
    
    private void viewAllDevices() {
        StringBuilder sb = new StringBuilder("=== ALL DEVICES STATUS ===\n");
        sb.append("=".repeat(40)).append("\n");
        
        // Group by room
        Map<String, List<SmartDevice>> rooms = new HashMap<>();
        for (SmartDevice device : devices) {
            rooms.putIfAbsent(device.getRoom(), new ArrayList<>());
            rooms.get(device.getRoom()).add(device);
        }
        
        for (String room : rooms.keySet()) {
            sb.append("\n🏠 ").append(room.toUpperCase()).append(":\n");
            for (SmartDevice device : rooms.get(room)) {
                sb.append("  ").append(device.getStatus()).append("\n");
            }
        }
        
        updateStatus(sb.toString());
    }
    
    private void updateStatus(String message) {
        Platform.runLater(() -> {
            statusArea.appendText("> " + message + "\n");
            statusArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    private void saveDevicesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (SmartDevice device : devices) {
                writer.println(device.getClass().getSimpleName() + ":" + 
                             device.getName() + ":" + 
                             device.getRoom() + ":" + 
                             device.isOn());
            }
            updateStatus("💾 All devices saved to file!");
        } catch (IOException e) {
            updateStatus("❌ Error saving: " + e.getMessage());
        }
    }
    
    private void loadDevicesFromFile() {
        devices.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    SmartDevice device = null;
                    String type = parts[0];
                    String name = parts[1];
                    String room = parts[2];
                    boolean isOn = Boolean.parseBoolean(parts[3]);
                    
                    if (type.equals("SmartLight")) {
                        device = new SmartLight(name, room);
                    } else if (type.equals("SmartThermostat")) {
                        device = new SmartThermostat(name, room);
                    } else if (type.equals("SmartSecurity")) {
                        device = new SmartSecurity(name, room);
                    }
                    
                    if (device != null) {
                        if (isOn) device.turnOn();
                        devices.add(device);
                    }
                }
            }
            updateStatus("📂 Loaded " + devices.size() + " devices from file");
        } catch (IOException e) {
            // File doesn't exist yet, that's fine
        }
    }
    
    private void setupDefaultDevices() {
        devices.add(new SmartLight("Living Room Main Light", "Living Room"));
        devices.add(new SmartLight("Bedroom Lamp", "Bedroom"));
        devices.add(new SmartThermostat("Main Thermostat", "Living Room"));
        devices.add(new SmartSecurity("Home Security", "Entrance"));
        updateStatus("🏠 Default smart home setup complete!");
    }
}