package com.augniture.beta.ui.supportutilities

// Support class to handle messages between fragments when a fragment transaction is required
class FragmentBundleConstants {

    companion object {
        const val FROM_HOME_TO_PRODUCTS_KEY = "FROM_HOME_TO_PRODUCTS"
        const val FROM_CATEGORIES_TO_PRODUCTS_KEY = "FROM_CATEGORIES_TO_PRODUCTS"
        const val FROM_SEARCH_TO_PRODUCTS_KEY = "FROM_CATEGORIES_TO_PRODUCTS"
    }

    val bundleConstantsMap = mutableMapOf<String, Int>()

    init {
        bundleConstantsMap[FROM_HOME_TO_PRODUCTS_KEY] = 100
        bundleConstantsMap[FROM_CATEGORIES_TO_PRODUCTS_KEY] = 101
        bundleConstantsMap[FROM_SEARCH_TO_PRODUCTS_KEY] = 102
    }

}
