package com.mattg.myrecipes


import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseFragment: Fragment(){

    val uiScope = CoroutineScope(Dispatchers.Default)

}