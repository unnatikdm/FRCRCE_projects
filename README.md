# Smart Home Control System

A full-stack smart home dashboard with Java Spring Boot backend and modern HTML/CSS/JS frontend.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js (optional, for running frontend separately)

### Backend Setup

1. **Navigate to backend directory:**
   \`\`\`bash
   cd backend
   \`\`\`

2. **Run the Spring Boot application:**
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

   Or if Maven isn't in your PATH:
   \`\`\`bash
   ./mvnw spring-boot:run
   \`\`\`

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory:**
   \`\`\`bash
   cd frontend
   \`\`\`

2. **Open in browser:**
   - Simply open `index.html` in your web browser, or
   - Use a local server:
     \`\`\`bash
     # If you have Python 3
     python -m http.server 8000
     
     # If you have Node.js with http-server
     npx http-server
     \`\`\`

3. **Access the dashboard:**
   Open `http://localhost:8000` (or your chosen port)

## 📱 Features

- **Device Control**: Toggle lights, thermostats, fans, and security devices
- **Room Filtering**: Filter devices by room
- **Real-time Controls**: Adjust brightness, temperature, and speed
- **Modern UI**: Beautiful gradients and glass-morphism design
- **Responsive Design**: Works on desktop, tablet, and mobile

## 🔌 API Endpoints

- `GET /api/devices` - Get all devices
- `GET /api/devices/{id}` - Get specific device
- `POST /api/devices/{id}/toggle` - Toggle device on/off
- `POST /api/devices/{id}/brightness` - Set brightness (0-100)
- `POST /api/devices/{id}/temperature` - Set temperature (60-85°F)
- `POST /api/devices/{id}/speed` - Set fan speed (0-3)

## 🛠️ Project Structure

\`\`\`
smart-home-system/
├── backend/
│   ├── src/main/java/com/smarthome/
│   │   ├── SmartHomeApplication.java
│   │   ├── controller/
│   │   │   └── DeviceController.java
│   │   └── model/
│   │       └── Device.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
└── frontend/
    ├── index.html
    ├── styles.css
    └── script.js
\`\`\`

## 🎨 Customization

- **Add New Devices**: Edit the static initialization in `DeviceController.java`
- **Modify Styles**: Update `frontend/styles.css` with your preferred colors/gradients
- **Add Features**: Extend `DeviceController.java` with new endpoints

## 📝 Notes

- Backend uses in-memory storage (data resets on restart)
- To persist data, integrate with a database (MySQL, PostgreSQL, etc.)
- Frontend communicates with backend via REST API
- CORS is enabled for cross-origin requests

---

**Enjoy your Smart Home Dashboard!** 🏠✨
