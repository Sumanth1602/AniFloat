package com.kotla.anifloat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A clear glass surface for overlays. 
 * Uses minimal background to let the system blur (FLAG_BLUR_BEHIND) show through.
 * Adds subtle glass-like borders and highlights.
 */
@Composable
fun ClearGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.Black.copy(alpha = 0.15f),
    borderGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.1f)
        )
    ),
    innerHighlight: Boolean = true,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderGradient, shape)
            .then(
                if (innerHighlight) {
                    Modifier.drawBehind {
                        // Inner highlight at top
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(size.width * 0.1f, 2f),
                            end = Offset(size.width * 0.9f, 2f),
                            strokeWidth = 1f
                        )
                    }
                } else Modifier
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        content = content
    )
}

/**
 * A circular clear glass button.
 */
@Composable
fun ClearGlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = Color.Black.copy(alpha = 0.2f),
    borderColor: Color = Color.White.copy(alpha = 0.4f),
    isHighlighted: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val actualBgColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.3f)
    } else {
        backgroundColor
    }
    
    val actualBorderColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.7f)
    } else {
        borderColor
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(actualBgColor)
            .border(1.dp, actualBorderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * A clear glass card for overlay content.
 */
@Composable
fun ClearGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = Color.Black.copy(alpha = 0.2f),
    borderGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.15f)
        )
    ),
    content: @Composable BoxScope.() -> Unit
) {
    ClearGlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        backgroundColor = backgroundColor,
        borderGradient = borderGradient,
        innerHighlight = true,
        content = content
    )
}

/**
 * Creates a frosted glass effect with a subtle tint.
 * For use when you want some color but still see-through.
 */
@Composable
fun FrostedGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    tintColor: Color = Color.White,
    tintAlpha: Float = 0.1f,
    borderAlpha: Float = 0.3f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(tintColor.copy(alpha = tintAlpha))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tintColor.copy(alpha = borderAlpha),
                        tintColor.copy(alpha = borderAlpha * 0.3f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        content = content
    )
}
