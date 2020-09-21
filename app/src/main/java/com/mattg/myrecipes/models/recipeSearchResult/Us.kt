package com.mattg.myrecipes.models.recipeSearchResult


import com.google.gson.annotations.SerializedName

data class Us(
    @SerializedName("amount")
    var amount: Double,
    @SerializedName("unitLong")
    var unitLong: String,
    @SerializedName("unitShort")
    var unitShort: String
)