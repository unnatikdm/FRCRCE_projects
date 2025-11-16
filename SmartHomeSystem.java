import java.io.*;
import java.util.*;

// Smart Device base class
abstract class SmartDevice {
    protected String name;
    protected boolean isOn;
    protected String room;
    
    public SmartDevice(String name, String room) {
        this.name = name;
        this.room = room;
        this.isOn = false;
    }
    
    public void turnOn() { 
        this.isOn = true; 
        System.out.println("🔆 " + name + " turned ON");
    }
    
    public void turnOff() { 
        this.isOn = false; 
        System.out.println("⚫ " + name + " turned OFF");
    }
    
    public abstract String getStatus();
    public abstract String getType();
    
    // Getters
    public String getName() { return name; }
    public String getRoom() { return room; }
    public boolean isOn() { return isOn; }
}

// Different types of smart devices
class SmartLight extends SmartDevice {
    private int brightness;
    private String color;
    
    public SmartLight(String name, String room) {
        super(name, room);
        this.brightness = 100;
        this.color = "White";
    }
    
    public void setBrightness(int brightness) {
        this.brightness = brightness;
        System.out.println("💡 " + name + " brightness set to " + brightness + "%");
    }
    
    public void setColor(String color) {
        this.color = color;
        System.out.println("🎨 " + name + " color changed to " + color);
    }
    
    @Override
    public String getStatus() {
        return "Light | Room: " + room + " | " + (isOn ? "ON" : "OFF") + 
               " | Brightness: " + brightness + "% | Color: " + color;
    }
    
    @Override
    public String getType() { return "Light"; }
}

class SmartThermostat extends SmartDevice {
    private double temperature;
    private String mode; // Heating, Cooling, Auto
    
    public SmartThermostat(String name, String room) {
        super(name, room);
        this.temperature = 22.0;
        this.mode = "Auto";
    }
    
    public void setTemperature(double temp) {
        this.temperature = temp;
        System.out.println("🌡️ " + name + " temperature set to " + temp + "°C");
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        System.out.println("🔄 " + name + " mode changed to " + mode);
    }
    
    @Override
    public String getStatus() {
        return "Thermostat | Room: " + room + " | " + (isOn ? "ON" : "OFF") + 
               " | Temp: " + temperature + "°C | Mode: " + mode;
    }
    
    @Override
    public String getType() { return "Thermostat"; }
}

class SmartSecurity extends SmartDevice {
    private boolean isArmed;
    private String securityLevel;
    
    public SmartSecurity(String name, String room) {
        super(name, room);
        this.isArmed = false;
        this.securityLevel = "Normal";
    }
    
    public void armSystem() {
        this.isArmed = true;
        System.out.println("🚨 " + name + " ARMED - Security activated!");
    }
    
    public void disarmSystem() {
        this.isArmed = false;
        System.out.println("✅ " + name + " DISARMED - Security deactivated");
    }
    
    public void setSecurityLevel(String level) {
        this.securityLevel = level;
        System.out.println("🛡️ " + name + " security level: " + level);
    }
    
    @Override
    public String getStatus() {
        return "Security | Room: " + room + " | " + (isOn ? "Active" : "Inactive") + 
               " | Armed: " + isArmed + " | Level: " + securityLevel;
    }
    
    @Override
    public String getType() { return "Security"; }
}

// Main Smart Home System
public class SmartHomeSystem {
    private static final String FILE_NAME = "smart_home_data.txt";
    private static Scanner scanner = new Scanner(System.in);
    private static List<SmartDevice> devices = new ArrayList<>();
    
