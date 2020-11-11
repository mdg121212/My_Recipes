package com.mattg.myrecipes.network

import com.mattg.myrecipes.models.recipeSearchResult.RecipeResponse
import com.mattg.myrecipes.models.responsePreset.AnalyzedInstruction
import com.mattg.myrecipes.models.responsePreset.SpoonacularResponsePreset
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCall {


    //allows a general query of any parameters; for search bar: home fragment
    @GET("complexSearch?&instructionsRequired=true&addRecipeInformation=true&number=10")
    fun getSearchedRecipes(
        @Query("apiKey") key: String,
        @Query("query") query: String?
    ): Call<SpoonacularResponsePreset>

    //get the detailed instructions for a recipe
    @GET("{id}/analyzedInstructions?apiKey={key}")
    fun getRecipeDetailsById(
        @Path("id") id: Int,
        @Path("key") key: String
    ): Call<AnalyzedInstruction>

    @GET("https://api.spoonacular.com/recipes/{id}/information?&includeNutrition=false")
    fun getSingleRecipe(
        @Path("id") id: Int,
        @Query("apiKey") key: String
    )
            : Call<RecipeResponse>

    @GET("complexSearch?&instructionsRequired=true&addRecipeInformation=true")
    fun getRecipesMain(
        @Query("apiKey") key: String,
        @Query("number") number: Int,
        @Query("query") cuisine: String?,
        @Query("sort") sort: String?

    ): Call<SpoonacularResponsePreset>
}