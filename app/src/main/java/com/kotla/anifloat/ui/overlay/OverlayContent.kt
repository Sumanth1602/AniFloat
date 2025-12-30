package com.kotla.anifloat.ui.overlay

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kotla.anifloat.ui.components.LiquidGlassCard
import com.kotla.anifloat.ui.components.LiquidGlassCircleButton
import com.kotla.anifloat.ui.components.rememberAmbientBackdrop

@Composable
fun OverlayContent(
    title: String,
    currentProgress: Int,
    totalEpisodes: Int,
    coverImage: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    isCollapsed: Boolean,
    onExpand: () -> Unit,
    isBlurSupported: Boolean = true,
    showAddSequelButton: Boolean = false,
    onAddSequel: () -> Unit = {}
) {
    // Create ambient backdrop for liquid glass effect
    val backdrop = rememberAmbientBackdrop()
    
    if (isCollapsed) {
        CollapsedOverlay(
            onExpand = onExpand,
            backdrop = backdrop
        )
    } else {
        ExpandedOverlay(
            title = title,
            progress = currentProgress,
            total = totalEpisodes,
            coverImage = coverImage,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
            onMinimize = onMinimize,
            onClose = onClose,
            isBlurSupported = isBlurSupported,
            showAddSequelButton = showAddSequelButton,
            onAddSequel = onAddSequel,
            backdrop = backdrop
        )
    }
}

@Composable
fun CollapsedOverlay(
    onExpand: () -> Unit,
    backdrop: Backdrop
) {
    LiquidGlassCircleButton(
        onClick = onExpand,
        backdrop = backdrop,
        modifier = Modifier.size(44.dp),
        size = 44.dp,
        blurRadius = 20.dp,
        tint = Color(0xFF6366F1),
        surfaceColor = Color.White.copy(alpha = 0.12f),
        borderColor = Color.White.copy(alpha = 0.3f)
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Expand",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedOverlay(
    title: String,
    progress: Int,
    total: Int,
    coverImage: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    isBlurSupported: Boolean,
    showAddSequelButton: Boolean,
    onAddSequel: () -> Unit,
    backdrop: Backdrop
) {
    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.4f),
            Color.White.copy(alpha = 0.1f)
        )
    )

    LiquidGlassCard(
        modifier = Modifier
            .width(220.dp)
            .wrapContentHeight(),
        backdrop = backdrop,
        cornerRadius = 20.dp,
        blurRadius = 28.dp,
        surfaceColor = Color.White.copy(alpha = 0.08f),
        borderGradient = glassBorder
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Minimize Icon + "Tracker"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Liquid glass minimize button
                LiquidGlassCircleButton(
                    onClick = onMinimize,
                    backdrop = backdrop,
                    modifier = Modifier.size(24.dp),
                    size = 24.dp,
                    blurRadius = 12.dp,
                    surfaceColor = Color.White.copy(alpha = 0.15f),
                    borderColor = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tracker",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Content Row: Poster + (Title/Progress/Controls)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Poster with subtle glass border
                if (coverImage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        AsyncImage(
                            model = coverImage,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                // Title + Controls Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Marquee Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee(
                                animationMode = MarqueeAnimationMode.Immediately,
                                initialDelayMillis = 1000
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Progress
                    Text(
                        text = "$progress / ${if (total > 0) total else "?"} Episodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Compact Controls with liquid glass buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minus Button - Liquid Glass
                        LiquidGlassCircleButton(
                            onClick = onDecrement,
                            backdrop = backdrop,
                            modifier = Modifier.size(32.dp),
                            size = 32.dp,
                            blurRadius = 12.dp,
                            surfaceColor = Color.White.copy(alpha = 0.12f),
                            borderColor = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                "-",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))

                        // Plus Button - Liquid Glass
                        LiquidGlassCircleButton(
                            onClick = onIncrement,
                            backdrop = backdrop,
                            modifier = Modifier.size(32.dp),
                            size = 32.dp,
                            blurRadius = 12.dp,
                            tint = Color(0xFF6366F1),
                            surfaceColor = Color.White.copy(alpha = 0.15f),
                            borderColor = Color.White.copy(alpha = 0.3f)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increment",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        if (showAddSequelButton) {
                            Spacer(modifier = Modifier.width(10.dp))
                            // Sequel button with liquid glass styling
                            LiquidGlassAddSequelButton(
                                onClick = onAddSequel,
                                backdrop = backdrop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiquidGlassAddSequelButton(
    onClick: () -> Unit,
    backdrop: Backdrop
) {
    val shape = RoundedCornerShape(50)
    val shapeProvider = remember { { shape } }
    
    Box(
        modifier = Modifier
            .height(26.dp)
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = shapeProvider,
                        effects = {
                            vibrancy()
                            blur(10f * density)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                lens(
                                    refractionHeight = 4f * density,
                                    refractionAmount = 8f * density
                                )
                            }
                        },
                        onDrawSurface = {
                            drawRect(Color(0xFF2AF598).copy(alpha = 0.4f))
                        }
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2AF598),
                                    Color(0xFF009EFD)
                                )
                            )
                        )
                }
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2AF598).copy(alpha = 0.8f),
                        Color(0xFF009EFD).copy(alpha = 0.6f)
                    )
                ),
                shape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Add",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
