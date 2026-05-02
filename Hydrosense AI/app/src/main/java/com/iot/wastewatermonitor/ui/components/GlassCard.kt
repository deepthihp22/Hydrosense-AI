package com.iot.wastewatermonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iot.wastewatermonitor.ui.theme.AppColors

/**
 * Glassmorphism-style card with a subtle gradient background and neon border glow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.NeonCyan,
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassGradient = Brush.linearGradient(
        colors = listOf(
            AppColors.SurfaceVariant.copy(alpha = 0.85f),
            AppColors.Surface.copy(alpha = 0.70f)
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(glassGradient)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.55f),
                        accentColor.copy(alpha = 0.10f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp),
        content = content
    )
}
