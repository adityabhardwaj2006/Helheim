package com.aditya.expensetracker.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

/**
 * A simple, easy-to-understand shimmer effect.
 * Usage: Modifier.shimmer()
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    // Animate the shimmer from left to right
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f, // Increased for better coverage on large screens
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    // The colors for the shimmer effect - now using theme-aware colors
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    
    val shimmerColors = listOf(
        baseColor,
        highlightColor,
        baseColor,
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    return@composed this.background(brush)
}
