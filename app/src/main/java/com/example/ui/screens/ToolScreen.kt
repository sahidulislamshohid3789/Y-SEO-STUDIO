package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScreen(
    viewModel: StudioViewModel,
    toolId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var inputQuery by remember { mutableStateOf("") }

    val toolTitle = when(toolId) {
        "video_tags" -> "Video Tags Extractor"
        "keyword_suggestion" -> "Keyword Suggestion"
        "ai_hashtag" -> "AI Hashtag Generator"
        "ai_tags" -> "AI Tags Generator"
        "ai_channel_name" -> "AI Channel Name Ideas"
        "ai_video_title" -> "AI Video Title Generator"
        "popular_hashtags" -> "Popular Hashtags Explorer"
        "explore_channel" -> "Explore Channel Analytics"
        "trending_videos" -> "Trending Viral Videos"
        "find_competitor" -> "Competitor Analyzer"
        "earning_calculator" -> "AdSense Earning Calculator"
        "thumbnail_downloader" -> "Thumbnail Downloader"
        else -> "Creator Studio Tool"
    }

    val placeholderText = when(toolId) {
        "earning_calculator" -> "Enter expected daily views (e.g. 50000)..."
        "thumbnail_downloader" -> "Enter YouTube video URL..."
        else -> "Enter video topic, keyword or niche..."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(toolTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(toolTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Powered by YouTube API & Gemini AI Engine", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text(placeholderText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (toolId == "earning_calculator") {
                                val views = inputQuery.toDoubleOrNull() ?: 50000.0
                                val estimated = (views / 1000.0) * 4.50
                                val result = "AdSense Revenue Estimation for $views views:\n\n• Estimated RPM: $4.50\n• Estimated Earnings: $${String.format("%.2f", estimated)}\n• Monthly Projection: $${String.format("%.2f", estimated * 30)}"
                                viewModel.runTool(toolTitle, result, context)
                            } else {
                                viewModel.runTool(toolTitle, inputQuery.ifEmpty { "Top YouTube Growth & SEO Strategies" }, context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Generate & Analyze with AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (uiState.aiResultText.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("AI Analysis Result", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = { viewModel.copyToClipboard(context, uiState.aiResultText, toolTitle) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Result", tint = YouTubeRed)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(uiState.aiResultText, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.copyToClipboard(context, uiState.aiResultText, toolTitle) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Copy Result", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.saveItem(toolId, toolTitle, uiState.aiResultText, context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save to Collection")
                            }
                        }
                    }
                }
            }
        }
    }
}
