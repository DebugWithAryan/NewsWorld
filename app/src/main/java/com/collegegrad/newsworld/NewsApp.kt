package com.collegegrad.newsworld

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.collegegrad.newsworld.ViewModel.NewsViewModel
import com.collegegrad.newsworld.ViewModel.UiState
import com.collegegrad.newsworld.dataclass.Article
import java.text.SimpleDateFormat
import java.util.*

// Custom Colors
val DarkBackground = Color(0xFF121212)
val CardGradientStart = Color(0xFF2C2C2C)
val CardGradientEnd = Color(0xFF1A1A1A)
val AccentColor = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApp(viewModel: NewsViewModel = viewModel()) {
    val newsState by viewModel.newsState.collectAsState()
    val selectedArticle by viewModel.selectedArticle.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("News World", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E1E1E)
                    ),
                    actions = {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Filter",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Filters section with higher z-index
                AnimatedVisibility(
                    visible = showFilters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E1E1E),
                        tonalElevation = 8.dp
                    ) {
                        FilterSection(
                            onApplyFilters = { category, fromDate, toDate ->
                                viewModel.fetchNews(category, fromDate, toDate)
                                showFilters = false
                            }
                        )
                    }
                }

                // Main content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                ) {
                    when {
                        selectedArticle != null -> {
                            ArticleDetailScreen(
                                article = selectedArticle!!,
                                onBackPress = { viewModel.clearSelectedArticle() }
                            )
                        }
                        else -> {
                            when (newsState) {
                                is UiState.Loading -> LoadingScreen()
                                is UiState.Success -> {
                                    NewsList(
                                        articles = (newsState as UiState.Success<List<Article>>).data,
                                        onArticleClick = { viewModel.selectArticle(it) }
                                    )
                                }
                                is UiState.Error -> ErrorScreen((newsState as UiState.Error).message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsList(articles: List<Article>, onArticleClick: (Article) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        items(articles) { article ->
            NewsCard(article = article, onClick = { onArticleClick(article) })
        }
    }
}

@Composable
fun NewsCard(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(CardGradientStart, CardGradientEnd)
                    )
                )
        ) {
            Column {
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatDate(article.publishedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(onApplyFilters: (String?, String?, String?) -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var fromDateMillis by remember { mutableStateOf<Long?>(null) }
    var toDateMillis by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomDatePicker(
            label = "From Date",
            selectedDate = fromDateMillis,
            onDateSelected = { fromDateMillis = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDatePicker(
            label = "To Date",
            selectedDate = toDateMillis,
            onDateSelected = { toDateMillis = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onApplyFilters(
                    selectedCategory,
                    fromDateMillis?.let { formatMillisToApiDate(it) },
                    toDateMillis?.let { formatMillisToApiDate(it) }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor
            )
        ) {
            Text("Apply Filters", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category", color = Color.White) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = AccentColor,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(article: Article, onBackPress: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Box {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onBackPress,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(DarkBackground)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "By ${article.source.name} • ${formatDate(article.publishedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = AccentColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article.content ?: article.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AccentColor)
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    label: String,
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    OutlinedTextField(
        value = selectedDate?.let { formatMillisToDate(it) } ?: "",
        onValueChange = { },
        label = { Text(label, color = Color.White) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Gray,
            focusedBorderColor = AccentColor,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White
        )
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    showDialog = false
                }) {
                    Text("OK", color = AccentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.LightGray,
                    subheadContentColor = Color.LightGray,
                    yearContentColor = Color.White,
                    currentYearContentColor = AccentColor,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = AccentColor,
                    dayContentColor = Color.White,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = AccentColor,
                    todayContentColor = AccentColor,
                    todayDateBorderColor = AccentColor
                )
            )
        }
    }
}

fun formatMillisToDate(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

fun formatMillisToApiDate(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}