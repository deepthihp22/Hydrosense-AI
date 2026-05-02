package com.iot.wastewatermonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iot.wastewatermonitor.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

data class DashboardUiState(
    val reading: SensorReading = MockDataSource.currentReading,
    val phStatus: PhStatus = MockDataSource.currentReading.phLevel.toPhStatus(),
    val turbidityStatus: TurbidityStatus = MockDataSource.currentReading.turbidity.toTurbidityStatus(),
    val overallQuality: WaterQuality = overallQuality(
        MockDataSource.currentReading.phLevel,
        MockDataSource.currentReading.turbidity,
        MockDataSource.currentReading.waterLevel
    ),
    val isLive: Boolean = true
)

data class HistoryUiState(
    val entries: List<HistoryEntry> = MockDataSource.todayHistory,
    val selectedRange: TimeRange = TimeRange.TODAY
)

enum class TimeRange(val label: String) { TODAY("Today"), WEEK("This Week") }

data class SettingsUiState(
    val connectionMode: ConnectionMode = ConnectionMode.NONE,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val deviceName: String = "—",
    val ipAddress: String = "192.168.1.100",
    val port: String = "8080",
    val autoRefreshSeconds: Int = 5
)

enum class ConnectionMode { NONE, BLUETOOTH, WIFI }
enum class DeviceStatus { CONNECTED, DISCONNECTED, CONNECTING }

class SensorViewModel : ViewModel() {

    // ── Dashboard ─────────────────────────────────────────────────────────────
    private val _dashboardState = MutableStateFlow(DashboardUiState())
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    // ── Alerts ────────────────────────────────────────────────────────────────
    private val _alerts = MutableStateFlow(MockDataSource.alerts)
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    // ── History ───────────────────────────────────────────────────────────────
    private val _historyState = MutableStateFlow(HistoryUiState())
    val historyState: StateFlow<HistoryUiState> = _historyState.asStateFlow()

    // ── Settings ──────────────────────────────────────────────────────────────
    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    init {
        startMockLiveUpdates()
    }

    /**
     * Simulates live sensor fluctuations every 5 seconds.
     * Replace this coroutine body with real BLE/WiFi reads when hardware is ready.
     */
    private fun startMockLiveUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(5_000)
                val current = _dashboardState.value.reading
                val newPh = (current.phLevel + Random.nextFloat() * 0.4f - 0.2f).coerceIn(4f, 10f)
                val newTurb = (current.turbidity + Random.nextFloat() * 2f - 1f).coerceIn(0f, 100f)
                val newLevel = (current.waterLevel + Random.nextFloat() * 1f - 0.3f).coerceIn(0f, 100f)
                val newReading = SensorReading(
                    phLevel    = newPh,
                    turbidity  = newTurb,
                    waterLevel = newLevel,
                    timestamp  = LocalDateTime.now()
                )
                _dashboardState.value = DashboardUiState(
                    reading          = newReading,
                    phStatus         = newPh.toPhStatus(),
                    turbidityStatus  = newTurb.toTurbidityStatus(),
                    overallQuality   = overallQuality(newPh, newTurb, newLevel)
                )
            }
        }
    }

    fun selectTimeRange(range: TimeRange) {
        val entries = if (range == TimeRange.TODAY) MockDataSource.todayHistory
                      else MockDataSource.weekHistory
        _historyState.value = HistoryUiState(entries = entries, selectedRange = range)
    }

    fun dismissAlert(id: Int) {
        _alerts.value = _alerts.value.filter { it.id != id }
    }

    fun connectBluetooth() {
        _settingsState.value = _settingsState.value.copy(
            connectionMode = ConnectionMode.BLUETOOTH,
            deviceStatus   = DeviceStatus.CONNECTING
        )
        viewModelScope.launch {
            delay(2_000)
            _settingsState.value = _settingsState.value.copy(
                deviceStatus = DeviceStatus.CONNECTED,
                deviceName   = "ESP32-WW-01"
            )
        }
    }

    fun connectWifi() {
        _settingsState.value = _settingsState.value.copy(
            connectionMode = ConnectionMode.WIFI,
            deviceStatus   = DeviceStatus.CONNECTING
        )
        viewModelScope.launch {
            delay(1_500)
            _settingsState.value = _settingsState.value.copy(
                deviceStatus = DeviceStatus.CONNECTED,
                deviceName   = "ESP32-WW-01 (WiFi)"
            )
        }
    }

    fun disconnect() {
        _settingsState.value = _settingsState.value.copy(
            connectionMode = ConnectionMode.NONE,
            deviceStatus   = DeviceStatus.DISCONNECTED,
            deviceName     = "—"
        )
    }

    fun updateIpAddress(ip: String) {
        _settingsState.value = _settingsState.value.copy(ipAddress = ip)
    }

    fun updatePort(port: String) {
        _settingsState.value = _settingsState.value.copy(port = port)
    }
}
