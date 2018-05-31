package com.zavgorodniy.news.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class SourceEntity : RealmObject() {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null
}