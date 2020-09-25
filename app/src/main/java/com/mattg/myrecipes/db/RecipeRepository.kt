package com.mattg.myrecipes.db

import androidx.lifecycle.LiveData



class RecipeRepository(private val dao: RecipeDao) {

    private val recipes = dao.getRecipesLiveData()


    suspend fun getRecipesSearch(query: String): List<Recipe> {
        return dao.getRecipesByTitleSearch(query)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return recipes
    }

    fun insertRecipe(recipe: Recipe) {
        dao.insertRecipe(recipe)
    }

//    fun updateRecipe(recipe: Recipe) {
//        dao.updateRecipe(recipe)
//
//    }
//     fun getRecipeById(id: String): Recipe {
//     return dao.getRecipeById(id)
// }

    fun getRecipeByIdLive(id: String): LiveData<Recipe> {
        return dao.getRecipeById2(id)
    }

    fun deleteRecipe(recipe: Recipe) {
        dao.deleteRecipe(recipe)
    }

}