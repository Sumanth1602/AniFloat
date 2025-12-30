package com.kotla.anifloat.ui.overlay

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

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
    
    val blurRadius by animateFloatAsState(
        targetValue = if (isOver) 56f else 40f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "blur"
    )
    
    val lensAmount by animateFloatAsState(
        targetValue = if (isOver) 24f else 16f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "lens"
    )
    
    val surfaceColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f),
        label = "surface"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.4f),
        label = "border"
    )
    
    // Create backdrop with animated colors
    val backdrop = rememberCanvasBackdrop {
        val primaryColor = if (isOver) Color(0xFF2D0A0A) else Color(0xFF0D1B2A)
        val secondaryColor = if (isOver) Color(0xFF4A1515) else Color(0xFF1B263B)
        val accentColor = if (isOver) Color(0xFF8B2020) else Color(0xFF415A77)
        
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(accentColor, secondaryColor, primaryColor),
                center = Offset(this.size.width * 0.4f, this.size.height * 0.4f),
                radius = this.size.maxDimension * 1.2f
            )
        )
        
        // Add highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(this.size.width * 0.3f, this.size.height * 0.3f),
                radius = this.size.minDimension * 0.4f
            ),
            center = Offset(this.size.width * 0.3f, this.size.height * 0.3f),
            radius = this.size.minDimension * 0.4f
        )
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .drawBackdrop(backdrop, CircleShape) {
                blur(blurRadius)
                vibrancy()
                lens(
                    refractionHeight = if (isOver) 8f else 5f,
                    refractionAmount = lensAmount,
                    depthEffect = true,
                    chromaticAberration = true
                )
            }
            .background(surfaceColor)
            .border(
                width = if (isOver) 2.dp else 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Service",
            tint = Color.White,
            modifier = Modifier.size(if (isOver) 32.dp else 28.dp)
        )
    }
}
