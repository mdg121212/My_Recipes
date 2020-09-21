package com.mattg.myrecipes.models.responsePreset


import com.google.gson.annotations.SerializedName

data class SpoonacularResponsePreset(
    @SerializedName("number")
    val number: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("totalResults")
    val totalResults: Int
)