    public static void main(String[] args) {
        loadDevicesFromFile();
        
        // Add some default devices if empty
        if (devices.isEmpty()) {
            setupDefaultDevices();
        }
        
        System.out.println("🏠 === SMART HOME SYSTEM ===");
        System.out.println("🤖 Welcome to your Smart Home!");
        
        while (true) {
            showCoolMenu();
            int choice = getIntInput("Choose option: ");
            
            switch (choice) {
                case 1: viewAllDevices(); break;
                case 2: controlDevice(); break;
                case 3: addNewDevice(); break;
                case 4: removeDevice(); break;
                case 5: roomControl(); break;
                case 6: voiceCommand(); break;
                case 7: 
                    saveDevicesToFile();
                    System.out.println("👋 Goodbye! Your home is secure.");
                    return;
                default: 
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
    
    private static void showCoolMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🏠 SMART HOME CONTROL PANEL");
        System.out.println("=".repeat(40));
        System.out.println("1. 📊 View All Devices");
        System.out.println("2. 🎮 Control Device");
        System.out.println("3. ➕ Add New Device");
        System.out.println("4. ❌ Remove Device");
        System.out.println("5. 🏠 Room Control");
        System.out.println("6. 🎤 Voice Command");
        System.out.println("7. 🚪 Exit");
        System.out.println("-".repeat(40));
    }
    
    private static void viewAllDevices() {
        if (devices.isEmpty()) {
            System.out.println("🤷 No devices found! Add some devices first.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 YOUR SMART DEVICES");
        System.out.println("=".repeat(50));
        
        Map<String, List<SmartDevice>> rooms = new HashMap<>();
        
        // Group by room
        for (SmartDevice device : devices) {
            rooms.putIfAbsent(device.getRoom(), new ArrayList<>());
            rooms.get(device.getRoom()).add(device);
        }
        
        // Display by room
        for (String room : rooms.keySet()) {
            System.out.println("\n🏠 " + room.toUpperCase() + ":");
            System.out.println("-".repeat(30));
            for (SmartDevice device : rooms.get(room)) {
                String statusIcon = device.isOn() ? "🟢" : "🔴";
                System.out.println(statusIcon + " " + device.getStatus());
            }
        }
    }
    
    private static void controlDevice() {
        if (devices.isEmpty()) {
            System.out.println("❌ No devices to control!");
            return;
        }
        
        System.out.println("\n🎮 CONTROL DEVICE");
        for (int i = 0; i < devices.size(); i++) {
            System.out.println((i+1) + ". " + devices.get(i).getName() + " (" + devices.get(i).getRoom() + ")");
        }
        
        int deviceIndex = getIntInput("Select device (1-" + devices.size() + "): ") - 1;
        
        if (deviceIndex < 0 || deviceIndex >= devices.size()) {
            System.out.println("❌ Invalid device selection!");
            return;
        }
        
        SmartDevice device = devices.get(deviceIndex);
        System.out.println("🎯 Controlling: " + device.getName());
        
        // Basic controls for all devices
        System.out.println("1. 🔄 Turn ON");
        System.out.println("2. ⚫ Turn OFF");
        System.out.println("3. 📊 Get Status");
        
        // Device-specific controls
        if (device instanceof SmartLight) {
            System.out.println("4. 💡 Set Brightness");
            System.out.println("5. 🎨 Change Color");
        } else if (device instanceof SmartThermostat) {
            System.out.println("4. 🌡️ Set Temperature");
            System.out.println("5. 🔄 Change Mode");
        } else if (device instanceof SmartSecurity) {
            System.out.println("4. 🚨 Arm System");
            System.out.println("5. ✅ Disarm System");
        }
        
        int action = getIntInput("Choose action: ");
        
        switch (action) {
            case 1: device.turnOn(); break;
            case 2: device.turnOff(); break;
            case 3: System.out.println(device.getStatus()); break;
            case 4: 
                if (device instanceof SmartLight) {
                    int brightness = getIntInput("Enter brightness (1-100): ");
                    ((SmartLight) device).setBrightness(brightness);
                } else if (device instanceof SmartThermostat) {
                    double temp = getDoubleInput("Enter temperature: ");
                    ((SmartThermostat) device).setTemperature(temp);
                } else if (device instanceof SmartSecurity) {
                    ((SmartSecurity) device).armSystem();
                }
                break;
            case 5:
                if (device instanceof SmartLight) {
                    System.out.print("Enter color: ");
                    String color = scanner.nextLine();
                    ((SmartLight) device).setColor(color);
                } else if (device instanceof SmartThermostat) {
                    System.out.print("Enter mode (Heating/Cooling/Auto): ");
                    String mode = scanner.nextLine();
                    ((SmartThermostat) device).setMode(mode);
                } else if (device instanceof SmartSecurity) {
                    ((SmartSecurity) device).disarmSystem();
                }
                break;
            default: System.out.println("❌ Invalid action!");
        }
    }
    
    private static void addNewDevice() {
        System.out.println("\n➕ ADD NEW SMART DEVICE");
        System.out.println("1. 💡 Smart Light");
        System.out.println("2. 🌡️ Smart Thermostat");
        System.out.println("3. 🛡️ Smart Security");
        
        int type = getIntInput("Choose device type: ");
        
        System.out.print("Enter device name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter room: ");
        String room = scanner.nextLine();
        
        SmartDevice newDevice;
        
        switch (type) {
            case 1: 
                newDevice = new SmartLight(name, room);
                break;
            case 2:
                newDevice = new SmartThermostat(name, room);
                break;
            case 3:
                newDevice = new SmartSecurity(name, room);
                break;
            default:
                System.out.println("❌ Invalid device type!");
                return;
        }
        
        devices.add(newDevice);
        System.out.println("✅ " + newDevice.getType() + " '" + name + "' added to " + room + "!");
    }
    
    private static void removeDevice() {
        if (devices.isEmpty()) {
            System.out.println("❌ No devices to remove!");
            return;
        }
        
        System.out.println("\n❌ REMOVE DEVICE");
        for (int i = 0; i < devices.size(); i++) {
            System.out.println((i+1) + ". " + devices.get(i).getName());
        }
        
        int index = getIntInput("Select device to remove: ") - 1;
        if (index >= 0 && index < devices.size()) {
            String removedName = devices.get(index).getName();
            devices.remove(index);
            System.out.println("✅ Device '" + removedName + "' removed!");
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }
    
    private static void roomControl() {
        System.out.println("\n🏠 ROOM CONTROL");
        Set<String> rooms = new HashSet<>();
        for (SmartDevice device : devices) {
            rooms.add(device.getRoom());
        }
        
        if (rooms.isEmpty()) {
            System.out.println("❌ No rooms found!");
            return;
        }
        
        System.out.println("Available rooms: " + String.join(", ", rooms));
        System.out.print("Enter room name: ");
        String room = scanner.nextLine();
        
        System.out.println("1. 🔄 Turn ON all devices in " + room);
        System.out.println("2. ⚫ Turn OFF all devices in " + room);
        int action = getIntInput("Choose action: ");
        
        int count = 0;
        for (SmartDevice device : devices) {
            if (device.getRoom().equalsIgnoreCase(room)) {
                if (action == 1) {
                    device.turnOn();
                } else if (action == 2) {
                    device.turnOff();
                }
                count++;
            }
        }
        
        System.out.println("✅ " + count + " devices in " + room + " updated!");
    }
    
    private static void voiceCommand() {
        System.out.println("\n🎤 VOICE COMMAND MODE");
        System.out.println("💡 Try commands like: 'turn on lights', 'set temperature to 25', 'good night'");
        System.out.print("🎤 Say command: ");
        String command = scanner.nextLine().toLowerCase();
        
        if (command.contains("turn on") && command.contains("light")) {
            for (SmartDevice device : devices) {
                if (device instanceof SmartLight) {
                    device.turnOn();
                }
            }
            System.out.println("✅ All lights turned ON!");
        }
        else if (command.contains("turn off") && command.contains("light")) {
            for (SmartDevice device : devices) {
                if (device instanceof SmartLight) {
                    device.turnOff();
                }
            }
            System.out.println("✅ All lights turned OFF!");
        }
        else if (command.contains("good night")) {
            for (SmartDevice device : devices) {
                if (device instanceof SmartLight) {
                    device.turnOff();
                }
                if (device instanceof SmartSecurity) {
                    device.turnOn();
                    ((SmartSecurity) device).armSystem();
                }
            }
            System.out.println("🌙 Good night! Lights off and security armed.");
        }
        else if (command.contains("temperature") && command.contains("set")) {
            // Simple extraction of number from command
            String[] words = command.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].equals("to") && i + 1 < words.length) {
                    try {
                        double temp = Double.parseDouble(words[i + 1]);
                        for (SmartDevice device : devices) {
                            if (device instanceof SmartThermostat) {
                                device.turnOn();
                                ((SmartThermostat) device).setTemperature(temp);
                            }
                        }
                        System.out.println("✅ Temperature set to " + temp + "°C");
                        return;
                    } catch (NumberFormatException e) {
                        // Ignore if not a number
                    }
                }
            }
            System.out.println("❌ Couldn't understand temperature value!");
        }
        else {
            System.out.println("❌ Command not recognized! Try simpler commands.");
        }
    }
    
    // Helper methods
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            }
        }
    }
    
    // File handling
    private static void saveDevicesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (SmartDevice device : devices) {
                writer.println(device.getClass().getSimpleName() + ":" + 
                             device.getName() + ":" + 
                             device.getRoom() + ":" + 
                             device.isOn());
            }
            System.out.println("💾 Smart home data saved!");
        } catch (IOException e) {
            
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }
    
    private static void loadDevicesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    String type = parts[0];
                    String name = parts[1];
                    String room = parts[2];
                    boolean isOn = Boolean.parseBoolean(parts[3]);
                    
                    SmartDevice device;
                    switch (type) {
                        case "SmartLight": device = new SmartLight(name, room); break;
                        case "SmartThermostat": device = new SmartThermostat(name, room); break;
                        case "SmartSecurity": device = new SmartSecurity(name, room); break;
                        default: continue;
                    }
                    
                    if (isOn) device.turnOn();
                    devices.add(device);
                }
            }
            System.out.println("📂 Loaded " + devices.size() + " devices from memory");
        } catch (IOException e) {
            System.out.println("💡 Starting with fresh smart home setup");
        }
    }
    
    private static void setupDefaultDevices() {
        devices.add(new SmartLight("Living Room Main Light", "Living Room"));
        devices.add(new SmartLight("Bedroom Lamp", "Bedroom"));
        devices.add(new SmartThermostat("Main Thermostat", "Living Room"));
        devices.add(new SmartSecurity("Home Security", "Entrance"));
        System.out.println("🏠 Default smart home devices setup complete!");
    }
}
