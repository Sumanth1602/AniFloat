package com.kotla.anifloat.ui.overlay

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kotla.anifloat.ui.components.ClearGlassCard
import com.kotla.anifloat.ui.components.ClearGlassCircleButton
import com.kotla.anifloat.ui.components.FrostedGlassSurface

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
    if (isCollapsed) {
        CollapsedOverlay(onExpand = onExpand)
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
            onAddSequel = onAddSequel
        )
    }
}

@Composable
fun CollapsedOverlay(onExpand: () -> Unit) {
    ClearGlassCircleButton(
        onClick = onExpand,
        modifier = Modifier.size(44.dp),
        size = 44.dp,
        backgroundColor = Color.Black.copy(alpha = 0.25f),
        borderColor = Color.White.copy(alpha = 0.5f)
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
    onAddSequel: () -> Unit
) {
    // Clear glass card - very transparent to show blur behind
    ClearGlassCard(
        modifier = Modifier
            .width(220.dp)
            .wrapContentHeight(),
        cornerRadius = 20.dp,
        backgroundColor = Color.Black.copy(alpha = if (isBlurSupported) 0.15f else 0.7f),
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
                // Minimize button
                ClearGlassCircleButton(
                    onClick = onMinimize,
                    modifier = Modifier.size(24.dp),
                    size = 24.dp,
                    backgroundColor = Color.White.copy(alpha = 0.1f),
                    borderColor = Color.White.copy(alpha = 0.3f)
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
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.3f),
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
                        ClearGlassCircleButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(32.dp),
                            size = 32.dp,
                            backgroundColor = Color.White.copy(alpha = 0.1f),
                            borderColor = Color.White.copy(alpha = 0.35f)
                        ) {
                            Text(
                                "-",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))

                        // Plus Button
                        ClearGlassCircleButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(32.dp),
                            size = 32.dp,
                            backgroundColor = Color.White.copy(alpha = 0.15f),
                            borderColor = Color.White.copy(alpha = 0.5f)
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
                            // Sequel button
                            FrostedGlassSurface(
                                modifier = Modifier.height(26.dp),
                                shape = RoundedCornerShape(13.dp),
                                tintColor = Color(0xFF2AF598),
                                tintAlpha = 0.25f,
                                borderAlpha = 0.6f,
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
