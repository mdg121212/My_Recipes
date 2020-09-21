package com.mattg.myrecipes.network

import com.mattg.myrecipes.models.recipeSearchResult.RecipeResponse
import com.mattg.myrecipes.models.responsePreset.AnalyzedInstruction
import com.mattg.myrecipes.models.responsePreset.SpoonacularResponsePreset
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCall {

    @GET("complexSearch?apiKey=02360b1608f54f4096530b82220410e6&instructionsRequired=true&addRecipeInformation=true")
    fun getRecipes(
        @Query("number") number: Int,
        @Query("cuisine") cuisine: String?,
        @Query("query") query: String?,
        @Query("sort") sort: String?

    ): Call<SpoonacularResponsePreset>

    //allows a general query of any parameters; for search bar home fragment
    @GET("complexSearch?apiKey=02360b1608f54f4096530b82220410e6&instructionsRequired=true&addRecipeInformation=true&number=10")
    fun getSearchedRecipes(
        @Query("query") query: String?
    ): Call<SpoonacularResponsePreset>

    //get the detailed instructions for a recipe
    @GET("{id}/analyzedInstructions?apiKey=02360b1608f54f4096530b82220410e6")
    fun getRecipeDetailsById(
        @Path("id") id: Int,
    ): Call<AnalyzedInstruction>

    @GET("https://api.spoonacular.com/recipes/{id}/information?apiKey=02360b1608f54f4096530b82220410e6&includeNutrition=false")
    fun getSingleRecipe(
        @Path("id") id: Int
    )
            : Call<RecipeResponse>

    @GET("complexSearch?apiKey=02360b1608f54f4096530b82220410e6&instructionsRequired=true&addRecipeInformation=true")
    fun getRecipesMain(
        @Query("number") number: Int,
        @Query("query") cuisine: String?,
        @Query("sort") sort: String?

    ): Call<SpoonacularResponsePreset>
}