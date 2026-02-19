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

/**
 * Creates an animated backdrop with subtle tonal shifts for liquid glass effect.
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
        val mix = (animatedOffset / 360f).coerceIn(0f, 1f)
        val base = Color(
            red = primaryColor.red + (secondaryColor.red - primaryColor.red) * mix,
            green = primaryColor.green + (secondaryColor.green - primaryColor.green) * mix,
            blue = primaryColor.blue + (secondaryColor.blue - primaryColor.blue) * mix,
            alpha = 1f
        )
        drawRect(base)
        drawRect(accentColor.copy(alpha = 0.08f + 0.04f * mix))
    }
}

/**
 * Creates a static backdrop for glass effect.
 */
@Composable
fun rememberGlassBackdrop(
    baseColor: Color = Color(0xFF1A1A2E),
    highlightColor: Color = Color(0xFF16213E),
    accentColor: Color = Color(0xFF0F3460)
): Backdrop {
    return rememberCanvasBackdrop {
        drawRect(baseColor)
        drawRect(highlightColor.copy(alpha = 0.2f))
        drawRect(accentColor.copy(alpha = 0.08f))
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
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
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
            )
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
            .drawBackdrop(
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    blur(blurRadius)
                    vibrancy()
                    lens(
                        refractionHeight = lensRefractionHeight,
                        refractionAmount = lensRefractionAmount,
                        depthEffect = true,
                        chromaticAberration = true
                    )
                }
            )
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
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(blurRadius)
                    vibrancy()
                    lens(
                        refractionHeight = lensRefractionHeight,
                        refractionAmount = lensRefractionAmount,
                        depthEffect = true,
                        chromaticAberration = true
                    )
                }
            )
            .background(surfaceColor)
            .border(1.dp, borderColor, shape),
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
    borderColor: Color = Color.White.copy(alpha = 0.3f),
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
            .border(borderWidth, borderColor, shape)
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
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    content: @Composable BoxScope.() -> Unit
) {
    ClearGlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        backgroundColor = backgroundColor,
        borderColor = borderColor,
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
                color = tintColor.copy(alpha = borderAlpha),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        content = content
    )
}
