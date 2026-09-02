
package com.aditya.expensetracker.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.data.model.ChartType
import com.aditya.expensetracker.data.model.TimeFilter
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartCalculator
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartUtils
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun StatisticsChart(
    transactions: List<TransactionItem>,
    chartType: ChartType,
    timeFilter: TimeFilter,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Black,
    gradientColor: Color = Color.LightGray,
    selectedColor: Color = Color.Black,
    gridColor: Color = Color.LightGray.copy(alpha = 0.5f),
    labelColor: Color = Color.Gray,
    backgroundColor: Color = Color.Transparent,
    strokeWidth: Dp = 4.dp,
    pointRadius: Dp = 4.dp,
    sharedMaxValue: Float? = null
) {
    val isPreview = LocalInspectionMode.current
    val chartPoints = remember(transactions, chartType, timeFilter) {
        ChartCalculator.calculateChartPoints(transactions, chartType, timeFilter, cumulative = false)
    }

    if (chartPoints.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No data available", color = labelColor)
        }
        return
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val animationProgress = remember { Animatable(if (isPreview) 1f else 0f) }
    val density = LocalDensity.current

    LaunchedEffect(transactions, chartType, timeFilter) {
        selectedIndex = -1
    }

    LaunchedEffect(chartPoints) {
        if (!isPreview) {
            animationProgress.animateTo(1f, animationSpec = tween(1000))
        }
    }

    val maxValue = remember(chartPoints, sharedMaxValue) {
        val max = sharedMaxValue ?: chartPoints.maxOf { it.value }
        if (max == 0f) 100f else max * 1.1f
    }

    BoxWithConstraints(
        modifier = modifier
            .background(backgroundColor)
            .pointerInput(chartPoints) {
                detectTapGestures { offset ->
                    if (chartPoints.size > 1) {
                        val width = size.width.toFloat()
                        val stepX = width / (chartPoints.size - 1)
                        val index = (offset.x / stepX).toInt().coerceIn(0, chartPoints.size - 1)
                        selectedIndex = index
                    } else if (chartPoints.size == 1) {
                        selectedIndex = 0
                    }
                }
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val labelAreaHeight = with(density) { 40.dp.toPx() }
        val chartHeight = height - labelAreaHeight
        val stepX = if (chartPoints.size > 1) width / (chartPoints.size - 1) else width

        val points = chartPoints.mapIndexed { index, point ->
            Offset(
                x = index * stepX,
                y = chartHeight - (if (maxValue > 0) (point.value / maxValue) * chartHeight else 0f)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Grid Lines (horizontal)
            val gridSteps = 5
            for (i in 0..gridSteps) {
                val y = chartHeight - (i.toFloat() / gridSteps) * chartHeight
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (points.isNotEmpty()) {
                val path = Path()
                path.moveTo(points[0].x, points[0].y)

                for (i in 0 until points.size - 1) {
                    val start = points[i]
                    val end = points[i + 1]
                    val prev = if (i > 0) points[i - 1] else null
                    val next = if (i < points.size - 2) points[i + 2] else null
                    
                    val (cp1, cp2) = ChartUtils.calculateControlPoints(start, end, prev, next)
                    path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, end.x, end.y)
                }

                // Draw Gradient
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, chartHeight)
                    lineTo(0f, chartHeight)
                    close()
                }
                
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientColor.copy(alpha = 0.5f), Color.Transparent),
                        startY = 0f,
                        endY = chartHeight
                    ),
                    alpha = animationProgress.value
                )

                // Draw Line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    alpha = animationProgress.value
                )

                // Draw circles on points
                points.forEachIndexed { index, point ->
                    val isSelected = index == selectedIndex
                    val radius = if (isSelected) (pointRadius * 2).toPx() else pointRadius.toPx()
                    
                    drawCircle(
                        color = if (isSelected) selectedColor else lineColor,
                        radius = radius * animationProgress.value,
                        center = point
                    )
                    
                    if (isSelected) {
                        // Vertical dashed line
                        drawLine(
                            color = selectedColor.copy(alpha = 0.5f),
                            start = Offset(point.x, point.y + radius),
                            end = Offset(point.x, chartHeight),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                }
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val itemWidth = with(density) { (width / chartPoints.size).toDp() }
            
            // For Month view, don't show all labels to avoid crowding
            val skipLabels = timeFilter == TimeFilter.MONTH
            
            chartPoints.forEachIndexed { index, point ->
                val shouldShowLabel = !skipLabels || index == 0 || index == chartPoints.size - 1 || (index + 1) % 5 == 0
                
                Box(modifier = Modifier.width(itemWidth), contentAlignment = Alignment.Center) {
                    if (shouldShowLabel) {
                        Text(
                            text = point.label,
                            fontSize = 10.sp,
                            color = if (index == selectedIndex) selectedColor else labelColor,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Tooltip
        if (selectedIndex != -1 && selectedIndex < points.size && selectedIndex < chartPoints.size) {
            val selectedPoint = points[selectedIndex]
            val dataPoint = chartPoints[selectedIndex]
            
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (selectedPoint.x).toDp() } - 50.dp,
                        y = with(density) { (selectedPoint.y).toDp() } - 70.dp
                    )
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .shadow(8.dp, RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = dataPoint.label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.2f", dataPoint.value)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
