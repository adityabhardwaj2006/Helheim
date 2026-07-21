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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aditya.expensetracker.domain.model.ChartType
import com.aditya.expensetracker.domain.model.Transaction
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartCalculator
import com.aditya.expensetracker.presentation.Screens.utils.chart.ChartUtils
import java.time.LocalDate
import java.util.Locale

@Composable
fun StatisticsChart(
    transactions: List<Transaction>,
    chartType: ChartType,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Black,
    gradientColor: Color = Color.LightGray,
    selectedColor: Color = Color.Black,
    gridColor: Color = Color.LightGray.copy(alpha = 0.5f),
    labelColor: Color = Color.Gray,
    backgroundColor: Color = Color.Transparent,
    strokeWidth: Dp = 4.dp,
    pointRadius: Dp = 4.dp
) {
    val isPreview = LocalInspectionMode.current
    val chartPoints = remember(transactions, chartType) {
        ChartCalculator.calculateMonthlyTotals(transactions, chartType)
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

    LaunchedEffect(chartPoints) {
        if (!isPreview) {
            animationProgress.animateTo(1f, animationSpec = tween(1000))
        }
    }

    val maxValue = remember(chartPoints) {
        val max = chartPoints.maxOf { it.value }
        if (max == 0f) 100f else max * 1.2f
    }

    BoxWithConstraints(
        modifier = modifier
            .background(backgroundColor)
            .pointerInput(chartPoints) {
                detectTapGestures { offset ->
                    val width = size.width.toFloat()
                    val stepX = width / (chartPoints.size - 1)
                    val index = (offset.x / stepX).toInt().coerceIn(0, chartPoints.size - 1)
                    selectedIndex = index
                }
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val labelAreaHeight = with(density) { 40.dp.toPx() }
        val chartHeight = height - labelAreaHeight
        val stepX = width / (chartPoints.size - 1)

        val points = chartPoints.mapIndexed { index, point ->
            Offset(
                x = index * stepX,
                y = chartHeight - (point.value / maxValue) * chartHeight
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

        // Month Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val itemWidth = with(density) { (width / chartPoints.size).toDp() }
            chartPoints.forEachIndexed { index, point ->
                Text(
                    text = point.month,
                    fontSize = 10.sp,
                    color = if (index == selectedIndex) selectedColor else labelColor,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.width(itemWidth),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Tooltip
        if (selectedIndex != -1) {
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
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = dataPoint.month, fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.2f", dataPoint.value)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun StatisticsChartPreview() {
    val sampleTransactions = listOf(
        Transaction("1", 5000.0, LocalDate.of(2026, 1, 15), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("2", 3000.0, LocalDate.of(2026, 2, 10), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("3", 7000.0, LocalDate.of(2026, 3, 5), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("4", 4000.0, LocalDate.of(2026, 4, 20), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("5", 9000.0, LocalDate.of(2026, 5, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("6", 6000.0, LocalDate.of(2026, 6, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("7", 8000.0, LocalDate.of(2026, 7, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("8", 5000.0, LocalDate.of(2026, 8, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("9", 7000.0, LocalDate.of(2026, 9, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("10", 4000.0, LocalDate.of(2026, 10, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("11", 9000.0, LocalDate.of(2026, 11, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME),
        Transaction("12", 6000.0, LocalDate.of(2026, 12, 12), com.aditya.expensetracker.domain.model.TransactionType.INCOME)
    )
    
    StatisticsChart(
        transactions = sampleTransactions,
        chartType = ChartType.INCOME,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}
