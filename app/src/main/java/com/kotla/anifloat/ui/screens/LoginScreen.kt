package com.kotla.anifloat.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.kotla.anifloat.ui.components.LiquidGlassSurface
import com.kotla.anifloat.ui.components.rememberAmbientBackdrop
import com.kotla.anifloat.ui.theme.DarkBackground
import com.kotla.anifloat.ui.theme.DarkSurface
import com.kotla.anifloat.ui.theme.PrimaryAccent
import com.kotla.anifloat.ui.theme.PrimaryGradientEnd
import com.kotla.anifloat.ui.theme.PrimaryGradientStart
import com.kotla.anifloat.ui.theme.TextPrimary
import com.kotla.anifloat.ui.theme.TextSecondary
import com.kotla.anifloat.util.Constants

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val backdrop = rememberAmbientBackdrop(
        baseColor = DarkBackground,
        highlightColor = DarkSurface
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient glow in background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo / Title with subtle glow
            Text(
                text = "AniFloat",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            
            Text(
                text = "Track your anime anywhere.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Liquid Glass Login Button
            LiquidGlassSurface(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth(0.75f),
                backdrop = backdrop,
                shape = RoundedCornerShape(27.dp),
                blurRadius = 24.dp,
                lensRefractionHeight = 10.dp,
                lensRefractionAmount = 18.dp,
                tint = PrimaryAccent,
                surfaceColor = PrimaryAccent.copy(alpha = 0.25f),
                borderColor = PrimaryAccent.copy(alpha = 0.5f),
                borderWidth = 1.5.dp,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Constants.ANILIST_AUTH_URL.toUri())
                    context.startActivity(intent)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login with AniList",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
