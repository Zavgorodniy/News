package com.zavgorodniy.news.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class NewsEntity() : RealmObject()  {

    constructor(headline: String?, imgUrl: String?, content: String?, author: String?, date: String?, url: String?) : this() {
        title = headline
        urlToImage = imgUrl
        description = content
        this.author = author
        publishedAt = date
        this.url = url

        source = SourceEntity()
        source!!.name = author
    }

    @SerializedName("source")
    var source: SourceEntity? = null

    @SerializedName("author")
    var author: String? = null

    @PrimaryKey
    @SerializedName("title")
    var title: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("url")
    var url: String? = null

    @SerializedName("urlToImage")
    var urlToImage: String? = null

    @SerializedName("publishedAt")
    var publishedAt: String? = null
}
