package com.aditya.expensetracker.presentation.Screens.utils.chart

import androidx.compose.ui.geometry.Offset

object ChartUtils {
    /**
     * Calculates control points for a smooth cubic Bézier curve between two points.
     */
    fun calculateControlPoints(
        startPoint: Offset,
        endPoint: Offset,
        prevPoint: Offset?,
        nextPoint: Offset?
    ): Pair<Offset, Offset> {
        val smoothness = 0.2f
        
        val cp1 = if (prevPoint != null) {
            startPoint + (endPoint - prevPoint) * smoothness
        } else {
            startPoint + (endPoint - startPoint) * smoothness
        }
        
        val cp2 = if (nextPoint != null) {
            endPoint - (nextPoint - startPoint) * smoothness
        } else {
            endPoint - (endPoint - startPoint) * smoothness
        }
        
        return Pair(cp1, cp2)
    }
}

// Extension functions for Offset math
private operator fun Offset.minus(other: Offset) = Offset(x - other.x, y - other.y)
private operator fun Offset.plus(other: Offset) = Offset(x + other.x, y + other.y)
private operator fun Offset.times(value: Float) = Offset(x * value, y * value)
