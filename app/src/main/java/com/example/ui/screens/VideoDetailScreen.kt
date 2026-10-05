package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    viewModel: StudioViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val video = uiState.selectedVideo
    var isExpandedDesc by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video SEO & Tag Inspector", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (video == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No video selected. Please enter a URL on the home screen.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Video Header (Thumbnail & Info)
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = video.thumbnailUri,
                                contentDescription = video.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(video.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(video.channelTitle, fontWeight = FontWeight.SemiBold, color = YouTubeRed)
                                    Text(video.publishedAt, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${video.viewCount} views", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${video.likeCount} likes", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Quick Action Grid (Mandatory One-Click Copy / Download)
                item {
                    Text("Quick Action Grid", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.ContentCopy,
                            label = "Copy Title"
                        ) {
                            viewModel.copyToClipboard(context, video.title, "Video Title")
                        }
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Description,
                            label = "Copy Desc"
                        ) {
                            viewModel.copyToClipboard(context, video.description, "Video Description")
                        }
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Download,
                            label = "Thumbnail"
                        ) {
                            viewModel.copyToClipboard(context, video.thumbnailUri, "Thumbnail URL")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalOffer,
                            label = "Copy Tags"
                        ) {
                            viewModel.copyAllTags(context, video.tags)
                        }
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Tag,
                            label = "Copy Hashtag"
                        ) {
                            viewModel.copyAllTags(context, video.hashtags)
                        }
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Bookmark,
                            label = "Save Item"
                        ) {
                            viewModel.saveItem("VIDEO", video.title, video.tags.joinToString(", "), context)
                        }
                    }
                }

                // 3. Description Box
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Video Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = video.description,
                                fontSize = 13.sp,
                                maxLines = if (isExpandedDesc) Int.MAX_VALUE else 4,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = { isExpandedDesc = !isExpandedDesc }) {
                                Text(if (isExpandedDesc) "Read Less" else "Read More...", color = YouTubeRed)
                            }
                        }
                    }
                }

                // 4. Video Tags Section with Single-Tap Copy & Batch Copy
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Extracted SEO Tags (${video.tags.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.copyAllTags(context, video.tags) }) {
                                Text("Copy All", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.copySelectedTags(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                            ) {
                                Text("Copy Selected", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    // Display Tags as Chips
                    FlowRowWithChips(
                        tags = video.tags,
                        selectedTags = uiState.selectedChipTags,
                        onTagClick = { tag ->
                            viewModel.toggleTagSelection(tag)
                            viewModel.copyToClipboard(context, tag, "Tag")
                        }
                    )
                }

                // 5. Hashtags Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Viral Hashtags", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRowWithChips(
                        tags = video.hashtags,
                        selectedTags = emptySet(),
                        onTagClick = { tag ->
                            viewModel.copyToClipboard(context, tag, "Hashtag")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}

@Composable
fun FlowRowWithChips(
    tags: List<String>,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit
) {
    // Simple custom layout or Column/Row for chips
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.chunked(3).forEach { rowTags ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTags.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTagClick(tag) },
                        label = { Text(tag, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowTags.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
