package com.mattg.myrecipes.models.responsePreset


import com.google.gson.annotations.SerializedName

data class WinePairing(
    @SerializedName("pairedWines")
    val pairedWines: List<String>,
    @SerializedName("pairingText")
    val pairingText: String,
    @SerializedName("productMatches")
    val productMatches: List<ProductMatche>
)