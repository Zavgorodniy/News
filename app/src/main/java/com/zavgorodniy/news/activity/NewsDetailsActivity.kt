package com.zavgorodniy.news.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.zavgorodniy.news.R
import com.zavgorodniy.news.model.NewsEntity
import com.zavgorodniy.news.utils.formatDate
import com.zavgorodniy.news.utils.loadImg
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_news_details.*

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var newsItem: NewsEntity
    private lateinit var realm: Realm
    private var isFavorite: Boolean = false
    private var urlToNews: String? = null
    private var urlToImage: String? = null
    private var title: String? = null

    private lateinit var shareDialog: ShareDialog
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val TITLE: String = "title"
        private const val IMG_URL: String = "imgUrl"
        private const val DESCRIPTION: String = "content"
        private const val AUTHOR: String = "author"
        private const val DATE: String = "date"
        private const val URL: String = "url"

        fun newIntent(context: Context?, headline: String?, imgUrl: String?, description: String?,
                      author: String?, date: String?, url: String?):
                Intent = Intent(context, NewsDetailsActivity::class.java).apply {
            putExtra(TITLE, headline)
            putExtra(IMG_URL, imgUrl)
            putExtra(DESCRIPTION, description)
            putExtra(AUTHOR, author)
            putExtra(DATE, date)
            putExtra(URL, url)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)
        setSupportActionBar(toolbar as Toolbar)

        urlToNews = intent.getStringExtra(URL)
        urlToImage = intent.getStringExtra(IMG_URL)
        title = intent.getStringExtra(TITLE)

        newsItem = NewsEntity(intent.getStringExtra(TITLE),
                intent.getStringExtra(IMG_URL),
                intent.getStringExtra(DESCRIPTION),
                intent.getStringExtra(AUTHOR),
                intent.getStringExtra(DATE),
                intent.getStringExtra(URL))

        realm = Realm.getDefaultInstance()
        isFavorite = checkInDb(newsItem)

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.findItem(R.id.sp_source).isVisible = false
        menu.findItem(R.id.action_filter_date_from).isVisible = false
        menu.findItem(R.id.action_filter_date_to).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_share -> {
                shareApp()
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareApp() {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent = ShareLinkContent.Builder()
                    .setQuote(title)
                    .setContentUrl(Uri.parse(urlToNews))
                    .build()

            shareDialog.show(linkContent)
        }
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        with (newsItem) {
            iv_news_preview.loadImg(urlToImage)
            tv_headline.text = title
            tv_author.text = source?.name
            tv_url.text = url

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                description?.let { tv_content.text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT) }
            } else {
                description?.let { tv_content.text = Html.fromHtml(description) }
            }

            tv_date.text = publishedAt?.formatDate(resources)
        }

        tv_url.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToNews))
            startActivity(browserIntent)
        }

        fab.setOnClickListener {
            manageFavorite()
        }

        setFabImage(isFavorite)

        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
    }

    private fun manageFavorite() {
        isFavorite = !isFavorite
        setFabImage(isFavorite)
    }

    private fun setFabImage(check: Boolean) {
        if (check) fab.setImageResource(R.drawable.btn_star_big_on_pressed)
        else fab.setImageResource(R.drawable.btn_star_big_off)
    }

    private fun checkInDb(item: NewsEntity): Boolean {
        if (realm.where(NewsEntity::class.java)
                        .equalTo(TITLE, item.title)
                        .findFirst() != null) return true

        return false
    }

    override fun onPause() {

        realm.executeTransaction { realm ->

            if (isFavorite && !checkInDb(newsItem)) {
                realm.insert(newsItem)

            } else if (!isFavorite && checkInDb(newsItem)) {
                realm.where(NewsEntity::class.java)
                        .equalTo(TITLE, newsItem.title)
                        .findFirst()
                        ?.deleteFromRealm()
            }
        }

        super.onPause()
    }
}