package com.zavgorodniy.news.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.zavgorodniy.news.adapter.NewsPagerAdapter
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_news.*
import android.net.Uri
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import android.content.Intent
import android.view.View
import android.widget.*
import com.facebook.CallbackManager
import com.zavgorodniy.news.R
import com.zavgorodniy.news.fragment.NewsListFragment
import com.zavgorodniy.news.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class NewsListActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private lateinit var mShareDialog: ShareDialog
    private lateinit var mCallbackManager: CallbackManager

    private var mPagerAdapter: PagerAdapter? = null
    private var mSourcesAdapter: ArrayAdapter<String>? = null
    private var mSpinner: Spinner? = null

    private lateinit var mMenu: Menu

    var mFilterSource = ""
    var mDateFrom: Calendar? = null
    var mDateTo: Calendar? = null
    private var mDateIsTo = false

    private var mSources = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setSupportActionBar(toolbar as Toolbar)

        initView()
    }

    private fun initView() {
        mSourcesAdapter = ArrayAdapter(this,
                R.layout.item_spinner, mSources)

        mSourcesAdapter!!.setDropDownViewResource(R.layout.item_spinner)

        mPagerAdapter = NewsPagerAdapter(supportFragmentManager, applicationContext)
        pager.adapter = mPagerAdapter
        tabs.setupWithViewPager(pager)

        mCallbackManager = CallbackManager.Factory.create()
        mShareDialog = ShareDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mMenu = menu

        val item = mMenu.findItem(R.id.sp_source)

        mSpinner = item.actionView as Spinner
        mSpinner?.adapter = mSourcesAdapter
        mSpinner?.onItemSelectedListener = this

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_share -> {
                shareApp()
                true
            }

            R.id.action_filter_date_from -> {
                chooseFilterDate(false)
                true
            }

            R.id.action_filter_date_to -> {
                chooseFilterDate(true)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun chooseFilterDate(isTo: Boolean) {
        mDateIsTo = isTo

        val date = Calendar.getInstance()

        DatePickerDialog(this, this,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    private fun shareApp() {

        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent = ShareLinkContent.Builder()
                    .setQuote(getString(R.string.share_message))
                    .setContentUrl(Uri.parse(getString(R.string.share_link)))
                    .build()

            mShareDialog.show(linkContent)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, day)

        if (mDateIsTo) {

            if (!Utils.checkDates(mDateFrom, date)) {
                showMessage(getString(R.string.incorrect_dates))
                return
            }

            mDateTo = date

            val filterDateTo = resources.getString(R.string.date_to) +
                    ": " + dateFormat.format(mDateTo?.time)

            mMenu.findItem(R.id.action_filter_date_to).title = filterDateTo

        } else {

            if (!Utils.checkDates(date, mDateTo)) {
                showMessage(getString(R.string.incorrect_dates))
                return
            }

            mDateFrom = date

            val filterDateFrom = resources.getString(R.string.date_from) +
                    ": " + dateFormat.format(mDateFrom?.time)

            mMenu.findItem(R.id.action_filter_date_from).title = filterDateFrom
        }

        makeFilterSearch()
    }

    override fun onDestroy() {
        super.onDestroy()
        Realm.getDefaultInstance().close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun initSourceList() {
        mSources.clear()
        mSourcesAdapter?.notifyDataSetChanged()
    }

    fun refreshSources(sources: Collection<String>) {
        initSourceList()

        mFilterSource = resources.getString(R.string.all)
        mSpinner?.setSelection(0)

        mSources.add(mFilterSource)
        mSources.addAll(sources)
        mSourcesAdapter?.notifyDataSetChanged()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mFilterSource = mSources[position]
        makeFilterSearch()
    }

    private fun makeFilterSearch() {
        val frAll = mPagerAdapter?.instantiateItem(pager, 0) as NewsListFragment
        val frFavorite = mPagerAdapter?.instantiateItem(pager, 1) as NewsListFragment
        frAll.showNews()
        frFavorite.showNews()
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}