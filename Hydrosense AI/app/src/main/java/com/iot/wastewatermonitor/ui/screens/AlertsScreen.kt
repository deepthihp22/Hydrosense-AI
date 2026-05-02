package com.iot.wastewatermonitor.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iot.wastewatermonitor.data.AlertItem
import com.iot.wastewatermonitor.data.AlertSeverity
import com.iot.wastewatermonitor.ui.theme.AppColors
import com.iot.wastewatermonitor.viewmodel.SensorViewModel
import java.time.format.DateTimeFormatter

@Composable
fun AlertsScreen(viewModel: SensorViewModel) {
    val alerts by viewModel.alerts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text  = "ALERTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.NeonRed,
                    letterSpacing = 3.sp
                )
                Text(
                    text  = "Notifications",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (alerts.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(AppColors.NeonRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text  = "${alerts.size} Active",
                        style = MaterialTheme.typography.labelLarge,
                        color = AppColors.NeonRed
                    )
                }
            }
        }

        if (alerts.isEmpty()) {
            EmptyAlertsState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(alerts, key = { it.id }) { alert ->
                    AnimatedVisibility(
                        visible = true,
                        enter   = fadeIn() + slideInVertically()
                    ) {
                        AlertCard(alert = alert, onDismiss = { viewModel.dismissAlert(alert.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: AlertItem, onDismiss: () -> Unit) {
    val color = if (alert.severity == AlertSeverity.DANGER) AppColors.NeonRed else AppColors.NeonYellow
    val icon  = if (alert.severity == AlertSeverity.DANGER) Icons.Filled.Error else Icons.Filled.Warning
    val formatter = DateTimeFormatter.ofPattern("HH:mm · MMM dd")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.18f), AppColors.Surface.copy(alpha = 0.85f))
                )
            )
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(color, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = alert.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = alert.timestamp.format(formatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SeverityChip(alert.severity)
                    TextButton(
                        onClick      = onDismiss,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text  = "Dismiss",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityChip(severity: AlertSeverity) {
    val (label, color) = if (severity == AlertSeverity.DANGER)
        "CRITICAL" to AppColors.NeonRed
    else
        "WARNING" to AppColors.NeonYellow

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun EmptyAlertsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AppColors.NeonGreen,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text  = "All Clear",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.NeonGreen
            )
            Text(
                text  = "No active alerts. System operating normally.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )
        }
    }
}
