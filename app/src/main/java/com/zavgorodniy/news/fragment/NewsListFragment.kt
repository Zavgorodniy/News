package com.zavgorodniy.news.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zavgorodniy.news.activity.NewsDetailsActivity
import com.zavgorodniy.news.activity.NewsListActivity
import com.zavgorodniy.news.R
import com.zavgorodniy.news.adapter.NewsListAdapter
import com.zavgorodniy.news.api.ApiManager
import com.zavgorodniy.news.utils.Utils.FRAGMENT_TYPE
import com.zavgorodniy.news.utils.Utils.TYPE_ALL
import com.zavgorodniy.news.utils.Utils.TYPE_FAVORITE
import com.zavgorodniy.news.model.NewsEntity
import com.zavgorodniy.news.utils.Utils
import com.zavgorodniy.news.utils.inflate
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*

class NewsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    //news num
    //TODO not works, api problem. Make search with "everything" request
    private var mPageSize: Int = 100

    private var mType: String? = null
    private lateinit var mAdapter: NewsListAdapter

    //TODO add country filter
    private lateinit var mCountry: String

    private lateinit var mActivity: NewsListActivity

    private var mNews: MutableList<NewsEntity> = mutableListOf()

    fun newInstance(type: String): NewsListFragment {
        val fragment = NewsListFragment()

        val args = Bundle()
        args.putString(FRAGMENT_TYPE, type)
        fragment.arguments = args

        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NewsListActivity

        mType = arguments?.getString(FRAGMENT_TYPE)
        mCountry = Utils.getCountryCode(resources)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mAdapter = NewsListAdapter(ArrayList(),
                {
                    startActivity(NewsDetailsActivity.newIntent(
                            context,
                            it.title,
                            it.urlToImage,
                            it.description,
                            it.source?.name,
                            it.publishedAt,
                            it.url)
                    )
                })

        rv_news_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_news_list.adapter = mAdapter

        refresh.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        refresh.isRefreshing = true
        onRefresh()
    }

    private fun getNews() {
        when (mType) {
            TYPE_ALL -> getFromApi()
            TYPE_FAVORITE -> getFromDb()
            else -> getFromApi()
        }
    }

    private fun getFromApi() {

        if (Utils.checkConnection(context)) {

            ApiManager.loadNews(mCountry, mPageSize)
                    .subscribe(
                            { news ->
                                run {
                                    mNews = news.articles!!
                                    setSourcesToActivity()
                                    showNews()
                                }
                            },
                            { onError(resources.getString(R.string.api_error)) }
                    )
        } else {
            onError(resources.getString(R.string.network_error))
        }
    }

    private fun setSourcesToActivity() {
        val sources = HashSet<String>()

        for (item in mNews) {
            item.source?.name?.let { sources.add(it) }
        }

        mActivity.refreshSources(sources)
    }

    private fun onError(message: String) {
        refresh.isRefreshing = false
        mActivity.showMessage(message)
    }

    private fun getFromDb() {
        mNews = Utils.getFromDb()
        showNews()
    }

    fun showNews() {
        val news = Utils.filterNews(mNews,
                resources.getString(R.string.all),
                mActivity.mFilterSource,
                mActivity.mDateFrom,
                mActivity.mDateTo)

        if (news.size == 0) showEmptyContent()
        else hideEmptyContent()

        mAdapter.addNews(news)
        mAdapter.notifyDataSetChanged()

        refresh.isRefreshing = false
    }

    private fun showEmptyContent() {
        tv_no_content.visibility = View.VISIBLE
    }

    private fun hideEmptyContent() {
        tv_no_content.visibility = View.GONE
    }

    override fun onRefresh() {
        getNews()
    }
}