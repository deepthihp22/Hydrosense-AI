package com.iot.wastewatermonitor.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.iot.wastewatermonitor.data.HistoryEntry
import com.iot.wastewatermonitor.ui.components.GlassCard
import com.iot.wastewatermonitor.ui.theme.AppColors
import com.iot.wastewatermonitor.viewmodel.SensorViewModel
import com.iot.wastewatermonitor.viewmodel.TimeRange

@Composable
fun HistoryScreen(viewModel: SensorViewModel) {
    val state by viewModel.historyState.collectAsState()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text  = "HISTORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.NeonCyan,
                    letterSpacing = 3.sp
                )
                Text(
                    text  = "Sensor Trends",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            // Time range toggle
            TimeRangeToggle(
                selected = state.selectedRange,
                onSelect = { viewModel.selectTimeRange(it) }
            )
        }

        // Legend
        ChartLegend()

        // pH chart
        ChartCard(
            title   = "pH Level",
            entries = state.entries,
            color   = AppColors.NeonCyan,
            range   = state.selectedRange,
            extract = { it.ph }
        )

        // Turbidity chart
        ChartCard(
            title   = "Turbidity (NTU)",
            entries = state.entries,
            color   = AppColors.NeonYellow,
            range   = state.selectedRange,
            extract = { it.turbidity }
        )

        // Water level chart
        ChartCard(
            title   = "Water Level (%)",
            entries = state.entries,
            color   = AppColors.NeonGreen,
            range   = state.selectedRange,
            extract = { it.waterLevel }
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TimeRangeToggle(selected: TimeRange, onSelect: (TimeRange) -> Unit) {
    Row(
        modifier = Modifier
            .background(AppColors.Surface, RoundedCornerShape(10.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeRange.entries.forEach { range ->
            val isSelected = range == selected
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) AppColors.NeonCyan.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .then(
                        if (isSelected) Modifier else Modifier
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick          = { onSelect(range) },
                    contentPadding   = PaddingValues(0.dp),
                    modifier         = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        text  = range.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) AppColors.NeonCyan else AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem("pH",          AppColors.NeonCyan)
        LegendItem("Turbidity",   AppColors.NeonYellow)
        LegendItem("Water Level", AppColors.NeonGreen)
    }
}

@Composable
private fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(5.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
    }
}

@Composable
private fun ChartCard(
    title: String,
    entries: List<HistoryEntry>,
    color: androidx.compose.ui.graphics.Color,
    range: TimeRange,
    extract: (HistoryEntry) -> Float
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = color) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SensorIconBox(Icons.Filled.ShowChart, color)
                Spacer(Modifier.width(10.dp))
                Text(
                    text  = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                factory  = { ctx ->
                    LineChart(ctx).apply {
                        description.isEnabled = false
                        legend.isEnabled      = false
                        setTouchEnabled(true)
                        isDragEnabled         = true
                        setScaleEnabled(false)
                        setDrawGridBackground(false)
                        setBackgroundColor(AndroidColor.TRANSPARENT)

                        xAxis.apply {
                            position        = XAxis.XAxisPosition.BOTTOM
                            textColor       = AppColors.TextSecondary.toArgb()
                            gridColor       = AppColors.Divider.toArgb()
                            axisLineColor   = AppColors.Divider.toArgb()
                            setDrawGridLines(true)
                            granularity     = 1f
                        }
                        axisLeft.apply {
                            textColor     = AppColors.TextSecondary.toArgb()
                            gridColor     = AppColors.Divider.toArgb()
                            axisLineColor = AppColors.Divider.toArgb()
                        }
                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    val dataPoints = entries.mapIndexed { idx, entry ->
                        Entry(idx.toFloat(), extract(entry))
                    }

                    val xLabels = if (range == TimeRange.TODAY)
                        entries.map { "${it.hour}:00" }
                    else
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                    chart.xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val idx = value.toInt()
                            return if (idx in xLabels.indices) xLabels[idx] else ""
                        }
                    }

                    val lineColor = color.toArgb()
                    val fillColor = color.copy(alpha = 0.15f).toArgb()

                    val dataSet = LineDataSet(dataPoints, title).apply {
                        this.color          = lineColor
                        valueTextColor      = AndroidColor.TRANSPARENT
                        lineWidth           = 2.5f
                        circleRadius        = 3f
                        setCircleColor(lineColor)
                        setDrawCircleHole(false)
                        setDrawFilled(true)
                        this.fillColor      = fillColor
                        fillAlpha           = 180
                        mode                = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity      = 0.2f
                        setDrawValues(false)
                    }

                    chart.data = LineData(dataSet)
                    chart.invalidate()
                }
            )
        }
    }
}
