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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.25f),
        label = "backgroundColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isOver) Color.Red.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f),
        label = "borderColor"
    )
    
    val iconColor = Color.White

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
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
            tint = iconColor,
            modifier = Modifier.size(if (isOver) 32.dp else 28.dp)
        )
    }
}
