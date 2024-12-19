package com.collegegrad.newsworld.Repository

import com.collegegrad.newsworld.Api.NewsApiService
import com.collegegrad.newsworld.dataclass.Article
import com.collegegrad.newsworld.dataclass.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NewsRepository(private val api: NewsApiService) {
    suspend fun getNews(
        category: String? = null,
        fromDate: String? = null,
        toDate: String? = null
    ): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val response: Response<NewsResponse> = api.getNews(
                category = category,
                fromDate = fromDate,
                toDate = toDate
            )

            when {
                response.isSuccessful -> {
                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        Result.success(newsResponse.articles)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                }
                else -> {
                    Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}