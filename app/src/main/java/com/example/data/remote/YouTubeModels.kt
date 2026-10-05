package com.example.data.remote

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val channelTitle: String,
    val description: String,
    val publishedAt: String,
    val thumbnailUri: String,
    val viewCount: String,
    val likeCount: String,
    val tags: List<String>,
    val hashtags: List<String>
)

data class CreatorStats(
    val estimatedEarnings: Double,
    val totalCredits: Int,
    val monthlyGrowthPercent: Double,
    val subscriberCount: String
)
