// Device Data
const devices = [
  { id: 1, name: "Living Room Light", room: "living", type: "💡", isOn: true, brightness: 80, power: 12 },
  { id: 2, name: "AC Unit", room: "living", type: "❄️", isOn: true, temp: 22, power: 2500 },
  { id: 3, name: "Smart TV", room: "living", type: "📺", isOn: false, input: "HDMI", power: 0 },
  { id: 4, name: "Bedroom Light", room: "bedroom", type: "💡", isOn: false, brightness: 0, power: 0 },
  { id: 5, name: "Fan", room: "bedroom", type: "🌀", isOn: true, speed: 3, power: 45 },
  { id: 6, name: "Bedroom Security", room: "bedroom", type: "📷", isOn: true, recording: true, power: 8 },
]

let currentRoom = "all"

// Initialize App
function init() {
  renderDevices()
  setupFilterTabs()
}

function renderDevices() {
  const grid = document.getElementById("devicesGrid")
  const filteredDevices = currentRoom === "all" ? devices : devices.filter((d) => d.room === currentRoom)

  grid.innerHTML = filteredDevices
    .map(
      (device) => `
        <div class="device-card ${device.isOn ? "active" : ""}">
            <div class="device-header">
                <div class="device-info">
                    <h3>${device.name}</h3>
                    <span class="device-room">${device.room.charAt(0).toUpperCase() + device.room.slice(1)}</span>
                </div>
                <div class="device-icon">${device.type}</div>
            </div>
            
            <div class="device-stats">
                ${getDeviceStats(device)}
            </div>
            
            <div class="toggle-container">
                <span class="toggle-label">${device.isOn ? "On" : "Off"}</span>
                <div class="toggle-switch ${device.isOn ? "on" : ""}" onclick="toggleDevice(${device.id})"></div>
            </div>
            
            <div style="margin-bottom: 1rem;">
                ${device.isOn ? '<span class="status-indicator"><span class="status-dot"></span>Active</span>' : '<span class="status-indicator" style="background: rgba(100,116,139,0.15); border-color: rgba(100,116,139,0.3); color: #94a3b8;"><span class="status-dot" style="background: #64748b; animation: none;"></span>Inactive</span>'}
            </div>
            
            <div class="device-actions">
                <button class="btn btn-primary" onclick="toggleDevice(${device.id})">Toggle</button>
                <button class="btn btn-secondary" onclick="openSettings(${device.id})">Settings</button>
            </div>
        </div>
    `,
    )
    .join("")
}

// Get Device Stats
function getDeviceStats(device) {
  let stats = `<div class="stat"><div class="stat-name">Power</div><div class="stat-val">${device.power}W</div></div>`

  if (device.brightness !== undefined) {
    stats += `<div class="stat"><div class="stat-name">Brightness</div><div class="stat-val">${device.brightness}%</div></div>`
  } else if (device.temp !== undefined) {
    stats += `<div class="stat"><div class="stat-name">Temperature</div><div class="stat-val">${device.temp}°C</div></div>`
  } else if (device.speed !== undefined) {
    stats += `<div class="stat"><div class="stat-name">Speed</div><div class="stat-val">${device.speed}/5</div></div>`
  }

  return stats
}

// Toggle Device
function toggleDevice(id) {
  const device = devices.find((d) => d.id === id)
  if (device) {
    device.isOn = !device.isOn
    renderDevices()
    showNotification(`${device.name} turned ${device.isOn ? "ON" : "OFF"}`)
  }
}

function setupFilterTabs() {
  document.querySelectorAll(".filter-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      document.querySelectorAll(".filter-btn").forEach((b) => b.classList.remove("active"))
      e.target.classList.add("active")
      currentRoom = e.target.dataset.room
      renderDevices()
    })
  })
}

// Show Notification
function showNotification(message) {
  const notification = document.getElementById("notification")
  notification.textContent = message
  notification.classList.add("show")

  setTimeout(() => {
    notification.classList.remove("show")
  }, 2500)
}

// Settings (placeholder)
function openSettings(id) {
  const device = devices.find((d) => d.id === id)
  showNotification(`⚙️ Opening settings for ${device.name}`)
}

// Initialize on load
document.addEventListener("DOMContentLoaded", init)
