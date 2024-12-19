package com.collegegrad.newsworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.collegegrad.newsworld.Api.NewsApiService
import com.collegegrad.newsworld.Repository.NewsRepository
import com.collegegrad.newsworld.ViewModel.NewsViewModel
import com.collegegrad.newsworld.ui.theme.NewsWorldTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Retrofit
        val apiService = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)

        // Initialize Repository
        val repository = NewsRepository(apiService)

        enableEdgeToEdge()
        setContent {
            val viewModel = NewsViewModel(repository = repository)
            NewsApp(viewModel = viewModel)
        }
    }
}
