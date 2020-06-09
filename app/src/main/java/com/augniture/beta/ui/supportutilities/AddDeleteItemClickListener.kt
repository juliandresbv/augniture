package com.augniture.beta.ui.supportutilities

interface AddDeleteItemClickListener<T> {
    fun onAddButtonClicked(t: T)

    fun onDeleteButtonClicked(t: T)
}