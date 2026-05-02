package com.iot.wastewatermonitor.data

import java.time.LocalDateTime

// ── Domain models ─────────────────────────────────────────────────────────────

data class SensorReading(
    val phLevel: Float,
    val turbidity: Float,       // NTU
    val waterLevel: Float,      // 0–100 %
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class AlertItem(
    val id: Int,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: LocalDateTime
)

enum class AlertSeverity { WARNING, DANGER }

// ── Status helpers ────────────────────────────────────────────────────────────

enum class PhStatus(val label: String) {
    ACIDIC("Acidic"), NEUTRAL("Neutral"), ALKALINE("Alkaline")
}

enum class TurbidityStatus(val label: String) {
    CLEAN("Clean"), MODERATE("Moderate"), DIRTY("Dirty")
}

enum class WaterQuality(val label: String) {
    GOOD("Good"), CAUTION("Caution"), UNSAFE("Unsafe")
}

fun Float.toPhStatus(): PhStatus = when {
    this < 6.5f  -> PhStatus.ACIDIC
    this > 8.5f  -> PhStatus.ALKALINE
    else         -> PhStatus.NEUTRAL
}

fun Float.toTurbidityStatus(): TurbidityStatus = when {
    this < 10f   -> TurbidityStatus.CLEAN
    this < 50f   -> TurbidityStatus.MODERATE
    else         -> TurbidityStatus.DIRTY
}

fun overallQuality(ph: Float, turbidity: Float, waterLevel: Float): WaterQuality {
    val phOk        = ph in 6.5f..8.5f
    val turbOk      = turbidity < 10f
    val levelOk     = waterLevel < 85f
    return when {
        phOk && turbOk && levelOk -> WaterQuality.GOOD
        !phOk && !turbOk          -> WaterQuality.UNSAFE
        else                      -> WaterQuality.CAUTION
    }
}

// ── History entry ─────────────────────────────────────────────────────────────

data class HistoryEntry(
    val hour: Int,          // 0–23 for "today" view
    val ph: Float,
    val turbidity: Float,
    val waterLevel: Float
)
