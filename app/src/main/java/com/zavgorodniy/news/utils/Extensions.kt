package com.zavgorodniy.news.utils

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.zavgorodniy.news.R
import java.text.SimpleDateFormat
import java.util.*

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String?) {
    Picasso.with(context)
            .load(imageUrl)
            .error(R.drawable.no_image)
            .into(this)
}

fun String.formatDate(res: Resources): String {
    val dateSeparator = res.getString(R.string.date_separator)
    val format = SimpleDateFormat("dd MMM yyyy '$dateSeparator' H:mm", Locale.getDefault())

    return format.format(getDateFromStr(this)).toString()
}

fun String.getDate(): Date {
    return getDateFromStr(this)
}

fun getDateFromStr(str: String): Date {
    val initialFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.ENGLISH)
    initialFormat.timeZone = TimeZone.getTimeZone("UTC")
    return  initialFormat.parse(str)
}