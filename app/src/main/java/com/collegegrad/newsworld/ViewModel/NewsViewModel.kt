package com.collegegrad.newsworld.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collegegrad.newsworld.Repository.NewsRepository
import com.collegegrad.newsworld.dataclass.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _newsState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val newsState: StateFlow<UiState<List<Article>>> = _newsState.asStateFlow()

    private val _selectedArticle = MutableStateFlow<Article?>(null)
    val selectedArticle: StateFlow<Article?> = _selectedArticle.asStateFlow()

    private var currentCategory: String? = null
    private var currentFromDate: String? = null
    private var currentToDate: String? = null

    init {
        fetchNews()
    }

    fun fetchNews(
        category: String? = null,
        fromDate: String? = null,
        toDate: String? = null
    ) {
        viewModelScope.launch {
            _newsState.value = UiState.Loading
            currentCategory = category
            currentFromDate = fromDate
            currentToDate = toDate

            repository.getNews(category, fromDate, toDate)
                .onSuccess { articles ->
                    _newsState.value = UiState.Success(articles)
                }
                .onFailure { error ->
                    _newsState.value = UiState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    fun selectArticle(article: Article) {
        _selectedArticle.value = article
    }

    fun clearSelectedArticle() {
        _selectedArticle.value = null
    }
}
