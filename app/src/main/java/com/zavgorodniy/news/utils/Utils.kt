package com.zavgorodniy.news.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import com.zavgorodniy.news.R
import com.zavgorodniy.news.model.NewsEntity
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

object Utils {

    const val FRAGMENT_TYPE = "fragment_type"
    const val TYPE_ALL = "all"
    const val TYPE_FAVORITE = "favorite"

    fun filterNews(news: MutableList<NewsEntity>,
                   sourceDefaultString: String,
                   filterSource: String,
                   dateFrom: Calendar?,
                   dateTo: Calendar?): MutableList<NewsEntity> {

        val filteredNews = mutableListOf<NewsEntity>()
        filteredNews.addAll(news)

        //source filter
        if (filterSource != sourceDefaultString) {

            for (item in news) {
                if (item.source?.name != filterSource) filteredNews.remove(item)
            }
        }

        //date from filter
        if (dateFrom != null) {

            for (item in news) {
                if (item.publishedAt!!.getDate().before(dateFrom.time)) filteredNews.remove(item)
            }
        }

        //date to filter
        if (dateTo != null) {

            for (item in news) {
                if (item.publishedAt!!.getDate().after(dateTo.time)) filteredNews.remove(item)
            }
        }

        return filteredNews
    }

    fun checkConnection(context: Context?): Boolean {
        val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo = connMgr!!.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) return true

        return false
    }

    fun getFromDb(): RealmResults<NewsEntity> {
        return Realm.getDefaultInstance().where(NewsEntity::class.java).findAll()
    }

    fun getCountryCode(res: Resources): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res.configuration.locales.get(0).country
        } else {
            res.configuration.locale.country
        }
    }

    fun checkDates(dateFrom: Calendar?, dateTo: Calendar?): Boolean {
        if (dateFrom != null && dateTo != null) {
            return dateFrom.before(dateTo)
        }

        return true
    }
}