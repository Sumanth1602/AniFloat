package com.kotla.anifloat.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creates an animated backdrop with moving gradient for liquid glass effect.
 * The animation creates subtle movement that enhances the glass refraction illusion.
 */
@Composable
fun rememberAnimatedGlassBackdrop(
    primaryColor: Color = Color(0xFF1E3A5F),
    secondaryColor: Color = Color(0xFF2D5A7B),
    accentColor: Color = Color(0xFF4A90D9)
): Backdrop {
    val infiniteTransition = rememberInfiniteTransition(label = "backdrop")
    
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )
    
    return rememberCanvasBackdrop {
        val angleRad = Math.toRadians(animatedOffset.toDouble())
        val centerX = size.width * (0.5f + 0.3f * cos(angleRad).toFloat())
        val centerY = size.height * (0.5f + 0.3f * sin(angleRad).toFloat())
        
        // Draw animated gradient backdrop
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(accentColor, secondaryColor, primaryColor),
                center = Offset(centerX, centerY),
                radius = size.maxDimension * 1.2f
            )
        )
        
        // Add secondary light source
        val angle2 = angleRad + Math.PI
        val center2X = size.width * (0.5f + 0.25f * cos(angle2).toFloat())
        val center2Y = size.height * (0.5f + 0.25f * sin(angle2).toFloat())
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(center2X, center2Y),
                radius = size.minDimension * 0.6f
            ),
            center = Offset(center2X, center2Y),
            radius = size.minDimension * 0.6f
        )
    }
}

/**
 * Creates a static gradient backdrop for glass effect.
 */
@Composable
fun rememberGlassBackdrop(
    baseColor: Color = Color(0xFF1A1A2E),
    highlightColor: Color = Color(0xFF16213E),
    accentColor: Color = Color(0xFF0F3460)
): Backdrop {
    return rememberCanvasBackdrop {
        // Multi-point gradient for depth
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(highlightColor, baseColor, accentColor),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
        
        // Add highlight spot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.3f, size.height * 0.3f),
                radius = size.minDimension * 0.5f
            ),
            center = Offset(size.width * 0.3f, size.height * 0.3f),
            radius = size.minDimension * 0.5f
        )
    }
}

/**
 * Liquid glass surface with blur, vibrancy, and lens refraction effects.
 */
@Composable
fun LiquidGlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    blurRadius: Float = 40f,
    lensRefractionHeight: Float = 8f,
    lensRefractionAmount: Float = 24f,
    surfaceColor: Color = Color.White.copy(alpha = 0.08f),
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .drawBackdrop(backdrop, { shape }) {
                // Apply blur effect
                blur(blurRadius)
                // Apply vibrancy (color overlay)
                vibrancy()
                // Apply lens refraction at edges
                lens(
                    refractionHeight = lensRefractionHeight,
                    refractionAmount = lensRefractionAmount,
                    depthEffect = true,
                    chromaticAberration = true
                )
            }
            .background(surfaceColor)
            .border(borderWidth, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        content = content
    )
}

/**
 * Circular liquid glass button with effects.
 */
@Composable
fun LiquidGlassCircleButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    blurRadius: Float = 32f,
    lensRefractionHeight: Float = 4f,
    lensRefractionAmount: Float = 12f,
    surfaceColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.4f),
    isHighlighted: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val actualSurfaceColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.25f)
    } else {
        surfaceColor
    }
    
    val actualBorderColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.6f)
    } else {
        borderColor
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .drawBackdrop(backdrop, { CircleShape }) {
                blur(blurRadius)
                vibrancy()
                lens(
                    refractionHeight = lensRefractionHeight,
                    refractionAmount = lensRefractionAmount,
                    depthEffect = true,
                    chromaticAberration = true
                )
            }
            .background(actualSurfaceColor)
            .border(1.dp, actualBorderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Liquid glass card for content containers.
 */
@Composable
fun LiquidGlassCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    blurRadius: Float = 48f,
    lensRefractionHeight: Float = 6f,
    lensRefractionAmount: Float = 20f,
    surfaceColor: Color = Color.White.copy(alpha = 0.06f),
    borderGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.4f),
            Color.White.copy(alpha = 0.1f)
        )
    ),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .drawBackdrop(backdrop, { shape }) {
                blur(blurRadius)
                vibrancy()
                lens(
                    refractionHeight = lensRefractionHeight,
                    refractionAmount = lensRefractionAmount,
                    depthEffect = true,
                    chromaticAberration = true
                )
            }
            .background(surfaceColor)
            .border(1.dp, borderGradient, shape),
        content = content
    )
}

// Keep simple versions for non-backdrop usage (dialogs in main app)

/**
 * Simple clear glass surface without backdrop (for main app UI).
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
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .border(borderWidth, borderGradient, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        content = content
    )
}

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
        content = content
    )
}

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
    val actualBgColor = if (isHighlighted) Color.Red.copy(alpha = 0.3f) else backgroundColor
    val actualBorderColor = if (isHighlighted) Color.Red.copy(alpha = 0.7f) else borderColor
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .drawBehind { drawRect(actualBgColor) }
            .border(1.dp, actualBorderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

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
            .drawBehind { drawRect(tintColor.copy(alpha = tintAlpha)) }
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
