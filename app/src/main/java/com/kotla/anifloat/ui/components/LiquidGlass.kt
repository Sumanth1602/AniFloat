package com.kotla.anifloat.ui.components

import android.os.Build
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/**
 * Creates a backdrop that simulates ambient light/glass effect for overlays
 * where we don't have access to the actual screen content behind.
 */
@Composable
fun rememberAmbientBackdrop(
    baseColor: Color = Color(0xFF1A1A2E),
    highlightColor: Color = Color(0xFF16213E)
): Backdrop {
    return rememberCanvasBackdrop {
        // Draw a gradient to simulate ambient light behind the glass
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(highlightColor, baseColor),
                center = Offset(size.width * 0.3f, size.height * 0.3f),
                radius = size.maxDimension
            )
        )
    }
}

/**
 * A liquid glass surface that can be used as a container.
 * Uses blur and lens effects to create the iconic liquid glass look.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    backdrop: Backdrop,
    shape: Shape = RoundedCornerShape(20.dp),
    blurRadius: Dp = 24.dp,
    lensRefractionHeight: Dp = 8.dp,
    lensRefractionAmount: Dp = 16.dp,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.White.copy(alpha = 0.08f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shapeProvider = remember(shape) { { shape } }
    
    Box(
        modifier = modifier
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = shapeProvider,
                        effects = {
                            vibrancy()
                            blur(blurRadius.value * density)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                lens(
                                    refractionHeight = lensRefractionHeight.value * density,
                                    refractionAmount = lensRefractionAmount.value * density
                                )
                            }
                        },
                        onDrawSurface = {
                            if (tint != Color.Unspecified) {
                                drawRect(tint.copy(alpha = 0.3f))
                            }
                            drawRect(surfaceColor)
                        }
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(Color(0xFF1F1F1F).copy(alpha = 0.85f))
                }
            )
            .border(borderWidth, borderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        content = content
    )
}

/**
 * A circular liquid glass button, perfect for close/expand buttons.
 */
@Composable
fun LiquidGlassCircleButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    blurRadius: Dp = 16.dp,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    isHighlighted: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val actualSurfaceColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.4f)
    } else {
        surfaceColor
    }
    
    val actualBorderColor = if (isHighlighted) {
        Color.Red.copy(alpha = 0.6f)
    } else {
        borderColor
    }
    
    LiquidGlassSurface(
        modifier = modifier.then(Modifier.padding(0.dp)),
        backdrop = backdrop,
        shape = CircleShape,
        blurRadius = blurRadius,
        lensRefractionHeight = 6.dp,
        lensRefractionAmount = 12.dp,
        tint = tint,
        surfaceColor = actualSurfaceColor,
        borderColor = actualBorderColor,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(((size.value - 24f) / 2).dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * A liquid glass card for containing content like the anime tracker overlay.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop,
    cornerRadius: Dp = 20.dp,
    blurRadius: Dp = 32.dp,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.White.copy(alpha = 0.06f),
    borderGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.1f)
        )
    ),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val shapeProvider = remember(shape) { { shape } }
    
    Box(
        modifier = modifier
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = shapeProvider,
                        effects = {
                            vibrancy()
                            blur(blurRadius.value * density)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                lens(
                                    refractionHeight = 10f.dp.value * density,
                                    refractionAmount = 20f.dp.value * density
                                )
                            }
                        },
                        onDrawSurface = {
                            if (tint != Color.Unspecified) {
                                drawRect(tint.copy(alpha = 0.2f))
                            }
                            drawRect(surfaceColor)
                        }
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(Color(0xFF1F1F1F).copy(alpha = 0.9f))
                }
            )
            .border(1.dp, borderGradient, shape),
        content = content
    )
}
