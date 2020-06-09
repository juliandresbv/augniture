package com.augniture.beta.ui.supportutilities

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

class TextValidator(private val textViewList: List<TextView>) : TextWatcher {

    //abstract fun validate(textView: TextView?, text: String?)

    override fun afterTextChanged(s: Editable) {
        //val text = textView.text.toString()
        //validate(textView, text)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
        /* Needs to be implemented, but we are not using it. */
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        /* Needs to be implemented, but we are not using it. */
    }

}
