package com.zavgorodniy.news.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zavgorodniy.news.fragment.NewsListFragment
import com.zavgorodniy.news.R
import com.zavgorodniy.news.utils.Utils.TYPE_ALL
import com.zavgorodniy.news.utils.Utils.TYPE_FAVORITE

class NewsPagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {

    private val mTabTitles = arrayOf(
            context.resources.getString(R.string.all_news),
            context.resources.getString(R.string.favorite_news))

    override fun getItem(position: Int): Fragment {
        val fragmentType = when (position) {
            0 -> TYPE_ALL
            1 -> TYPE_FAVORITE
            else -> TYPE_ALL
        }

        return NewsListFragment().newInstance(fragmentType)
    }

    override fun getCount(): Int {
        return mTabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTabTitles[position]
    }
}