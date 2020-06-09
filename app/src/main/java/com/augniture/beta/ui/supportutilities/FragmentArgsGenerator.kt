package com.augniture.beta.ui.supportutilities

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.lang.Exception

class FragmentArgsGenerator {

    companion object {
        fun createFragmentWithArgs(fragment: Fragment, argsKey: String?, argsValue: Any?): Fragment {
            try {
                val args = Bundle()

                when(argsValue) {
                    is Int -> { args.putInt(argsKey, argsValue) }
                }

                fragment.arguments = args

                return fragment
            } catch (e: Exception) {
                throw Exception("$e: Error while creating fragment with args")
            }
        }
    }

}