package com.iot.wastewatermonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iot.wastewatermonitor.ui.theme.AppColors

@Composable
fun NeonDividerLine(
    color: Color = AppColors.NeonCyan,
    width: Dp = 3.dp,
    height: Dp = 18.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(color, RoundedCornerShape(2.dp))
    )
}
