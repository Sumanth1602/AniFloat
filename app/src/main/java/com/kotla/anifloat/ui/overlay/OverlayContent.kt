package com.kotla.anifloat.ui.overlay

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kotla.anifloat.ui.components.LiquidGlassCard
import com.kotla.anifloat.ui.components.LiquidGlassCircleButton
import com.kotla.anifloat.ui.components.LiquidGlassSurface
import com.kotla.anifloat.ui.components.rememberAnimatedGlassBackdrop

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
    // Create animated backdrop for liquid glass effect
    val backdrop = rememberAnimatedGlassBackdrop(
        primaryColor = Color(0xFF0D1B2A),
        secondaryColor = Color(0xFF1B263B),
        accentColor = Color(0xFF415A77)
    )
    
    if (isCollapsed) {
        CollapsedOverlay(onExpand = onExpand, backdrop = backdrop)
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
    backdrop: com.kyant.backdrop.Backdrop
) {
    LiquidGlassCircleButton(
        onClick = onExpand,
        backdrop = backdrop,
        modifier = Modifier.size(48.dp),
        size = 48.dp,
        blurRadius = 40f,
        lensRefractionHeight = 6f,
        lensRefractionAmount = 16f,
        surfaceColor = Color.White.copy(alpha = 0.08f),
        borderColor = Color.White.copy(alpha = 0.5f)
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Expand",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
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
    backdrop: com.kyant.backdrop.Backdrop
) {
    // Liquid glass card with blur, vibrancy, and lens effects
    LiquidGlassCard(
        backdrop = backdrop,
        modifier = Modifier
            .width(220.dp)
            .wrapContentHeight(),
        cornerRadius = 24.dp,
        blurRadius = 56f,
        lensRefractionHeight = 10f,
        lensRefractionAmount = 28f,
        surfaceColor = Color.White.copy(alpha = 0.05f),
        borderGradient = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                Color.White.copy(alpha = 0.15f)
            )
        )
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
                // Minimize button with glass effect
                LiquidGlassCircleButton(
                    onClick = onMinimize,
                    backdrop = backdrop,
                    modifier = Modifier.size(26.dp),
                    size = 26.dp,
                    blurRadius = 24f,
                    lensRefractionHeight = 3f,
                    lensRefractionAmount = 8f,
                    surfaceColor = Color.White.copy(alpha = 0.08f),
                    borderColor = Color.White.copy(alpha = 0.35f)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tracker",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f),
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
                // Poster with glass border
                if (coverImage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
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
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minus Button
                        LiquidGlassCircleButton(
                            onClick = onDecrement,
                            backdrop = backdrop,
                            modifier = Modifier.size(34.dp),
                            size = 34.dp,
                            blurRadius = 28f,
                            lensRefractionHeight = 4f,
                            lensRefractionAmount = 10f,
                            surfaceColor = Color.White.copy(alpha = 0.08f),
                            borderColor = Color.White.copy(alpha = 0.35f)
                        ) {
                            Text(
                                "-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))

                        // Plus Button
                        LiquidGlassCircleButton(
                            onClick = onIncrement,
                            backdrop = backdrop,
                            modifier = Modifier.size(34.dp),
                            size = 34.dp,
                            blurRadius = 28f,
                            lensRefractionHeight = 4f,
                            lensRefractionAmount = 10f,
                            surfaceColor = Color.White.copy(alpha = 0.12f),
                            borderColor = Color.White.copy(alpha = 0.5f)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increment",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (showAddSequelButton) {
                            Spacer(modifier = Modifier.width(10.dp))
                            // Sequel button with glass effect
                            LiquidGlassSurface(
                                backdrop = backdrop,
                                modifier = Modifier.height(28.dp),
                                shape = RoundedCornerShape(14.dp),
                                blurRadius = 24f,
                                lensRefractionHeight = 3f,
                                lensRefractionAmount = 8f,
                                surfaceColor = Color(0xFF2AF598).copy(alpha = 0.15f),
                                borderColor = Color(0xFF2AF598).copy(alpha = 0.5f),
                                onClick = onAddSequel
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
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
                        }
                    }
                }
            }
        }
    }
}
