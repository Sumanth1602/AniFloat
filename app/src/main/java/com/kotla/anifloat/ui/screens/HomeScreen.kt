package com.kotla.anifloat.ui.screens

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kotla.anifloat.data.UpdateInfo
import com.kotla.anifloat.data.model.MediaListEntry
import com.kotla.anifloat.service.FloatingOverlayService
import com.kotla.anifloat.ui.theme.BorderColor
import com.kotla.anifloat.ui.theme.CardStroke
import com.kotla.anifloat.ui.theme.DarkBackground
import com.kotla.anifloat.ui.theme.DarkSurface
import com.kotla.anifloat.ui.theme.PrimaryAccent
import com.kotla.anifloat.ui.theme.PrimaryAccentVariant
import com.kotla.anifloat.ui.theme.PrimaryGradientEnd
import com.kotla.anifloat.ui.theme.PrimaryGradientStart
import com.kotla.anifloat.ui.theme.TextPrimary
import com.kotla.anifloat.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current.applicationContext as Application))
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val context = LocalContext.current

    // Update dialog
    when (val state = updateState) {
        is UpdateUiState.Available -> {
            UpdateAvailableDialog(
                updateInfo = state.updateInfo,
                onDismiss = { viewModel.dismissUpdate() },
                onDownload = { downloadUrl ->
                    if (downloadUrl != null) {
                        viewModel.downloadUpdate(downloadUrl)
                    } else {
                        viewModel.openReleasePage(state.updateInfo.releasePageUrl)
                    }
                },
                onOpenReleasePage = { viewModel.openReleasePage(state.updateInfo.releasePageUrl) }
            )
        }
        is UpdateUiState.Downloading -> {
            DownloadingDialog(progress = state.progress)
        }
        is UpdateUiState.Error -> {
            UpdateErrorDialog(
                message = state.message,
                onDismiss = { viewModel.dismissUpdate() },
                onRetry = { viewModel.checkForUpdates() }
            )
        }
        else -> { /* No dialog */ }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("My Watching List", color = TextPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = PrimaryAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val viewer = (uiState as? HomeUiState.Success)?.viewer
                    ProfileMenu(
                        avatarUrl = viewer?.avatar?.large,
                        viewerName = viewer?.name,
                        onProfile = {
                            viewer?.id?.let { openAniListProfile(context, it) }
                        },
                        onReset = {
                            val intent = Intent(context, FloatingOverlayService::class.java)
                            context.stopService(intent)
                        },
                        onLogout = {
                            viewModel.logout(onLogout)
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(DarkBackground)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    if (!isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryAccent)
                    }
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchAnimeList() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is HomeUiState.Success -> {
                    AnimeList(
                        animeList = state.animeList,
                        onItemClick = { entry ->
                            if (Settings.canDrawOverlays(context)) {
                                val sequelNode = entry.media.relations?.edges?.firstOrNull { it.relationType == "SEQUEL" }?.node
                                val intent = Intent(context, FloatingOverlayService::class.java).apply {
                                    action = FloatingOverlayService.ACTION_START_OVERLAY
                                    putExtra("EXTRA_ENTRY_ID", entry.id)
                                    putExtra("EXTRA_TITLE", entry.media.title.userPreferred)
                                    putExtra("EXTRA_PROGRESS", entry.progress)
                                    putExtra("EXTRA_EPISODES", entry.media.episodes ?: 0)
                                    putExtra("EXTRA_COVER_IMAGE", entry.media.coverImage.medium)
                                    if (sequelNode != null) {
                                        putExtra("EXTRA_SEQUEL_ID", sequelNode.id)
                                        putExtra("EXTRA_SEQUEL_TITLE", sequelNode.title.userPreferred)
                                        putExtra("EXTRA_SEQUEL_COVER", sequelNode.coverImage.medium)
                                        putExtra("EXTRA_SEQUEL_EPISODES", sequelNode.episodes ?: 0)
                                    }
                                }
                                context.startForegroundService(intent)
                            } else {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    android.net.Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeList(animeList: List<MediaListEntry>, onItemClick: (MediaListEntry) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(animeList) { entry ->
            AnimeItem(entry, onClick = { onItemClick(entry) })
        }
    }
}

@Composable
fun AnimeItem(entry: MediaListEntry, onClick: () -> Unit) {
    val progress = entry.progress
    val total = entry.media.episodes ?: 0
    val progressFraction = if (total > 0) progress.toFloat() / total.toFloat() else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, CardStroke, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Poster
        AsyncImage(
            model = entry.media.coverImage.medium,
            contentDescription = null,
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight(),
            contentScale = ContentScale.Crop
        )

        // Info Column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title & Ep Info
            Column {
                Text(
                    text = entry.media.title.userPreferred,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$progress / ${if (total > 0) total else "?"} Episodes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryAccent,
                trackColor = Color.DarkGray,
            )
        }

        IconButton(
            onClick = onClick,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Open overlay",
                tint = TextPrimary
            )
        }
    }
}

@Composable
private fun ProfileMenu(
    avatarUrl: String?,
    viewerName: String?,
    onProfile: () -> Unit,
    onReset: () -> Unit,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        if (!avatarUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (!viewerName.isNullOrBlank()) {
                    Text(
                        text = viewerName.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                }
            }
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Profile") },
            onClick = {
                expanded = false
                onProfile()
            }
        )
        DropdownMenuItem(
            text = { Text("Reset") },
            onClick = {
                expanded = false
                onReset()
            }
        )
        DropdownMenuItem(
            text = { Text("Logout") },
            onClick = {
                expanded = false
                onLogout()
            }
        )
    }
}

private fun openAniListProfile(context: android.content.Context, userId: Int) {
    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://anilist.co/user/$userId"))
    context.startActivity(intent)
}

@Composable
private fun UpdateAvailableDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit,
    onDownload: (String?) -> Unit,
    onOpenReleasePage: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "🎉 Update Available!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Version info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "v${updateInfo.currentVersion}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                    
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryAccent
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Latest",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "v${updateInfo.latestVersion}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }
                }
                
                // Release notes (if available)
                if (!updateInfo.releaseNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "What's New",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = updateInfo.releaseNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Button(
                    onClick = { onDownload(updateInfo.downloadUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (updateInfo.downloadUrl != null) "Download & Install" else "View Release",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (updateInfo.downloadUrl != null) {
                    TextButton(
                        onClick = onOpenReleasePage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "View on GitHub",
                            color = TextSecondary
                        )
                    }
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Maybe Later",
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadingDialog(progress: Int) {
    Dialog(
        onDismissRequest = { /* Cannot dismiss while downloading */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Downloading Update",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryAccent,
                    trackColor = DarkBackground,
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAccent
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Please wait...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun UpdateErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update Failed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF5350)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
