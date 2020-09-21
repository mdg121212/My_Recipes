package com.mattg.myrecipes.ui.viewrecipes

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("android:setVisible")
fun setVisible(textView: TextView, isLeftover: Boolean){
    if(isLeftover){
        textView.visibility = View.VISIBLE
    }else{
        textView.visibility = View.GONE
    }
}

@BindingAdapter("android:loadImage")
fun loadImage(imageView: ImageView, imageUrl: String){
    Glide.with(imageView)
        .load(imageUrl)
        .into(imageView)
}