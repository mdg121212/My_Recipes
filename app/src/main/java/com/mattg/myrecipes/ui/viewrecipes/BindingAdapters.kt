package com.mattg.myrecipes.ui.viewrecipes

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mattg.myrecipes.R
import com.mattg.myrecipes.utils.GlideApp

@BindingAdapter("android:setVisible")
fun setVisible(textView: TextView, isLeftover: Boolean){
    if(isLeftover){
        textView.visibility = View.VISIBLE
    }else{
        textView.visibility = View.GONE
    }
}

@BindingAdapter("android:loadImage")
fun loadImage(imageView: ImageView, imageUrl: String?){
    if(imageUrl == null || imageUrl == ""){
        GlideApp.with(imageView)
            .load(R.drawable.ic_baseline_add_a_photo_24)
            .into(imageView)
    } else {
        GlideApp.with(imageView)
            .load(imageUrl)
            .into(imageView)
    }
    }