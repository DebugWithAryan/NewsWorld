package com.collegegrad.newsworld.Api


import com.collegegrad.newsworld.ApiConfig
import com.collegegrad.newsworld.dataclass.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("from") fromDate: String? = null,
        @Query("to") toDate: String? = null,
        @Query("apiKey") apiKey: String = ApiConfig.API_KEY
    ): Response<NewsResponse>
}


