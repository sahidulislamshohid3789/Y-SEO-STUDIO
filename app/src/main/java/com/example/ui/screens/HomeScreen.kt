package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.StudioViewModel

data class ToolItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    onNavigateToTool: (String) -> Unit,
    onNavigateToSaved: () -> Unit,
    onOpenWithdrawDialog: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var urlInput by remember { mutableStateOf("") }
    var showDrawer by remember { mutableStateOf(false) }

    val allTools = listOf(
        ToolItem("video_tags", "Video Tags Extractor", "Extract high-ranking SEO tags from any video", Icons.Default.Tag, true),
        ToolItem("keyword_suggestion", "Keyword Suggestion", "Discover high-volume YouTube search keywords", Icons.Default.Key, true),
        ToolItem("ai_hashtag", "AI Hashtag Generator", "Generate viral hashtags instantly with AI", Icons.Default.LocalOffer, true),
        ToolItem("ai_tags", "AI Tags Generator", "AI-powered tag optimization for ranking", Icons.Default.AutoAwesome),
        ToolItem("ai_channel_name", "AI Channel Name Ideas", "Brainstorm catchy, brandable channel names", Icons.Default.SmartToy),
        ToolItem("ai_video_title", "AI Video Title Generator", "Craft high-CTR viral titles using AI", Icons.Default.Title),
        ToolItem("popular_hashtags", "Popular Hashtags", "Explore trending hashtags in your niche", Icons.Default.TrendingUp),
        ToolItem("explore_channel", "Explore Channel", "Analyze channel statistics and growth rate", Icons.Default.Analytics),
        ToolItem("trending_videos", "Trending Videos", "Browse viral videos across top categories", Icons.Default.Whatshot),
        ToolItem("find_competitor", "Find Competitor", "Analyze competing channels & top videos", Icons.Default.Group),
        ToolItem("earning_calculator", "Earning Calculator", "Estimate YouTube AdSense revenue & RPM", Icons.Default.AttachMoney),
        ToolItem("thumbnail_downloader", "Thumbnail Downloader", "Download HD video thumbnails instantly", Icons.Default.Download)
    )

    ModalNavigationDrawer(
        drawerState = rememberDrawerState(initialValue = if (showDrawer) DrawerValue.Open else DrawerValue.Closed),
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Y-SEO Studio Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Saved Items & Tags") },
                    selected = false,
                    onClick = {
                        showDrawer = false
                        onNavigateToSaved()
                    },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Earning Calculator") },
                    selected = false,
                    onClick = {
                        showDrawer = false
                        onNavigateToTool("earning_calculator")
                    },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Trending Videos") },
                    selected = false,
                    onClick = {
                        showDrawer = false
                        onNavigateToTool("trending_videos")
                    },
                    icon = { Icon(Icons.Default.Whatshot, contentDescription = null) }
                )
            }
        },
        gesturesEnabled = showDrawer
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(YouTubeRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Y-SEO Studio", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSaved) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Saved Items", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Universal Search / URL Bar
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("YouTube URL & SEO Analyzer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Paste video or channel URL to extract tags, metadata & analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = urlInput,
                                    onValueChange = { urlInput = it },
                                    placeholder = { Text("Enter video/channel URL or keywords...") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.analyzeUrlOrQuery(urlInput, context)
                                    },
                                    modifier = Modifier.height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                    } else {
                                        Text("Go", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Earning / Balance Banner
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF2C2C3E), Color(0xFF1E1E2C))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ESTIMATED EARNINGS", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                    Text("$${uiState.creatorStats.estimatedEarnings}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Creator Credits: ${uiState.creatorStats.totalCredits} pts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = onOpenWithdrawDialog,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                ) {
                                    Text("Withdraw", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 3. Popular Tools Section
                item {
                    Text("Popular Creator Tools", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        allTools.filter { it.isPopular }.forEach { tool ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToTool(tool.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(YouTubeRed.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(tool.icon, contentDescription = null, tint = YouTubeRed)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(tool.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                // 4. All Tools Grid
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("All Studio Tools", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(items = allTools.chunked(2)) { rowTools ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTools.forEach { tool ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToTool(tool.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(YouTubeRed.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(tool.icon, contentDescription = null, tint = YouTubeRed)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(tool.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(tool.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    }
                                }
                            }
                        }
                        if (rowTools.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
