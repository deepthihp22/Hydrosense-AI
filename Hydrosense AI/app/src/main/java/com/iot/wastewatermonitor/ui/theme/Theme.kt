package com.iot.wastewatermonitor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Neon accent palette ──────────────────────────────────────────────────────
object AppColors {
    val Background      = Color(0xFF050D1A)   // deep navy-black
    val Surface         = Color(0xFF0D1B2E)   // card background base
    val SurfaceVariant  = Color(0xFF112240)   // slightly lighter surface

    val NeonCyan        = Color(0xFF00E5FF)
    val NeonGreen       = Color(0xFF00FF87)
    val NeonYellow      = Color(0xFFFFD600)
    val NeonRed         = Color(0xFFFF1744)
    val NeonOrange      = Color(0xFFFF6D00)

    val TextPrimary     = Color(0xFFE8F4FD)
    val TextSecondary   = Color(0xFF7B9BB5)
    val Divider         = Color(0xFF1A3050)

    // Status colours
    val Safe            = NeonGreen
    val Warning         = NeonYellow
    val Danger          = NeonRed
}

private val DarkColorScheme = darkColorScheme(
    primary         = AppColors.NeonCyan,
    secondary       = AppColors.NeonGreen,
    tertiary        = AppColors.NeonYellow,
    background      = AppColors.Background,
    surface         = AppColors.Surface,
    onPrimary       = Color.Black,
    onSecondary     = Color.Black,
    onBackground    = AppColors.TextPrimary,
    onSurface       = AppColors.TextPrimary,
    error           = AppColors.NeonRed
)

@Composable
fun SmartWastewaterMonitorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
