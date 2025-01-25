package com.panco.multichoice.utils

import androidx.fragment.app.Fragment
import com.panco.multichoice.MainActivity

object ToolBarHelper {

    fun setToolBarTitle(fragment: Fragment, title: String) {
        (fragment.activity as MainActivity).supportActionBar?.title = title
    }
}