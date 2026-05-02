package com.iot.wastewatermonitor.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iot.wastewatermonitor.data.*
import com.iot.wastewatermonitor.ui.components.GlassCard
import com.iot.wastewatermonitor.ui.components.NeonBadge
import com.iot.wastewatermonitor.ui.theme.AppColors
import com.iot.wastewatermonitor.viewmodel.SensorViewModel
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(viewModel: SensorViewModel) {
    val state by viewModel.dashboardState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        DashboardTopBar(state)

        // ── Overall quality banner ────────────────────────────────────────────
        OverallQualityBanner(state.overallQuality)

        // ── Sensor cards ──────────────────────────────────────────────────────
        PhCard(state.reading.phLevel, state.phStatus)
        TurbidityCard(state.reading.turbidity, state.turbidityStatus)
        WaterLevelCard(state.reading.waterLevel)

        // ── System info row ───────────────────────────────────────────────────
        SystemInfoRow()

        Spacer(Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(state: com.iot.wastewatermonitor.viewmodel.DashboardUiState) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text  = "WASTEWATER",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.NeonCyan,
                letterSpacing = 3.sp
            )
            Text(
                text  = "Monitor",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            // Live pulse indicator
            LivePulse()
            Spacer(Modifier.height(4.dp))
            Text(
                text  = state.reading.timestamp.format(formatter),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
private fun LivePulse() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(AppColors.NeonGreen.copy(alpha = alpha), CircleShape)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text  = "LIVE",
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.NeonGreen.copy(alpha = alpha),
            letterSpacing = 1.5.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Overall quality banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OverallQualityBanner(quality: WaterQuality) {
    val (color, icon, desc) = when (quality) {
        WaterQuality.GOOD    -> Triple(AppColors.NeonGreen,  Icons.Filled.CheckCircle,  "All parameters within safe range")
        WaterQuality.CAUTION -> Triple(AppColors.NeonYellow, Icons.Filled.Warning,       "Some parameters need attention")
        WaterQuality.UNSAFE  -> Triple(AppColors.NeonRed,    Icons.Filled.Cancel,        "Critical — immediate action required")
    }

    val animColor by animateColorAsState(targetValue = color, label = "qualityColor")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(animColor.copy(alpha = 0.20f), animColor.copy(alpha = 0.05f))
                )
            )
            .then(
                Modifier.shadow(0.dp) // no shadow, border only
            )
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(72.dp)
                .background(animColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = animColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text  = "WATER QUALITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text  = quality.label.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = animColor,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text  = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// pH Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PhCard(ph: Float, status: PhStatus) {
    val statusColor = when (status) {
        PhStatus.NEUTRAL  -> AppColors.NeonGreen
        PhStatus.ACIDIC   -> AppColors.NeonRed
        PhStatus.ALKALINE -> AppColors.NeonYellow
    }

    SensorCard(
        title       = "pH Level",
        icon        = Icons.Filled.WaterDrop,
        accentColor = statusColor,
        value       = "%.2f".format(ph),
        unit        = "pH",
        statusLabel = status.label,
        statusColor = statusColor,
        detail      = phDetail(ph)
    )
}

private fun phDetail(ph: Float): String = when {
    ph < 6.5f -> "Below safe range (6.5–8.5). Acidic conditions may corrode pipes."
    ph > 8.5f -> "Above safe range (6.5–8.5). Alkaline — check chemical dosing."
    else      -> "Within safe range (6.5–8.5). No action required."
}

// ─────────────────────────────────────────────────────────────────────────────
// Turbidity Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TurbidityCard(turbidity: Float, status: TurbidityStatus) {
    val statusColor = when (status) {
        TurbidityStatus.CLEAN    -> AppColors.NeonGreen
        TurbidityStatus.MODERATE -> AppColors.NeonYellow
        TurbidityStatus.DIRTY    -> AppColors.NeonRed
    }

    SensorCard(
        title       = "Turbidity",
        icon        = Icons.Filled.Opacity,
        accentColor = statusColor,
        value       = "%.1f".format(turbidity),
        unit        = "NTU",
        statusLabel = status.label,
        statusColor = statusColor,
        detail      = turbidityDetail(turbidity)
    )
}

private fun turbidityDetail(ntu: Float): String = when {
    ntu < 10f  -> "Clear water. Turbidity within WHO limit (<10 NTU)."
    ntu < 50f  -> "Moderate cloudiness. Consider filtration check."
    else       -> "High turbidity (>50 NTU). Filtration system may be failing."
}

// ─────────────────────────────────────────────────────────────────────────────
// Water Level Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WaterLevelCard(level: Float) {
    val statusColor = when {
        level < 70f -> AppColors.NeonGreen
        level < 85f -> AppColors.NeonYellow
        else        -> AppColors.NeonRed
    }
    val statusLabel = when {
        level < 70f -> "Normal"
        level < 85f -> "High"
        else        -> "Critical"
    }

    GlassCard(
        modifier    = Modifier.fillMaxWidth(),
        accentColor = statusColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SensorIconBox(Icons.Filled.Water, statusColor)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text  = "WATER LEVEL",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextSecondary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text  = "Tank Capacity",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary
                        )
                    }
                }
                NeonBadge(statusLabel, statusColor)
            }

            // Big percentage
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text  = "%.0f".format(level),
                    style = MaterialTheme.typography.displayLarge,
                    color = statusColor,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text     = " %",
                    style    = MaterialTheme.typography.headlineMedium,
                    color    = statusColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Animated progress bar
            AnimatedProgressBar(progress = level / 100f, color = statusColor)

            // Threshold markers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%",   style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
                Text("Safe ≤70%", style = MaterialTheme.typography.bodySmall, color = AppColors.NeonGreen.copy(alpha = 0.7f))
                Text("100%", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun AnimatedProgressBar(progress: Float, color: Color) {
    val animatedProgress by animateFloatAsState(
        targetValue  = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label        = "progressAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(AppColors.Divider)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(7.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0.6f), color)
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Generic sensor card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SensorCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    value: String,
    unit: String,
    statusLabel: String,
    statusColor: Color,
    detail: String
) {
    GlassCard(
        modifier    = Modifier.fillMaxWidth(),
        accentColor = accentColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SensorIconBox(icon, accentColor)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text  = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextSecondary,
                        letterSpacing = 1.5.sp
                    )
                }
                NeonBadge(statusLabel, statusColor)
            }

            // Value row
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text  = value,
                    style = MaterialTheme.typography.displayLarge,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text     = unit,
                    style    = MaterialTheme.typography.headlineMedium,
                    color    = accentColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppColors.Divider)
            )

            // Detail text
            Text(
                text  = detail,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// System info row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SystemInfoRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoChip(
            modifier = Modifier.weight(1f),
            icon     = Icons.Filled.Sensors,
            label    = "Sensors",
            value    = "3 Active",
            color    = AppColors.NeonCyan
        )
        InfoChip(
            modifier = Modifier.weight(1f),
            icon     = Icons.Filled.Wifi,
            label    = "Device",
            value    = "Offline",
            color    = AppColors.TextSecondary
        )
        InfoChip(
            modifier = Modifier.weight(1f),
            icon     = Icons.Filled.BatteryFull,
            label    = "Battery",
            value    = "87%",
            color    = AppColors.NeonGreen
        )
    }
}

@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    GlassCard(modifier = modifier, accentColor = color, cornerRadius = 12.dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(text = value, style = MaterialTheme.typography.labelLarge, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppColors.TextSecondary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared icon box
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SensorIconBox(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}
