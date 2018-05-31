package com.zavgorodniy.news.model

import com.google.gson.annotations.SerializedName

data class ResponseEntity(@SerializedName("status")
                          var status: String = "",

                          @SerializedName("totalResults")
                          var totalResults: Int = 0,

                          @SerializedName("articles")
                          var articles: MutableList<NewsEntity>? = null)