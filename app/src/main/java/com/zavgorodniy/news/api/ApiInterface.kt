package com.zavgorodniy.news.api

import com.zavgorodniy.news.model.ResponseEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("top-headlines")
    fun getHeadlines(@Query("country") country: String,
                     @Query("pageSize") pageSize: Int) : Observable<ResponseEntity>

}