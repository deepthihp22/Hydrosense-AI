package com.iot.wastewatermonitor.data

import java.time.LocalDateTime

/**
 * Provides mock sensor data.
 * Replace this class with a real BLE / WiFi data source when hardware is ready.
 */
object MockDataSource {

    /** Current live reading (simulated) */
    val currentReading = SensorReading(
        phLevel    = 7.2f,
        turbidity  = 8.4f,
        waterLevel = 62f,
        timestamp  = LocalDateTime.now()
    )

    /** Active alerts */
    val alerts: List<AlertItem> = listOf(
        AlertItem(
            id        = 1,
            title     = "High Turbidity Detected",
            message   = "Turbidity reading of 52 NTU exceeds safe threshold (10 NTU). Immediate inspection recommended.",
            severity  = AlertSeverity.DANGER,
            timestamp = LocalDateTime.now().minusMinutes(5)
        ),
        AlertItem(
            id        = 2,
            title     = "pH Level Warning",
            message   = "pH dropped to 6.1 — slightly acidic. Monitor closely.",
            severity  = AlertSeverity.WARNING,
            timestamp = LocalDateTime.now().minusMinutes(22)
        ),
        AlertItem(
            id        = 3,
            title     = "Water Level Rising",
            message   = "Tank at 88%. Approaching overflow threshold (90%).",
            severity  = AlertSeverity.WARNING,
            timestamp = LocalDateTime.now().minusHours(1)
        ),
        AlertItem(
            id        = 4,
            title     = "Sensor Offline",
            message   = "Turbidity sensor #2 has not reported in 15 minutes.",
            severity  = AlertSeverity.DANGER,
            timestamp = LocalDateTime.now().minusHours(2)
        )
    )

    /** 24-hour history for charts */
    val todayHistory: List<HistoryEntry> = listOf(
        HistoryEntry(0,  7.0f, 6.2f,  55f),
        HistoryEntry(1,  7.1f, 6.8f,  56f),
        HistoryEntry(2,  7.0f, 7.1f,  57f),
        HistoryEntry(3,  6.9f, 7.5f,  58f),
        HistoryEntry(4,  6.8f, 8.0f,  59f),
        HistoryEntry(5,  6.7f, 9.2f,  60f),
        HistoryEntry(6,  6.6f, 10.5f, 61f),
        HistoryEntry(7,  6.5f, 12.0f, 62f),
        HistoryEntry(8,  6.8f, 15.3f, 63f),
        HistoryEntry(9,  7.0f, 18.7f, 64f),
        HistoryEntry(10, 7.2f, 22.1f, 65f),
        HistoryEntry(11, 7.4f, 28.4f, 66f),
        HistoryEntry(12, 7.6f, 35.0f, 67f),
        HistoryEntry(13, 7.8f, 42.3f, 68f),
        HistoryEntry(14, 8.0f, 50.1f, 70f),
        HistoryEntry(15, 8.2f, 52.0f, 72f),
        HistoryEntry(16, 8.0f, 48.5f, 74f),
        HistoryEntry(17, 7.8f, 40.2f, 76f),
        HistoryEntry(18, 7.5f, 30.1f, 78f),
        HistoryEntry(19, 7.3f, 20.5f, 80f),
        HistoryEntry(20, 7.1f, 14.2f, 82f),
        HistoryEntry(21, 7.0f, 10.8f, 84f),
        HistoryEntry(22, 6.9f, 9.5f,  86f),
        HistoryEntry(23, 7.2f, 8.4f,  62f)
    )

    /** 7-day history (one entry per day, averaged) */
    val weekHistory: List<HistoryEntry> = listOf(
        HistoryEntry(0, 7.1f, 9.2f,  58f),
        HistoryEntry(1, 7.3f, 11.5f, 60f),
        HistoryEntry(2, 6.9f, 14.8f, 63f),
        HistoryEntry(3, 7.5f, 22.3f, 67f),
        HistoryEntry(4, 8.1f, 38.7f, 72f),
        HistoryEntry(5, 7.8f, 45.2f, 78f),
        HistoryEntry(6, 7.2f, 8.4f,  62f)
    )
}
