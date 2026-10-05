package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedItemEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.remote.CreatorStats
import com.example.data.remote.YouTubeVideo
import com.example.data.repository.StudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudioUiState(
    val currentScreen: String = "home", // "home", "video_detail", "tool_result", "saved_items", "history"
    val activeToolName: String = "",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedVideo: YouTubeVideo? = null,
    val aiResultText: String = "",
    val creatorStats: CreatorStats = CreatorStats(1420.50, 5400, 28.4, "48.2K"),
    val selectedChipTags: Set<String> = emptySet(),
    val toastMessage: String? = null
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudioRepository(application)

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    val savedItems: Flow<List<SavedItemEntity>> = repository.savedItems
    val searchHistory: Flow<List<SearchHistoryEntity>> = repository.searchHistory

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val stats = repository.getCreatorStats()
            _uiState.update { it.copy(creatorStats = stats) }
        }
    }

    fun navigateTo(screen: String, toolName: String = "") {
        _uiState.update { it.copy(currentScreen = screen, activeToolName = toolName, aiResultText = if (toolName.isNotEmpty()) it.aiResultText else "") }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun analyzeUrlOrQuery(query: String, context: Context) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, searchQuery = query) }
            repository.addSearchHistory(query)
            try {
                val video = repository.fetchVideoDetails(query)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedVideo = video,
                        currentScreen = "video_detail",
                        toastMessage = "Video analyzed successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        toastMessage = "Error analyzing URL: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun runTool(toolName: String, input: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, activeToolName = toolName, currentScreen = "tool_result") }
            val prompt = "Act as an expert YouTube SEO and Growth Specialist. For the tool '$toolName' and input/topic '$input', provide professional, high-ranking, production-grade output formatted clearly with bullet points and copy-ready tags/hashtags/titles."
            val result = repository.generateAIText(prompt)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    aiResultText = result,
                    toastMessage = "$toolName generated successfully!"
                )
            }
        }
    }

    fun toggleTagSelection(tag: String) {
        _uiState.update { state ->
            val newSelected = if (state.selectedChipTags.contains(tag)) {
                state.selectedChipTags - tag
            } else {
                state.selectedChipTags + tag
            }
            state.copy(selectedChipTags = newSelected)
        }
    }

    fun saveItem(type: String, title: String, content: String, context: Context) {
        viewModelScope.launch {
            repository.saveItem(type, title, content)
            showToast(context, "Saved to collection!")
        }
    }

    fun deleteSavedItem(id: Long, context: Context) {
        viewModelScope.launch {
            repository.deleteSavedItem(id)
            showToast(context, "Removed from saved items")
        }
    }

    fun clearHistory(context: Context) {
        viewModelScope.launch {
            repository.clearHistory()
            showToast(context, "Search history cleared")
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Copied") {
        if (text.isBlank()) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast(context, "Copied $label to Clipboard!")
    }

    fun copySelectedTags(context: Context) {
        val tags = _uiState.value.selectedChipTags
        if (tags.isEmpty()) {
            showToast(context, "No tags selected!")
            return
        }
        val text = tags.joinToString(", ")
        copyToClipboard(context, text, "Selected Tags")
    }

    fun copyAllTags(context: Context, tags: List<String>) {
        if (tags.isEmpty()) {
            showToast(context, "No tags available!")
            return
        }
        val text = tags.joinToString(", ")
        copyToClipboard(context, text, "All Tags")
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
