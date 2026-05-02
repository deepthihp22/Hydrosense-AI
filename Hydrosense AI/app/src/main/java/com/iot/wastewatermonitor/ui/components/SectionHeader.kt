package com.iot.wastewatermonitor.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iot.wastewatermonitor.ui.theme.AppColors

@Composable
fun SectionHeader(
    title: String,
    accentColor: Color = AppColors.NeonCyan,
    modifier: Modifier = Modifier
) {
    Row(
        modifier  = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonDividerLine(color = accentColor, width = 3.dp)
        Spacer(Modifier.width(8.dp))
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary
        )
    }
}
