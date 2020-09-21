package com.mattg.myrecipes.db

import androidx.lifecycle.LiveData


//PROVIDE CLEAN API FOR VIEW MODELS TO GET AND SEND DATA ... MEDIATOR BETWEEN DATABASES
class RecipeRepository(private val dao: RecipeDao) {  //need to pass dao to call functions of dao from this class
//room database auto generates live data from these functions on a background thread USING THE SECOND VERSION WITH NO SUSPEND


    private val recipes = dao.getRecipesLiveData()


   suspend fun getRecipesSearch(query: String) : List<Recipe> {
       return dao.getRecipesByTitleSearch(query)
    }
    //use coroutines in viewmodel class to execute them, define functions as suspend
    fun getAllRecipes(): LiveData<List<Recipe>> {
          return recipes
      }

    fun insertRecipe(recipe: Recipe) {
        dao.insertRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) {
        dao.updateRecipe(recipe)

    }
     fun getRecipeById(id: String): Recipe {
     return dao.getRecipeById(id)
 }

    fun getRecipeByIdLive(id: String): LiveData<Recipe>{
       return dao.getRecipeById2(id)
    }

    fun deleteRecipe(recipe: Recipe){
        dao.deleteRecipe(recipe)
    }

}