package com.zavgorodniy.news.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zavgorodniy.news.R
import com.zavgorodniy.news.utils.formatDate
import com.zavgorodniy.news.utils.loadImg
import com.zavgorodniy.news.model.NewsEntity
import kotlinx.android.synthetic.main.item_news.view.*

class NewsListAdapter(private val news: MutableList<NewsEntity>,
                      private val onClick: (NewsEntity) -> Unit)
    : RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false).let {
                    ViewHolder(it, onClick)
                }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(news[position])
    }

    override fun getItemCount(): Int {
        return news.size
    }

    class ViewHolder (view: View, private val onClick: (NewsEntity) -> Unit) : RecyclerView.ViewHolder(view) {
        private val tvHeadline = view.tv_news_headline
        private val tvSource = view.tv_news_source
        private val tvDate = view.tv_news_date
        private val ivPreview = view.iv_news_preview

        fun bind(item: NewsEntity) {

            with(item) {
                ivPreview.loadImg(urlToImage)
                tvHeadline.text = title
                tvSource.text = source?.name
                tvDate.text = publishedAt?.formatDate(tvDate.context.resources)

                itemView.setOnClickListener{ onClick(this) }
            }
        }
    }

    fun addNews(freshNews: List<NewsEntity>) {
        news.clear()
        news.addAll(freshNews)
    }
}