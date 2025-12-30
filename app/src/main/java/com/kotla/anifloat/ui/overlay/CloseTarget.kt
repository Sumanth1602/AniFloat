package com.kotla.anifloat.ui.overlay

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kotla.anifloat.ui.components.rememberAmbientBackdrop

@Composable
fun CloseTarget(isOver: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isOver) 1.3f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    
    val size by animateDpAsState(
        targetValue = if (isOver) 72.dp else 60.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "size"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.3f),
        label = "borderColor"
    )
    
    val surfaceColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
        label = "surfaceColor"
    )
    
    val iconColor = Color.White
    
    // Create backdrop for liquid glass effect
    val backdrop = rememberAmbientBackdrop(
        baseColor = if (isOver) Color(0xFF2D1B1B) else Color(0xFF1A1A2E),
        highlightColor = if (isOver) Color(0xFF3D2020) else Color(0xFF16213E)
    )
    
    val shape = CircleShape
    val shapeProvider = remember { { shape } }

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = shapeProvider,
                        effects = {
                            vibrancy()
                            blur(24f * density)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                lens(
                                    refractionHeight = 8f * density,
                                    refractionAmount = 16f * density
                                )
                            }
                        },
                        onDrawSurface = {
                            drawRect(surfaceColor)
                        }
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(
                            if (isOver) Color.Red.copy(alpha = 0.6f) 
                            else Color.Black.copy(alpha = 0.5f)
                        )
                }
            )
            .border(
                width = if (isOver) 2.dp else 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.5f)
                    )
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Service",
            tint = iconColor,
            modifier = Modifier.size(if (isOver) 32.dp else 28.dp)
        )
    }
}

