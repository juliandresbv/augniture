package com.augniture.beta.ui.home

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.augniture.beta.R

import com.augniture.beta.ui.home.featuredproducts.*
import com.augniture.beta.ui.home.productcategories.CategoriasProductosFragment
import com.augniture.beta.ui.supportutilities.FragmentArgsGenerator
import com.augniture.beta.ui.supportutilities.FragmentBundleConstants

class HomeFragmentStatePagerAdapter(
    private val fragmentManager: FragmentManager,
    private val fragmentList: MutableList<Fragment> = mutableListOf(),
    private val fragmentTitleList: MutableList<String> = mutableListOf()
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    override fun getCount(): Int = 2

    fun addFragment(fragment: Fragment, fragmentTitle: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(fragmentTitle)
    }

}