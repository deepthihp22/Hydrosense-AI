package com.iot.wastewatermonitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iot.wastewatermonitor.ui.components.GlassCard
import com.iot.wastewatermonitor.ui.components.NeonBadge
import com.iot.wastewatermonitor.ui.theme.AppColors
import com.iot.wastewatermonitor.viewmodel.*

@Composable
fun SettingsScreen(viewModel: SensorViewModel) {
    val state by viewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text  = "SETTINGS",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.NeonCyan,
                letterSpacing = 3.sp
            )
            Text(
                text  = "Device & Config",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Device status card
        DeviceStatusCard(state)

        // Connection section
        ConnectionSection(state, viewModel)

        // WiFi config
        WifiConfigSection(state, viewModel)

        // Calibration section
        CalibrationSection()

        // App info
        AppInfoSection()

        Spacer(Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Device status
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeviceStatusCard(state: SettingsUiState) {
    val (statusColor, statusLabel) = when (state.deviceStatus) {
        DeviceStatus.CONNECTED    -> AppColors.NeonGreen  to "Connected"
        DeviceStatus.DISCONNECTED -> AppColors.TextSecondary to "Disconnected"
        DeviceStatus.CONNECTING   -> AppColors.NeonYellow to "Connecting…"
    }

    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = statusColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SensorIconBox(Icons.Filled.DeviceHub, statusColor)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text  = "IoT Device",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text  = state.deviceName,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            NeonBadge(statusLabel, statusColor)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Connection section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ConnectionSection(state: SettingsUiState, viewModel: SensorViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = AppColors.NeonCyan) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionTitle("Connect Device", Icons.Filled.Link, AppColors.NeonCyan)

            // Bluetooth button
            ConnectButton(
                label    = "Connect via Bluetooth",
                icon     = Icons.Filled.Bluetooth,
                color    = AppColors.NeonCyan,
                enabled  = state.deviceStatus != DeviceStatus.CONNECTING,
                isActive = state.connectionMode == ConnectionMode.BLUETOOTH && state.deviceStatus == DeviceStatus.CONNECTED,
                onClick  = { viewModel.connectBluetooth() }
            )

            // WiFi button
            ConnectButton(
                label    = "Connect via WiFi",
                icon     = Icons.Filled.Wifi,
                color    = AppColors.NeonGreen,
                enabled  = state.deviceStatus != DeviceStatus.CONNECTING,
                isActive = state.connectionMode == ConnectionMode.WIFI && state.deviceStatus == DeviceStatus.CONNECTED,
                onClick  = { viewModel.connectWifi() }
            )

            // Disconnect
            if (state.deviceStatus == DeviceStatus.CONNECTED) {
                OutlinedButton(
                    onClick  = { viewModel.disconnect() },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.NeonRed),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, AppColors.NeonRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Filled.LinkOff, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Disconnect")
                }
            }

            if (state.deviceStatus == DeviceStatus.CONNECTING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color    = AppColors.NeonYellow,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = "Establishing connection…",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.NeonYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectButton(
    label: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick  = onClick,
        enabled  = enabled && !isActive,
        modifier = Modifier.fillMaxWidth(),
        colors   = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = if (isActive) 0.25f else 0.15f),
            contentColor   = color,
            disabledContainerColor = color.copy(alpha = 0.08f),
            disabledContentColor   = color.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (isActive) "✓ $label" else label)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// WiFi config
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WifiConfigSection(state: SettingsUiState, viewModel: SensorViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = AppColors.NeonGreen) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("WiFi Configuration", Icons.Filled.SettingsEthernet, AppColors.NeonGreen)

            NeonTextField(
                value       = state.ipAddress,
                onValueChange = { viewModel.updateIpAddress(it) },
                label       = "Device IP Address",
                placeholder = "192.168.1.100",
                keyboardType = KeyboardType.Uri
            )

            NeonTextField(
                value       = state.port,
                onValueChange = { viewModel.updatePort(it) },
                label       = "Port",
                placeholder = "8080",
                keyboardType = KeyboardType.Number
            )

            Text(
                text  = "Endpoint: http://${state.ipAddress}:${state.port}/api/sensors",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
private fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, style = MaterialTheme.typography.bodySmall) },
        placeholder   = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColors.NeonCyan,
            unfocusedBorderColor = AppColors.Divider,
            focusedLabelColor    = AppColors.NeonCyan,
            unfocusedLabelColor  = AppColors.TextSecondary,
            cursorColor          = AppColors.NeonCyan,
            focusedTextColor     = AppColors.TextPrimary,
            unfocusedTextColor   = AppColors.TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Calibration
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalibrationSection() {
    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = AppColors.NeonYellow) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Sensor Calibration", Icons.Filled.Tune, AppColors.NeonYellow)

            CalibrationRow("pH Sensor",        "Last: 2 days ago",  AppColors.NeonGreen)
            CalibrationRow("Turbidity Sensor", "Last: 5 days ago",  AppColors.NeonYellow)
            CalibrationRow("Level Sensor",     "Last: 1 day ago",   AppColors.NeonGreen)

            Spacer(Modifier.height(4.dp))

            Button(
                onClick  = { /* TODO: trigger calibration wizard */ },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AppColors.NeonYellow.copy(alpha = 0.15f),
                    contentColor   = AppColors.NeonYellow
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Calibration Wizard")
            }
        }
    }
}

@Composable
private fun CalibrationRow(sensor: String, lastCalib: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(sensor,    style = MaterialTheme.typography.bodyMedium, color = AppColors.TextPrimary)
            Text(lastCalib, style = MaterialTheme.typography.bodySmall,  color = AppColors.TextSecondary)
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(statusColor, androidx.compose.foundation.shape.CircleShape)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// App info
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AppInfoSection() {
    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = AppColors.TextSecondary) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionTitle("About", Icons.Filled.Info, AppColors.TextSecondary)
            InfoRow("App Version",    "1.0.0")
            InfoRow("Protocol",       "Arduino / ESP32 REST + BLE")
            InfoRow("Data Mode",      "Mock (Demo)")
            InfoRow("Refresh Rate",   "5 seconds")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary
        )
    }
}
